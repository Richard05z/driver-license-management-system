import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Initialize Swing in EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                // Create and show main application frame
                MainApplicationFrame mainFrame = new MainApplicationFrame();
                mainFrame.setVisible(true);
                
            } catch (Exception e) {
                // Show error dialog if initialization fails
                JOptionPane.showMessageDialog(null,
                    "Error al iniciar la aplicación:\n" + e.getMessage(),
                    "Error de Inicialización",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}