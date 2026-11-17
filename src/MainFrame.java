import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private DashboardPanel dashboardPanel;
    private StockPanel stockPanel;
    private DispensePanel dispensePanel;
    private AlertsPanel alertsPanel;
    private HistoryPanel historyPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public MainFrame(InventoryManager inventory) {


        setTitle("MediStock — Pharmacy Expiry Management");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(PharmacyTheme.BACKGROUND);

        // SIDEBAR
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // MAIN CONTENT (with CardLayout to switch between panels)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(PharmacyTheme.BACKGROUND);

        dashboardPanel = new DashboardPanel(inventory);
        stockPanel = new StockPanel(inventory);
        dispensePanel = new DispensePanel(inventory);
        alertsPanel = new AlertsPanel(inventory);
        historyPanel = new HistoryPanel(inventory);

        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(stockPanel, "STOCK");
        contentPanel.add(dispensePanel, "DISPENSE");
        contentPanel.add(alertsPanel, "ALERTS");
        contentPanel.add(historyPanel, "HISTORY");

        // --- GLOBAL REFRESH BUTTON ---
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.setOpaque(false);
        JButton refreshAllBtn = new JButton("Refresh All Data");
        refreshAllBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshAllBtn.setBackground(PharmacyTheme.SURFACE);
        refreshAllBtn.setForeground(PharmacyTheme.TEXT_PRIMARY);
        refreshAllBtn.setBorder(BorderFactory.createLineBorder(PharmacyTheme.BORDER, 1));

        refreshAllBtn.addActionListener(e -> {
            dashboardPanel.refreshUI();
            stockPanel.refreshTable();
            alertsPanel.refreshAlerts();
            historyPanel.refreshTable();
        });

        topBar.add(refreshAllBtn);
        add(topBar, BorderLayout.NORTH);
        // -----------------------------

        add(contentPanel, BorderLayout.CENTER);

        // Show dashboard by default
        cardLayout.show(contentPanel, "DASHBOARD");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(PharmacyTheme.PRIMARY_DARK);
        sidebar.setLayout(new BorderLayout());

        // Logo / Brand area
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 28));
        brandPanel.setOpaque(false);

        JLabel logo = new JLabel("⚕ MediStock");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(PharmacyTheme.TEXT_ON_PRIMARY);
        brandPanel.add(logo);

        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Menu items
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        menuPanel.add(createMenuItem("Dashboard", "DASHBOARD"));
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(createMenuItem("Stock Inventory", "STOCK"));
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(createMenuItem("Dispense", "DISPENSE"));
        menuPanel.add(Box.createVerticalStrut(8));
        menuPanel.add(createMenuItem("Expiry Alerts", "ALERTS"));
        menuPanel.add(createMenuItem("History", "HISTORY"));

        sidebar.add(menuPanel, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 20));
        footer.setOpaque(false);
        JLabel version = new JLabel("v1.0 • Student Project");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        version.setForeground(new Color(255, 255, 255, 120));
        footer.add(version);
        sidebar.add(footer, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel createMenuItem(String text, String panelName) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(255, 255, 255, 200));
        item.add(label);

        // Hover effect
        item.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                item.setBackground(new Color(255, 255, 255, 25));
                label.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                item.setBackground(new Color(0, 0, 0, 0));
                label.setForeground(new Color(255, 255, 255, 200));
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(contentPanel, panelName);
            }
        });

        return item;
    }
}