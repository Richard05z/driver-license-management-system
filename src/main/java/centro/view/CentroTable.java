package centro.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import centro.controller.CentroController;
import centro.dto.CentroResponseDto;

public class CentroTable extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private CentroController centroController;
    private CentroSelectionListener selectionListener;
    
    // Interface for selection events
    public interface CentroSelectionListener {
        void onCentroSelected(Long centroId);
    }
    
    public CentroTable(CentroController controller) {
        this.centroController = controller;
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
        
        JLabel filterLabel = new JLabel("Ordenar por:");
        filterCombo = new JComboBox<>(new String[]{"Código", "Nombre", "Email", "Director General"});
        filterCombo.addActionListener(e -> sortTable());
        
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
        String[] columnNames = {"ID", "Nombre", "Código", "Dirección", "Teléfono", "Email", "Director"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Long.class; // ID column
                return String.class;
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(33, 150, 243));
        table.getTableHeader().setForeground(Color.WHITE);
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Nombre
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Código
        table.getColumnModel().getColumn(3).setPreferredWidth(250); // Dirección
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Teléfono
        table.getColumnModel().getColumn(5).setPreferredWidth(200); // Email
        table.getColumnModel().getColumn(6).setPreferredWidth(150); // Director
        
        // Renderer for Email column
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                        isSelected, hasFocus, row, column);
                
                if (value != null && value.toString().contains("@")) {
                    c.setForeground(new Color(0, 100, 200)); // Blue for emails
                    c.setFont(c.getFont().deriveFont(Font.PLAIN));
                }
                
                return c;
            }
        });
        
        // Renderer for Phone column
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                        isSelected, hasFocus, row, column);
                
                if (value != null && !value.toString().isEmpty()) {
                    c.setForeground(new Color(0, 128, 0)); // Green for phones
                    c.setFont(c.getFont().deriveFont(Font.PLAIN));
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
                        Long centroId = (Long) tableModel.getValueAt(row, 0);
                        selectionListener.onCentroSelected(centroId);
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
        JLabel countLabel = new JLabel("Total: 0 centros");
        countLabel.setName("countLabel"); // Set name for easy access
        bottomPanel.add(countLabel);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    public void loadData() {
        SwingUtilities.invokeLater(() -> {
            try {
                List<CentroResponseDto> centros = centroController.obtenerCentros();
                updateTable(centros);
                updateCounter(centros.size());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al cargar centros: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
    
    private void search() {
        String criteria = searchField.getText().trim();
        if (criteria.isEmpty()) {
            loadData();
        } else {
            try {
                List<CentroResponseDto> allCentros = centroController.obtenerCentros();
                List<CentroResponseDto> results = allCentros.stream()
                    .filter(centro -> 
                        centro.nombre().toLowerCase().contains(criteria.toLowerCase()) ||
                        centro.codigo().toLowerCase().contains(criteria.toLowerCase()) ||
                        centro.email().toLowerCase().contains(criteria.toLowerCase()) ||
                        (centro.direccionPostal() != null && 
                         centro.direccionPostal().toLowerCase().contains(criteria.toLowerCase())) ||
                        (centro.directorGeneral() != null && 
                         centro.directorGeneral().toLowerCase().contains(criteria.toLowerCase())))
                    .toList();
                updateTable(results);
                updateCounter(results.size());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al buscar: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void sortTable() {
        String sortBy = (String) filterCombo.getSelectedItem();
        int columnIndex = -1;
        
        switch (sortBy) {
            case "Código":
                columnIndex = 2;
                break;
            case "Nombre":
                columnIndex = 1;
                break;
            case "Email":
                columnIndex = 5;
                break;
            case "Director General":
                columnIndex = 6;
                break;
        }
        
        if (columnIndex != -1) {
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
            table.setRowSorter(sorter);
            List<RowSorter.SortKey> sortKeys = new java.util.ArrayList<>();
            sortKeys.add(new RowSorter.SortKey(columnIndex, SortOrder.ASCENDING));
            sorter.setSortKeys(sortKeys);
        }
    }
    
    private void updateTable(List<CentroResponseDto> centros) {
        tableModel.setRowCount(0);
        
        for (CentroResponseDto centro : centros) {
            Object[] row = {
                centro.idCentro(),  // ID
                centro.nombre(),    // Nombre
                centro.codigo(),    // Código
                centro.direccionPostal(), // Dirección
                centro.telefono(),  // Teléfono
                centro.email(),     // Email
                centro.directorGeneral() // Director
            };
            tableModel.addRow(row);
        }
        
        // Sort by name by default
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new java.util.ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING)); // Column Name
        sorter.setSortKeys(sortKeys);
    }
    
    private void updateCounter(int count) {
        Component[] comps = ((JPanel)getComponent(2)).getComponents();
        for (Component comp : comps) {
            if (comp instanceof JLabel && comp.getName() != null && comp.getName().equals("countLabel")) {
                ((JLabel) comp).setText("Total: " + count + " centros");
                break;
            }
        }
    }
    
    public JTable getTable() {
        return table;
    }
    
    public Long getSelectedCentroId() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            return (Long) tableModel.getValueAt(row, 0);
        }
        return null;
    }
    
    public CentroResponseDto getSelectedCentro() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            Long centroId = (Long) tableModel.getValueAt(row, 0);
            try {
                return centroController.obtenerCentroResponseDto(centroId);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al obtener centro: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
    
    public void setSelectionListener(CentroSelectionListener listener) {
        this.selectionListener = listener;
    }
    
    public void refresh() {
        loadData();
    }
    
    public void selectCentro(Long centroId) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Long id = (Long) tableModel.getValueAt(i, 0);
            if (id.equals(centroId)) {
                table.setRowSelectionInterval(i, i);
                table.scrollRectToVisible(table.getCellRect(i, 0, true));
                break;
            }
        }
    }
}