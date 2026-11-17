import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InventoryManager {
    private List<Batch> stock;
    private List<Transaction> transactions;

    public InventoryManager() {
        this.stock = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    // --- BATCH METHODS ---
    public void addBatch(String name, String batchNum, int qty, LocalDate expiry) {
        stock.add(new Batch(name, batchNum, qty, expiry));
    }

    public void removeBatch(String batchNumber) {
        stock.removeIf(batch -> batch.getBatchNumber().equals(batchNumber));
    }

    public List<Batch> getStock() {
        return stock;
    }

    public int getLowStockCount() {
        int count = 0;
        for (Batch batch : stock) {
            if (!batch.isExpired() && batch.getQuantity() < 10) count++;
        }
        return count;
    }

    // --- TRANSACTION METHODS ---
    public void recordTransaction(String medName, String batchNum, int qty) {
        transactions.add(new Transaction(medName, batchNum, qty));
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    // --- SAVE & LOAD (FIXES MULTIPLICATION) ---
    public void loadAllData() {
        loadFromFile("stock.csv");
        loadTransactions("transactions.csv");
    }

    public void saveAllData() {
        saveToFile("stock.csv");
        saveTransactions("transactions.csv");
    }

    public void saveToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("medicineName,batchNumber,quantity,expiryDate");
            for (Batch batch : stock) {
                writer.println(batch.getMedicineName() + "," + batch.getBatchNumber() + "," + batch.getQuantity() + "," + batch.getExpiryDate());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void loadFromFile(String filename) {
        stock.clear(); // <--- THIS STOPS MULTIPLICATION
        File file = new File(filename);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    stock.add(new Batch(parts[0], parts[1], Integer.parseInt(parts[2]), LocalDate.parse(parts[3])));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void saveTransactions(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("TransactionID,MedicineName,BatchNumber,Quantity,Timestamp");
            for (Transaction t : transactions) {
                writer.println(t.getId() + "," + t.getMedicineName() + "," + t.getBatchNumber() + "," + t.getQuantity() + "," + t.getTimestamp().toString());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void loadTransactions(String filename) {
        transactions.clear(); // <--- THIS STOPS HISTORY MULTIPLICATION
        File file = new File(filename);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    recordTransaction(parts[1], parts[2], Integer.parseInt(parts[3]));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- DISPENSING LOGIC ---
    public String dispenseMedicineGUI(String medicineName, int quantityRequested) {
        StringBuilder result = new StringBuilder();
        if (quantityRequested <= 0) return "Error: Quantity must be greater than zero.";

        List<Batch> matchingBatches = new ArrayList<>();
        for (Batch batch : stock) {
            if (batch.getMedicineName().equalsIgnoreCase(medicineName)) matchingBatches.add(batch);
        }
        if (matchingBatches.isEmpty()) return "Error: Medicine '" + medicineName + "' not found.";

        matchingBatches.sort((b1, b2) -> b1.getExpiryDate().compareTo(b2.getExpiryDate()));
        int remaining = quantityRequested;
        int totalDispensed = 0;

        for (Batch batch : matchingBatches) {
            if (remaining <= 0) break;
            if (batch.isExpired()) continue;

            int available = batch.getQuantity();
            if (available <= remaining) {
                batch.reduceQuantity(available);
                remaining -= available;
                totalDispensed += available;
            } else {
                batch.reduceQuantity(remaining);
                totalDispensed += remaining;
                remaining = 0;
            }
        }

        if (remaining > 0) return "Warning: Only dispensed " + totalDispensed + " out of " + quantityRequested + ".";
        return "Successfully dispensed " + totalDispensed + " units of " + medicineName + ".";
    }
}