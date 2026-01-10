package examen.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import examen.controller.ExamController;
import examen.dto.ExamResponseDto;

public class ExamTable extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private ExamController examController;
    private ExamSelectionListener selectionListener;
    
    // Interface for selection events
    public interface ExamSelectionListener {
        void onExamSelected(Long examId);
    }
    
    public ExamTable(ExamController controller) {
        this.examController = controller;
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel searchLabel = new JLabel("Buscar:");
        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
        });
        
        JLabel filterLabel = new JLabel("Filtrar por:");
        filterCombo = new JComboBox<>(new String[]{"Todos", "medico", "teorico", "practico", "aprobado", "reprobado"});
        filterCombo.addActionListener(e -> filter());
        
        JButton clearButton = new JButton("Limpiar");
        clearButton.addActionListener(e -> {
            searchField.setText("");
            filterCombo.setSelectedIndex(0);
            loadData();
        });
        
        JButton refreshButton = new JButton("Actualizar");
        refreshButton.addActionListener(e -> loadData());
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(filterLabel);
        searchPanel.add(filterCombo);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(clearButton);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(refreshButton);
        
        add(searchPanel, BorderLayout.NORTH);
        
        // Table
        String[] columnNames = {"ID", "Tipo", "Fecha", "Resultado", "ID Entidad", "ID Conductor", "Examinador"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 4 || columnIndex == 5) return Long.class; // ID columns
                return String.class;
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(80);  // Tipo
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Fecha
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Resultado
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  // ID Entidad
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // ID Conductor
        table.getColumnModel().getColumn(6).setPreferredWidth(150); // Examinador
        
        // Renderer for exam result
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                        isSelected, hasFocus, row, column);
                
                if (value != null) {
                    String result = value.toString();
                    switch (result) {
                        case "aprobado":
                            c.setForeground(new Color(0, 128, 0)); // Green
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                            break;
                        case "reprobado":
                            c.setForeground(Color.RED);
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                            break;
                    }
                }
                
                return c;
            }
        });
        
        // Renderer for exam type
        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                        isSelected, hasFocus, row, column);
                
                if (value != null) {
                    String type = value.toString();
                    switch (type) {
                        case "medico":
                            c.setForeground(new Color(0, 100, 200)); // Blue
                            break;
                        case "teorico":
                            c.setForeground(new Color(150, 0, 200)); // Purple
                            break;
                        case "practico":
                            c.setForeground(new Color(200, 100, 0)); // Orange
                            break;
                    }
                }
                
                return c;
            }
        });
        
        // Add mouse listener for row selection
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && selectionListener != null) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        Long examId = (Long) tableModel.getValueAt(row, 0);
                        selectionListener.onExamSelected(examId);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel countLabel = new JLabel("Total: 0 exámenes");
        countLabel.setName("countLabel"); // Set name for easy access
        bottomPanel.add(countLabel);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    public void loadData() {
        SwingUtilities.invokeLater(() -> {
            try {
                List<ExamResponseDto> exams = examController.getAllExams();
                updateTable(exams);
                updateCounter(exams.size());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al cargar exámenes: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void search() {
        String criteria = searchField.getText().trim();
        if (criteria.isEmpty()) {
            loadData();
        } else {
            try {
                List<ExamResponseDto> allExams = examController.getAllExams();
                List<ExamResponseDto> results = allExams.stream()
                    .filter(exam -> 
                        exam.examType().toLowerCase().contains(criteria.toLowerCase()) ||
                        exam.result().toLowerCase().contains(criteria.toLowerCase()) ||
                        exam.examiner().toLowerCase().contains(criteria.toLowerCase()) ||
                        exam.date().contains(criteria) ||
                        String.valueOf(exam.entityId()).contains(criteria) ||
                        String.valueOf(exam.driverId()).contains(criteria))
                    .toList();
                updateTable(results);
                updateCounter(results.size());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al buscar: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void filter() {
        String filter = (String) filterCombo.getSelectedItem();
        if (filter.equals("Todos")) {
            loadData();
        } else {
            try {
                List<ExamResponseDto> allExams = examController.getAllExams();
                List<ExamResponseDto> results = allExams.stream()
                    .filter(exam -> exam.examType().equals(filter) || exam.result().equals(filter))
                    .toList();
                updateTable(results);
                updateCounter(results.size());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al filtrar: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateTable(List<ExamResponseDto> exams) {
        tableModel.setRowCount(0);
        
        for (ExamResponseDto exam : exams) {
            Object[] row = {
                exam.idExam(),
                exam.examType(),
                exam.date(),
                exam.result(),
                exam.entityId(),
                exam.driverId(),
                exam.examiner()
            };
            tableModel.addRow(row);
        }
    }
    
    private void updateCounter(int count) {
        Component[] comps = ((JPanel)getComponent(2)).getComponents();
        for (Component comp : comps) {
            if (comp instanceof JLabel && comp.getName() != null && comp.getName().equals("countLabel")) {
                ((JLabel) comp).setText("Total: " + count + " exámenes");
                break;
            }
        }
    }
    
    public JTable getTable() {
        return table;
    }
    
    public Long getSelectedExamId() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            return (Long) tableModel.getValueAt(row, 0);
        }
        return null;
    }
    
    public ExamResponseDto getSelectedExam() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            Long examId = (Long) tableModel.getValueAt(row, 0);
            try {
                return examController.getExamResponseById(examId);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al obtener examen: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
    
    public void setSelectionListener(ExamSelectionListener listener) {
        this.selectionListener = listener;
    }
    
    public void refresh() {
        loadData();
    }
}