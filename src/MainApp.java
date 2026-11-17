import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainApp {
    public static void main(String[] args) {
        // Apply the modern look
        PharmacyTheme.apply();

        // Initialize inventory and load all saved data (stock + history)
        InventoryManager inventory = new InventoryManager();
        inventory.loadAllData();

        // Open the GUI window immediately. No popups.
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(inventory);

            // Intercept the close button to save data before exiting
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    inventory.saveAllData();
                    System.exit(0);
                }
            });

            frame.setVisible(true);
        });
    }
}