package lab5;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Directory {
    private final String dirAbsolutePath;
    private String name = "/";

    //public FileDescriptor dirDescriptor;
    public Map<String, FileDescriptor> files;
    private Directory parent;


    public Directory(String dirAbsolutePath, Directory parent) {
        this.dirAbsolutePath = dirAbsolutePath;
        this.parent = parent;
        this.files = new HashMap<String, FileDescriptor>() {{put(".", new FileDescriptor("dot", 3));
            put("..", new FileDescriptor("dot-dot", 3));}};
        this.name = getDirNameByAbsolutePath(dirAbsolutePath);

    }

//    public String resolvePath() {
//        String ;
//        return dirMa;
//    }
    private String getDirNameByAbsolutePath(String path) {
        if (path == null || path.isEmpty() || path.equals("/")) {
            return null;
        }
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }
    public void addFile(String name, FileDescriptor file) {
        files.put(name, file);
    }

    public FileDescriptor getFileDescriptor(String name) {
        return files.get(name);
    }

    public String getName() {
        return name;
    }

    public void removeFile(String name) {
        files.remove(name);
    }

    public void createFile(String name) {
        if(!files.containsKey(name)){
            files.put(name, new FileDescriptor(name,2));
        }
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public boolean isEmpty() {
        return files.size() == 2; // Only '.' and '..'
    }
    public Directory getParent() {
        return parent;
    }

    public String getDirAbsolutePath() {
        return dirAbsolutePath;
    }

    public Map<String, FileDescriptor> getFiles() {
        return files;
    }

//    public void createSubdirectory(String name) {
//        if (!subdirectories.containsKey(name)) {
//            subdirectories.put(name, new Directory()); // Створюється новий підкаталог
//        }
//    }
//
//    public Directory getSubdirectory(String name) {
//        return subdirectories.get(name);
//    }

    //public boolean isSymlink(String name) {
//        return symlinks.containsKey(name);
//    }

//    public void removeLink(String name) {
//        if(files.containsKey(name)) {
//            FileDescriptor fd = files.get(name);
//            if(fd.getRefCount() == 1) {
//                files.remove(name);
//            } else {
//                fd.decrementRefCount();
//            }
//        }
//    }

//    public FileDescriptor getFileDescriptor(String name) {
//        // Якщо це символічне посилання, то отримуємо відповідний файл
//        if(symlinks.containsKey(name)) {
//            return getFileDescriptor(symlinks.get(name)); // отримаємо реальний файл
//        }
//        return files.get(name);
//    }
}
