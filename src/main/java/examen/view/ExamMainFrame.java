package examen.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import examen.controller.ExamController;
import examen.persistence.ExamDao;
import examen.repository.ExamRepository;
import examen.service.ExamService;

public class ExamMainFrame extends JFrame {
    private ExamController examController;
    private ExamTable examTable;
    private ExamForm examForm;
    private JLabel statusLabel;
    
    public ExamMainFrame() {
        initComponents();
        setupListeners();
        setupWindow();
    }
    
    private void initComponents() {
        try {
            // Initialize the full chain: DAO → Repository → Service → Controller
            ExamDao examDao = new ExamDao();
            ExamRepository examRepository = new ExamRepository(examDao);
            ExamService examService = new ExamService(examRepository);
            examController = new ExamController(examService);
            
            setTitle("Gestión de Exámenes - CRUD Completo");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // IMPORTANTE: No EXIT_ON_CLOSE
            
            // Main split panel
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setDividerLocation(600);
            splitPane.setResizeWeight(0.5);
            
            // Left panel: Exam table
            examTable = new ExamTable(examController);
            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.add(examTable, BorderLayout.CENTER);
            
            // Right panel: Form
            examForm = new ExamForm(examController);
            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.add(examForm, BorderLayout.CENTER);
            
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
            examForm.setOnSaveCallback(() -> {
                examTable.refresh();
                updateStatus("Examen guardado exitosamente");
            });
            
            examForm.setOnDeleteCallback(() -> {
                examTable.refresh();
                updateStatus("Examen eliminado exitosamente");
            });
            
            // Configure table selection listener
            examTable.setSelectionListener(examId -> {
                if (examId != null) {
                    examForm.loadExam(examId);
                    updateStatus("Examen seleccionado ID: " + examId);
                }
            });
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al inicializar la aplicación: " + e.getMessage(), 
                "Error de Inicialización", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            // No System.exit(1) aquí, solo mostrar error
        }
    }
    
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton btnNew = new JButton("Nuevo Examen");
        btnNew.addActionListener(e -> {
            examForm.newExam();
            updateStatus("Listo para crear nuevo examen");
        });
        
        JButton btnReport = new JButton("Generar Reporte");
        btnReport.addActionListener(e -> generateReport());
        
        JButton btnDelete = new JButton("Eliminar Seleccionado");
        btnDelete.addActionListener(e -> {
            Long id = examTable.getSelectedExamId();
            if (id != null) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "¿Está seguro de eliminar este examen?", 
                    "Confirmar Eliminación", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        examController.deleteExam(id);
                        examTable.refresh();
                        examForm.newExam();
                        updateStatus("Examen eliminado exitosamente");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Error al eliminar examen: " + ex.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Seleccione un examen de la lista primero", 
                    "Advertencia", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        toolBar.add(btnNew);
        toolBar.addSeparator();
        toolBar.add(btnReport);
        toolBar.addSeparator();
        toolBar.add(btnDelete);
        
        return toolBar;
    }
    
    private JPanel createStatusBar() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusLabel = new JLabel("Sistema de Gestión de Exámenes - Listo");
        statusPanel.add(statusLabel);
        return statusPanel;
    }
    
    private void setupListeners() {
        // Double click on table to edit
        if (examTable != null && examTable.getTable() != null) {
            examTable.getTable().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        Long id = examTable.getSelectedExamId();
                        if (id != null) {
                            examForm.loadExam(id);
                            updateStatus("Editando examen ID: " + id);
                        }
                    }
                }
            });
            
            // Simple click to show in status bar
            examTable.getTable().getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    Long id = examTable.getSelectedExamId();
                    if (id != null) {
                        updateStatus("Examen seleccionado ID: " + id);
                    }
                }
            });
        }
    }
    
    private void setupWindow() {
        setSize(1200, 700);
        setLocationRelativeTo(null); // Center on screen
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Add keyboard shortcuts (igual que DriverMainFrame)
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "new");
        getRootPane().getActionMap().put("new", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                examForm.newExam();
                updateStatus("Listo para crear nuevo examen");
            }
        });
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "save");
        getRootPane().getActionMap().put("save", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                examForm.saveExam();
            }
        });
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
        getRootPane().getActionMap().put("cancel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                examForm.newExam();
                updateStatus("Operación cancelada");
            }
        });
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refresh");
        getRootPane().getActionMap().put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                examTable.refresh();
                updateStatus("Lista actualizada");
            }
        });
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        getRootPane().getActionMap().put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Long id = examTable.getSelectedExamId();
                if (id != null) {
                    int confirm = JOptionPane.showConfirmDialog(ExamMainFrame.this, 
                        "¿Está seguro de eliminar este examen?", 
                        "Confirmar Eliminación", 
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            examController.deleteExam(id);
                            examTable.refresh();
                            examForm.newExam();
                            updateStatus("Examen eliminado exitosamente");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(ExamMainFrame.this, 
                                "Error al eliminar examen: " + ex.getMessage(), 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
        
        // Report shortcut
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), "report");
        getRootPane().getActionMap().put("report", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });
    }
    
    private void generateReport() {
        try {
            int totalExams = examController.getAllExams().size();
            int passedExams = examController.countExamsByResult("aprobado");
            int failedExams = examController.countExamsByResult("reprobado");
            
            String report = "REPORTE DE EXÁMENES\n" +
                          "===================\n\n" +
                          "Total de exámenes registrados: " + totalExams + "\n" +
                          "Exámenes aprobados: " + passedExams + "\n" +
                          "Exámenes reprobados: " + failedExams + "\n" +
                          "Tasa de aprobación: " + (totalExams > 0 ? 
                              String.format("%.2f%%", ((double) passedExams / totalExams * 100)) : "0%") + "\n\n" +
                          "Por tipo de examen:\n";
            
            // Add exam type breakdown
            String[] examTypes = {"medico", "teorico", "practico"};
            for (String type : examTypes) {
                int typeCount = examController.countExamsByType(type);
                report += "  - " + type + ": " + typeCount + "\n";
            }
            
            JTextArea textArea = new JTextArea(report);
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 250));
            
            JOptionPane.showMessageDialog(this, scrollPane, 
                "Reporte de Exámenes", JOptionPane.INFORMATION_MESSAGE);
            
            updateStatus("Reporte generado - Total exámenes: " + totalExams);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al generar reporte: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
    
    // Main method to launch standalone
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                UIManager.put("Button.font", new Font("SansSerif", Font.PLAIN, 12));
                UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 12));
                UIManager.put("TextField.font", new Font("SansSerif", Font.PLAIN, 12));
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            ExamMainFrame frame = new ExamMainFrame();
            frame.setVisible(true);
        });
    }
}