package lab3;

import java.util.ArrayList;
import java.util.List;

public class PageTable {
    final List<PageEntry> pageEntries;

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
        public long lastAccessTime = System.currentTimeMillis(); // Час останнього доступу

        @Override
        public String toString() {
            return String.format("Present: %s, Accessed: %s, Modified: %s, PhysicalPageId: %d, LastAccessTime: %d",
                    present, accessed, modified, physicalPageId, lastAccessTime);
        }
    }
}

