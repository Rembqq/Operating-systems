package lab3;

import java.util.Random;

public class Process {
    private final int id;                 // Унікальний ID процесу
    private final PageTable pageTable;    // Таблиця сторінок
    private WorkingSet workingSet;  // Робочий набір
    private final int totalVirtualPages;  // Загальна кількість віртуальних сторінок

    public Process(int id, int totalVirtualPages) {
        this.id = id;
        this.totalVirtualPages = totalVirtualPages;
        this.pageTable = new PageTable();
        this.workingSet = new WorkingSet();

        for(int i = 0; i < totalVirtualPages; ++i) {
            this.pageTable.addPage(i);
        }
    }

    public int getId() {
        return id;
    }

    public PageTable getPageTable() {
        return pageTable;
    }

    public int getRandomPageAccess() {
        Random random = new Random();
        return random.nextInt(totalVirtualPages);
    }

}







