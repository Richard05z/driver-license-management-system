package licencia.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import licencia.controller.LicenseController;
import licencia.persistence.LicenseDao;
import licencia.repository.LicenseRepository;
import licencia.service.LicenseService;
import conductor.persistence.DriverDao;
import conductor.repository.DriverRepository;
import conductor.service.DriverService;
import conductor.controller.DriverController;

public class LicenseMainFrame extends JFrame {
    private LicenseController licenseController;
    private LicenseTable licenseTable;
    private LicenseForm licenseForm;
    private DriverController driverController;
    private JLabel statusLabel;
    
    public LicenseMainFrame() {
        initComponents();
        setupListeners();
        setupWindow();
    }
    
    private void initComponents() {
        try {
            // Initialize Driver chain (needed for driver validation)
            DriverDao driverDao = new DriverDao();
            DriverRepository driverRepository = new DriverRepository(driverDao);
            DriverService driverService = new DriverService(driverRepository);
            driverController = new DriverController(driverService);
            
            // Initialize License chain
            LicenseDao licenseDao = new LicenseDao();
            LicenseRepository licenseRepository = new LicenseRepository(licenseDao);
            LicenseService licenseService = new LicenseService(licenseRepository);
            licenseController = new LicenseController(licenseService);
            
            setTitle("Gestión de Licencias de Conducir - CRUD Completo");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // Main split panel
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setDividerLocation(700);
            splitPane.setResizeWeight(0.6);
            
            // Left panel: License table
            licenseTable = new LicenseTable(licenseController);
            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.add(licenseTable, BorderLayout.CENTER);
            
            // Right panel: Form
            licenseForm = new LicenseForm(licenseController);
            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.add(licenseForm, BorderLayout.CENTER);
            
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
            licenseForm.setOnSaveCallback(() -> {
                licenseTable.refresh();
                updateStatus("Licencia guardada exitosamente");
            });
            
            licenseForm.setOnDeleteCallback(() -> {
                licenseTable.refresh();
                updateStatus("Licencia eliminada exitosamente");
            });
            
            // Configure table selection listener
            licenseTable.setSelectionListener(licenseId -> {
                if (licenseId != null) {
                    licenseForm.loadLicense(licenseId);
                    updateStatus("Licencia seleccionada ID: " + licenseId);
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
        
        JButton btnNew = new JButton("Nueva Licencia");
        btnNew.addActionListener(e -> {
            licenseForm.newLicense();
            updateStatus("Listo para crear nueva licencia");
        });
        
        JButton btnNewIssue = new JButton("Emitir Nueva Licencia");
        btnNewIssue.addActionListener(e -> issueNewLicense());
        
        JButton btnRenew = new JButton("Renovar Seleccionada");
        btnRenew.addActionListener(e -> {
            Long id = licenseTable.getSelectedLicenseId();
            if (id != null) {
                licenseForm.renewLicense();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Seleccione una licencia de la lista primero", 
                    "Advertencia", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JButton btnDelete = new JButton("Eliminar Seleccionada");
        btnDelete.addActionListener(e -> {
            Long id = licenseTable.getSelectedLicenseId();
            if (id != null) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "¿Está seguro de eliminar esta licencia?", 
                    "Confirmar Eliminación", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        licenseController.deleteLicense(id);
                        licenseTable.refresh();
                        licenseForm.newLicense();
                        updateStatus("Licencia eliminada exitosamente");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Error al eliminar licencia: " + ex.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Seleccione una licencia de la lista primero", 
                    "Advertencia", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        toolBar.add(btnNew);
        toolBar.add(btnNewIssue);
        toolBar.addSeparator();
        toolBar.add(btnRenew);
        toolBar.addSeparator();
        toolBar.add(btnDelete);
        
        return toolBar;
    }
    
    private void issueNewLicense() {
        JDialog dialog = new JDialog(this, "Emitir Nueva Licencia", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Driver ID
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("ID Conductor:"), gbc);
        gbc.gridx = 1;
        JTextField txtDriverId = new JTextField(15);
        formPanel.add(txtDriverId, gbc);
        
        // License Type
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Tipo de Licencia:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> cmbLicenseType = new JComboBox<>(new String[]{"A", "B", "C", "D", "E", "F"});
        formPanel.add(cmbLicenseType, gbc);
        
        // Category
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Categoría:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> cmbCategory = new JComboBox<>(new String[]{"camion", "moto", "automovil", "autobus"});
        formPanel.add(cmbCategory, gbc);
        
        // Validity Years
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Años de Validez (1-10):"), gbc);
        gbc.gridx = 1;
        JSpinner spnValidityYears = new JSpinner(new SpinnerNumberModel(10, 1, 10, 1));
        formPanel.add(spnValidityYears, gbc);
        
        // Restrictions
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Restricciones:"), gbc);
        gbc.gridx = 1;
        JTextArea txtRestrictions = new JTextArea(3, 15);
        txtRestrictions.setLineWrap(true);
        txtRestrictions.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtRestrictions);
        formPanel.add(scrollPane, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnIssue = new JButton("Emitir Licencia");
        btnIssue.setBackground(new Color(76, 175, 80));
        btnIssue.setForeground(Color.WHITE);
        btnIssue.addActionListener(e -> {
            try {
                Long driverId = Long.parseLong(txtDriverId.getText().trim());
                String licenseType = (String) cmbLicenseType.getSelectedItem();
                String category = (String) cmbCategory.getSelectedItem();
                int validityYears = (Integer) spnValidityYears.getValue();
                String restrictions = txtRestrictions.getText().trim();
                
                // Check if driver exists
                if (!driverController.checkDriverExistsById(driverId)) {
                    JOptionPane.showMessageDialog(dialog, 
                        "El conductor con ID " + driverId + " no existe", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create and save license
                var license = licenseController.issueNewLicense(driverId, licenseType, category, validityYears);
                
                // Add restrictions if any
                if (!restrictions.isEmpty()) {
                    license.setRestrictions(restrictions);
                    licenseController.updateLicense(license);
                }
                
                JOptionPane.showMessageDialog(dialog, 
                    "Licencia emitida exitosamente\nID: " + license.getId(), 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                dialog.dispose();
                licenseTable.refresh();
                updateStatus("Nueva licencia emitida ID: " + license.getId());
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "ID de conductor inválido", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton btnCancel = new JButton("Cancelar");
        btnCancel.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnIssue);
        buttonPanel.add(btnCancel);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private JPanel createStatusBar() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusLabel = new JLabel("Sistema de Gestión de Licencias - Listo");
        statusPanel.add(statusLabel);
        return statusPanel;
    }
    
    private void setupListeners() {
        // Double click on table to edit
        licenseTable.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Long id = licenseTable.getSelectedLicenseId();
                    if (id != null) {
                        licenseForm.loadLicense(id);
                        updateStatus("Editando licencia ID: " + id);
                    }
                }
            }
        });
        
        // Simple click to show in status bar
        licenseTable.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Long id = licenseTable.getSelectedLicenseId();
                if (id != null) {
                    updateStatus("Licencia seleccionada ID: " + id);
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
        setSize(1300, 750);
        setLocationRelativeTo(null); // Center on screen
        
        // Add keyboard shortcuts
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "new");
        getRootPane().getActionMap().put("new", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                licenseForm.newLicense();
                updateStatus("Listo para crear nueva licencia");
            }
        });
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK), "issue");
        getRootPane().getActionMap().put("issue", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                issueNewLicense();
            }
        });
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "save");
        getRootPane().getActionMap().put("save", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                licenseForm.saveLicense();
            }
        });
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), "renew");
        getRootPane().getActionMap().put("renew", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Long id = licenseTable.getSelectedLicenseId();
                if (id != null) {
                    licenseForm.renewLicense();
                }
            }
        });
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
        getRootPane().getActionMap().put("cancel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                licenseForm.newLicense();
                updateStatus("Operación cancelada");
            }
        });
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refresh");
        getRootPane().getActionMap().put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                licenseTable.refresh();
                updateStatus("Lista actualizada");
            }
        });
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        getRootPane().getActionMap().put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Long id = licenseTable.getSelectedLicenseId();
                if (id != null) {
                    int confirm = JOptionPane.showConfirmDialog(LicenseMainFrame.this, 
                        "¿Está seguro de eliminar esta licencia?", 
                        "Confirmar Eliminación", 
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            licenseController.deleteLicense(id);
                            licenseTable.refresh();
                            licenseForm.newLicense();
                            updateStatus("Licencia eliminada exitosamente");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(LicenseMainFrame.this, 
                                "Error al eliminar licencia: " + ex.getMessage(), 
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
                
                // Optional: Set more specific UI improvements for better appearance
                UIManager.put("Table.font", new Font("SansSerif", Font.PLAIN, 11));
                UIManager.put("TableHeader.font", new Font("SansSerif", Font.BOLD, 12));
                UIManager.put("ComboBox.font", new Font("SansSerif", Font.PLAIN, 12));
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            LicenseMainFrame frame = new LicenseMainFrame();
            frame.setVisible(true);
        });
    }
}