package examen.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import examen.controller.ExamController;
import examen.dto.ExamResponseDto;
import examen.model.Exam;

public class ExamForm extends JPanel {
    private JTextField txtId;
    private JComboBox<String> cmbExamType;
    private JTextField txtDate;
    private JComboBox<String> cmbResult;
    private JTextField txtEntityId;
    private JTextField txtDriverId;
    private JTextField txtExaminer;
    
    private JButton btnSave;
    private JButton btnCancel;
    private JButton btnDelete;
    
    private ExamController examController;
    private Runnable onSaveCallback;
    private Runnable onDeleteCallback;
    
    public ExamForm(ExamController controller) {
        this.examController = controller;
        initComponents();
        newExam();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Gestión de Exámenes");
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
        formPanel.add(new JLabel("Tipo de Examen:"), gbc);
        gbc.gridx = 1;
        cmbExamType = new JComboBox<>(new String[]{"medico", "teorico", "practico"});
        formPanel.add(cmbExamType, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Fecha (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        txtDate = new JTextField(20);
        formPanel.add(txtDate, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Resultado:"), gbc);
        gbc.gridx = 1;
        cmbResult = new JComboBox<>(new String[]{"aprobado", "reprobado"});
        formPanel.add(cmbResult, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("ID de Entidad:"), gbc);
        gbc.gridx = 1;
        txtEntityId = new JTextField(20);
        formPanel.add(txtEntityId, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("ID del Conductor:"), gbc);
        gbc.gridx = 1;
        txtDriverId = new JTextField(20);
        formPanel.add(txtDriverId, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Examinador:"), gbc);
        gbc.gridx = 1;
        txtExaminer = new JTextField(20);
        formPanel.add(txtExaminer, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        btnSave = new JButton("Guardar");
        btnSave.setBackground(new Color(76, 175, 80));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> saveExam());
        
        btnCancel = new JButton("Cancelar");
        btnCancel.setBackground(new Color(158, 158, 158));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.addActionListener(e -> newExam());
        
        btnDelete = new JButton("Eliminar");
        btnDelete.setBackground(new Color(244, 67, 54));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteExam());
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnDelete);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Validations
        txtEntityId.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
        
        txtDriverId.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
        
        // Date format validation
        txtDate.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String currentText = txtDate.getText();
                
                // Only allow digits and hyphens
                if (!Character.isDigit(c) && c != '-' && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                    return;
                }
                
                // Auto-format date as YYYY-MM-DD
                if (Character.isDigit(c)) {
                    int len = currentText.length();
                    
                    // Auto-insert hyphens
                    if (len == 4 || len == 7) {
                        txtDate.setText(currentText + "-" + c);
                        e.consume();
                    }
                    
                    // Limit length
                    if (len >= 10) {
                        e.consume();
                    }
                }
            }
        });
    }
    
    public void saveExam() {
        if (!validateFields()) {
            return;
        }
        
        try {
            Exam exam = new Exam(
                (String) cmbExamType.getSelectedItem(),
                txtDate.getText().trim(),
                (String) cmbResult.getSelectedItem(),
                Long.parseLong(txtEntityId.getText().trim()),
                Long.parseLong(txtDriverId.getText().trim()),
                txtExaminer.getText().trim()
            );
            
            // If there's ID, it's an update
            if (!txtId.getText().isEmpty()) {
                exam.setIdExam(Long.parseLong(txtId.getText()));
                examController.updateExam(exam);
                JOptionPane.showMessageDialog(this, "Examen actualizado exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                examController.addExam(exam);
                JOptionPane.showMessageDialog(this, "Examen agregado exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            
            newExam();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteExam() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay examen seleccionado para eliminar", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar este examen?", 
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Long id = Long.parseLong(txtId.getText());
                examController.deleteExam(id);
                
                JOptionPane.showMessageDialog(this, "Examen eliminado exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                if (onDeleteCallback != null) {
                    onDeleteCallback.run();
                }
                
                newExam();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validateFields() {
        // Validate required fields
        if (txtDate.getText().trim().isEmpty() ||
            txtEntityId.getText().trim().isEmpty() ||
            txtDriverId.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                "Los campos Fecha, ID de Entidad y ID del Conductor son obligatorios", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate date format
        if (!txtDate.getText().trim().matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, 
                "La fecha debe tener el formato YYYY-MM-DD", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate numeric IDs
        try {
            Long entityId = Long.parseLong(txtEntityId.getText().trim());
            if (entityId <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "El ID de entidad debe ser un número positivo", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            Long driverId = Long.parseLong(txtDriverId.getText().trim());
            if (driverId <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "El ID del conductor debe ser un número positivo", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate examiner name if provided
        if (!txtExaminer.getText().trim().isEmpty() && 
            txtExaminer.getText().trim().length() > 150) {
            
            JOptionPane.showMessageDialog(this, 
                "El nombre del examinador no puede exceder 150 caracteres", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public void newExam() {
        txtId.setText("");
        cmbExamType.setSelectedItem("medico");
        txtDate.setText("");
        cmbResult.setSelectedItem("aprobado");
        txtEntityId.setText("");
        txtDriverId.setText("");
        txtExaminer.setText("");
        btnDelete.setEnabled(false);
    }
    
    public void loadExam(Long id) {
        try {
            ExamResponseDto examDto = examController.getExamResponseById(id);
            if (examDto != null) {
                txtId.setText(String.valueOf(examDto.idExam()));
                cmbExamType.setSelectedItem(examDto.examType());
                txtDate.setText(examDto.date());
                cmbResult.setSelectedItem(examDto.result());
                txtEntityId.setText(String.valueOf(examDto.entityId()));
                txtDriverId.setText(String.valueOf(examDto.driverId()));
                txtExaminer.setText(examDto.examiner());
                btnDelete.setEnabled(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar examen: " + e.getMessage(),
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