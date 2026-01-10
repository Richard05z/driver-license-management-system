package entidad.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import entidad.controller.EntidadController;
import entidad.dto.EntidadRequestDto;
import entidad.model.Entidad;
import entidad.vo.TipoEntidad;

public class EntidadForm extends JPanel {
    private JTextField txtId;
    private JTextField txtNombre;
    private JComboBox<String> cmbTipoEntidad;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private JTextField txtDirector;
    private JTextField txtIdCentro;
    
    private JButton btnSave;
    private JButton btnCancel;
    private JButton btnDelete;
    
    private EntidadController entidadController;
    private Runnable onSaveCallback;
    private Runnable onDeleteCallback;
    
    public EntidadForm(EntidadController controller) {
        this.entidadController = controller;
        initComponents();
        newEntidad();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Formulario de Entidad");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titlePanel.add(titleLabel);
        
        add(titlePanel, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        txtId = new JTextField();
        txtId.setVisible(false);
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(20);
        formPanel.add(txtNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Tipo de Entidad:"), gbc);
        gbc.gridx = 1;
        cmbTipoEntidad = new JComboBox<>(new String[]{"clinica", "autoescuela"});
        formPanel.add(cmbTipoEntidad, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1;
        txtDireccion = new JTextField(20);
        formPanel.add(txtDireccion, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        txtTelefono = new JTextField(20);
        formPanel.add(txtTelefono, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        formPanel.add(txtEmail, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Director:"), gbc);
        gbc.gridx = 1;
        txtDirector = new JTextField(20);
        formPanel.add(txtDirector, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("ID del Centro:"), gbc);
        gbc.gridx = 1;
        txtIdCentro = new JTextField(20);
        formPanel.add(txtIdCentro, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        btnSave = new JButton("Guardar");
        btnSave.setBackground(new Color(76, 175, 80));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> saveEntidad());
        
        btnCancel = new JButton("Cancelar");
        btnCancel.setBackground(new Color(158, 158, 158));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> newEntidad());
        
        btnDelete = new JButton("Eliminar");
        btnDelete.setBackground(new Color(244, 67, 54));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteEntidad());
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnDelete);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        txtIdCentro.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
    }
    
    public void saveEntidad() {
        if (!validateFields()) {
            return;
        }
        
        try {
            String tipoSeleccionado = (String) cmbTipoEntidad.getSelectedItem();
            TipoEntidad tipo = TipoEntidad.valueOf(tipoSeleccionado.toLowerCase());
            
            EntidadRequestDto requestDto = new EntidadRequestDto(
                txtNombre.getText().trim(),
                tipo,
                txtDireccion.getText().trim(),
                txtTelefono.getText().trim(),
                txtEmail.getText().trim(),
                txtDirector.getText().trim(),
                Long.parseLong(txtIdCentro.getText().trim())
            );
            
            entidadController.anadirEntidad(requestDto);
            JOptionPane.showMessageDialog(this, "Entidad agregada exitosamente", 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            
            newEntidad();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void deleteEntidad() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay entidad seleccionada para eliminar", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar esta entidad?", 
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Long id = Long.parseLong(txtId.getText());
                entidadController.eliminarEntidad(id);
                
                JOptionPane.showMessageDialog(this, "Entidad eliminada exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                if (onDeleteCallback != null) {
                    onDeleteCallback.run();
                }
                
                newEntidad();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validateFields() {
        if (txtNombre.getText().trim().isEmpty() || 
            txtDireccion.getText().trim().isEmpty() ||
            txtTelefono.getText().trim().isEmpty() ||
            txtEmail.getText().trim().isEmpty() ||
            txtDirector.getText().trim().isEmpty() ||
            txtIdCentro.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                "Todos los campos son obligatorios", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!txtEmail.getText().trim().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, 
                "El email no tiene un formato válido", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            Long.parseLong(txtIdCentro.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "El ID del Centro debe ser un número válido", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public void newEntidad() {
        txtId.setText("");
        txtNombre.setText("");
        cmbTipoEntidad.setSelectedItem("clinica");
        txtDireccion.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        txtDirector.setText("");
        txtIdCentro.setText("");
        btnDelete.setEnabled(false);
    }
    
    public void loadEntidad(Entidad entidad) {
        if (entidad != null) {
            txtId.setText(String.valueOf(entidad.getIdEntidad()));
            txtNombre.setText(entidad.getNombre());
            cmbTipoEntidad.setSelectedItem(entidad.getTipoEntidad().name().toLowerCase());
            txtDireccion.setText(entidad.getDireccion());
            txtTelefono.setText(entidad.getTelefono());
            txtEmail.setText(entidad.getEmail());
            txtDirector.setText(entidad.getDirectorGeneral());
            txtIdCentro.setText(entidad.getCentro() != null ? 
                String.valueOf(entidad.getCentro().getIdCentro()) : "");
            btnDelete.setEnabled(true);
        }
    }
    
    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }
    
    public void setOnDeleteCallback(Runnable callback) {
        this.onDeleteCallback = callback;
    }
}