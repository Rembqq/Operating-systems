package lab4;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
public class FileSystemDriver {
    private final Directory directory;
    private final Map<Integer, FileDescriptor> openFiles;
    private final Map<Integer, Integer> fileOffsets;
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
                fileOffsets.put(fd, offset);
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
                int bytesRemaining = bytesRead;
                int blockIndex = currentOffset / FileDescriptor.BLOCK_SIZE;
                int blockOffset = currentOffset % FileDescriptor.BLOCK_SIZE;

                int bytesCopied = 0;
                while (bytesRemaining > 0 && blockIndex < fileDescriptor.getBlockMap().size()) {
                    //byte[] block = fileDescriptor.getBlockMap().get(blockIndex);
                    byte[] block = fileDescriptor.readBlock(blockIndex);

                    int bytesToCopy = Math.min(bytesRemaining, FileDescriptor.BLOCK_SIZE - blockOffset);

                    System.arraycopy(block, blockOffset, data, bytesCopied, bytesToCopy);

                    bytesCopied += bytesToCopy;
                    bytesRemaining -= bytesToCopy;
                    blockIndex++;
                    blockOffset = 0;
                }

                fileOffsets.put(fd, currentOffset + bytesRead);

                System.out.println("Read " + bytesRead + " bytes from file " + getFileName(fileDescriptor));
                // Display read data as a string
                System.out.println("Data: " + new String(data));
                System.out.println("Blocks: " + fileDescriptor.getBlockMap());

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
            int blockIndex = currentOffset / FileDescriptor.BLOCK_SIZE;
            int blockOffset = currentOffset % FileDescriptor.BLOCK_SIZE;
            int bytesWrittenTotal = 0;

            while (bytesWrittenTotal < size) {
                byte[] block = fileDescriptor.readBlock(blockIndex);
                int toWrite = Math.min(size - bytesWrittenTotal, FileDescriptor.BLOCK_SIZE - blockOffset);
                byte[] data = new byte[toWrite];
                Arrays.fill(data, (byte) 'A'); // dummy data
                System.arraycopy(data, 0, block, blockOffset, toWrite);

                fileDescriptor.writeBlock(blockIndex, block);
                bytesWrittenTotal += toWrite;
                blockOffset = 0;
                blockIndex++;
            }

            fileDescriptor.setSize(Math.max(fileDescriptor.getSize(), currentOffset + size));
            fileOffsets.put(fd, currentOffset + size);
            System.out.println("Written " + size + " bytes to file " + getFileName(fileDescriptor));
        } else {
            System.out.println("Invalid file descriptor.");
        }

//            if (currentOffset + size <= fileDescriptor.getContent().length) {
//
//                byte[] data = new byte[size];
//                Arrays.fill(data, (byte) 'A'); // Fill with dummy data (character 'A')
//                System.arraycopy(data, 0, fileDescriptor.getContent(), currentOffset, size);
//                fileDescriptor.setSize(Math.max(fileDescriptor.getSize(), currentOffset + size)); // Update size
//                fileOffsets.put(fd, currentOffset + size); // Move offset forward
//                System.out.println("Written " + size + " bytes to file " + getFileName(fileDescriptor));
//            } else {
//                System.out.println("Not enough space to write data.");
//            }
//        } else {
//            System.out.println("Invalid file descriptor.");
//        }
    }

    public void link(String name1, String name2) {
        FileDescriptor fd = directory.getFileDescriptor(name1);
        if (fd != null) {
            if(directory.files.containsKey(name1)){
                directory.files.put(name2, fd);
                fd.incrementRefCount();
                System.out.println("Created hard link " + name2 + " to " + name1);
            }
        } else {
            System.out.println("File not found: " + name1);
        }
    }

//    public void unlink(String name) {
//        FileDescriptor fd = directory.getFileDescriptor(name);
//        if(fd != null) {
//            if(fd.getRefCount() > 1) {
//                directory.removeLink(name);
//                System.out.println("Unlinked file: " + name);
//            } else {
//                System.out.println("Cannot unlink the last hard link for the file: " + name);
//            }
//        } else {
//            System.out.println("File not found: " + name);
//        }
//    }


    public void unlink(String name) {
        FileDescriptor fd = directory.getFileDescriptor(name);

        if (fd == null) {
            System.out.println("File not found: " + name);
            return;
        }

        if (!directory.files.containsKey(name)) {
            System.out.println("No key found: " + name);
            return;
        }

        boolean isOpen = openFiles.containsValue(fd);
        boolean isLastReference = (fd.getRefCount() == 1);

        if (isOpen) {
            if (isLastReference) {
                System.out.println("The last reference is opened");
            } else {
                fd.decrementRefCount();
                System.out.println("One reference deleted: " + name);
            }
        } else {
            fd.decrementRefCount();
            directory.files.remove(name);
            if (isLastReference) {
                System.out.println("Removed the last link: " + name);
            } else {
                System.out.println("One reference deleted: " + name);
            }
        }

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
        fsDriver.write(fd, 8193); // Writing 4096 * 2 + 1 bytes


        fsDriver.seek(fd, 0);
        fsDriver.read(fd, 8000); // Reading 8000 bytes

        //fsDriver.stat("file1.txt");
        fsDriver.close(fd);
        System.out.println("\nStat: ");
        fsDriver.stat("file1.txt");

        fsDriver.link("file1.txt", "file2.txt");

        System.out.println("\nStat: ");
        fsDriver.stat("file2.txt");

        System.out.println("\nls: ");
        fsDriver.ls();

        fsDriver.unlink("file2.txt");

        System.out.println("\nls: ");
        fsDriver.ls();

        System.out.println("\nStat: ");
        fsDriver.stat("file1.txt");

        fsDriver.truncate("file1.txt", 2048); // Truncate to 2048 bytes

        System.out.println("\nStat: ");
        fsDriver.stat("file1.txt");
    }
}
