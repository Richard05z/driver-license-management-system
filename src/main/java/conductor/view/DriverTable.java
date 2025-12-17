package conductor.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import conductor.controller.DriverController;
import conductor.dto.DriverResponseDto;

public class DriverTable extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private DriverController driverController;
    private DriverSelectionListener selectionListener;
    
    // Interface for selection events
    public interface DriverSelectionListener {
        void onDriverSelected(Integer driverId);
    }
    
    public DriverTable(DriverController controller) {
        this.driverController = controller;
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
        filterCombo = new JComboBox<>(new String[]{"Todos", "vigente", "vencida", "suspendida", "revocada"});
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
        String[] columnNames = {"ID", "Nombre", "Apellidos", "CI", "Teléfono", "Email", "Estado Licencia"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class; // ID column
                return String.class;
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // Nombre
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Apellidos
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // CI
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Teléfono
        table.getColumnModel().getColumn(5).setPreferredWidth(150); // Email
        table.getColumnModel().getColumn(6).setPreferredWidth(100); // Estado
        
        // Renderer for license status
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                        isSelected, hasFocus, row, column);
                
                if (value != null) {
                    String status = value.toString();
                    switch (status) {
                        case "vigente":
                            c.setForeground(new Color(0, 128, 0)); // Verde
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                            break;
                        case "vencida":
                            c.setForeground(Color.RED);
                            break;
                        case "suspendida":
                            c.setForeground(Color.ORANGE);
                            break;
                        case "revocada":
                            c.setForeground(Color.DARK_GRAY);
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
                        Integer driverId = (Integer) tableModel.getValueAt(row, 0);
                        selectionListener.onDriverSelected(driverId);
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
        JLabel countLabel = new JLabel("Total: 0 conductores");
        countLabel.setName("countLabel"); // Set name for easy access
        bottomPanel.add(countLabel);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    public void loadData() {
        SwingUtilities.invokeLater(() -> {
            try {
                List<DriverResponseDto> drivers = driverController.getAllDrivers();
                updateTable(drivers);
                updateCounter(drivers.size());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al cargar conductores: " + e.getMessage(), 
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
                List<DriverResponseDto> allDrivers = driverController.getAllDrivers();
                List<DriverResponseDto> results = allDrivers.stream()
                    .filter(driver -> 
                        driver.firstName().toLowerCase().contains(criteria.toLowerCase()) ||
                        driver.lastName().toLowerCase().contains(criteria.toLowerCase()) ||
                        driver.idDocument().contains(criteria) ||
                        driver.email().toLowerCase().contains(criteria.toLowerCase()))
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
                List<DriverResponseDto> allDrivers = driverController.getAllDrivers();
                List<DriverResponseDto> results = allDrivers.stream()
                    .filter(driver -> driver.licenseStatus().equals(filter))
                    .toList();
                updateTable(results);
                updateCounter(results.size());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al filtrar: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateTable(List<DriverResponseDto> drivers) {
        tableModel.setRowCount(0);
        
        for (DriverResponseDto driver : drivers) {
            Object[] row = {
                driver.id(),
                driver.firstName(),
                driver.lastName(),
                driver.idDocument(),
                driver.phone(),
                driver.email(),
                driver.licenseStatus()
            };
            tableModel.addRow(row);
        }
    }
    
    private void updateCounter(int count) {
        Component[] comps = ((JPanel)getComponent(2)).getComponents();
        for (Component comp : comps) {
            if (comp instanceof JLabel && comp.getName() != null && comp.getName().equals("countLabel")) {
                ((JLabel) comp).setText("Total: " + count + " conductores");
                break;
            }
        }
    }
    
    public JTable getTable() {
        return table;
    }
    
    public Integer getSelectedDriverId() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            return (Integer) tableModel.getValueAt(row, 0);
        }
        return null;
    }
    
    public DriverResponseDto getSelectedDriver() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            Integer driverId = (Integer) tableModel.getValueAt(row, 0);
            try {
                return driverController.getDriverResponseById(driverId);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al obtener conductor: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
    
    public void setSelectionListener(DriverSelectionListener listener) {
        this.selectionListener = listener;
    }
    
    public void refresh() {
        loadData();
    }
}