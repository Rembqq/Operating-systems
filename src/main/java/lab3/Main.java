package lab3;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Kernel kernel = new Kernel(5, true); // Використовуємо WSClock (true) або Random (false)

        kernel.createProcess(1, 100, 10); // Процес 1: 10 віртуальних сторінок, розмір робочого набору 3
        //kernel.createProcess(2, 15, 4); // Процес 2: 15 віртуальних сторінок, розмір робочого набору 4

        kernel.run();
    }
}
