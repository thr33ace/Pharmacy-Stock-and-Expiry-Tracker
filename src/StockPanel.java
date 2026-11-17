import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class StockPanel extends JPanel {
    private InventoryManager inventory;
    private JTable table;
    private DefaultTableModel model;

    public StockPanel(InventoryManager inventory) {
        this.inventory = inventory;
        setLayout(new BorderLayout(0, 16));
        setBackground(PharmacyTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        // 1. Header (Title + Add Button)
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

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        deleteBtn.setBackground(PharmacyTheme.DANGER);
        deleteBtn.setForeground(PharmacyTheme.TEXT_ON_PRIMARY);
        deleteBtn.setPreferredSize(new Dimension(150, 40));
        deleteBtn.addActionListener(e -> deleteSelectedBatch());

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightButtons.setOpaque(false);
        rightButtons.add(deleteBtn);
        rightButtons.add(addBtn);

        header.remove(addBtn); // Remove the old button
        header.add(rightButtons, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // 2. Main Card (Holds Search Bar and Table)
        JPanel tableCard = new JPanel(new BorderLayout(0, 16));
        tableCard.setBackground(PharmacyTheme.SURFACE);
        tableCard.setBorder(BorderFactory.createLineBorder(PharmacyTheme.BORDER, 1));
        tableCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // --- SEARCH BAR ---
        JPanel searchPanel = new JPanel(new BorderLayout(0, 8));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search Medicine or Batch:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchLabel.setForeground(PharmacyTheme.TEXT_SECONDARY);

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PharmacyTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        searchPanel.add(searchLabel, BorderLayout.NORTH);
        searchPanel.add(searchField, BorderLayout.CENTER);
        tableCard.add(searchPanel, BorderLayout.NORTH);
        // --------------------

        // 3. Table Setup
        String[] columns = {"Medicine", "Batch No.", "Quantity", "Expiry Date", "Status"};
        model = new DefaultTableModel(columns, 0) {
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

        table = new JTable(model);

        // --- INSTANT SEARCH LOGIC ---
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }

            private void filter() {
                String text = searchField.getText().trim();
                if (text.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + text));
                }
            }
        });
        // ----------------------------

        // Table Styling
        table.setRowHeight(48);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setForeground(PharmacyTheme.TEXT_SECONDARY);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setShowGrid(false);

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

    // Updates the table when a new batch is added
    public void refreshTable() {
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
                refreshTable();

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

    // Colored status pill renderer
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

    private void deleteSelectedBatch() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a batch to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // CRITICAL: Convert view row to model row (because of the search filter)
        int modelRow = table.convertRowIndexToModel(viewRow);
        String batchNum = (String) model.getValueAt(modelRow, 1);
        String medName = (String) model.getValueAt(modelRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + medName + " (Batch: " + batchNum + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            inventory.removeBatch(batchNum);
            inventory.saveToFile("stock.csv");
            refreshTable();
        }
    }
}