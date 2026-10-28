import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MP4 {
    private final Scanner scanner = new Scanner(System.in);
    private final HashMap<Integer, Product> products = new HashMap<>();

    private static class Product {
        private final int id;
        private final String name;
        private final double price;
        private int stock;

        private Product(int id, String name, double price, int stock) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }

        private int getId() { return id; }
        private double getPrice() { return price; }

        private boolean restock(int quantity) {
            if (quantity <= 0 || quantity > Integer.MAX_VALUE - stock) {
                return false;
            }
            stock += quantity;
            return true;
        }

        private boolean sell(int quantity) {
            if (quantity <= 0 || quantity > stock) {
                return false;
            }
            stock -= quantity;
            return true;
        }

        private void display() {
            System.out.printf(Locale.US,
                    "ID: %d | Name: %s | Price: %.2f | Stock: %d%n",
                    id, name, price, stock);
        }
    }

    public static void main(String[] args) {
        new MP4().run();
    }

    private void run() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== INVENTORY AND PRODUCT LOOKUP ===");
            System.out.println("1 Add | 2 Find | 3 Restock | 4 Sell");
            System.out.println("5 List | 6 Sort by price | 0 Exit");
            try {
                int choice = readInt("Choose an option: ");
                switch (choice) {
                    case 1: addProduct(); break;
                    case 2: findProduct(); break;
                    case 3: restockProduct(); break;
                    case 4: sellProduct(); break;
                    case 5: listProducts(); break;
                    case 6: sortByPrice(); break;
                    case 0:
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid menu option. Choose 0 to 6.");
                }
            } catch (IllegalArgumentException e) {
                // Cancel this action without changing stored data.
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

    private double readPrice() {
        String input = readText("Price: ");
        double price;
        try {
            price = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid price. Action cancelled.");
        }
        if (!Double.isFinite(price) || price < 0) {
            throw new IllegalArgumentException("Price must be a finite, nonnegative number.");
        }
        return price;
    }

    private void addProduct() {
        int id = readInt("Product ID: ");
        if (products.containsKey(id)) {
            System.out.println("Duplicate ID. Product not added.");
            return;
        }
        String name = readText("Product name: ");
        double price = readPrice();
        int stock = readInt("Initial stock: ");
        if (stock < 0) {
            System.out.println("Stock cannot be negative. Product not added.");
            return;
        }
        // Store only after every field passes validation.
        Product product = new Product(id, name, price, stock);
        products.put(id, product);
        System.out.println("Product added.");
        product.display();
    }

    private Product lookupProduct() {
        int id = readInt("Product ID: ");
        Product product = products.get(id);
        if (product == null) {
            System.out.println("Product not found.");
        }
        return product;
    }

    private void findProduct() {
        Product product = lookupProduct();
        if (product != null) {
            product.display();
        }
    }

    private void restockProduct() {
        Product product = lookupProduct();
        if (product == null) { return; }
        int quantity = readInt("Quantity to add: ");
        if (!product.restock(quantity)) {
            System.out.println("Restock rejected: quantity must be positive and stock must fit in an int.");
        } else {
            System.out.println("Restock successful.");
        }
        product.display();
    }

    private void sellProduct() {
        Product product = lookupProduct();
        if (product == null) { return; }
        int quantity = readInt("Quantity to sell: ");
        if (!product.sell(quantity)) {
            System.out.println("Sale rejected: quantity must be positive and cannot exceed stock.");
        } else {
            System.out.println("Sale successful.");
        }
        product.display();
    }

    private void listProducts() {
        if (products.isEmpty()) {
            System.out.println("No products stored.");
            return;
        }
        System.out.println("All products:");
        for (Product product : products.values()) {
            product.display();
        }
    }

    private void sortByPrice() {
        if (products.isEmpty()) {
            System.out.println("No products stored.");
            return;
        }
        // Sorting the snapshot does not change the HashMap.
        ArrayList<Product> sortedProducts = new ArrayList<>(products.values());
        Comparator<Product> byPriceThenId =
                Comparator.comparingDouble(Product::getPrice)
                          .thenComparingInt(Product::getId);
        sortedProducts.sort(byPriceThenId);
        System.out.println("Products sorted by price (ascending), then ID:");
        for (Product product : sortedProducts) {
            product.display();
        }
    }
}
