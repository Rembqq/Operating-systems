package lab4;

import java.util.ArrayList;
import java.util.List;

public class FileDescriptor {
    static final int BLOCK_SIZE = 4096; // 4 KB blocks
    static final int MAX_FILE_NAME_LENGTH = 255;
    static final int DIRECT_BLOCKS = 12;
    //private String name;
    private int size;
                                            // An array that stores indexes or block identifiers
                                            // that correspond to different parts of the file.
////    private int[] directBlocks;             // This is a map of block numbers, so that the skin
////                                            // element indicates a specific block in which the file data is saved.
////    private List<byte[]> blockIndices;
    private byte[] content;
    // private int currentOffset;
    private int refCount;

    public FileDescriptor(String name) {

        if(name.length() > MAX_FILE_NAME_LENGTH) {
            throw new IllegalArgumentException();
        }

        //this.name = name;
        this.size = 0;
////        this.directBlocks = new int[DIRECT_BLOCKS];
////        this.blockIndices = new ArrayList<>();
        this.content = new byte[DIRECT_BLOCKS * BLOCK_SIZE];
        this.refCount = 1;
    }

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        if (name.length() > MAX_FILE_NAME_LENGTH) {
//            throw new IllegalArgumentException("File name too long!");
//        }
//        this.name = name;
//    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

////    public List<Integer> getBlockIndices() {
////        return blockIndices;
////    }
////
////    public void setBlockIndices(List<Integer> blockIndices) {
////        this.blockIndices = blockIndices;
////    }

    public int getRefCount() {
        return refCount;
    }

    public void setRefCount(int refCount) {
        this.refCount = refCount;
    }
    public void incrementRefCount() {
        refCount++;
    }
    public void decrementRefCount() {
        refCount--;
    }

    public byte[] getContent() {
        return content;
    }

//    public int getCurrentOffset() {
//        return currentOffset;
//    }
//
//    public void setCurrentOffset(int currentOffset) {
//        this.currentOffset = currentOffset;
//    }
}
