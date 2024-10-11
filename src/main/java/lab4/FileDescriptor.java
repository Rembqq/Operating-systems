package lab4;

import java.util.ArrayList;
import java.util.List;

public class FileDescriptor {
    String name;
    int size;
    List<Integer> blockIndices;
    int refCount;

    public FileDescriptor(String name) {
        this.name = name;
        this.size = 0;
        this.blockIndices = new ArrayList<>();
        this.refCount = 1;
    }
}
