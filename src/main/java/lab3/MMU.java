package lab3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MMU {
    private final List<PhysicalPage> physicalMemory = new ArrayList<>();
    private final LinkedList<Integer> clockQueue = new LinkedList<>(); // Часове кільце WSClock
    private final Random random = new Random();
    private int clockHand = 0;

    public MMU(int physicalMemorySize) {
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

    private int wsClockReplacement(int processId, int virtualPageId) {
        int scannedPages = 0;

        while (scannedPages < physicalMemory.size()) {
            int pageIndex = clockQueue.get(clockHand);
            PhysicalPage page = physicalMemory.get(pageIndex);

            if (!page.isModified()) { // Кандидат для заміни
                page.mapPage(processId, virtualPageId);
                clockHand = (clockHand + 1) % physicalMemory.size();
                return pageIndex;
            }

            // Позначаємо сторінку для подальшого аналізу
            page.setModified(false);
            clockHand = (clockHand + 1) % physicalMemory.size();
            scannedPages++;
        }

        // Якщо не знайшли немодифікованих сторінок, замінюємо поточну
        int pageIndex = clockQueue.get(clockHand);
        physicalMemory.get(pageIndex).mapPage(processId, virtualPageId);
        clockHand = (clockHand + 1) % physicalMemory.size();
        return pageIndex;
    }
}
