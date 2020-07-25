import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class FolderSizeCalculator extends RecursiveTask<Long> {
    private File folder;
    private long sizeKB = 1024;

    public FolderSizeCalculator(File folder) {
        this.folder = folder;
    }

    @Override
    protected Long compute() {
        if(folder.isFile()) {
            return folder.length();
        }

        long sum = 0;
        List<FolderSizeCalculator> subTasks = new LinkedList<>();

        File[] files = folder.listFiles();
        for(File file : files)
        {
            FolderSizeCalculator task = new FolderSizeCalculator(file);
            task.fork();
            subTasks.add(task);
        }

        for(FolderSizeCalculator task : subTasks) {
            sum += task.join();
        }

        return sum;
    }

    public String getHumanReadableSize(long size) {

        if (size / sizeKB > 0) {
            if (size / (sizeKB * sizeKB * sizeKB) >= 1) {
                return (size/(sizeKB * sizeKB * sizeKB)) + " Gb";
            } else if(size/(sizeKB * sizeKB) >= 1) {
                return (size/(sizeKB * sizeKB)) + " Mb";
            }
            return size/sizeKB + " Kb";

        } else {
            return size + " b";
        }
    }

    public long getSizeFromHumanReadable(String size) {

        if (size.contains("b")) {
            return Long.parseLong(size.split("b")[0]);

        }

        return 0;
    }

}