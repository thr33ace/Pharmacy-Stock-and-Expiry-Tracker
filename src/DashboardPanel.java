import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardPanel extends JPanel {
    private InventoryManager inventory;

    public DashboardPanel(InventoryManager inventory) {
        this.inventory = inventory;

        // Use BorderLayout for the whole panel
        setLayout(new BorderLayout(0, 24));
        setBackground(PharmacyTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        // 1. Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(PharmacyTheme.TEXT_PRIMARY);
        header.add(title, BorderLayout.NORTH);

        JLabel subtitle = new JLabel("Overview of your pharmacy inventory - " +
                LocalDate.now().toString());
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(PharmacyTheme.TEXT_SECONDARY);
        header.add(subtitle, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        // 2. Center Wrapper (Holds Banner + Cards)
        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
        centerWrapper.setOpaque(false);

        // Add the new Status Banner
        centerWrapper.add(createStatusBanner());
        centerWrapper.add(Box.createVerticalStrut(24));

        // Add the Stat Cards
        JPanel cardsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsGrid.setOpaque(false);

        cardsGrid.add(createStatCard("Total Batches",
                String.valueOf(inventory.getStock().size()),
                "B", PharmacyTheme.PRIMARY, "All tracked batches"));

        cardsGrid.add(createStatCard("Safe Stock",
                String.valueOf(countByStatus("safe")),
                "S", PharmacyTheme.SAFE, "> 90 days until expiry"));

        cardsGrid.add(createStatCard("Expiring Soon",
                String.valueOf(countByStatus("critical") + countByStatus("warning")),
                "W", PharmacyTheme.WARNING, "< 90 days until expiry"));

        cardsGrid.add(createStatCard("Low Stock Alert",
                String.valueOf(inventory.getLowStockCount()),
                "L", PharmacyTheme.CRITICAL, "< 10 units remaining"));

        centerWrapper.add(cardsGrid);
        // --- ADD PIE CHART ---
        centerWrapper.add(Box.createVerticalStrut(24));

        Map<String, Integer> chartData = new LinkedHashMap<>();
        chartData.put("Safe", countByStatus("safe"));
        chartData.put("Warning", countByStatus("warning"));
        chartData.put("Critical", countByStatus("critical"));
        chartData.put("Expired", countByStatus("expired"));

        Map<String, Color> chartColors = new LinkedHashMap<>();
        chartColors.put("Safe", PharmacyTheme.SAFE);
        chartColors.put("Warning", PharmacyTheme.WARNING);
        chartColors.put("Critical", PharmacyTheme.CRITICAL);
        chartColors.put("Expired", PharmacyTheme.DANGER);

        PieChartPanel chart = new PieChartPanel(chartData, chartColors);
        chart.setPreferredSize(new Dimension(400, 200));
        chart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        chart.setAlignmentX(Component.LEFT_ALIGNMENT);

        centerWrapper.add(chart);
        // ---------------------
        add(centerWrapper, BorderLayout.CENTER);
    }

    // --- NEW: THE STATUS BANNER ---
    private JPanel createStatusBanner() {
        int expired = countByStatus("expired");
        int critical = countByStatus("critical");
        int lowStock = inventory.getLowStockCount();

        JPanel banner = new JPanel(new BorderLayout());
        banner.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        if (expired > 0 || critical > 0 || lowStock > 0) {
            // WARNING STATE
            banner.setBackground(new Color(255, 243, 224)); // Light amber background
            banner.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PharmacyTheme.WARNING, 1),
                    BorderFactory.createEmptyBorder(0, 0, 0, 0)
            ));

            StringBuilder msg = new StringBuilder("<html><b>Action Required:</b><br>");
            if (expired > 0) msg.append("- ").append(expired).append(" expired batches detected. Remove immediately.<br>");
            if (critical > 0) msg.append("- ").append(critical).append(" batches expiring in less than 30 days.<br>");
            if (lowStock > 0) msg.append("- ").append(lowStock).append(" items are running low on stock.");
            msg.append("</html>");

            JLabel msgLabel = new JLabel(msg.toString());
            msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            msgLabel.setForeground(new Color(102, 60, 0)); // Dark brown/amber text
            banner.add(msgLabel, BorderLayout.CENTER);

        } else {
            // ALL CLEAR STATE
            banner.setBackground(new Color(232, 245, 233)); // Light green background
            banner.setBorder(BorderFactory.createLineBorder(PharmacyTheme.SAFE, 1));

            JLabel msgLabel = new JLabel("<html><b>All Clear:</b> Inventory is up to date. No immediate actions required.</html>");
            msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            msgLabel.setForeground(new Color(27, 94, 32)); // Dark green text
            banner.add(msgLabel, BorderLayout.CENTER);
        }

        return banner;
    }
    // --------------------------------

    private JPanel createStatCard(String label, String value, String icon,
                                  Color accent, String description) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(PharmacyTheme.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PharmacyTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        iconLabel.setForeground(accent);
        top.add(iconLabel, BorderLayout.WEST);

        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(PharmacyTheme.TEXT_SECONDARY);
        top.add(titleLabel, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        valueLabel.setForeground(PharmacyTheme.TEXT_PRIMARY);
        card.add(valueLabel, BorderLayout.CENTER);

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

    public void refreshUI() {
        removeAll();
        revalidate();
        repaint();

        // Rebuild the dashboard
        setLayout(new BorderLayout(0, 24));
        setBackground(PharmacyTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        // (Copy the exact same constructor code here to rebuild the header, banner, and cards)
        // OR, simpler: just restart the app. But if you want the button to work:

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(PharmacyTheme.TEXT_PRIMARY);
        header.add(title, BorderLayout.NORTH);
        add(header, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
        centerWrapper.setOpaque(false);
        centerWrapper.add(createStatusBanner());
        centerWrapper.add(Box.createVerticalStrut(24));

        JPanel cardsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsGrid.setOpaque(false);
        cardsGrid.add(createStatCard("Total Batches", String.valueOf(inventory.getStock().size()), "B", PharmacyTheme.PRIMARY, "All tracked batches"));
        cardsGrid.add(createStatCard("Safe Stock", String.valueOf(countByStatus("safe")), "S", PharmacyTheme.SAFE, "> 90 days until expiry"));
        cardsGrid.add(createStatCard("Expiring Soon", String.valueOf(countByStatus("critical") + countByStatus("warning")), "W", PharmacyTheme.WARNING, "< 90 days until expiry"));
        cardsGrid.add(createStatCard("Low Stock Alert", String.valueOf(inventory.getLowStockCount()), "L", PharmacyTheme.CRITICAL, "< 10 units remaining"));

        centerWrapper.add(cardsGrid);
        add(centerWrapper, BorderLayout.CENTER);
    }
}