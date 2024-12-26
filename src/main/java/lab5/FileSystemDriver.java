package lab5;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FileSystemDriver {
    private Directory root;
    private Directory currentDirectory;
    public Map<String, FileDescriptor> symlinks = new HashMap<>(); // для зберігання символічних посилань
    private final Map<String, Directory> directories;
    private final Map<Integer, FileDescriptor> openFiles;
    private final Map<Integer, Integer> fileOffsets;
    private final int maxFileDescriptors;
    private int nextFd;

    public FileSystemDriver(int n) {
        this.root = new Directory("/", null);
        this.currentDirectory = root;
        this.directories = new HashMap<>();

        fileOffsets = new HashMap<>();
        this.openFiles = new HashMap<>();
        this.maxFileDescriptors = n;
        this.nextFd = 0;
    }

    // Створення директорії
    public void mkdir(String pathname) { // the only flaw is that you can create directory only from its parent directory

        if(pathname.startsWith("/")) {
            pathname = pathname.substring(1, pathname.length() - 1);
        }
        String absolutePath;
        if(currentDirectory == root) {
            absolutePath = "/" + pathname;
        } else {
            absolutePath = currentDirectory.getDirAbsolutePath() + "/" + pathname;
        }
        Directory newDir = new Directory(absolutePath, currentDirectory);
        newDir.setParent(currentDirectory);

        String resolvedAbsolutePath = resolvePath(absolutePath);

        currentDirectory.files.put(newDir.getName(), new FileDescriptor(newDir.getName(), 1));
        directories.put(resolvedAbsolutePath, newDir);


//        String resolvedPath = resolvePath(pathname);
//        String absolutePath = getAbsolutePath(resolvedPath);
//
//        String[] parts = pathname.split("/");
//        Directory parent;
//
//        for(String part: parts) {
//            if(parts.length == 1) {
//                parent = currentDirectory;
//            }
//            if(parts[0].isEmpty()) { // pathname start with '/'
//                parent = root;
//            } else {
//
//            }
//        }
//
//        if (directories.containsKey(absolutePath)) {
//            return;
//        }
//
//        parent = directories.get(getParentByPath(resolvedPath));
//
//
//        if (parent != null) {
//            parent.files.put(resolvedPath, new FileDescriptor(pathname, 1));
//        }
//
//        directories.put(resolvedPath, newDir);
//        newDir.setParent(parent);
//
//        System.out.println("Directory " + absolutePath + " created.");
    }

    public void rmdir(String pathname) {

        String resolvedPath = resolvePath(pathname);
        String absolutePath = getAbsolutePath(resolvedPath);

        Directory dir = directories.get(absolutePath);
        if (resolvedPath.equals(currentDirectory.getDirAbsolutePath())) {
            return;
        }
        //System.out.println(dir.files);
        if (dir == null || !dir.isEmpty()) {
            System.out.println("Directory cannot be removed: not empty or invalid path.");
            return;
        }
        dir.getParent().removeFile(dir.getName());

        directories.remove(absolutePath);
        System.out.println("Directory " + absolutePath + " removed.");
    }

    public String resolvePath(String path) {
        if (path.equals("/")) {
            return path;
        }

        // Удаляем конечный слэш, если он есть
        path = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;

        if (path.isEmpty()) {
            return null;
        }

        // Переменная для хранения результата
        StringBuilder resolvedPath = new StringBuilder();

        // Начальный каталог зависит от первого символа

        Directory dir;

        if(path.startsWith("/")) {
            resolvedPath.append("/");
            dir = root;
        } else {
            dir = currentDirectory;
        }

        //Directory dir = path.startsWith("/") ? root : currentDirectory;

        // Разбиваем путь на части
        String[] parts = path.split("/");
        //System.out.println(Arrays.toString(parts));

        boolean firstPart = true; // Флаг для обработки первой части пути

        for (String part : parts) {
            if (part.equals("..")) {
                // Переход к родительскому каталогу
                dir = dir.getParent() != null ? dir.getParent() : dir;

                // Удаляем последнюю часть из resolvedPath
                int lastSlash = resolvedPath.lastIndexOf("/");
                if (lastSlash > 0) {
                    resolvedPath.setLength(lastSlash);
                }
            } else if (part.equals(".")) {
                // Пропускаем, так как "." означает текущую директорию
                continue;
            }

            else if (!part.isEmpty()) {
                // Проверяем, является ли это символической ссылкой
                if (symlinks.containsKey(part)) {
                    String symlinkTarget = symlinks.get(part).getSymlinkTarget();

                    // Если это первая часть пути и symlinkTarget начинается с "/", заменяем полностью
                    if (firstPart && symlinkTarget.startsWith("/")) {
                        resolvedPath.setLength(0); // Сбрасываем путь
                        resolvedPath.append(symlinkTarget);
                    } else if (!symlinkTarget.startsWith("/")) {
                        // Для остальных частей заменяем только относительные ссылки
                        part = symlinkTarget;
                    }
                }

                // Добавляем часть к результату
                if (resolvedPath.length() > 0 && resolvedPath.charAt(resolvedPath.length() - 1) != '/') {
                    resolvedPath.append("/");
                }
                if(!symlinks.containsKey(part)){
                    resolvedPath.append(part);
                }
            }

            firstPart = false; // После обработки первой части флаг отключается
        }
        if (resolvedPath.charAt(resolvedPath.length() - 1) == '/') {
            resolvedPath.deleteCharAt(resolvedPath.length() - 1);
        }
        // Возвращаем абсолютный путь
        return resolvedPath.length() > 0 ? resolvedPath.toString() : "/";
    }

    private String getAbsolutePath(String resolvedPath) {

        // Если путь уже абсолютный, возвращаем его
        if (resolvedPath.startsWith("/")) {
            return resolvedPath;
        }

        // Построение абсолютного пути
        StringBuilder absolutePath = new StringBuilder();
        Directory dir = currentDirectory;

        // Проходим вверх по дереву директорий до корневой
        while (!Objects.equals(dir.getDirAbsolutePath(), "/")) {
            absolutePath.insert(0, "/" + dir.getName());
            dir = dir.getParent();
        }

        // Добавляем относительный путь к абсолютному
        if (!resolvedPath.isEmpty()) {
            if (absolutePath.length() > 1) {
                absolutePath.append("/"); // Добавляем слэш между абсолютным и относительным путями
            }
            absolutePath.append(resolvedPath);
        }

        return absolutePath.toString();
    }
    private String getParentByPath(String path) {

//        String[] parts = path.split("/");
//        if(parts.length == 1) {
//            return "/";
//        }
//
//        return parts[parts.length - 2];

        if (path == null || !path.contains("/")) {
            return null; // Некоректний шлях
        }

        if(path.equals("/")) {
            return path;
        }

        // Видаляємо кінцевий слеш, якщо він є
        path = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;

        // Знаходимо останній слеш
        int lastSlashIndex = path.lastIndexOf("/");


        // Повертаємо шлях до батьківської директорії
        return path.substring(0, lastSlashIndex);

    }

    public void cd(String pathname) {

        String resolvedPath = resolvePath(pathname);
        String absolutePath = getAbsolutePath(resolvedPath);

        Directory dir = directories.get(absolutePath);
        if (dir != null) {
            this.currentDirectory = dir;
            System.out.println("Current directory changed to " + resolvedPath);
        } else {
            System.out.println("Invalid path.");
        }
    }

    public void symlink(String str, String pathname) {
        if (str.length() > FileDescriptor.BLOCK_SIZE) {
            System.out.println("Symlink content exceeds maximum length.");
            return;
        }

        String resolvedPath = resolvePath(pathname);

        FileDescriptor symlink = new FileDescriptor(resolvedPath, 3);
        symlink.setSymlinkTarget(resolvedPath);
        currentDirectory.addFile(resolvedPath, symlink);
        if(!symlinks.containsKey(str)) {
            symlinks.put(str, symlink);
        }
        System.out.println("Symbolic link " + resolvedPath + " created -> " + str);
    }

    public void mkfs() {
        System.out.println("File System initialized with " + maxFileDescriptors + " file descriptors.");
    }

    public void stat(String name) {
        FileDescriptor fd = currentDirectory.getFileDescriptor(name);
        if (fd != null) {
            System.out.println("File Name: " + name);
            System.out.println("File Size: " + fd.getSize());
            System.out.println("Reference Count: " + fd.getRefCount());
        } else {
            System.out.println("File not found.");
        }
    }

    public void ls() {

        if (currentDirectory.files.isEmpty()) {
            System.out.println("No files in the directory.");
            return;
        }

        System.out.println("Files in the directory:");
        for (Map.Entry<String, FileDescriptor> entry : currentDirectory.files.entrySet()) {
            String fileName = entry.getKey();
            FileDescriptor fd = entry.getValue();
            System.out.println("Name: " + fileName + ", Size: " + fd.getSize() + ", RefCount: " + fd.getRefCount());
        }

    }
    public void create(String name) {
        if (currentDirectory.getFileDescriptor(name) == null) {
            currentDirectory.createFile(name);
            System.out.println("File created: " + name);
        } else {
            System.out.println("File already exists: " + name);
        }
    }

//    public int open(String name) {
//        FileDescriptor fd = currentDirectory.getFileDescriptor(name);
//        if (fd == null) {
//            System.out.println("File not found: " + name);
//            return -1;
//        }
//        if (nextFd >= maxFileDescriptors) {
//            System.out.println("Maximum file descriptors limit reached.");
//            return -1;
//        }
//        fd.incrementRefCount();
//        openFiles.put(nextFd, fd);
//        fileOffsets.put(nextFd, 0);
//        System.out.println("File opened: " + name + " with descriptor: " + nextFd);
//        return nextFd++;
//    }
    public int open(String pathname) {
        String resolvedPath = resolvePath(pathname);
        String absolutePath = getAbsolutePath(resolvedPath);

        FileDescriptor fd = getFileDescriptorByPath(absolutePath);
        if (fd == null) {
            System.out.println("File not found: " + pathname);
            return -1;
        }

        if (nextFd >= maxFileDescriptors) {
            System.out.println("Maximum file descriptors limit reached.");
            return -1;
        }

        fd.incrementRefCount();
        openFiles.put(nextFd, fd);
        fileOffsets.put(nextFd, 0);
        System.out.println("File opened: " + pathname + " with descriptor: " + nextFd);
        return nextFd++;
    }

    public void close(int fd) {
        if (!openFiles.containsKey(fd)) {
            System.out.println("Invalid file descriptor: " + fd);
            return;
        }
        FileDescriptor fileDescriptor = openFiles.get(fd);
        fileDescriptor.decrementRefCount();
        openFiles.remove(fd);
        fileOffsets.remove(fd);
        System.out.println("File with descriptor " + fd + " closed.");
    }

    public void seek(int fd, int offset) {
        FileDescriptor fdObj = openFiles.get(fd);
        if (fdObj == null) {
            System.out.println("Invalid file descriptor: " + fd);
            return;
        }
        if (offset < 0 || offset > fdObj.getSize()) {
            System.out.println("Invalid offset.");
            return;
        }
        fileOffsets.put(fd, offset);
        System.out.println("Seeked to offset " + offset + " in file descriptor " + fd);
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
    }

//    public void link(String name1, String name2) {
//        FileDescriptor fd = currentDirectory.getFileDescriptor(name1);
//        if (fd == null) {
//            System.out.println("File not found: " + name1);
//            return;
//        }
//        if (currentDirectory.getFileDescriptor(name2) != null) {
//            System.out.println("File already exists: " + name2);
//            return;
//        }
//        currentDirectory.addFile(name2, fd);
//        fd.incrementRefCount();
//        System.out.println("Created hard link from " + name2 + " to " + name1);
//    }
//
//    public void unlink(String name) {
//        FileDescriptor fd = currentDirectory.getFileDescriptor(name);
//        if (fd == null) {
//            System.out.println("File not found: " + name);
//            return;
//        }
//        // for folder deletions we have rmdir()
//        if (fd.getType() == 1) {
//            return;
//        }
//        if (openFiles.containsValue(fd)) {
//            System.out.println("Cannot unlink an open file: " + name);
//            return;
//        }
//
//        fd.decrementRefCount();
//        if (fd.getRefCount() == 0) {
//            currentDirectory.removeFile(name);
//            System.out.println("File removed: " + name);
//        } else {
//            System.out.println("Unlinked file: " + name);
//        }
//    }
public void link(String pathname1, String pathname2) {
    String resolvedPath1 = resolvePath(pathname1);
    String absolutePath1 = getAbsolutePath(resolvedPath1);

    String resolvedPath2 = resolvePath(pathname2);
    String absolutePath2 = getAbsolutePath(resolvedPath2);

    FileDescriptor fd = getFileDescriptorByPath(absolutePath1);
    if (fd == null) {
        System.out.println("File not found: " + pathname1);
        return;
    }

    Directory parentDir2 = getParentDirectory(absolutePath2);
    if (parentDir2 == null) {
        System.out.println("Invalid path: " + pathname2);
        return;
    }

    String fileName2 = getFileNameFromPath(absolutePath2);
    if (parentDir2.getFileDescriptor(fileName2) != null) {
        System.out.println("File already exists: " + pathname2);
        return;
    }

    parentDir2.addFile(fileName2, fd);
    fd.incrementRefCount();
    System.out.println("Created hard link from " + pathname2 + " to " + pathname1);
}

    public void unlink(String pathname) {
        String resolvedPath = resolvePath(pathname);
        String absolutePath = getAbsolutePath(resolvedPath);

        FileDescriptor fd = getFileDescriptorByPath(absolutePath);
        if (fd == null) {
            System.out.println("File not found: " + pathname);
            return;
        }

        if (fd.getType() == 1) {
            System.out.println("Cannot unlink a directory using this command.");
            return;
        }

        if (openFiles.containsValue(fd)) {
            System.out.println("Cannot unlink an open file: " + pathname);
            return;
        }

        fd.decrementRefCount();
        if (fd.getRefCount() == 0) {
            Directory parentDir = getParentDirectory(absolutePath);
            if (parentDir != null) {
                parentDir.removeFile(getFileNameFromPath(absolutePath));
                System.out.println("File removed: " + pathname);
            }
        } else {
            System.out.println("Unlinked file: " + pathname);
        }
    }

    private String getFileNameFromPath(String path) {
        int lastSeparatorIndex = path.lastIndexOf('/');
        return lastSeparatorIndex == -1 ? path : path.substring(lastSeparatorIndex + 1);
    }

    private Directory getParentDirectory(String path) {
        int lastSeparatorIndex = path.lastIndexOf('/');
        if (lastSeparatorIndex == -1) {
            return null;
        }

        String parentPath = path.substring(0, lastSeparatorIndex);
        return directories.get(parentPath);
    }

    private FileDescriptor getFileDescriptorByPath(String path) {
        Directory parentDir = getParentDirectory(path);
        if (parentDir == null) {
            return null;
        }

        String fileName = getFileNameFromPath(path);
        return parentDir.getFileDescriptor(fileName);
    }

//    public void truncate(String name, int size) {
//        FileDescriptor fd = currentDirectory.getFileDescriptor(name);
//        if (fd == null) {
//            System.out.println("File not found: " + name);
//            return;
//        }
//        fd.setSize(size);
//        System.out.println("Truncated file " + name + " to size " + size);
//    }
    public void truncate(String pathname, int size) {
        String resolvedPath = resolvePath(pathname);
        System.out.println(resolvedPath);
        String absolutePath = getAbsolutePath(resolvedPath);
        System.out.println(absolutePath);

        String parentDirectory = getParentByPath(absolutePath);
        System.out.println(parentDirectory);
        Directory dir = directories.get(parentDirectory);
        if (dir == null) {
            System.out.println("Invalid path: " + pathname);
            return;
        }

        FileDescriptor fd = dir.getFileDescriptor(resolvedPath.substring(resolvedPath.lastIndexOf("/") + 1));
        if (fd == null) {
            System.out.println("File not found: " + resolvedPath);
            return;
        }

        if (size < 0) {
            System.out.println("Invalid size: " + size);
            return;
        }

        fd.setSize(size);
        System.out.println("Truncated file " + resolvedPath + " to size " + size);
    }



    private String getFileName(FileDescriptor fd) {
        for(Map.Entry<String, FileDescriptor> entry : currentDirectory.files.entrySet()) {
            if(entry.getValue() == fd) {
                return entry.getKey();
            }
        }
        throw new RuntimeException();
    }

    public static void main(String[] args) {
//        FileSystemDriver fsDriver = new FileSystemDriver(10);
//        fsDriver.mkfs();
//
//        // Створення директорії
//        System.out.println("Створення директорії /home");
//        fsDriver.mkdir("home");
//
//        System.out.println("\nЗміна поточної директорії на /home");
//        fsDriver.cd("/home");
//
//        System.out.println("\nСтворення директорії /home/user");
//        fsDriver.mkdir("user");
//
//        System.out.println("\nСтворення директорії /home/user/systems");
//        fsDriver.mkdir("systems");
//
////        FileDescriptor fid = new FileDescriptor("rrr", 3);
////        fsDriver.symlinks.put("rrr", fid);
////        fid.setSymlinkTarget("/mma/brr");
//
//        // Створення символічного посилання
//        System.out.println("\nСтворення символічного посилання OS на /home/user/systems");
//        System.out.println("\nСтворення символічного посилання USER на /home/user");
//        fsDriver.symlink("OS", "/home/user/systems");
//        fsDriver.symlink("USER", "/home/user");
//
//        System.out.println("Демонстрація правильної роботи інтерпретатора symlink та '.', '..'");
//        System.out.println(fsDriver.resolvePath("xxx/home/rrr/../."));
//
//        // Зміна поточної директорії
//        System.out.println("\nЗміна поточної директорії на /home");
//        fsDriver.cd("/home");
//
//        // Зміна поточної директорії
//        System.out.println("\nЗміна поточної директорії на символічне посилання OS");
//        fsDriver.cd("OS");
//
//        System.out.println("\nЗміна поточної директорії на символічне посилання USER");
//        fsDriver.cd("/home");
//
//        fsDriver.rmdir("OS");
//        fsDriver.rmdir("USER");
//
//        // Видалення символічного посилання перед видаленням директорії
//        System.out.println("\nВидалення символічного посилання OS");
//        fsDriver.unlink("OS");
//
//        // Перевірка поточної директорії
//        fsDriver.ls();
//
//        // Example usage
//        fsDriver.create("file1.txt");
//        System.out.println("Stat: ");
//        fsDriver.stat("file1.txt");
//
//        System.out.println("\nls: ");
//        fsDriver.ls();
//
//        int fd = fsDriver.open("file1.txt");
//        System.out.println("Write Seek Read: ");
//        fsDriver.write(fd, 8193); // Writing 4096 * 2 + 1 bytes
//
//
//        fsDriver.seek(fd, 0);
//        fsDriver.read(fd, 8000); // Reading 8000 bytes
//
//        //fsDriver.stat("file1.txt");
//        fsDriver.close(fd);
//        System.out.println("\nStat: ");
//        fsDriver.stat("file1.txt");
//
//        fsDriver.link("file1.txt", "file2.txt");
//
//        System.out.println("\nStat: ");
//        fsDriver.stat("file2.txt");
//
//        System.out.println("\nls: ");
//        fsDriver.ls();
//
//        fsDriver.unlink("file2.txt");
//
//        System.out.println("\nls: ");
//        fsDriver.ls();
//
//        System.out.println("\nStat: ");
//        fsDriver.stat("file1.txt");
//
//        fsDriver.truncate("file1.txt", 2048); // Truncate to 2048 bytes
//
//        System.out.println("\nStat: ");
//        fsDriver.stat("file1.txt");
        FileSystemDriver fsDriver = new FileSystemDriver(10);
        fsDriver.mkfs();

        // Створення директорій
        System.out.println("Створення директорії /home");
        fsDriver.mkdir("home");

        System.out.println("\nЗміна поточної директорії на /home");
        fsDriver.cd("/home");

        System.out.println("\nСтворення директорії /home/user");
        fsDriver.mkdir("user");

        System.out.println("\nЗміна поточної директорії на /home/user");
        fsDriver.cd("user");

        System.out.println("\nСтворення директорії /home/user/systems");
        fsDriver.mkdir("systems");

        // Створення символічного посилання
        System.out.println("\nСтворення символічного посилання OS на /home/user/systems");
        fsDriver.symlink("OS", "/home/user/systems");
        System.out.println("\nСтворення символічного посилання USER на /home/user");
        fsDriver.symlink("USER", "/home/user");

        System.out.println("Демонстрація роботи інтерпретатора symlink, '.', '..'");
        System.out.println(fsDriver.resolvePath("xxx/home/rrr/../."));

        // Зміна поточної директорії
        System.out.println("\nЗміна поточної директорії на /home");
        fsDriver.cd("/home");

        System.out.println("\nЗміна поточної директорії на символічне посилання OS");
        fsDriver.cd("OS");

        System.out.println("\nЗміна поточної директорії на символічне посилання USER");
        fsDriver.cd("USER");

        fsDriver.rmdir("USER");

        // Видалення символічного посилання перед видаленням директорії
        System.out.println("\nВидалення символічного посилання OS");
        fsDriver.unlink("OS");

        fsDriver.rmdir("OS");

        // Перевірка поточної директорії
        fsDriver.ls();

        // Створення файлу
        fsDriver.create("file1.txt");
        System.out.println("Stat: ");
        fsDriver.stat("file1.txt");

        System.out.println("\nls: ");
        fsDriver.ls();

        int fd = fsDriver.open("file1.txt");
        System.out.println("Write Seek Read: ");
        fsDriver.write(fd, 8193); // Запис 4096 * 2 + 1 байт

        fsDriver.seek(fd, 0);
        fsDriver.read(fd, 8000); // Читання 8000 байт

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

        // Використання truncate
        System.out.println("\nУсічення файлу file1.txt до 2048 байт");
        fsDriver.truncate("file1.txt", 2048);

        System.out.println("\nStat: ");
        fsDriver.stat("file1.txt");

        // Приклад з шляхами
        System.out.println("\nСтворення файлу в директорії user");
        fsDriver.cd("user");
        fsDriver.create("file3.txt");

        System.out.println("\nУсічення файлу /home/user/file3.txt до 1024 байт");
        fsDriver.truncate("/home/user/file3.txt", 1024);

        System.out.println("\nStat: ");
        fsDriver.stat("file3.txt");
    }
}
