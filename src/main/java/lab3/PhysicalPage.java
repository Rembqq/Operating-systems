package lab3;

public class PhysicalPage {
    private boolean isOccupied;  // Чи зайнята сторінка
    private int processId;       // ID процесу, який використовує сторінку
    private int virtualPageId;   // Віртуальна сторінка, яка відображена на цій фізичній сторінці

    public PhysicalPage() {
        this.isOccupied = false;
        this.processId = -1;
        this.virtualPageId = -1;
    }

    public boolean isOccupied() { return isOccupied; }

    public void occupy(int processId, int virtualPageId) {
        this.isOccupied = true;
        this.processId = processId;
        this.virtualPageId = virtualPageId;
    }
    public void release() {
        this.isOccupied = false;
        this.processId = -1;
        this.virtualPageId = -1;
    }
}


















