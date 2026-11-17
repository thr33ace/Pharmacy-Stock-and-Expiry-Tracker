import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AlertsPanel extends JPanel {
    private InventoryManager inventory;
    private JPanel scrollContent; // We keep a reference to the inner panel

    public AlertsPanel(InventoryManager inventory) {
        this.inventory = inventory;
        setLayout(new BorderLayout(0, 24));
        setBackground(PharmacyTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        // 1. Header (Added only once)
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Expiry Alerts");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(PharmacyTheme.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        JButton refreshBtn = new JButton("Refresh Alerts");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refreshBtn.setBackground(PharmacyTheme.PRIMARY);
        refreshBtn.setForeground(PharmacyTheme.TEXT_ON_PRIMARY);
        refreshBtn.addActionListener(e -> refreshAlerts());
        header.add(refreshBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // 2. Scrollable Content Area (Created only once)
        scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBackground(PharmacyTheme.BACKGROUND);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // 3. Initial Load
        refreshAlerts();
    }

    void refreshAlerts() {
        // FIX: Only clear the inner scroll content, NOT the whole panel
        scrollContent.removeAll();

        // Categorize batches
        List<Batch> expired = new ArrayList<>();
        List<Batch> critical = new ArrayList<>();
        List<Batch> warning = new ArrayList<>();
        List<Batch> safe = new ArrayList<>();

        for (Batch batch : inventory.getStock()) {
            if (batch.isExpired()) expired.add(batch);
            else if (batch.getDaysUntilExpiry() < 30) critical.add(batch);
            else if (batch.getDaysUntilExpiry() < 90) warning.add(batch);
            else safe.add(batch);
        }

        // Add sections to the inner panel
        scrollContent.add(createAlertSection("EXPIRED (DO NOT SELL)", expired, PharmacyTheme.DANGER));
        scrollContent.add(Box.createVerticalStrut(16));
        scrollContent.add(createAlertSection("CRITICAL (< 30 Days)", critical, PharmacyTheme.CRITICAL));
        scrollContent.add(Box.createVerticalStrut(16));
        scrollContent.add(createAlertSection("WARNING (30-90 Days)", warning, PharmacyTheme.WARNING));
        scrollContent.add(Box.createVerticalStrut(16));
        scrollContent.add(createAlertSection("SAFE (> 90 Days)", safe, PharmacyTheme.SAFE));
        scrollContent.add(Box.createVerticalGlue()); // Pushes content to the top nicely

        // Refresh the UI
        scrollContent.revalidate();
        scrollContent.repaint();
    }

    private JPanel createAlertSection(String sectionTitle, List<Batch> batches, Color accentColor) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT); // Important for BoxLayout

        JLabel titleLabel = new JLabel(sectionTitle + " (" + batches.size() + ")");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(accentColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(titleLabel);
        section.add(Box.createVerticalStrut(12));

        if (batches.isEmpty()) {
            JLabel emptyLabel = new JLabel("No items in this category.");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            emptyLabel.setForeground(PharmacyTheme.TEXT_SECONDARY);
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            section.add(emptyLabel);
        } else {
            for (Batch b : batches) {
                JPanel card = createAlertCard(b, accentColor);
                card.setAlignmentX(Component.LEFT_ALIGNMENT); // Important for BoxLayout
                section.add(card);
                section.add(Box.createVerticalStrut(8));
            }
        }
        return section;
    }

    private JPanel createAlertCard(Batch batch, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(16, 0));
        card.setBackground(PharmacyTheme.SURFACE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor), // Colored left border
                BorderFactory.createLineBorder(PharmacyTheme.BORDER, 1)
        ));

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 4));
        left.setOpaque(false);
        left.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 0));

        JLabel name = new JLabel(batch.getMedicineName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));
        name.setForeground(PharmacyTheme.TEXT_PRIMARY);

        JLabel batchNum = new JLabel("Batch: " + batch.getBatchNumber());
        batchNum.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        batchNum.setForeground(PharmacyTheme.TEXT_SECONDARY);

        left.add(name);
        left.add(batchNum);

        JPanel right = new JPanel(new GridLayout(2, 1, 0, 4));
        right.setOpaque(false);
        right.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 16));

        JLabel qty = new JLabel("Qty: " + batch.getQuantity());
        qty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        qty.setForeground(PharmacyTheme.TEXT_PRIMARY);
        qty.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel date = new JLabel("Expires: " + batch.getExpiryDate().toString());
        date.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        date.setForeground(PharmacyTheme.TEXT_SECONDARY);
        date.setHorizontalAlignment(SwingConstants.RIGHT);

        right.add(qty);
        right.add(date);

        card.add(left, BorderLayout.WEST);
        card.add(right, BorderLayout.EAST);
        return card;
    }
}