import java.time.LocalDateTime;

public class Transaction {
    private String id;
    private String medicineName;
    private String batchNumber;
    private int quantity;
    private LocalDateTime timestamp;

    public Transaction(String medicineName, String batchNumber, int quantity) {
        this.id = "TXN-" + System.currentTimeMillis();
        this.medicineName = medicineName;
        this.batchNumber = batchNumber;
        this.quantity = quantity;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getMedicineName() { return medicineName; }
    public String getBatchNumber() { return batchNumber; }
    public int getQuantity() { return quantity; }
    public LocalDateTime getTimestamp() { return timestamp; }
}