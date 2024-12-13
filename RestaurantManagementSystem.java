import java.io.*;
import java.util.*;

public class RestaurantManagementSystem {
    static List<String> menu = new ArrayList<>();
    static Map<String, Integer> prices = new HashMap<>();
    static double dailyIncome = 0.0;
    static List<String> reviews = new ArrayList<>();
    static final String ADMIN_USERNAME = "admin";
    static final String ADMIN_PASSWORD = "password";

    public static void main(String[] args) {
        loadMenu();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Restaurant Management System ---");
            System.out.println("1. Display Menu");
            System.out.println("2. Order Food");
            System.out.println("3. Admin Login");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> displayMenu();
                case 2 -> orderFood(scanner);
                case 3 -> adminLogin(scanner);
                case 4 -> {
                    System.out.println("Thank you for visiting!");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void loadMenu() {
        try (BufferedReader br = new BufferedReader(new FileReader("menu.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String item = parts[0].trim();
                    int price = Integer.parseInt(parts[1].trim());
                    menu.add(item);
                    prices.put(item, price);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading menu: " + e.getMessage());
        }
    }

    private static void displayMenu() {
        System.out.println("\n--- Food Menu ---");
        for (int i = 0; i < menu.size(); i++) {
            System.out.printf("%d. %s - %d BDT%n", i + 1, menu.get(i), prices.get(menu.get(i)));
        }
    }

    private static void orderFood(Scanner scanner) {
        displayMenu();
        double orderTotal = 0.0;

        while (true) {
            System.out.print("\nEnter item number to order (0 to finish): ");
            int itemNumber = scanner.nextInt();

            if (itemNumber == 0) break;

            if (itemNumber > 0 && itemNumber <= menu.size()) {
                System.out.print("Enter quantity: ");
                int quantity = scanner.nextInt();

                String itemName = menu.get(itemNumber - 1);
                int itemPrice = prices.get(itemName);
                orderTotal += itemPrice * quantity;

                System.out.printf("Added %d x %s to the order. Total so far: %.2f BDT%n", quantity, itemName, orderTotal);
            } else {
                System.out.println("Invalid item number. Please try again.");
            }
        }

        System.out.printf("Order complete! Total: %.2f BDT%n", orderTotal);
        dailyIncome += orderTotal;


        System.out.print("Enter the amount paid (BDT): ");
        int amountPaid = scanner.nextInt();
        if (amountPaid < orderTotal) {
            System.out.println("Insufficient amount. Order canceled.");
            dailyIncome -= orderTotal;
            return;
        }

        int change = (int) (amountPaid - orderTotal);
        System.out.printf("Change: %d BDT%n", change);
        calculateChange(change);

  
        scanner.nextLine();
        System.out.print("Rate us (1 to 5 stars): ");
        int rating = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter your comment: ");
        String comment = scanner.nextLine();
        reviews.add("Rating: " + rating + " Stars, Comment: " + comment);
        System.out.println("Thank you for your feedback!");
    }

    private static void calculateChange(int change) {
        int[] notes = {200, 100, 50, 20, 10, 5, 2, 1};
        System.out.println("Change given as:");
        for (int note : notes) {
            if (change >= note) {
                int count = change / note;
                change %= note;
                System.out.printf("%d x %d BDT%n", count, note);
            }
        }
    }

    private static void adminLogin(Scanner scanner) {
        System.out.print("Enter admin username: ");
        String username = scanner.nextLine();
        System.out.print("Enter admin password: ");
        String password = scanner.nextLine();

        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            adminPanel(scanner);
        } else {
            System.out.println("Invalid credentials!");
        }
    }

    private static void adminPanel(Scanner scanner) {
        while (true) {
            System.out.println("\n--- Admin Panel ---");
            System.out.println("1. View Daily Income");
            System.out.println("2. View Reviews");
            System.out.println("3. Update Menu");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> System.out.println("Today's Income: " + dailyIncome + " BDT");
                case 2 -> {
                    System.out.println("--- Customer Reviews ---");
                    for (String review : reviews) {
                        System.out.println(review);
                    }
                }
                case 3 -> updateMenu(scanner);
                case 4 -> {
                    System.out.println("Logged out.");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void updateMenu(Scanner scanner) {
        System.out.println("\n--- Update Menu ---");
        System.out.println("1. Add Item");
        System.out.println("2. Remove Item");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> {
                System.out.print("Enter item name: ");
                String itemName = scanner.nextLine();
                System.out.print("Enter item price (BDT): ");
                int price = scanner.nextInt();
                menu.add(itemName);
                prices.put(itemName, price);
                saveMenu();
                System.out.println("Item added successfully.");
            }
            case 2 -> {
                displayMenu();
                System.out.print("Enter item number to remove: ");
                int itemNumber = scanner.nextInt();
                if (itemNumber > 0 && itemNumber <= menu.size()) {
                    String itemName = menu.remove(itemNumber - 1);
                    prices.remove(itemName);
                    saveMenu();
                    System.out.println("Item removed successfully.");
                } else {
                    System.out.println("Invalid item number.");
                }
            }
            default -> System.out.println("Invalid choice.");
        }
    }

    private static void saveMenu() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("menu.txt"))) {
            for (String item : menu) {
                pw.println(item + "," + prices.get(item));
            }
        } catch (IOException e) {
            System.out.println("Error saving menu: " + e.getMessage());
        }
    }
}
