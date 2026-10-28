import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MP5 {
    private final Scanner scanner = new Scanner(System.in);
    private final HashMap<Integer, Book> books = new HashMap<>();
    private final HashSet<String> reservationKeys = new HashSet<>();

    private static class Book {
        private final int id;
        private final String title;
        private int available;
        private int borrowed;
        private final LinkedList<String> waitingPatrons = new LinkedList<>();

        private Book(int id, String title, int available) {
            this.id = id;
            this.title = title;
            this.available = available;
            this.borrowed = 0;
        }

        private void display() {
            System.out.printf("ID: %d | Title: %s | Available: %d | Borrowed: %d%n",
                    id, title, available, borrowed);
        }
    }

    public static void main(String[] args) {
        new MP5().run();
    }

    private void seedBooks() {
        books.put(1, new Book(1, "Java Basics", 1));
        books.put(2, new Book(2, "Algorithms", 0));
        // The activity asks for three books but specifies only IDs 1 and 2.
        // This third book is a placeholder; change it if your instructor specifies one.
        books.put(3, new Book(3, "Data Structures", 1));
    }

    private void run() {
        seedBooks();
        boolean running = true;
        while (running) {
            System.out.println("\n=== LIBRARY BORROWING AND RESERVATIONS ===");
            System.out.println("1 Borrow | 2 Reserve | 3 Return");
            System.out.println("4 Find | 5 Show waiting | 0 Exit");
            try {
                int choice = readInt("Choose an option: ");
                switch (choice) {
                    case 1: borrowBook(); break;
                    case 2: reserveBook(); break;
                    case 3: returnBook(); break;
                    case 4: findBook(); break;
                    case 5: showWaiting(); break;
                    case 0:
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid menu option. Choose 0 to 5.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (NoSuchElementException e) {
                System.out.println("\nInput closed. Goodbye!");
                running = false;
            }
        }
        scanner.close();
    }

    private String readText(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty. Action cancelled.");
        }
        return input;
    }

    private int readInt(String prompt) {
        String input = readText(prompt);
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid whole number. Action cancelled.");
        }
    }

    private Book lookupBook() {
        int id = readInt("Book ID: ");
        Book book = books.get(id);
        if (book == null) {
            System.out.println("Book not found.");
        }
        return book;
    }

    private String reservationKey(Book book, String patron) {
        // A numeric ID followed by ':' makes the book portion unambiguous.
        // Patron names are trimmed and compared case-sensitively.
        return book.id + ":" + patron;
    }

    private void borrowBook() {
        Book book = lookupBook();
        if (book == null) { return; }
        String patron = readText("Patron name: ");
        if (book.available == 0) {
            System.out.println("No copies available. Use Reserve to join the waiting list.");
            return;
        }
        book.available--;
        book.borrowed++;
        System.out.println("Book borrowed by " + patron + ".");
        book.display();
    }

    private void reserveBook() {
        Book book = lookupBook();
        if (book == null) { return; }
        String patron = readText("Patron name: ");
        String key = reservationKey(book, patron);
        if (reservationKeys.contains(key)) {
            System.out.println("Duplicate reservation rejected.");
            return;
        }
        if (book.available > 0) {
            System.out.println("Copies are available. Use Borrow instead.");
            return;
        }
        reservationKeys.add(key);
        book.waitingPatrons.addLast(patron);
        System.out.println(patron + " added to the waiting list.");
        displayWaiting(book);
    }

    private void returnBook() {
        Book book = lookupBook();
        if (book == null) { return; }
        if (book.borrowed == 0) {
            System.out.println("Return rejected: no borrowed copies to return.");
            return;
        }
        if (!book.waitingPatrons.isEmpty()) {
            String nextPatron = book.waitingPatrons.removeFirst();
            reservationKeys.remove(reservationKey(book, nextPatron));
            // One loan ends and another starts immediately, so both counts stay unchanged.
            System.out.println("Returned copy assigned to " + nextPatron + ".");
        } else {
            book.borrowed--;
            book.available++;
            System.out.println("Book returned successfully.");
        }
        book.display();
        displayWaiting(book);
    }

    private void findBook() {
        Book book = lookupBook();
        if (book != null) {
            book.display();
        }
    }

    private void showWaiting() {
        Book book = lookupBook();
        if (book != null) {
            displayWaiting(book);
        }
    }

    private void displayWaiting(Book book) {
        if (book.waitingPatrons.isEmpty()) {
            System.out.println("No waiting patrons.");
            return;
        }
        System.out.println("Waiting patrons (arrival order):");
        int position = 1;
        for (String patron : book.waitingPatrons) {
            System.out.println(position++ + ". " + patron);
        }
    }
}
