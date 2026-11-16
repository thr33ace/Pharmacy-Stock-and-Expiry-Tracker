import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;



public class InventoryManager {
    private List<Batch> stock;

    public InventoryManager() {
        this.stock = new ArrayList<>();
    }

    public void addBatch(String name, String batchNum, int qty, LocalDate expiry) {
        Batch newBatch = new Batch(name, batchNum, qty, expiry);
        stock.add(newBatch);
        System.out.println("Batch added successfully!");
    }

    public void displayStock() {
        if (stock.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }
        System.out.println("\n--- CURRENT STOCK ---");
        for (Batch b : stock) {
            System.out.println(b.toString());
        }
        System.out.println("---------------------\n");
    }

    public void generateExpiryAlerts() {
        if (stock.isEmpty()) {
            System.out.println("No stock in the system.");
            return;
        }

        List<Batch> expired = new ArrayList<>();
        List<Batch> critical = new ArrayList<>();
        List<Batch> warning = new ArrayList<>();
        List<Batch> safe = new ArrayList<>();

        for (Batch batch : stock) {
            if (batch.isExpired()) {
                expired.add(batch);
            } else if (batch.getDaysUntilExpiry() < 30) {
                critical.add(batch);
            } else if (batch.getDaysUntilExpiry() < 90) {
                warning.add(batch);
            } else {
                safe.add(batch);
            }
        }

        System.out.println("\n========== EXPIRY ALERT DASHBOARD ==========\n");

        if (!expired.isEmpty()) {
            System.out.println(" EXPIRED (DO NOT SELL):");
            for (Batch b : expired) {
                System.out.println("   " + b.toString());
            }
            System.out.println();
        }

        if (!critical.isEmpty()) {
            System.out.println("CRITICAL (Expiring in < 30 days):");
            for (Batch b : critical) {
                System.out.println("   " + b.toString());
            }
            System.out.println();
        }

        if (!warning.isEmpty()) {
            System.out.println(" WARNING (Expiring in 30-90 days):");
            for (Batch b : warning) {
                System.out.println("   " + b.toString());
            }
            System.out.println();
        }

        if (!safe.isEmpty()) {
            System.out.println("SAFE (More than 90 days until expiry):");
            for (Batch b : safe) {
                System.out.println("   " + b.toString());
            }
            System.out.println();
        }

        System.out.println("=============================================\n");
    }

    public void dispenseMedicine(String medicineName, int quantityRequested) {
        if (quantityRequested <= 0) {
            System.out.println("Error: Quantity must be greater than zero.");
            return;
        }

        List<Batch> matchingBatches = new ArrayList<>();
        for (Batch batch : stock) {
            if (batch.getMedicineName().equalsIgnoreCase(medicineName)) {
                matchingBatches.add(batch);
            }
        }

        if (matchingBatches.isEmpty()) {
            System.out.println("Error: Medicine '" + medicineName + "' not found in inventory.");
            return;
        }

        matchingBatches.sort((b1, b2) -> b1.getExpiryDate().compareTo(b2.getExpiryDate()));

        int remainingToDispense = quantityRequested;
        int totalDispensed = 0;

        System.out.println("\n--- DISPENSING: " + medicineName + " (Requested: " + quantityRequested + ") ---");

        for (Batch batch : matchingBatches) {
            if (remainingToDispense <= 0) {
                break;
            }

            if (batch.isExpired()) {
                System.out.println("️ Skipped expired batch: " + batch.getBatchNumber() + " (Expired: " + batch.getExpiryDate() + ")");
                continue;
            }

            int available = batch.getQuantity();
            if (available <= remainingToDispense) {
                batch.reduceQuantity(available);
                remainingToDispense -= available;
                totalDispensed += available;
                System.out.println("  -> Took " + available + " from batch " + batch.getBatchNumber() + " (Expires: " + batch.getExpiryDate() + ")");
            } else {
                int amountToTake = remainingToDispense;
                batch.reduceQuantity(amountToTake);
                totalDispensed += amountToTake;
                remainingToDispense = 0;
                System.out.println("  -> Took " + amountToTake + " from batch " + batch.getBatchNumber() + " (Expires: " + batch.getExpiryDate() + ")");
            }
        }

        if (remainingToDispense > 0) {
            System.out.println("\nWARNING: Only dispensed " + totalDispensed + " out of " + quantityRequested + " requested.");
            System.out.println("Reason: Not enough valid (non-expired) stock available.");
        } else {
            System.out.println("\n Successfully dispensed " + totalDispensed + " units of " + medicineName + ".");
        }
        System.out.println("---------------------------------------------------\n");
    }

