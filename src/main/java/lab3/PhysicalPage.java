package lab3;

class PhysicalPage {
    private boolean free = true;
    private boolean modified = false;
    private boolean referenced = false;
    private int processId;
    private int virtualPageId;

    public boolean isFree() {
        return free;
    }

    public void mapPage(int processId, int virtualPageId) {
        this.processId = processId;
        this.virtualPageId = virtualPageId;
        this.free = false;
        this.referenced = true;  // Сторінка позначена як відвідана, коли вона відображена
    }

    public boolean isModified() {
        return modified;
    }

    public boolean isReferenced() {
        return referenced;
    }

    public void setReferenced(boolean referenced) {
        this.referenced = referenced;
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

//
//public class PhysicalPage {
//    private boolean free = true; // Чи вільна сторінка
//    private int processId = -1; // Ідентифікатор процесу, який використовує сторінку
//    private int virtualPageId = -1; // Віртуальна сторінка, яка відповідає цій фізичній сторінці
//    private boolean modified = false; // Чи була сторінка модифікована
//
//    public boolean isFree() {
//        return free;
//    }
//
//    public void mapPage(int processId, int virtualPageId) {
//        this.free = false;
//        this.processId = processId;
//        this.virtualPageId = virtualPageId;
//    }
//
//    public void freePage() {
//        this.free = true;
//        this.processId = -1;
//        this.virtualPageId = -1;
//    }
//
//    public boolean isModified() {
//        return modified;
//    }
//
//    public void setModified(boolean modified) {
//        this.modified = modified;
//    }
//
//    @Override
//    public String toString() {
//        if (free) {
//            return "Free";
//        }
//        return String.format("Used by Process %d, Virtual Page %d", processId, virtualPageId);
//    }
//}

