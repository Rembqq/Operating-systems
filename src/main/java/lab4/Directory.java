package lab4;

import java.util.HashMap;
import java.util.Map;

public class Directory {
    Map<String, FileDescriptor> files;

    public Directory(Map<String, FileDescriptor> files) {
        this.files = new HashMap<>();
    }

    public void createFile(String name) {
        if(!files.containsKey(name)){
            files.put(name, new FileDescriptor(name));
        }
    }

    public void deleteFile(String name) {
        if(files.containsKey(name)) {
            FileDescriptor fd = files.get(name);
            if(fd.refCount == 1) {
                files.remove(name);
            } else {
                fd.refCount--;
            }
        }
    }

    public FileDescriptor getFileDescriptor(String name) {
        return files.get(name);
    }
}
