package lab3;

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

