package lab3;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

//import java.util.ArrayList;
//import java.util.List;
//
//public class Kernel {
//    private List<Process> processes;
//    private MMU mmu;
//    private int maxProcesses;
//
//    public Kernel(int maxProcesses, int totalPhysicalPages) {
//        this.processes = new ArrayList<>();
//        this.mmu = new MMU(totalPhysicalPages);
//        this.maxProcesses = maxProcesses;
//    }
//
//    public void createProcess(int totalVirtualPages) {
//        if(processes.size() < maxProcesses) {
//            Process process = new Process(processes.size(), totalVirtualPages);
//            processes.add(process);
//        }
//    }
//
//    public void executeProcess() {
//        for(Process process: processes) {
//            mmu.handleProcess(process);
//        }
//    }
//}
public class Kernel {
    private final MMU mmu;
    private final Queue<Process> processQueue = new ArrayDeque<>();
    private final boolean useWSClock; // Вибір алгоритму заміни сторінок

    public Kernel(int physicalMemorySize, boolean useWSClock) {
        this.mmu = new MMU(physicalMemorySize);
        this.useWSClock = useWSClock;
    }

    public void createProcess(int processId, int virtualMemorySize, int workingSetSize) {
        Process process = new Process(processId, virtualMemorySize, workingSetSize);
        processQueue.add(process);
    }

    public void run() throws InterruptedException {

        int totalPageRequests = 0; // Общее количество запросов страниц
        int totalPageFaults = 0;   // Общее количество страничных промахов

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
            System.out.println("Page fault ratio: " + ((double)(totalPageFaults / totalPageRequests) * 100));

            // Повертаємо процес в чергу для наступного виконання
            processQueue.add(process);
            Thread.sleep(2000);
        }
    }
}

