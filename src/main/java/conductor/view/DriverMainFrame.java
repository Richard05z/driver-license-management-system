package conductor.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import conductor.controller.DriverController;
import conductor.persistence.DriverDao;
import conductor.repository.DriverRepository;
import conductor.service.DriverService;

public class DriverMainFrame extends JFrame {
    private DriverController driverController;
    private DriverTable driverTable;
    private DriverForm driverForm;
    private JLabel statusLabel;
    
    public DriverMainFrame() {
        initComponents();
        setupListeners();
        setupWindow();
    }
    
    private void initComponents() {
        try {
            // Initialize the full chain: DAO → Repository → Service → Controller
            DriverDao driverDao = new DriverDao();
            DriverRepository driverRepository = new DriverRepository(driverDao);
            DriverService driverService = new DriverService(driverRepository);
            driverController = new DriverController(driverService);
            
            setTitle("Gestión de Conductores - CRUD Completo");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // Main split panel
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setDividerLocation(600);
            splitPane.setResizeWeight(0.5);
            
            // Left panel: Driver table
            driverTable = new DriverTable(driverController);
            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.add(driverTable, BorderLayout.CENTER);
            
            // Right panel: Form
            driverForm = new DriverForm(driverController);
            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.add(driverForm, BorderLayout.CENTER);
            
            splitPane.setLeftComponent(new JScrollPane(leftPanel));
            splitPane.setRightComponent(new JScrollPane(rightPanel));
            
            // Toolbar
            JToolBar toolBar = createToolBar();
            
            // Status bar
            JPanel statusPanel = createStatusBar();
            
            // Configure main layout
            Container contentPane = getContentPane();
            contentPane.setLayout(new BorderLayout());
            contentPane.add(toolBar, BorderLayout.NORTH);
            contentPane.add(splitPane, BorderLayout.CENTER);
            contentPane.add(statusPanel, BorderLayout.SOUTH);
            
            // Configure callbacks
            driverForm.setOnSaveCallback(() -> {
                driverTable.refresh();
                updateStatus("Conductor guardado exitosamente");
            });
            
            driverForm.setOnDeleteCallback(() -> {
                driverTable.refresh();
                updateStatus("Conductor eliminado exitosamente");
            });
            
            // Configure table selection listener
            driverTable.setSelectionListener(driverId -> {
                if (driverId != null) {
                    driverForm.loadDriver(driverId);
                    updateStatus("Conductor seleccionado ID: " + driverId);
                }
            });
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al inicializar la aplicación: " + e.getMessage(), 
                "Error de Inicialización", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton btnNew = new JButton("Nuevo Conductor");
        btnNew.addActionListener(e -> {
            driverForm.newDriver();
            updateStatus("Listo para crear nuevo conductor");
        });
        
        JButton btnDelete = new JButton("Eliminar Seleccionado");
        btnDelete.addActionListener(e -> {
            Integer id = driverTable.getSelectedDriverId();
            if (id != null) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "¿Está seguro de eliminar este conductor?", 
                    "Confirmar Eliminación", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        driverController.deleteDriver(id);
                        driverTable.refresh();
                        driverForm.newDriver();
                        updateStatus("Conductor eliminado exitosamente");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Error al eliminar conductor: " + ex.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Seleccione un conductor de la lista primero", 
                    "Advertencia", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        toolBar.add(btnNew);
        toolBar.addSeparator();
        toolBar.add(btnDelete);
        
        return toolBar;
    }
    
    private JPanel createStatusBar() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusLabel = new JLabel("Sistema de Gestión de Conductores - Listo");
        statusPanel.add(statusLabel);
        return statusPanel;
    }
    
    private void setupListeners() {
        // Double click on table to edit
        driverTable.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Integer id = driverTable.getSelectedDriverId();
                    if (id != null) {
                        driverForm.loadDriver(id);
                        updateStatus("Editando conductor ID: " + id);
                    }
                }
            }
        });
        
        // Simple click to show in status bar
        driverTable.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Integer id = driverTable.getSelectedDriverId();
                if (id != null) {
                    updateStatus("Conductor seleccionado ID: " + id);
                }
            }
        });
        
        // Window close listener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    db.ConnectionPool.closePool();
                } catch (Exception ex) {
                    System.err.println("Error closing connection pool: " + ex.getMessage());
                }
            }
        });
    }
    
    private void setupWindow() {
        setSize(1200, 700);
        setLocationRelativeTo(null); // Center on screen
        
        // Add keyboard shortcuts
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "new");
        getRootPane().getActionMap().put("new", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                driverForm.newDriver();
                updateStatus("Listo para crear nuevo conductor");
            }
        });
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "save");
        getRootPane().getActionMap().put("save", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                driverForm.saveDriver();
            }
        });
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
        getRootPane().getActionMap().put("cancel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                driverForm.newDriver();
                updateStatus("Operación cancelada");
            }
        });
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refresh");
        getRootPane().getActionMap().put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                driverTable.refresh();
                updateStatus("Lista actualizada");
            }
        });
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        getRootPane().getActionMap().put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer id = driverTable.getSelectedDriverId();
                if (id != null) {
                    int confirm = JOptionPane.showConfirmDialog(DriverMainFrame.this, 
                        "¿Está seguro de eliminar este conductor?", 
                        "Confirmar Eliminación", 
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            driverController.deleteDriver(id);
                            driverTable.refresh();
                            driverForm.newDriver();
                            updateStatus("Conductor eliminado exitosamente");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(DriverMainFrame.this, 
                                "Error al eliminar conductor: " + ex.getMessage(), 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
    
    // Main method to launch the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                // Optional: Set some UI improvements
                UIManager.put("Button.font", new Font("SansSerif", Font.PLAIN, 12));
                UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 12));
                UIManager.put("TextField.font", new Font("SansSerif", Font.PLAIN, 12));
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            DriverMainFrame frame = new DriverMainFrame();
            frame.setVisible(true);
        });
    }
}