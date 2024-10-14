package lab4;

import java.util.HashMap;
import java.util.Map;

public class FileSystemDriver {
    private Directory rootDirectory;
    private Map<Integer, FileDescriptor> openFiles;
    private Map<Integer, Integer> fileOffsets = new HashMap<>();
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
    public void ls() {
        for(String fileName : rootDirectory.files.keySet()) {
            FileDescriptor fd = rootDirectory.getFileDescriptor(fileName);
            System.out.println(fileName + " -> Descriptor: " + fd);
        }
    }

    public int open(String name) {
        FileDescriptor fd = rootDirectory.getFileDescriptor(name);
        if(fd != null) {
            openFiles.put(nextFd, fd);
            return nextFd++;
        }
        return -1; // File not found
    }
    public void close(int fd) {
        openFiles.remove(fd);
    }

    public void seek() {

    }

    public void truncate(String name, int size) {
        FileDescriptor fd = rootDirectory.getFileDescriptor(name);
        if(fd != null) {
            fd.setSize(size);
        }
    }

    public void link() {

    }

}
