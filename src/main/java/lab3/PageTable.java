package lab3;

import java.util.HashMap;

public class PageTable {
    private final HashMap<Integer, PageEntry> table;

    public PageTable() {
        this.table = new HashMap<>();
    }
    public void addPage(int virtualPageId) {
        table.put(virtualPageId, new PageEntry());
    }

    public PageEntry getPageEntry(int virtualPageId) {
        return table.getOrDefault(virtualPageId, null);
    }

    public class PageEntry {
        public boolean isPresent;  // Біт присутності
        public boolean isAccessed; // Біт звернення
        public boolean isModified; // Біт модифікації
        public int physicalPageId; // Номер фізичної сторінки

        public PageEntry() {
            this.isPresent = false;
            this.isAccessed = false;
            this.isModified = false;
            this.physicalPageId = -1;
        }
    }
}






