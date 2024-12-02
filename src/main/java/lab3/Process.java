package lab3;
//
//import java.util.Random;
//
//public class Process {
//    private final int id;                 // Унікальний ID процесу
//    private final PageTable pageTable;    // Таблиця сторінок
//    private WorkingSet workingSet;  // Робочий набір
//    private final int totalVirtualPages;  // Загальна кількість віртуальних сторінок
//
//    public Process(int id, int totalVirtualPages) {
//        this.id = id;
//        this.totalVirtualPages = totalVirtualPages;
//        this.pageTable = new PageTable();
//        this.workingSet = new WorkingSet();
//
//        for(int i = 0; i < totalVirtualPages; ++i) {
//            this.pageTable.addPage(i);
//        }
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public PageTable getPageTable() {
//        return pageTable;
//    }
//
//    public int getRandomPageAccess() {
//        Random random = new Random();
//        return random.nextInt(totalVirtualPages);
//    }
//
//}
import java.util.HashSet;
import java.util.Set;

public class Process {
    private final int id; // Унікальний ідентифікатор процесу
    private final PageTable pageTable; // Таблиця сторінок
    private final WorkingSet workingSet; // Робочий набір сторінок

    public Process(int id, int virtualMemorySize, int workingSetSize) {
        this.id = id;
        this.pageTable = new PageTable(virtualMemorySize);
        this.workingSet = new WorkingSet(workingSetSize);
    }

    public int getId() {
        return id;
    }

    public PageTable getPageTable() {
        return pageTable;
    }

    public WorkingSet getWorkingSet() {
        return workingSet;
    }

    @Override
    public String toString() {
        return String.format("Process %d: Virtual Memory Size: %d, Working Set Size: %d",
                id, pageTable.getSize(), workingSet.getSize());
    }
}







