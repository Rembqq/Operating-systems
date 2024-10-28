package lab4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileDescriptor {
    static final int BLOCK_SIZE = 4096; // 4 KB blocks
    static final int MAX_FILE_NAME_LENGTH = 255;
    //static final int DIRECT_BLOCKS = 12;
    //private String name;
    private int size;
                                            // An array that stores indexes or block identifiers
                                            // that correspond to different parts of the file.
    //private int[] directBlocks;             // This is a map of block numbers, so that the skin
                                            // element indicates a specific block in which the file data is saved.
    private final Map<Integer, byte[]> blockMap;
    private int refCount;

    public FileDescriptor(String name) {

        if(name.length() > MAX_FILE_NAME_LENGTH) {
            throw new IllegalArgumentException();
        }

        this.size = 0;

        //this.directBlocks = new int[DIRECT_BLOCKS];
        this.blockMap = new HashMap<>();
        //this.content = new byte[DIRECT_BLOCKS * BLOCK_SIZE];
        this.refCount = 1;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

//    public List<byte[]> getBlockIndices() {
//        return blockIndices;
//    }

    public Map<Integer, byte[]> getBlockMap() {
        return blockMap;
    }

    public byte[] readBlock(int blockNumber) {
        return blockMap.getOrDefault(blockNumber, new byte[BLOCK_SIZE]);
    }

    public void writeBlock(int blockNumber, byte[] data) {
        if(data.length > BLOCK_SIZE) {
            throw new IllegalArgumentException("Дані перевищують розмір блоку.");
        }
        blockMap.put(blockNumber, data);
    }

//    public void printBlockMap() {
//        for (Map.Entry<Integer, byte[]> entry : blockMap.entrySet()) {
//            Integer blockNumber = entry.getKey();
//            byte[] blockContent = entry.getValue();
//
//            System.out.print("Block number " + blockNumber + ": ");
//
//            // Convert the byte array into a string for easy display.
//            for (byte b : blockContent) {
//                System.out.printf("%02X ", b); // 16-x format
//            }
//
//            System.out.println();
//        }
//    }

    public int getRefCount() {
        return refCount;
    }
    public void incrementRefCount() {
        refCount++;
    }
    public void decrementRefCount() {
        refCount--;
    }
}
