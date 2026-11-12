import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {

        PharmacyTheme.apply();

        Scanner scanner = new Scanner(System.in);
        InventoryManager inventory = new InventoryManager();
        inventory.loadFromFile("stock.csv");
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(inventory);
            frame.setVisible(true);
        });
        boolean running = true;
        System.out.println("=== PHARMACY EXPIRY ALERT SYSTEM ===");

        while (running) {
            System.out.println("\n1. Add Medicine Batch");
            System.out.println("2. View Current Stock");
            System.out.println("3. View Expiry Alerts");
            System.out.println("4. Dispense Medicine");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    try {
                        System.out.print("Medicine Name: ");
                        String name = scanner.nextLine();

                        System.out.print("Batch Number: ");
                        String batchNum = scanner.nextLine();

                        System.out.print("Quantity: ");
                        int qty = Integer.parseInt(scanner.nextLine());

                        System.out.print("Expiry Date (YYYY-MM-DD): ");
                        LocalDate expiry = LocalDate.parse(scanner.nextLine());

                        inventory.addBatch(name, batchNum, qty, expiry);
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Please enter a valid number for quantity.");
                    } catch (DateTimeParseException e) {
                        System.out.println("Error: Please enter a valid date in YYYY-MM-DD format.");
                    }
                    break;

                case "2":
                    inventory.displayStock();
                    break;

                case "3":
                    inventory.generateExpiryAlerts();
                    break;

                case "4":
                    System.out.print("Enter Medicine Name to dispense: ");
                    String medName = scanner.nextLine();

                    try {
                        System.out.print("Enter Quantity to dispense: ");
                        int qty = Integer.parseInt(scanner.nextLine());
                        inventory.dispenseMedicine(medName, qty);
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Please enter a valid number for quantity.");
                    }
                    break;

                case "5":
                    inventory.saveToFile("stock.csv");
                    System.out.println("Exiting system. Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }
}