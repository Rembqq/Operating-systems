package lab4;

import java.util.HashMap;
import java.util.Map;

public class FileSystemDriver {
    private Directory rootDirectory;
    private Map<Integer, FileDescriptor> openFiles;
    private int nextFd = 0;

    public FileSystemDriver() {
        this.rootDirectory = new Directory();
        this.openFiles = new HashMap<>();
    }

    public void mkfs(int n) {
        rootDirectory = new Directory();
    }

    public void create(String name) {
        rootDirectory.createFile(name);
    }

    public void stat(String name) {
        FileDescriptor fd = rootDirectory.getFileDescriptor(name);
        if(fd != null) {
            System.out.println("File: " + fd.getName() + ", Size: " + fd.getSize() + ", Blocks: " + fd.getBlockIndices().size());
        } else {
            System.out.println("FIle not found");
        }
    }

}
