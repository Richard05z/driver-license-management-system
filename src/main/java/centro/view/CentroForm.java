package centro.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import centro.controller.CentroController;
import centro.dto.CentroResponseDto;
import centro.model.Centro;

public class CentroForm extends JPanel {
    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtCode;
    private JTextField txtAddress;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtLogo;
    private JTextField txtGeneralDirector;
    private JTextField txtHRManager;
    private JTextField txtAccountingManager;
    private JTextField txtUnionSecretary;
    
    private JButton btnSave;
    private JButton btnCancel;
    private JButton btnDelete;
    
    private CentroController centroController;
    private Runnable onSaveCallback;
    private Runnable onDeleteCallback;
    
    public CentroForm(CentroController controller) {
        this.centroController = controller;
        initComponents();
        newCentro();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Panel de título
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Gestión de Centros");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titlePanel.add(titleLabel);
        
        add(titlePanel, BorderLayout.NORTH);
        
        // Panel de formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // ID (oculto)
        txtId = new JTextField();
        txtId.setVisible(false);
        
        // Campos del formulario - Columna izquierda
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre del Centro:"), gbc);
        gbc.gridx = 1;
        txtName = new JTextField(20);
        formPanel.add(txtName, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1;
        txtCode = new JTextField(20);
        formPanel.add(txtCode, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Dirección Postal:"), gbc);
        gbc.gridx = 1;
        txtAddress = new JTextField(20);
        formPanel.add(txtAddress, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        txtPhone = new JTextField(20);
        formPanel.add(txtPhone, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        formPanel.add(txtEmail, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("URL del Logo:"), gbc);
        gbc.gridx = 1;
        txtLogo = new JTextField(20);
        formPanel.add(txtLogo, gbc);
        
        // Campos del formulario - Columna derecha
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.insets = new Insets(5, 30, 5, 5); // Margen izquierdo mayor
        formPanel.add(new JLabel("Director General:"), gbc);
        gbc.gridx = 3;
        txtGeneralDirector = new JTextField(20);
        formPanel.add(txtGeneralDirector, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(new JLabel("Jefe de RRHH:"), gbc);
        gbc.gridx = 3;
        txtHRManager = new JTextField(20);
        formPanel.add(txtHRManager, gbc);
        
        gbc.gridx = 2; gbc.gridy = 2;
        formPanel.add(new JLabel("Jefe de Contabilidad:"), gbc);
        gbc.gridx = 3;
        txtAccountingManager = new JTextField(20);
        formPanel.add(txtAccountingManager, gbc);
        
        gbc.gridx = 2; gbc.gridy = 3;
        formPanel.add(new JLabel("Secretario del Sindicato:"), gbc);
        gbc.gridx = 3;
        txtUnionSecretary = new JTextField(20);
        formPanel.add(txtUnionSecretary, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        btnSave = new JButton("Guardar");
        btnSave.setBackground(new Color(76, 175, 80)); // Verde
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> saveCentro());
        
        btnCancel = new JButton("Cancelar");
        btnCancel.setBackground(new Color(158, 158, 158)); // Gris
        btnCancel.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> newCentro());
        
        btnDelete = new JButton("Eliminar");
        btnDelete.setBackground(new Color(244, 67, 54)); // Rojo
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteCentro());
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnDelete);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Validaciones
        txtCode.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetterOrDigit(c) && 
                    c != '-' && 
                    c != '_' && 
                    c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
        
        txtPhone.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && 
                    c != '+' && 
                    c != '(' && 
                    c != ')' && 
                    c != '-' &&
                    c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
    }
    
    public void saveCentro() {
        if (!validateFields()) {
            return;
        }
        
        try {
            Centro centro = new Centro(
                txtName.getText().trim(),
                txtCode.getText().trim(),
                txtAddress.getText().trim(),
                txtPhone.getText().trim(),
                txtEmail.getText().trim(),
                txtGeneralDirector.getText().trim(),
                txtHRManager.getText().trim(),
                txtAccountingManager.getText().trim(),
                txtUnionSecretary.getText().trim(),
                txtLogo.getText().trim()
            );
            
            // Si hay ID, es una actualización
            if (!txtId.getText().isEmpty()) {
                centro.setIdCentro(Long.parseLong(txtId.getText()));
                centroController.actualizarCentro(centro);
                JOptionPane.showMessageDialog(this, "Centro actualizado exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                centroController.anadirCentro(centro);
                JOptionPane.showMessageDialog(this, "Centro agregado exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            
            newCentro();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteCentro() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay centro seleccionado para eliminar", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar este centro?", 
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Long id = Long.parseLong(txtId.getText());
                centroController.eliminarCentro(id);
                
                JOptionPane.showMessageDialog(this, "Centro eliminado exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                if (onDeleteCallback != null) {
                    onDeleteCallback.run();
                }
                
                newCentro();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validateFields() {
        // Validar campos obligatorios
        if (txtName.getText().trim().isEmpty() || 
            txtCode.getText().trim().isEmpty() ||
            txtEmail.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                "Los campos Nombre, Código y Email son obligatorios", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validar formato de email
        if (!txtEmail.getText().trim().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, 
                "El email no tiene un formato válido", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validar longitud del código (mínimo 3 caracteres)
        if (txtCode.getText().trim().length() < 3) {
            JOptionPane.showMessageDialog(this, 
                "El código debe tener al menos 3 caracteres", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validar URL del logo si se proporciona
        if (!txtLogo.getText().trim().isEmpty() && 
            !txtLogo.getText().trim().matches("^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$")) {
            
            JOptionPane.showMessageDialog(this, 
                "La URL del logo no es válida", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public void newCentro() {
        txtId.setText("");
        txtName.setText("");
        txtCode.setText("");
        txtAddress.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtLogo.setText("");
        txtGeneralDirector.setText("");
        txtHRManager.setText("");
        txtAccountingManager.setText("");
        txtUnionSecretary.setText("");
        btnDelete.setEnabled(false);
    }
    
    public void loadCentro(Long id) {
    try {
        CentroResponseDto centroDto = centroController.obtenerCentroResponseDto(id);
        if (centroDto != null) {
            txtId.setText(String.valueOf(centroDto.idCentro()));
            txtName.setText(centroDto.nombre());
            txtCode.setText(centroDto.codigo());
            txtAddress.setText(centroDto.direccionPostal());
            txtPhone.setText(centroDto.telefono());
            txtEmail.setText(centroDto.email());
            txtLogo.setText(centroDto.logo());
            txtGeneralDirector.setText(centroDto.directorGeneral());
            txtHRManager.setText(centroDto.jefeRRHH());
            txtAccountingManager.setText(centroDto.jefeContabilidad());
            txtUnionSecretary.setText(centroDto.secretarioSindicato());
            btnDelete.setEnabled(true);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al cargar centro: " + e.getMessage(),
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