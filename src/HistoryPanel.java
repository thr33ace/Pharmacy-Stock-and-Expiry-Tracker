import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class HistoryPanel extends JPanel {
    private InventoryManager inventory;
    private JTable table;
    private DefaultTableModel model;

    public HistoryPanel(InventoryManager inventory) {
        this.inventory = inventory;
        setLayout(new BorderLayout(0, 16));
        setBackground(PharmacyTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Transaction History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(PharmacyTheme.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refreshBtn.setBackground(PharmacyTheme.PRIMARY);
        refreshBtn.setForeground(PharmacyTheme.TEXT_ON_PRIMARY);
        refreshBtn.setPreferredSize(new Dimension(100, 40));
        refreshBtn.addActionListener(e -> refreshTable());
        header.add(refreshBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(PharmacyTheme.SURFACE);
        tableCard.setBorder(BorderFactory.createLineBorder(PharmacyTheme.BORDER, 1));

        String[] columns = {"Transaction ID", "Medicine", "Batch", "Qty", "Date & Time"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setShowGrid(false);

        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);
    }

    public void refreshTable() {
        System.out.println("[DEBUG] Refresh button clicked. Current transactions in memory: " + inventory.getTransactions().size());
        model.setRowCount(0);
        for (Transaction t : inventory.getTransactions()) {
            model.addRow(new Object[]{
                    t.getId(), t.getMedicineName(), t.getBatchNumber(), t.getQuantity(),
                    t.getTimestamp().toString().replace("T", " ")
            });
        }
    }
}