    public void saveToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("medicineName,batchNumber,quantity,expiryDate");

            for (Batch batch : stock) {
                writer.println(batch.getMedicineName() + "," +
                        batch.getBatchNumber() + "," +
                        batch.getQuantity() + "," +
                        batch.getExpiryDate());
            }
            System.out.println("✓ Stock saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    public void loadFromFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("No existing stock file found. Starting with empty inventory.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String name = parts[0];
                    String batchNum = parts[1];
                    int qty = Integer.parseInt(parts[2]);
                    LocalDate expiry = LocalDate.parse(parts[3]);

                    Batch batch = new Batch(name, batchNum, qty, expiry);
                    stock.add(batch);
                }
            }
            System.out.println("✓ Loaded " + stock.size() + " batches from " + filename);
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        } catch (NumberFormatException | DateTimeParseException e) {
            System.out.println("Error parsing file data: " + e.getMessage());
        }

    }

    public List<Batch> getStock() {
        return stock;
    }
    public String dispenseMedicineGUI(String medicineName, int quantityRequested) {
        StringBuilder result = new StringBuilder();

        if (quantityRequested <= 0) {
            return "Error: Quantity must be greater than zero.";
        }

        List<Batch> matchingBatches = new ArrayList<>();
        for (Batch batch : stock) {
            if (batch.getMedicineName().equalsIgnoreCase(medicineName)) {
                matchingBatches.add(batch);
            }
        }

        if (matchingBatches.isEmpty()) {
            return "Error: Medicine '" + medicineName + "' not found in inventory.";
        }

        matchingBatches.sort((b1, b2) -> b1.getExpiryDate().compareTo(b2.getExpiryDate()));

        int remainingToDispense = quantityRequested;
        int totalDispensed = 0;

        result.append("--- DISPENSING: ").append(medicineName).append(" (Requested: ").append(quantityRequested).append(") ---\n\n");

        for (Batch batch : matchingBatches) {
            if (remainingToDispense <= 0) break;

            if (batch.isExpired()) {
                result.append("⚠️ Skipped expired batch: ").append(batch.getBatchNumber()).append("\n");
                continue;
            }

            int available = batch.getQuantity();
            if (available <= remainingToDispense) {
                batch.reduceQuantity(available);
                remainingToDispense -= available;
                totalDispensed += available;
                result.append("  -> Took ").append(available).append(" from batch ").append(batch.getBatchNumber()).append("\n");
            } else {
                int amountToTake = remainingToDispense;
                batch.reduceQuantity(amountToTake);
                totalDispensed += amountToTake;
                remainingToDispense = 0;
                result.append("  -> Took ").append(amountToTake).append(" from batch ").append(batch.getBatchNumber()).append("\n");
            }
        }

        if (remainingToDispense > 0) {
            result.append("\nWARNING: Only dispensed ").append(totalDispensed).append(" out of ").append(quantityRequested).append(" requested.\n");
            result.append("Reason: Not enough valid (non-expired) stock available.");
        } else {
            result.append("\nSuccessfully dispensed ").append(totalDispensed).append(" units of ").append(medicineName).append(".");
        }

        // Automatically save after dispensing
        saveToFile("stock.csv");

        return result.toString();
    }
    public int getLowStockCount() {
        int count = 0;
        for (Batch batch : stock) {
            if (!batch.isExpired() && batch.getQuantity() < 10) {
                count++;
            }
        }
        return count;
    }
<<<<<<< HEAD
    public void recordTransaction(String medName, String batchNum, int qty) {
        transactions.add(new Transaction(medName, batchNum, qty));
    }
    private List<Transaction> transactions = new ArrayList<>();
    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void exportTransactions(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("TransactionID,MedicineName,BatchNumber,Quantity,Timestamp");
            for (Transaction t : transactions) {
                writer.println(t.getId() + "," + t.getMedicineName() + "," +
                        t.getBatchNumber() + "," + t.getQuantity() + "," +
                        t.getTimestamp().toString());
            }
            System.out.println("Transactions exported to " + filename);
        } catch (IOException e) {
            System.out.println("Error exporting transactions: " + e.getMessage());
        }
    }
=======
>>>>>>> c77e166ce7a680e021415ef49ea1d311ab6123d8
}
