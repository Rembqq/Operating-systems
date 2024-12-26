package lab3;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int physicalMemorySize = 5; // Розмір фізичної пам'яті
        boolean useWSClock = true;  // Використовувати WSClock
        int tau = 10;               // Параметр часу для WSClock

        Kernel kernel = new Kernel(physicalMemorySize, useWSClock, tau);

        kernel.createProcess(1, 100, 10); // Процес 1: 10 віртуальних сторінок, розмір робочого набору 3
        //kernel.createProcess(2, 15, 4); // Процес 2: 15 віртуальних сторінок, розмір робочого набору 4

        kernel.run();
    }
}
