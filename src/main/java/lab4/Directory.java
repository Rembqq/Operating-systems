package lab4;

import java.util.HashMap;
import java.util.Map;

public class Directory {
    Map<String, FileDescriptor> files;

    public Directory() {
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
            if(fd.getRefCount() == 1) {
                files.remove(name);
            } else {
                fd.setRefCount(fd.getRefCount() - 1);
            }
        }
    }

    public FileDescriptor getFileDescriptor(String name) {
        return files.get(name);
    }
}
