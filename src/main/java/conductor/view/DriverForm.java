package conductor.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import conductor.controller.DriverController;
import conductor.dto.DriverResponseDto;
import conductor.model.Conductor;

public class DriverForm extends JPanel {
    private JTextField txtId;
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtIdDocument;
    private JTextField txtBirthDate;
    private JTextField txtAddress;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JComboBox<String> cmbLicenseStatus;
    
    private JButton btnSave;
    private JButton btnCancel;
    private JButton btnDelete;
    
    private DriverController driverController;
    private Runnable onSaveCallback;
    private Runnable onDeleteCallback;
    
    public DriverForm(DriverController controller) {
        this.driverController = controller;
        initComponents();
        newDriver();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Gestión de Conductores");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titlePanel.add(titleLabel);
        
        add(titlePanel, BorderLayout.NORTH);
        
        // Form panel
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
        formPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtFirstName = new JTextField(20);
        formPanel.add(txtFirstName, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Apellidos:"), gbc);
        gbc.gridx = 1;
        txtLastName = new JTextField(20);
        formPanel.add(txtLastName, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Documento de Identidad:"), gbc);
        gbc.gridx = 1;
        txtIdDocument = new JTextField(20);
        formPanel.add(txtIdDocument, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Fecha de Nacimiento (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        txtBirthDate = new JTextField(20);
        formPanel.add(txtBirthDate, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1;
        txtAddress = new JTextField(20);
        formPanel.add(txtAddress, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        txtPhone = new JTextField(20);
        formPanel.add(txtPhone, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        formPanel.add(txtEmail, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Estado de Licencia:"), gbc);
        gbc.gridx = 1;
        cmbLicenseStatus = new JComboBox<>(new String[]{"vigente", "vencida", "suspendida", "revocada"});
        formPanel.add(cmbLicenseStatus, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        btnSave = new JButton("Guardar");
        btnSave.setBackground(new Color(76, 175, 80));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> saveDriver());
        
        btnCancel = new JButton("Cancelar");
        btnCancel.setBackground(new Color(158, 158, 158));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> newDriver());
        
        btnDelete = new JButton("Eliminar");
        btnDelete.setBackground(new Color(244, 67, 54));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteDriver());
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnDelete);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Validations
        txtIdDocument.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
    }
    
    public void saveDriver() {
        if (!validateFields()) {
            return;
        }
        
        try {
            Conductor conductor = new Conductor(
                txtFirstName.getText().trim(),
                txtLastName.getText().trim(),
                txtIdDocument.getText().trim(),
                txtBirthDate.getText().trim(),
                txtAddress.getText().trim(),
                txtPhone.getText().trim(),
                txtEmail.getText().trim(),
                (String) cmbLicenseStatus.getSelectedItem()
            );
            
            // If there's ID, it's an update
            if (!txtId.getText().isEmpty()) {
                conductor.setId(Integer.parseInt(txtId.getText()));
                driverController.updateDriver(conductor);
                JOptionPane.showMessageDialog(this, "Conductor actualizado exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                driverController.addDriver(conductor);
                JOptionPane.showMessageDialog(this, "Conductor agregado exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            
            newDriver();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteDriver() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay conductor seleccionado para eliminar", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar este conductor?", 
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(txtId.getText());
                driverController.deleteDriver(id);
                
                JOptionPane.showMessageDialog(this, "Conductor eliminado exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                if (onDeleteCallback != null) {
                    onDeleteCallback.run();
                }
                
                newDriver();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validateFields() {
        // Validate required fields
        if (txtFirstName.getText().trim().isEmpty() || 
            txtLastName.getText().trim().isEmpty() ||
            txtIdDocument.getText().trim().isEmpty() ||
            txtBirthDate.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                "Los campos Nombre, Apellidos, Documento de Identidad y Fecha de Nacimiento son obligatorios", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate email format
        if (!txtEmail.getText().trim().isEmpty() && 
            !txtEmail.getText().trim().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            
            JOptionPane.showMessageDialog(this, 
                "El email no tiene un formato válido", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate date format
        if (!txtBirthDate.getText().trim().matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, 
                "La fecha debe tener el formato YYYY-MM-DD", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public void newDriver() {
        txtId.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtIdDocument.setText("");
        txtBirthDate.setText("");
        txtAddress.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        cmbLicenseStatus.setSelectedItem("vigente");
        btnDelete.setEnabled(false);
    }
    
    public void loadDriver(int id) {
        try {
            DriverResponseDto driverDto = driverController.getDriverResponseById(id);
            if (driverDto != null) {
                txtId.setText(String.valueOf(driverDto.id()));
                txtFirstName.setText(driverDto.firstName());
                txtLastName.setText(driverDto.lastName());
                txtIdDocument.setText(driverDto.idDocument());
                txtBirthDate.setText(driverDto.birthDate());
                txtAddress.setText(driverDto.address());
                txtPhone.setText(driverDto.phone());
                txtEmail.setText(driverDto.email());
                cmbLicenseStatus.setSelectedItem(driverDto.licenseStatus());
                btnDelete.setEnabled(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar conductor: " + e.getMessage(),
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