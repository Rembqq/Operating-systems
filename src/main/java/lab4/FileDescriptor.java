package lab4;

import java.util.List;

public class FileDescriptor {
    String name;
    int size;
    List<Integer> blockIndices;
    int refCount;

    public FileDescriptor(String name, int size, List<Integer> blockIndices, int refCount) {
        this.name = name;
        this.size = size;
        this.blockIndices = blockIndices;
        this.refCount = refCount;
    }
}
