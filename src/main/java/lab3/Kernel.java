package lab3;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

public class Kernel {
    private final MMU mmu;
    private static final Queue<Process> processQueue = new ArrayDeque<>();
    private final boolean useWSClock; // Вибір алгоритму заміни сторінок

    public Kernel(int physicalMemorySize, boolean useWSClock, int tau) {
        this.mmu = new MMU(physicalMemorySize, tau);
        this.useWSClock = useWSClock;
    }

    public void createProcess(int processId, int virtualMemorySize, int workingSetSize) {
        Process process = new Process(processId, virtualMemorySize, workingSetSize);
        processQueue.add(process);
    }

    public static Queue<Process> getProcessQueue() {
        return processQueue;
    }

    public void run() throws InterruptedException {

        int totalPageRequests = 0; // Total number of page requests
        int totalPageFaults = 0;   // Total number of page misses

        Random random = new Random();

        while (!processQueue.isEmpty()) {
            Process process = processQueue.poll();
            System.out.println("Running process " + process.getId());
            PageTable pageTable = process.getPageTable();
            WorkingSet workingSet = process.getWorkingSet();

            for (int i = 0; i < 100; i++) { // Симуляція 100 звернень до сторінок
                totalPageRequests++;

                int virtualPageId;

                // 90% звернень до робочого набору
                if (random.nextDouble() < 0.9 && !workingSet.pages.isEmpty()) {
                    virtualPageId = workingSet.pages.stream()
                            .skip(random.nextInt(workingSet.pages.size()))
                            .findFirst()
                            .orElse(random.nextInt(pageTable.getSize()));
                } else {
                    // 10% звернень до випадкових сторінок
                    virtualPageId = random.nextInt(pageTable.getSize());
                }

                PageTable.PageEntry entry = pageTable.getEntry(virtualPageId);

                if (!entry.present) { // Сторінковий промах
                    totalPageFaults++;
                    System.out.println("Page fault at process " + process.getId() + ", page " + virtualPageId);

                    int physicalPageId = mmu.allocatePage(process.getId(), virtualPageId, useWSClock);
                    entry.physicalPageId = physicalPageId;
                    entry.present = true;
                }

                // Оновлення атрибутів сторінки
                entry.accessed = true;
                if (random.nextBoolean()) {
                    entry.modified = true; // Модифікація сторінки
                }

                // Оновлення робочого набору
                workingSet.accessPage(virtualPageId);
            }

            // Output page fault ratio:
            double pageFaultRatio = totalPageRequests > 0
                    ? ((double) totalPageFaults / totalPageRequests * 100)
                    : 0.0;
            System.out.printf("Iteration: Page Faults = %d, Total Requests = %d, Page Fault Ratio = %.6f%n",
                    totalPageFaults, totalPageRequests, pageFaultRatio);

            // Повертаємо процес в чергу для наступного виконання
            processQueue.add(process);

            //Thread.sleep(1000);
        }
    }
}

