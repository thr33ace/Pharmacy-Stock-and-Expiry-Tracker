import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.LinkedHashMap;
import java.util.Map;

public class PieChartPanel extends JPanel {
    private final Map<String, Integer> data;
    private final Map<String, Color> colors;

    public PieChartPanel(Map<String, Integer> data, Map<String, Color> colors) {
        this.data = data;
        this.colors = colors;
        setBackground(PharmacyTheme.SURFACE);
        setBorder(BorderFactory.createLineBorder(PharmacyTheme.BORDER, 1));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int total = data.values().stream().mapToInt(Integer::intValue).sum();
        if (total == 0) {
            g2.setColor(PharmacyTheme.TEXT_SECONDARY);
            g2.drawString("No data available", 20, getHeight() / 2);
            return;
        }

        // Draw Pie
        int diameter = Math.min(getWidth() - 120, getHeight() - 20);
        int x = 20;
        int y = (getHeight() - diameter) / 2;
        double currentAngle = 0;

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            double angle = (entry.getValue() / (double) total) * 360;
            g2.setColor(colors.get(entry.getKey()));
            g2.fill(new Arc2D.Double(x, y, diameter, diameter, currentAngle, angle, Arc2D.PIE));
            currentAngle += angle;
        }

        // Draw Legend
        int legendX = x + diameter + 20;
        int legendY = y + 10;
        int boxSize = 12;

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            g2.setColor(colors.get(entry.getKey()));
            g2.fillRect(legendX, legendY, boxSize, boxSize);

            g2.setColor(PharmacyTheme.TEXT_PRIMARY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            String label = entry.getKey() + " (" + entry.getValue() + ")";
            g2.drawString(label, legendX + boxSize + 8, legendY + 10);

            legendY += 24;
        }
    }
}