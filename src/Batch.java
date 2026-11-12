import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Batch {
    private String medicineName;
    private String batchNumber;
    private int quantity;
    private LocalDate expiryDate;

    // Constructor
    public Batch(String medicineName, String batchNumber, int quantity, LocalDate expiryDate) {
        this.medicineName = medicineName;
        this.batchNumber = batchNumber;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }

    // Getters
    public String getMedicineName() { return medicineName; }
    public String getBatchNumber() { return batchNumber; }
    public int getQuantity() { return quantity; }
    public LocalDate getExpiryDate() { return expiryDate; }

    // Logic Methods
    public boolean isExpired() {
        // Returns true if today is after the expiry date
        return LocalDate.now().isAfter(expiryDate);
    }

    public long getDaysUntilExpiry() {
        // Calculates days between today and expiry date
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    public void reduceQuantity(int amount) {
        if (amount > this.quantity) {
            throw new IllegalArgumentException("Not enough stock in this batch!");
        }
        this.quantity -= amount;
    }

    @Override
    public String toString() {
        String status = isExpired() ? "[EXPIRED]" : "[" + getDaysUntilExpiry() + " days left]";
        return String.format("%-15s | Batch: %-8s | Qty: %-4d | Expires: %s %s",
                medicineName, batchNumber, quantity, expiryDate, status);
    }
}
