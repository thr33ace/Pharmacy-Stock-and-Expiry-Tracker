import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StockPanel extends JPanel {
    private InventoryManager inventory;
    private JTable table; // <-- Table is declared here at the top

    public StockPanel(InventoryManager inventory) {
        this.inventory = inventory;
        setLayout(new BorderLayout(0, 16));
        setBackground(PharmacyTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Stock Inventory");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(PharmacyTheme.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        JButton addBtn = new JButton("+ Add Batch");
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.setBackground(PharmacyTheme.PRIMARY);
        addBtn.setForeground(PharmacyTheme.TEXT_ON_PRIMARY);
        addBtn.setPreferredSize(new Dimension(130, 40));
        addBtn.addActionListener(e -> showAddDialog());
        header.add(addBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Table inside a card
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(PharmacyTheme.SURFACE);
        tableCard.setBorder(BorderFactory.createLineBorder(PharmacyTheme.BORDER, 1));

        String[] columns = {"Medicine", "Batch No.", "Quantity", "Expiry Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        for (Batch b : inventory.getStock()) {
            model.addRow(new Object[]{
                    b.getMedicineName(),
                    b.getBatchNumber(),
                    b.getQuantity(),
                    b.getExpiryDate().toString(),
                    getStatusLabel(b)
            });
        }

        // Notice there is no "JTable" keyword here, just "table ="
        table = new JTable(model);
        table.setRowHeight(48);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setForeground(PharmacyTheme.TEXT_SECONDARY);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setShowGrid(false);

        // Status column: colored badge renderer
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());
        table.getColumnModel().getColumn(2).setCellRenderer(new CenterRenderer());

        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);
    }

    private String getStatusLabel(Batch b) {
        if (b.isExpired()) return "EXPIRED";
        long days = b.getDaysUntilExpiry();
        if (days < 30) return "CRITICAL";
        if (days < 90) return "WARNING";
        return "SAFE";
    }

    // This is the method that updates the table when you add a new batch
    public void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear existing rows

        for (Batch b : inventory.getStock()) {
            model.addRow(new Object[]{
                    b.getMedicineName(),
                    b.getBatchNumber(),
                    b.getQuantity(),
                    b.getExpiryDate().toString(),
                    getStatusLabel(b)
            });
        }
    }

    private void showAddDialog() {
        JTextField nameField = new JTextField();
        JTextField batchField = new JTextField();
        JTextField qtyField = new JTextField();
        JTextField expiryField = new JTextField("YYYY-MM-DD");

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.add(new JLabel("Medicine Name:")); panel.add(nameField);
        panel.add(new JLabel("Batch Number:")); panel.add(batchField);
        panel.add(new JLabel("Quantity:")); panel.add(qtyField);
        panel.add(new JLabel("Expiry Date:")); panel.add(expiryField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Batch",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String medName = nameField.getText().trim();
                String batchNo = batchField.getText().trim();
                int quantity = Integer.parseInt(qtyField.getText().trim());
                java.time.LocalDate expDate = java.time.LocalDate.parse(expiryField.getText().trim());

                inventory.addBatch(medName, batchNo, quantity, expDate);
                inventory.saveToFile("stock.csv");
                refreshTable(); // This updates the screen!

                JOptionPane.showMessageDialog(this, "Batch added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a valid number.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Custom renderer: colored status pill
    class StatusBadgeRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            label.setOpaque(true);

            String status = value.toString();
            Color bg, fg;

            switch (status) {
                case "EXPIRED":  bg = PharmacyTheme.DANGER;   fg = Color.WHITE; break;
                case "CRITICAL": bg = PharmacyTheme.CRITICAL; fg = Color.WHITE; break;
                case "WARNING":  bg = PharmacyTheme.WARNING;  fg = Color.WHITE; break;
                case "SAFE":     bg = PharmacyTheme.SAFE;     fg = Color.WHITE; break;
                default:         bg = PharmacyTheme.BORDER;   fg = PharmacyTheme.TEXT_PRIMARY;
            }

            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            } else {
                label.setBackground(bg);
                label.setForeground(fg);
            }

            label.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            label.setText("  " + status + "  ");
            return label;
        }
    }

    class CenterRenderer extends DefaultTableCellRenderer {
        public CenterRenderer() { setHorizontalAlignment(SwingConstants.CENTER); }
    }
}