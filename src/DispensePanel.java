import javax.swing.*;
import java.awt.*;

public class DispensePanel extends JPanel {
    private InventoryManager inventory;
    private JTextField nameField;
    private JTextField qtyField;
    private JTextArea resultArea;

    public DispensePanel(InventoryManager inventory) {
        this.inventory = inventory;
        setLayout(new BorderLayout(0, 24));
        setBackground(PharmacyTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Dispense Medicine");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(PharmacyTheme.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Input Card
        JPanel inputCard = new JPanel(new GridBagLayout());
        inputCard.setBackground(PharmacyTheme.SURFACE);
        inputCard.setBorder(BorderFactory.createLineBorder(PharmacyTheme.BORDER, 1));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        inputCard.add(new JLabel("Medicine Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        nameField = new JTextField(20);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputCard.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        inputCard.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        qtyField = new JTextField(20);
        qtyField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputCard.add(qtyField, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        JButton dispenseBtn = new JButton("Dispense Medicine");
        dispenseBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dispenseBtn.setBackground(PharmacyTheme.PRIMARY);
        dispenseBtn.setForeground(PharmacyTheme.TEXT_ON_PRIMARY);
        dispenseBtn.setPreferredSize(new Dimension(200, 45));
        dispenseBtn.addActionListener(e -> performDispense());
        inputCard.add(dispenseBtn, gbc);

        add(inputCard, BorderLayout.NORTH);

        // Result Area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        resultArea.setBackground(PharmacyTheme.SURFACE);
        resultArea.setForeground(PharmacyTheme.TEXT_PRIMARY);
        resultArea.setBorder(BorderFactory.createLineBorder(PharmacyTheme.BORDER, 1));
        resultArea.setMargin(new Insets(20, 20, 20, 20));
        resultArea.setText("Enter a medicine name and quantity, then click Dispense.");

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(PharmacyTheme.BORDER, 1));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void performDispense() {
        String name = nameField.getText().trim();
        String qtyText = qtyField.getText().trim();

        if (name.isEmpty() || qtyText.isEmpty()) {
            resultArea.setText("Error: Please fill in both fields.");
            return;
        }

        try {
            int qty = Integer.parseInt(qtyText);
            String result = inventory.dispenseMedicineGUI(name, qty);
            resultArea.setText(result);
            nameField.setText("");
            qtyField.setText("");
        } catch (NumberFormatException e) {
            resultArea.setText("Error: Please enter a valid number for quantity.");
        }
    }
}