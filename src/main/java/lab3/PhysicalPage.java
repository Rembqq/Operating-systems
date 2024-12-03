package lab3;

public class PhysicalPage {
    private boolean free = true; // Чи вільна сторінка
    private int processId = -1; // Ідентифікатор процесу, який використовує сторінку
    private int virtualPageId = -1; // Віртуальна сторінка, яка відповідає цій фізичній сторінці
    private boolean modified = false; // Чи була сторінка модифікована

    public boolean isFree() {
        return free;
    }

    public void mapPage(int processId, int virtualPageId) {
        this.free = false;
        this.processId = processId;
        this.virtualPageId = virtualPageId;
    }

    public void freePage() {
        this.free = true;
        this.processId = -1;
        this.virtualPageId = -1;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    @Override
    public String toString() {
        if (free) {
            return "Free";
        }
        return String.format("Used by Process %d, Virtual Page %d", processId, virtualPageId);
    }
}

















