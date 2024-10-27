package lab4;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
public class FileSystemDriver {
    private Directory directory;
    private Map<Integer, FileDescriptor> openFiles;
    private Map<Integer, Integer> fileOffsets;
    private final int maxFileDescriptors;
    private int nextFd;

    public FileSystemDriver(int n) {
        fileOffsets = new HashMap<>();
        this.directory = new Directory();
        this.openFiles = new HashMap<>();
        this.maxFileDescriptors = n;
        this.nextFd = 0;
    }

    public void mkfs() {
        System.out.println("File System initialized with " + maxFileDescriptors + " file descriptors.");
    }

    public void stat(String name) {
        FileDescriptor fd = directory.getFileDescriptor(name);
        if (fd != null) {
            System.out.println("File Name: " + name);
            System.out.println("File Size: " + fd.getSize());
            System.out.println("Reference Count: " + fd.getRefCount());
        } else {
            System.out.println("File not found.");
        }
    }

    public void ls() {
        System.out.println("Listing files:");
        for(String fileName : directory.files.keySet()) {
            FileDescriptor fd = directory.getFileDescriptor(fileName);
            System.out.println(fileName + " -> Descriptor: " + fd);
        }
    }

    public void create(String name) {
        directory.createFile(name);
        System.out.println("File created: " + name);
    }

    public int open(String name) {
        FileDescriptor fd = directory.getFileDescriptor(name);
        if (fd != null) {
            if (nextFd < maxFileDescriptors) {
                fd.incrementRefCount();
                openFiles.put(nextFd, fd);
                fileOffsets.put(nextFd, 0);  // Reset offset to 0 when opened
                System.out.println("File opened with descriptor: " + nextFd);
                return nextFd++;
            } else {
                System.out.println("Maximum file descriptors limit reached.");
            }
        } else {
            System.out.println("File not found.");
        }
        return -1;
    }

    public void close(int fd) {
        if (openFiles.containsKey(fd)) {
            FileDescriptor fileDescriptor = openFiles.get(fd);
            fileDescriptor.decrementRefCount();
            openFiles.remove(fd);
            System.out.println("File with descriptor " + fd + " closed.");
        } else {
            System.out.println("Invalid file descriptor.");
        }
    }

    public void seek(int fd, int offset) {
        if (openFiles.containsKey(fd)) {
            FileDescriptor fileDescriptor = openFiles.get(fd);
            if (offset >= 0 && offset <= fileDescriptor.getSize()) {
                fileOffsets.put(nextFd, offset);
                System.out.println("Seek operation on fd " + fd + " to offset " + offset);
            } else {
                System.out.println("Invalid offset.");
            }
        } else {
            System.out.println("Invalid file descriptor.");
        }
    }

    public void read(int fd, int size) {
        if (openFiles.containsKey(fd)) {
            FileDescriptor fileDescriptor = openFiles.get(fd);
            int currentOffset = fileOffsets.get(fd);
            int bytesRead = Math.min(size, fileDescriptor.getSize() - currentOffset);
            if (bytesRead > 0) {
                // Simulating reading from file
                byte[] data = new byte[bytesRead];
                System.arraycopy(fileDescriptor.getContent(), currentOffset, data, 0, bytesRead);
                fileOffsets.put(fd, currentOffset + bytesRead);

                System.out.println("Read " + bytesRead + " bytes from file " + getFileName(fileDescriptor));
                // Display read data as a string
                System.out.println("Data: " + new String(data));
            } else {
                System.out.println("End of file reached or no data to read.");
            }
        } else {
            System.out.println("Invalid file descriptor.");
        }
    }

    public void write(int fd, int size) {
        if (openFiles.containsKey(fd)) {
            FileDescriptor fileDescriptor = openFiles.get(fd);
            int currentOffset = fileOffsets.get(fd);
            if (currentOffset + size <= fileDescriptor.getContent().length) {
                // Simulating writing to file
                byte[] data = new byte[size];
                Arrays.fill(data, (byte) 'A'); // Fill with dummy data (character 'A')
                System.arraycopy(data, 0, fileDescriptor.getContent(), currentOffset, size);
                fileDescriptor.setSize(Math.max(fileDescriptor.getSize(), currentOffset + size)); // Update size
                fileOffsets.put(fd, currentOffset + size); // Move offset forward
                System.out.println("Written " + size + " bytes to file " + getFileName(fileDescriptor));
            } else {
                System.out.println("Not enough space to write data.");
            }
        } else {
            System.out.println("Invalid file descriptor.");
        }
    }

    public void link(String name1, String name2) {
        FileDescriptor fd = directory.getFileDescriptor(name1);
        if (fd != null) {
            directory.createFile(name2);
            fd.incrementRefCount();
            System.out.println("Created hard link " + name2 + " to " + name1);
        } else {
            System.out.println("File not found: " + name1);
        }
    }

    public void unlink(String name) {
        directory.deleteFile(name);
        System.out.println("Unlinked file: " + name);
    }

    public void truncate(String name, int size) {
        FileDescriptor fd = directory.getFileDescriptor(name);
        if (fd != null) {
            fd.setSize(size);
            System.out.println("Truncated file " + name + " to size " + size);
        } else {
            System.out.println("File not found: " + name);
        }
    }

    private String getFileName(FileDescriptor fd) {
        for(Map.Entry<String, FileDescriptor> entry : directory.files.entrySet()) {
            if(entry.getValue() == fd) {
                return entry.getKey();
            }
        }
        throw new RuntimeException();
    }

    public static void main(String[] args) {
        FileSystemDriver fsDriver = new FileSystemDriver(10); // Initialize FS with 10 file descriptors
        fsDriver.mkfs();

        // Example usage
        fsDriver.create("file1.txt");
        System.out.println("Stat: ");
        fsDriver.stat("file1.txt");
        System.out.println("\nls: ");
        fsDriver.ls();
        int fd = fsDriver.open("file1.txt");
        System.out.println("Write Seek Read: ");
        fsDriver.write(fd, 1024); // Writing 1024 bytes
        fsDriver.seek(fd, 0);
        fsDriver.read(fd, 512); // Reading 512 bytes

        //fsDriver.stat("file1.txt");
        fsDriver.close(fd);
        System.out.println("\nStat: ");
        fsDriver.stat("file1.txt");
        fsDriver.link("file1.txt", "file2.txt");
        System.out.println("\nStat: ");
        fsDriver.stat("file2.txt");
        fsDriver.unlink("file2.txt");
        fsDriver.truncate("file1.txt", 2048); // Truncate to 2048 bytes
    }
}
