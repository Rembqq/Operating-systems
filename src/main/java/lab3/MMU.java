package lab3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

//public class MMU {
//    private final List<PhysicalPage> physicalMemory = new ArrayList<>();
//    private final LinkedList<Integer> clockQueue = new LinkedList<>(); // Часове кільце WSClock
//    private final Random random = new Random();
//    private int clockHand = 0;
//
//    public MMU(int physicalMemorySize) {
//        for (int i = 0; i < physicalMemorySize; i++) {
//            physicalMemory.add(new PhysicalPage());
//            clockQueue.add(i); // Ініціалізація черги
//        }
//    }
//
//    public int allocatePage(int processId, int virtualPageId, boolean useWSClock) {
//        for (int i = 0; i < physicalMemory.size(); i++) {
//            if (physicalMemory.get(i).isFree()) {
//                physicalMemory.get(i).mapPage(processId, virtualPageId);
//                return i;
//            }
//        }
//
//        return useWSClock ? wsClockReplacement(processId, virtualPageId)
//                : randomReplacement(processId, virtualPageId);
//    }
//
//    private int randomReplacement(int processId, int virtualPageId) {
//        int indexToReplace = random.nextInt(physicalMemory.size());
//        PhysicalPage replacedPage = physicalMemory.get(indexToReplace);
//
//        if (replacedPage.isModified()) {
//            System.out.println("Saving modified page to disk...");
//        }
//
//        replacedPage.mapPage(processId, virtualPageId);
//        return indexToReplace;
//    }
//
//    private int wsClockReplacement(int processId, int virtualPageId) {
//        int scannedPages = 0;
//
//        while (scannedPages < physicalMemory.size()) {
//            int pageIndex = clockQueue.get(clockHand);
//            PhysicalPage page = physicalMemory.get(pageIndex);
//
//            if (!page.isModified()) { // Кандидат для заміни
//                page.mapPage(processId, virtualPageId);
//                clockHand = (clockHand + 1) % physicalMemory.size();
//                return pageIndex;
//            }
//
//            // Позначаємо сторінку для подальшого аналізу
//            page.setModified(false);
//            clockHand = (clockHand + 1) % physicalMemory.size();
//            scannedPages++;
//        }
//
//        // Якщо не знайшли немодифікованих сторінок, замінюємо поточну
//        int pageIndex = clockQueue.get(clockHand);
//        physicalMemory.get(pageIndex).mapPage(processId, virtualPageId);
//        clockHand = (clockHand + 1) % physicalMemory.size();
//        return pageIndex;
//    }
//}
public class MMU {
    private final List<PhysicalPage> physicalMemory;
    private final List<Integer> clockQueue;
    private int clockHand = 0;
    private final Random random = new Random();
    private final int tau; // Параметр часу для WSClock

    public MMU(int physicalMemorySize, int tau) {
        this.physicalMemory = new ArrayList<>();
        this.clockQueue = new ArrayList<>();
        this.tau = tau;

        for (int i = 0; i < physicalMemorySize; i++) {
            physicalMemory.add(new PhysicalPage());
            clockQueue.add(i); // Ініціалізація черги
        }
    }

    public int allocatePage(int processId, int virtualPageId, boolean useWSClock) {
        for (int i = 0; i < physicalMemory.size(); i++) {
            if (physicalMemory.get(i).isFree()) {
                physicalMemory.get(i).mapPage(processId, virtualPageId);
                return i;
            }
        }

        return useWSClock ? wsClockReplacement(processId, virtualPageId)
                : randomReplacement(processId, virtualPageId);
    }



    private int randomReplacement(int processId, int virtualPageId) {
        int indexToReplace = random.nextInt(physicalMemory.size());
        PhysicalPage replacedPage = physicalMemory.get(indexToReplace);

        if (replacedPage.isModified()) {
            System.out.println("Saving modified page to disk...");
        }

        replacedPage.mapPage(processId, virtualPageId);
        return indexToReplace;
    }

    private PageTable.PageEntry getPageEntryForPhysicalPage(int physicalPageId) {
        // Проходимо через всі процеси у черзі
        for (Process process : Kernel.getProcessQueue()) {
            // Отримуємо таблицю сторінок процесу
            PageTable pageTable = process.getPageTable();

            // Проходимо через всі записи в таблиці сторінок
            for (PageTable.PageEntry entry : pageTable.pageEntries) {
                // Якщо знайшли запис із відповідним фізичним ідентифікатором сторінки, повертаємо його
                if (entry.physicalPageId == physicalPageId) {
                    return entry;
                }
            }
        }

        // Якщо відповідного запису не знайдено, повертаємо null
        return null;
    }

    private int wsClockReplacement(int processId, int virtualPageId) {
//        int scannedPages = 0;
//
//        while (scannedPages < physicalMemory.size()) {
//            int pageIndex = clockQueue.get(clockHand);
//            PhysicalPage page = physicalMemory.get(pageIndex);
//
//            // Якщо сторінка не була відвідана (referenced = false) і не змінена, вона є кандидатом для заміни
//            if (!page.isReferenced() && !page.isModified()) {
//                page.mapPage(processId, virtualPageId);
//                clockHand = (clockHand + 1) % physicalMemory.size();
//                return pageIndex;
//            }
//
//            // Якщо сторінка була відвідана, скидаємо її біт "відвідано"
//            if (page.isReferenced()) {
//                page.setReferenced(false);
//            }
//
//            clockHand = (clockHand + 1) % physicalMemory.size();
//            scannedPages++;
//        }
//
//        // Якщо не знайшли сторінку, що не була відвідана, замінюємо поточну сторінку
//        int pageIndex = clockQueue.get(clockHand);
//        physicalMemory.get(pageIndex).mapPage(processId, virtualPageId);
//        clockHand = (clockHand + 1) % physicalMemory.size();
//        return pageIndex;
        int scannedPages = 0;

        while (scannedPages < physicalMemory.size()) {
            int pageIndex = clockQueue.get(clockHand);
            PhysicalPage page = physicalMemory.get(pageIndex);
            PageTable.PageEntry entry = getPageEntryForPhysicalPage(pageIndex);

            long currentTime = System.currentTimeMillis();

            // Перевірка сторінки на умови заміни
            if (entry != null && (!entry.accessed && currentTime - entry.lastAccessTime > tau)) {
                // Якщо сторінка немодифікована, її можна відразу замінити
                if (!entry.modified) {
                    entry.present = false;
                    page.mapPage(processId, virtualPageId);
                    entry.physicalPageId = pageIndex;
                    entry.present = true;
                    clockHand = (clockHand + 1) % physicalMemory.size();
                    return pageIndex;
                }

                // Якщо сторінка модифікована, зберігаємо її на диск
                System.out.println("Saving modified page to disk...");
                entry.modified = false;
            }

            // Скидаємо біт "accessed" для подальшого аналізу
            if (entry != null && entry.accessed) {
                entry.accessed = false;
            }

            clockHand = (clockHand + 1) % physicalMemory.size();
            scannedPages++;
        }

        // Якщо не знайшли підходящу сторінку, замінюємо поточну
        int pageIndex = clockQueue.get(clockHand);
        PhysicalPage page = physicalMemory.get(pageIndex);
        PageTable.PageEntry entry = getPageEntryForPhysicalPage(pageIndex);

        if (entry != null) {
            entry.present = false;
        }
        page.mapPage(processId, virtualPageId);
        clockHand = (clockHand + 1) % physicalMemory.size();
        return pageIndex;
    }
}