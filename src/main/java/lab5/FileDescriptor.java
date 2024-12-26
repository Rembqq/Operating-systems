package lab5;

import java.util.HashMap;
import java.util.Map;

public class FileDescriptor {

    public static final int DIRECTORY = 1;
    public static final int FILE = 2;
    public static final int SYMLINK = 3;
    private final int type; // Тип: директорія, файл або символічне посилання
    private String symlinkTarget; // Для символічних посилань
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

    public FileDescriptor(String name, int type) {

        if(name.length() > MAX_FILE_NAME_LENGTH) {
            throw new IllegalArgumentException();
        }

        this.size = 0;
        this.type = type;
        //this.directBlocks = new int[DIRECT_BLOCKS];
        this.blockMap = new HashMap<>();
        //this.content = new byte[DIRECT_BLOCKS * BLOCK_SIZE];
        this.refCount = 1;
        if (type == SYMLINK) {
            this.symlinkTarget = "";
        } else if (type == DIRECTORY) {
            incrementRefCount();
        }
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

    // Методи для роботи з типом файлу
    public int getType() {
        return type;
    }

    public void setSymlinkTarget(String target) {
        if (type != SYMLINK) {
            throw new IllegalArgumentException("This is not a symlink");
        }
        if (target.length() > BLOCK_SIZE) {
            throw new IllegalArgumentException("Target path is too long");
        }
        this.symlinkTarget = target;
    }

    public String getSymlinkTarget() {
        if (type != SYMLINK) {
            throw new IllegalArgumentException("This is not a symlink");
        }
        return symlinkTarget;
    }

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
