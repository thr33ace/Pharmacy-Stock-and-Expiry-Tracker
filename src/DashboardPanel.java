import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class DashboardPanel extends JPanel {
    private InventoryManager inventory;

    public DashboardPanel(InventoryManager inventory) {
        this.inventory = inventory;
        setLayout(new BorderLayout(0, 24));
        setBackground(PharmacyTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(PharmacyTheme.TEXT_PRIMARY);
        header.add(title, BorderLayout.NORTH);

        JLabel subtitle = new JLabel("Overview of your pharmacy inventory • " +
                LocalDate.now().toString());
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(PharmacyTheme.TEXT_SECONDARY);
        header.add(subtitle, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        // Stat cards grid
        JPanel cardsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsGrid.setOpaque(false);

        cardsGrid.add(createStatCard("Total Batches",
                String.valueOf(inventory.getStock().size()),
                "📦", PharmacyTheme.PRIMARY, "All tracked batches"));

        cardsGrid.add(createStatCard("Safe Stock",
                String.valueOf(countByStatus("safe")),
                "✓", PharmacyTheme.SAFE, "> 90 days until expiry"));

        cardsGrid.add(createStatCard("Expiring Soon",
                String.valueOf(countByStatus("critical") + countByStatus("warning")),
                "⚠", PharmacyTheme.WARNING, "< 90 days until expiry"));

        cardsGrid.add(createStatCard("Expired / Critical",
                String.valueOf(countByStatus("expired") + countByStatus("critical")),
                "⚕", PharmacyTheme.DANGER, "Requires immediate action"));

        add(cardsGrid, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String label, String value, String icon,
                                  Color accent, String description) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(PharmacyTheme.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PharmacyTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        // Top: icon + label
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        iconLabel.setForeground(accent);
        top.add(iconLabel, BorderLayout.WEST);

        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(PharmacyTheme.TEXT_SECONDARY);
        top.add(titleLabel, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);

        // Center: big number
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        valueLabel.setForeground(PharmacyTheme.TEXT_PRIMARY);
        card.add(valueLabel, BorderLayout.CENTER);

        // Bottom: description
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(PharmacyTheme.TEXT_SECONDARY);
        card.add(descLabel, BorderLayout.SOUTH);

        return card;
    }

    private int countByStatus(String status) {
        int count = 0;
        for (Batch b : inventory.getStock()) {
            if (status.equals("expired") && b.isExpired()) count++;
            else if (status.equals("critical") && !b.isExpired() && b.getDaysUntilExpiry() < 30) count++;
            else if (status.equals("warning") && !b.isExpired() && b.getDaysUntilExpiry() >= 30 && b.getDaysUntilExpiry() < 90) count++;
            else if (status.equals("safe") && !b.isExpired() && b.getDaysUntilExpiry() >= 90) count++;
        }
        return count;
    }
}