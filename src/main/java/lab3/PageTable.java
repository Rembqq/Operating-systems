package lab3;
//
//import java.util.HashMap;
//
//public class PageTable {
//    private final HashMap<Integer, PageEntry> table;
//
//    public PageTable() {
//        this.table = new HashMap<>();
//    }
//    public void addPage(int virtualPageId) {
//        table.put(virtualPageId, new PageEntry());
//    }
//
//    public PageEntry getPageEntry(int virtualPageId) {
//        return table.getOrDefault(virtualPageId, null);
//    }
//
//    public class PageEntry {
//        public boolean isPresent;  // Біт присутності
//        public boolean isAccessed; // Біт звернення
//        public boolean isModified; // Біт модифікації
//        public int physicalPageId; // Номер фізичної сторінки
//
//        public PageEntry() {
//            this.isPresent = false;
//            this.isAccessed = false;
//            this.isModified = false;
//            this.physicalPageId = -1;
//        }
//    }
//}
import java.util.ArrayList;
import java.util.List;

public class PageTable {
    private final List<PageEntry> pageEntries;

    public PageTable(int size) {
        this.pageEntries = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            pageEntries.add(new PageEntry());
        }
    }

    public PageEntry getEntry(int pageId) {
        return pageEntries.get(pageId);
    }

    public int getSize() {
        return pageEntries.size();
    }

    public static class PageEntry {
        public boolean present = false; // Чи відображена сторінка в фізичній пам'яті
        public boolean accessed = false; // Чи була сторінка використана
        public boolean modified = false; // Чи була сторінка модифікована
        public int physicalPageId = -1; // Індекс фізичної сторінки

        @Override
        public String toString() {
            return String.format("Present: %s, Accessed: %s, Modified: %s, PhysicalPageId: %d",
                    present, accessed, modified, physicalPageId);
        }
    }
}






