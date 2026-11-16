import javax.swing.*;
public class FleetHubLauncherTemp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainAppFrame("admin", "ADMIN").setVisible(true));
    }
}
