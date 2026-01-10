package licencia.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import licencia.controller.LicenseController;
import licencia.dto.LicenseResponseDto;
import licencia.model.License;

public class LicenseForm extends JPanel {
    // Form fields
    private JTextField txtId;
    private JTextField txtDriverId;
    private JComboBox<String> cmbLicenseType;
    private JComboBox<String> cmbCategory;
    private JTextField txtIssueDate;
    private JTextField txtExpiryDate;
    private JSpinner spnPoints;
    private JTextArea txtRestrictions;
    private JCheckBox chkRenewed;
    
    // Buttons
    private JButton btnSave;
    private JButton btnCancel;
    private JButton btnDelete;
    private JButton btnRenew;
    private JButton btnResetPoints;
    private JButton btnDeductPoints;
    
    // Controller and callbacks
    private LicenseController licenseController;
    private Runnable onSaveCallback;
    private Runnable onDeleteCallback;
    
    // Constants
    private static final String[] LICENSE_TYPES = {"A", "B", "C", "D", "E", "F"};
    private static final String[] CATEGORIES = {"camion", "moto", "automovil", "autobus"};
    
    public LicenseForm(LicenseController controller) {
        this.licenseController = controller;
        initComponents();
        newLicense();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Gestión de Licencias de Conducir");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titlePanel.add(titleLabel);
        
        add(titlePanel, BorderLayout.NORTH);
        
        // Form panel with scroll
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // ID (hidden)
        txtId = new JTextField();
        txtId.setVisible(false);
        
        // Form fields
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("ID Conductor:"), gbc);
        gbc.gridx = 1;
        txtDriverId = new JTextField(20);
        formPanel.add(txtDriverId, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Tipo de Licencia:"), gbc);
        gbc.gridx = 1;
        cmbLicenseType = new JComboBox<>(LICENSE_TYPES);
        formPanel.add(cmbLicenseType, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Categoría:"), gbc);
        gbc.gridx = 1;
        cmbCategory = new JComboBox<>(CATEGORIES);
        formPanel.add(cmbCategory, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Fecha Emisión (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        txtIssueDate = new JTextField(20);
        formPanel.add(txtIssueDate, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Fecha Vencimiento (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        txtExpiryDate = new JTextField(20);
        formPanel.add(txtExpiryDate, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Puntos (0-20):"), gbc);
        gbc.gridx = 1;
        spnPoints = new JSpinner(new SpinnerNumberModel(20, 0, 20, 1));
        formPanel.add(spnPoints, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Restricciones:"), gbc);
        gbc.gridx = 1;
        txtRestrictions = new JTextArea(3, 20);
        txtRestrictions.setLineWrap(true);
        txtRestrictions.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtRestrictions);
        formPanel.add(scrollPane, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Renovada:"), gbc);
        gbc.gridx = 1;
        chkRenewed = new JCheckBox();
        formPanel.add(chkRenewed, gbc);
        
        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(formScrollPane, BorderLayout.CENTER);
        
        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        btnRenew = new JButton("Renovar");
        btnRenew.setBackground(new Color(33, 150, 243));
        btnRenew.setForeground(Color.WHITE);
        btnRenew.addActionListener(e -> renewLicense());
        
        btnResetPoints = new JButton("Resetear Puntos");
        btnResetPoints.setBackground(new Color(255, 152, 0));
        btnResetPoints.setForeground(Color.WHITE);
        btnResetPoints.addActionListener(e -> resetPoints());
        
        btnDeductPoints = new JButton("Deducir Puntos");
        btnDeductPoints.setBackground(new Color(233, 30, 99));
        btnDeductPoints.setForeground(Color.WHITE);
        btnDeductPoints.addActionListener(e -> deductPoints());
        
        actionPanel.add(btnRenew);
        actionPanel.add(btnResetPoints);
        actionPanel.add(btnDeductPoints);
        
        // Main button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        btnSave = new JButton("Guardar");
        btnSave.setBackground(new Color(76, 175, 80));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> saveLicense());
        
        btnCancel = new JButton("Cancelar");
        btnCancel.setBackground(new Color(158, 158, 158));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> newLicense());
        
        btnDelete = new JButton("Eliminar");
        btnDelete.setBackground(new Color(244, 67, 54));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteLicense());
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnDelete);
        
        // Combine panels
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(actionPanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        
        add(southPanel, BorderLayout.SOUTH);
        
        // Validations
        txtDriverId.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
        
        // Auto-set expiry date based on issue date (10 years)
        txtIssueDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                autoSetExpiryDate();
            }
        });
    }
    
    public void saveLicense() {
        if (!validateFields()) {
            return;
        }
        
        try {
            License license = new License(
                Long.parseLong(txtDriverId.getText().trim()),
                (String) cmbLicenseType.getSelectedItem(),
                (String) cmbCategory.getSelectedItem(),
                txtIssueDate.getText().trim(),
                txtExpiryDate.getText().trim(),
                (Integer) spnPoints.getValue(),
                txtRestrictions.getText().trim(),
                chkRenewed.isSelected()
            );
            
            // If there's ID, it's an update
            if (!txtId.getText().isEmpty()) {
                license.setId(Long.parseLong(txtId.getText()));
                licenseController.updateLicense(license);
                JOptionPane.showMessageDialog(this, "Licencia actualizada exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                licenseController.addLicense(license);
                JOptionPane.showMessageDialog(this, "Licencia agregada exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            
            newLicense();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteLicense() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay licencia seleccionada para eliminar", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar esta licencia?", 
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Long id = Long.parseLong(txtId.getText());
                licenseController.deleteLicense(id);
                
                JOptionPane.showMessageDialog(this, "Licencia eliminada exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                if (onDeleteCallback != null) {
                    onDeleteCallback.run();
                }
                
                newLicense();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void renewLicense() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay licencia seleccionada para renovar", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Get current expiry date and add 10 years
            String currentExpiry = txtExpiryDate.getText().trim();
            java.time.LocalDate expiryDate = java.time.LocalDate.parse(currentExpiry);
            java.time.LocalDate newExpiryDate = expiryDate.plusYears(10);
            
            String newExpiry = newExpiryDate.toString();
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Renovar licencia hasta " + newExpiry + "?",
                "Confirmar Renovación",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                Long id = Long.parseLong(txtId.getText());
                boolean success = licenseController.renewLicense(id, newExpiry);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, "Licencia renovada exitosamente", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    loadLicense(id); // Reload to show updated data
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al renovar: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void resetPoints() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay licencia seleccionada", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Restablecer puntos a 20?",
            "Confirmar Restablecimiento",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Long id = Long.parseLong(txtId.getText());
                boolean success = licenseController.resetPoints(id);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, "Puntos restablecidos a 20", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    spnPoints.setValue(20);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al restablecer puntos: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deductPoints() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay licencia seleccionada", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String input = JOptionPane.showInputDialog(this, 
            "Ingrese puntos a deducir (1-20):",
            "Deducir Puntos",
            JOptionPane.QUESTION_MESSAGE);
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                int points = Integer.parseInt(input.trim());
                if (points <= 0 || points > 20) {
                    JOptionPane.showMessageDialog(this, "Los puntos deben estar entre 1 y 20", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Long id = Long.parseLong(txtId.getText());
                int currentPoints = (Integer) spnPoints.getValue();
                
                if (points > currentPoints) {
                    JOptionPane.showMessageDialog(this, 
                        "No se pueden deducir " + points + " puntos. Solo hay " + currentPoints + " puntos disponibles", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "¿Deducir " + points + " puntos? Nuevo total: " + (currentPoints - points),
                    "Confirmar Deducción",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = licenseController.deductPoints(id, points);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(this, points + " puntos deducidos exitosamente", 
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        spnPoints.setValue(currentPoints - points);
                    }
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Ingrese un número válido", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al deducir puntos: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void autoSetExpiryDate() {
        if (!txtIssueDate.getText().trim().isEmpty() && txtExpiryDate.getText().trim().isEmpty()) {
            try {
                java.time.LocalDate issueDate = java.time.LocalDate.parse(txtIssueDate.getText().trim());
                java.time.LocalDate expiryDate = issueDate.plusYears(10);
                txtExpiryDate.setText(expiryDate.toString());
            } catch (Exception e) {
                // Ignore parsing errors, validation will catch them
            }
        }
    }
    
    private boolean validateFields() {
        // Validate required fields
        if (txtDriverId.getText().trim().isEmpty() || 
            txtIssueDate.getText().trim().isEmpty() ||
            txtExpiryDate.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                "Los campos ID Conductor, Fecha Emisión y Fecha Vencimiento son obligatorios", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate driver ID is a number
        try {
            Long.parseLong(txtDriverId.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "El ID Conductor debe ser un número válido", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate date formats
        if (!txtIssueDate.getText().trim().matches("\\d{4}-\\d{2}-\\d{2}") ||
            !txtExpiryDate.getText().trim().matches("\\d{4}-\\d{2}-\\d{2}")) {
            
            JOptionPane.showMessageDialog(this, 
                "Las fechas deben tener el formato YYYY-MM-DD", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate issue date is before expiry date
        try {
            java.time.LocalDate issueDate = java.time.LocalDate.parse(txtIssueDate.getText().trim());
            java.time.LocalDate expiryDate = java.time.LocalDate.parse(txtExpiryDate.getText().trim());
            
            if (!expiryDate.isAfter(issueDate)) {
                JOptionPane.showMessageDialog(this, 
                    "La fecha de vencimiento debe ser posterior a la fecha de emisión", 
                    "Error de Validación", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Validate license is not too long (max 20 years)
            long yearsBetween = java.time.temporal.ChronoUnit.YEARS.between(issueDate, expiryDate);
            if (yearsBetween > 20) {
                JOptionPane.showMessageDialog(this, 
                    "La licencia no puede tener más de 20 años de validez", 
                    "Error de Validación", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error en las fechas: " + e.getMessage(), 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public void newLicense() {
        txtId.setText("");
        txtDriverId.setText("");
        cmbLicenseType.setSelectedIndex(0);
        cmbCategory.setSelectedIndex(0);
        txtIssueDate.setText("");
        txtExpiryDate.setText("");
        spnPoints.setValue(20);
        txtRestrictions.setText("");
        chkRenewed.setSelected(false);
        btnDelete.setEnabled(false);
        btnRenew.setEnabled(false);
        btnResetPoints.setEnabled(false);
        btnDeductPoints.setEnabled(false);
    }
    
    public void loadLicense(Long id) {
        try {
            LicenseResponseDto licenseDto = licenseController.getLicenseResponseById(id);
            if (licenseDto != null) {
                txtId.setText(String.valueOf(licenseDto.id()));
                txtDriverId.setText(String.valueOf(licenseDto.driverId()));
                cmbLicenseType.setSelectedItem(licenseDto.licenseType());
                cmbCategory.setSelectedItem(licenseDto.category());
                txtIssueDate.setText(licenseDto.issueDate());
                txtExpiryDate.setText(licenseDto.expiryDate());
                spnPoints.setValue(licenseDto.points());
                txtRestrictions.setText(licenseDto.restrictions());
                chkRenewed.setSelected(licenseDto.renewed());
                
                // Enable action buttons
                btnDelete.setEnabled(true);
                btnRenew.setEnabled(true);
                btnResetPoints.setEnabled(true);
                btnDeductPoints.setEnabled(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar licencia: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }
    
    public void setOnDeleteCallback(Runnable callback) {
        this.onDeleteCallback = callback;
    }
}