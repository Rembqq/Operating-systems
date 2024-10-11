package lab4;

import java.util.ArrayList;
import java.util.List;

public class FileDescriptor {
    private String name;
    private int size;
    private List<Integer> blockIndices;
    private int refCount;

    public FileDescriptor(String name) {
        this.name = name;
        this.size = 0;
        this.blockIndices = new ArrayList<>();
        this.refCount = 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Integer> getBlockIndices() {
        return blockIndices;
    }

    public void setBlockIndices(List<Integer> blockIndices) {
        this.blockIndices = blockIndices;
    }

    public int getRefCount() {
        return refCount;
    }

    public void setRefCount(int refCount) {
        this.refCount = refCount;
    }
}
