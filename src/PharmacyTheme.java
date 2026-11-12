import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class PharmacyTheme {
    // PHARMACEUTICAL COLOR PALETTE
    // Inspired by clinical design: trust, cleanliness, clarity

    public static final Color PRIMARY = new Color(10, 110, 124);        // Deep teal - trust
    public static final Color PRIMARY_DARK = new Color(7, 82, 92);      // Darker teal - sidebar
    public static final Color PRIMARY_LIGHT = new Color(168, 218, 220); // Soft mint - accents

    public static final Color BACKGROUND = new Color(248, 249, 250);    // Off-white - main bg
    public static final Color SURFACE = Color.WHITE;                    // Pure white - cards
    public static final Color SURFACE_HOVER = new Color(241, 243, 245); // Hover state

    public static final Color TEXT_PRIMARY = new Color(29, 53, 87);     // Dark slate - headings
    public static final Color TEXT_SECONDARY = new Color(108, 117, 125); // Muted - labels
    public static final Color TEXT_ON_PRIMARY = Color.WHITE;            // White on teal

    public static final Color SAFE = new Color(82, 183, 136);           // Muted green
    public static final Color WARNING = new Color(244, 162, 97);        // Soft amber
    public static final Color CRITICAL = new Color(231, 111, 81);       // Soft coral
    public static final Color DANGER = new Color(214, 40, 40);          // Deep red

    public static final Color BORDER = new Color(222, 226, 230);        // Subtle borders
    public static final Color SHADOW = new Color(0, 0, 0, 20);          // Very subtle shadow

    public static void apply() {
        try {
            FlatLightLaf.setup();

            // Override FlatLaf defaults with pharmaceutical palette
            UIManager.put("Background", BACKGROUND);
            UIManager.put("Panel.background", BACKGROUND);
            UIManager.put("Component.background", BACKGROUND);

            UIManager.put("TextForeground", TEXT_PRIMARY);
            UIManager.put("Label.foreground", TEXT_PRIMARY);

            UIManager.put("Button.background", PRIMARY);
            UIManager.put("Button.foreground", TEXT_ON_PRIMARY);
            UIManager.put("Button.hoverBackground", PRIMARY_DARK);
            UIManager.put("Button.arc", 8); // Rounded buttons

            UIManager.put("Component.arc", 8); // Rounded corners everywhere
            UIManager.put("TextComponent.arc", 8);

            UIManager.put("Table.selectionBackground", PRIMARY_LIGHT);
            UIManager.put("Table.selectionForeground", TEXT_PRIMARY);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.intercellSpacing", new Dimension(0, 1));

            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}