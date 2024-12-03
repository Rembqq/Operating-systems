package lab3;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class WorkingSet {
    private final int maxSize; // Максимальний розмір робочого набору
    public final Set<Integer> pages; // Сторінки, які входять у робочий набір

    public WorkingSet(int maxSize) {
        this.maxSize = maxSize;
        this.pages = new LinkedHashSet<>();
    }

    public int getSize() {
        return pages.size();
    }

    public void accessPage(int pageId) {
        if (pages.contains(pageId)) {
            // Якщо сторінка вже є в робочому наборі, переміщуємо її на початок
            //System.out.println("Moving page " + pageId + " to start");
            pages.remove(pageId);
            pages.add(pageId);
        } else {
            // Додаємо нову сторінку
            if (pages.size() >= maxSize) {
                // Якщо перевищено максимальний розмір, видаляємо найстарішу сторінку
                int removedPage = pages.iterator().next();
                pages.remove(removedPage);
                System.out.println("Removing page " + removedPage + " from working set");
            }
            pages.add(pageId);
        }
    }

//    public void update() {
//        Iterator<Integer> iterator = pages.iterator();
//
//        while (iterator.hasNext()) {
//            int page = iterator.next();
//
//            // Логіка видалення сторінок, які не використовуються
//            if (shouldRemovePage(page)) { // Замініть на вашу умову
//                System.out.println("Removing page " + page + " from working set");
//                iterator.remove();
//            }
//        }
//    }
//
//    private boolean shouldRemovePage(int page) {
//        // Умови видалення сторінки з робочого набору
//        // Наприклад: якщо сторінка не використовувалася протягом останнього часу
//        return !recentlyAccessedPages.contains(page); // Додайте потрібну умову
//    }

    @Override
    public String toString() {
        return "Working Set: " + pages.toString();
    }
}
