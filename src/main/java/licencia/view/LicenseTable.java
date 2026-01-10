package licencia.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import licencia.controller.LicenseController;
import licencia.dto.LicenseResponseDto;

public class LicenseTable extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private JComboBox<String> typeFilterCombo;
    private JComboBox<String> statusFilterCombo;
    private LicenseController licenseController;
    private LicenseSelectionListener selectionListener;
    
    // Constants
    private static final String[] FILTER_OPTIONS = {"Todas", "Activas", "Vencidas", "Próximas a Vencer", "Renovadas"};
    private static final String[] TYPE_FILTER_OPTIONS = {"Todos", "A", "B", "C", "D", "E", "F"};
    private static final String[] STATUS_FILTER_OPTIONS = {"Todos", "vigente", "vencida", "suspendida", "revocada"};
    
    // Interface for selection events
    public interface LicenseSelectionListener {
        void onLicenseSelected(Long licenseId);
    }
    
    public LicenseTable(LicenseController controller) {
        this.licenseController = controller;
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Search and filter panel
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Search
        gbc.gridx = 0; gbc.gridy = 0;
        filterPanel.add(new JLabel("Buscar:"), gbc);
        gbc.gridx = 1;
        searchField = new JTextField(15);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
        });
        filterPanel.add(searchField, gbc);
        
        // Type filter
        gbc.gridx = 2; gbc.gridy = 0;
        filterPanel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 3;
        typeFilterCombo = new JComboBox<>(TYPE_FILTER_OPTIONS);
        typeFilterCombo.addActionListener(e -> filter());
        filterPanel.add(typeFilterCombo, gbc);
        
        // Status filter
        gbc.gridx = 4; gbc.gridy = 0;
        filterPanel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 5;
        statusFilterCombo = new JComboBox<>(STATUS_FILTER_OPTIONS);
        statusFilterCombo.addActionListener(e -> filter());
        filterPanel.add(statusFilterCombo, gbc);
        
        // General filter
        gbc.gridx = 0; gbc.gridy = 1;
        filterPanel.add(new JLabel("Filtrar por:"), gbc);
        gbc.gridx = 1;
        filterCombo = new JComboBox<>(FILTER_OPTIONS);
        filterCombo.addActionListener(e -> filter());
        filterPanel.add(filterCombo, gbc);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        gbc.gridx = 2; gbc.gridy = 1;
        gbc.gridwidth = 4;
        filterPanel.add(buttonPanel, gbc);
        
        JButton clearButton = new JButton("Limpiar");
        clearButton.addActionListener(e -> {
            searchField.setText("");
            filterCombo.setSelectedIndex(0);
            typeFilterCombo.setSelectedIndex(0);
            statusFilterCombo.setSelectedIndex(0);
            loadData();
        });
        
        JButton refreshButton = new JButton("Actualizar");
        refreshButton.addActionListener(e -> loadData());
        
        buttonPanel.add(clearButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(refreshButton);
        
        add(filterPanel, BorderLayout.NORTH);
        
        // Table
        String[] columnNames = {"ID", "ID Conductor", "Tipo", "Categoría", "Emisión", "Vencimiento", 
                               "Puntos", "Renovada", "Estado", "Días Restantes"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Long.class;    // ID
                    case 1: return Long.class;    // ID Conductor
                    case 5: return Integer.class; // Puntos
                    case 6: return Boolean.class; // Renovada
                    case 9: return Integer.class; // Días Restantes
                    default: return String.class;
                }
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(80);  // ID Conductor
        table.getColumnModel().getColumn(2).setPreferredWidth(50);  // Tipo
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Categoría
        table.getColumnModel().getColumn(4).setPreferredWidth(90);  // Emisión
        table.getColumnModel().getColumn(5).setPreferredWidth(90);  // Vencimiento
        table.getColumnModel().getColumn(6).setPreferredWidth(60);  // Puntos
        table.getColumnModel().getColumn(7).setPreferredWidth(70);  // Renovada
        table.getColumnModel().getColumn(8).setPreferredWidth(80);  // Estado
        table.getColumnModel().getColumn(9).setPreferredWidth(100); // Días Restantes
        
        // Custom renderers
        table.getColumnModel().getColumn(6).setCellRenderer(new PointsRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new BooleanRenderer());
        table.getColumnModel().getColumn(8).setCellRenderer(new StatusRenderer());
        table.getColumnModel().getColumn(9).setCellRenderer(new DaysRemainingRenderer());
        
        // Add mouse listener for row selection
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && selectionListener != null) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        Long licenseId = (Long) tableModel.getValueAt(row, 0);
                        selectionListener.onLicenseSelected(licenseId);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with statistics
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel countLabel = new JLabel("Total: 0 licencias");
        countLabel.setName("countLabel");
        
        JLabel activeLabel = new JLabel("Activas: 0");
        activeLabel.setName("activeLabel");
        
        JLabel expiredLabel = new JLabel("Vencidas: 0");
        expiredLabel.setName("expiredLabel");
        
        JLabel pointsLabel = new JLabel("Puntos Promedio: 0.0");
        pointsLabel.setName("pointsLabel");
        
        bottomPanel.add(countLabel);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(activeLabel);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(expiredLabel);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(pointsLabel);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    public void loadData() {
        SwingUtilities.invokeLater(() -> {
            try {
                List<LicenseResponseDto> licenses = licenseController.getAllLicenses();
                updateTable(licenses);
                updateStatistics(licenses);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al cargar licencias: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void search() {
        String criteria = searchField.getText().trim();
        if (criteria.isEmpty()) {
            filter();
        } else {
            try {
                List<LicenseResponseDto> allLicenses = licenseController.getAllLicenses();
                List<LicenseResponseDto> results = allLicenses.stream()
                    .filter(license -> 
                        String.valueOf(license.driverId()).contains(criteria) ||
                        license.licenseType().toLowerCase().contains(criteria.toLowerCase()) ||
                        license.category().toLowerCase().contains(criteria.toLowerCase()) ||
                        (license.restrictions() != null && 
                         license.restrictions().toLowerCase().contains(criteria.toLowerCase())))
                    .toList();
                updateTable(results);
                updateStatistics(results);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al buscar: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void filter() {
        String filter = (String) filterCombo.getSelectedItem();
        String typeFilter = (String) typeFilterCombo.getSelectedItem();
        String statusFilter = (String) statusFilterCombo.getSelectedItem();
        
        try {
            List<LicenseResponseDto> allLicenses = licenseController.getAllLicenses();
            List<LicenseResponseDto> results = allLicenses.stream()
                .filter(license -> applyFilters(license, filter, typeFilter, statusFilter))
                .toList();
            updateTable(results);
            updateStatistics(results);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al filtrar: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean applyFilters(LicenseResponseDto license, String filter, String typeFilter, String statusFilter) {
        // Apply type filter
        if (!typeFilter.equals("Todos") && !license.licenseType().equals(typeFilter)) {
            return false;
        }
        
        // Apply status filter (requires calculating current status)
        if (!statusFilter.equals("Todos")) {
            String currentStatus = calculateLicenseStatus(license);
            if (!currentStatus.equals(statusFilter)) {
                return false;
            }
        }
        
        // Apply general filter
        switch (filter) {
            case "Todas":
                return true;
            case "Activas":
                return isLicenseActive(license);
            case "Vencidas":
                return isLicenseExpired(license);
            case "Próximas a Vencer":
                return isLicenseExpiringSoon(license);
            case "Renovadas":
                return license.renewed();
            default:
                return true;
        }
    }
    
    private String calculateLicenseStatus(LicenseResponseDto license) {
        if (isLicenseExpired(license)) {
            return "vencida";
        } else if (license.renewed()) {
            return "renovada";
        } else {
            return "vigente";
        }
    }
    
    private boolean isLicenseActive(LicenseResponseDto license) {
        try {
            LocalDate expiryDate = LocalDate.parse(license.expiryDate());
            return expiryDate.isAfter(LocalDate.now()) || expiryDate.isEqual(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean isLicenseExpired(LicenseResponseDto license) {
        try {
            LocalDate expiryDate = LocalDate.parse(license.expiryDate());
            return expiryDate.isBefore(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean isLicenseExpiringSoon(LicenseResponseDto license) {
        try {
            LocalDate expiryDate = LocalDate.parse(license.expiryDate());
            LocalDate now = LocalDate.now();
            long daysRemaining = ChronoUnit.DAYS.between(now, expiryDate);
            return daysRemaining > 0 && daysRemaining <= 30; // Within 30 days
        } catch (Exception e) {
            return false;
        }
    }
    
    private void updateTable(List<LicenseResponseDto> licenses) {
        tableModel.setRowCount(0);
        
        for (LicenseResponseDto license : licenses) {
            int daysRemaining = calculateDaysRemaining(license);
            String status = calculateLicenseStatus(license);
            
            Object[] row = {
                license.id(),
                license.driverId(),
                license.licenseType(),
                license.category(),
                license.issueDate(),
                license.expiryDate(),
                license.points(),
                license.renewed(),
                status,
                daysRemaining
            };
            tableModel.addRow(row);
        }
    }
    
    private int calculateDaysRemaining(LicenseResponseDto license) {
        try {
            LocalDate expiryDate = LocalDate.parse(license.expiryDate());
            LocalDate now = LocalDate.now();
            long days = ChronoUnit.DAYS.between(now, expiryDate);
            return (int) days;
        } catch (Exception e) {
            return -999; // Error indicator
        }
    }
    
    private void updateStatistics(List<LicenseResponseDto> licenses) {
        int total = licenses.size();
        long activeCount = licenses.stream().filter(this::isLicenseActive).count();
        long expiredCount = licenses.stream().filter(this::isLicenseExpired).count();
        double avgPoints = licenses.stream()
            .mapToInt(LicenseResponseDto::points)
            .average()
            .orElse(0.0);
        
        // Update labels
        Component[] comps = ((JPanel)getComponent(2)).getComponents();
        for (Component comp : comps) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                switch (label.getName()) {
                    case "countLabel":
                        label.setText("Total: " + total + " licencias");
                        break;
                    case "activeLabel":
                        label.setText("Activas: " + activeCount);
                        label.setForeground(activeCount > 0 ? new Color(0, 128, 0) : Color.BLACK);
                        break;
                    case "expiredLabel":
                        label.setText("Vencidas: " + expiredCount);
                        label.setForeground(expiredCount > 0 ? Color.RED : Color.BLACK);
                        break;
                    case "pointsLabel":
                        label.setText(String.format("Puntos Promedio: %.1f", avgPoints));
                        label.setForeground(avgPoints < 10 ? Color.RED : 
                                          avgPoints < 15 ? Color.ORANGE : 
                                          new Color(0, 128, 0));
                        break;
                }
            }
        }
    }
    
    // Custom cell renderers
    class PointsRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
            
            if (value instanceof Integer) {
                int points = (Integer) value;
                if (points <= 5) {
                    c.setForeground(Color.RED);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if (points <= 10) {
                    c.setForeground(Color.ORANGE);
                } else {
                    c.setForeground(new Color(0, 128, 0));
                }
            }
            
            return c;
        }
    }
    
    class BooleanRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
            
            if (value instanceof Boolean) {
                boolean renewed = (Boolean) value;
                if (renewed) {
                    c.setForeground(new Color(0, 128, 0));
                    setText("SÍ");
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else {
                    c.setForeground(Color.DARK_GRAY);
                    setText("NO");
                }
            }
            
            return c;
        }
    }
    
    class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
            
            if (value instanceof String) {
                String status = (String) value;
                switch (status) {
                    case "vigente":
                        c.setForeground(new Color(0, 128, 0));
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                        break;
                    case "vencida":
                        c.setForeground(Color.RED);
                        break;
                    case "renovada":
                        c.setForeground(new Color(33, 150, 243)); // Blue
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                        break;
                    case "suspendida":
                        c.setForeground(Color.ORANGE);
                        break;
                    default:
                        c.setForeground(Color.DARK_GRAY);
                }
            }
            
            return c;
        }
    }
    
    class DaysRemainingRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
            
            if (value instanceof Integer) {
                int days = (Integer) value;
                
                if (days == -999) {
                    setText("ERROR");
                    c.setForeground(Color.RED);
                } else if (days < 0) {
                    setText("Vencida (" + Math.abs(days) + " días)");
                    c.setForeground(Color.RED);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if (days == 0) {
                    setText("Hoy vence");
                    c.setForeground(Color.ORANGE);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if (days <= 7) {
                    setText(days + " días (URGENTE)");
                    c.setForeground(Color.RED);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if (days <= 30) {
                    setText(days + " días (Próximo)");
                    c.setForeground(Color.ORANGE);
                } else if (days <= 365) {
                    setText(days + " días");
                    c.setForeground(new Color(0, 128, 0));
                } else {
                    setText(days + " días");
                    c.setForeground(Color.BLUE);
                }
            }
            
            return c;
        }
    }
    
    public JTable getTable() {
        return table;
    }
    
    public Long getSelectedLicenseId() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            return (Long) tableModel.getValueAt(row, 0);
        }
        return null;
    }
    
    public LicenseResponseDto getSelectedLicense() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            Long licenseId = (Long) tableModel.getValueAt(row, 0);
            try {
                return licenseController.getLicenseResponseById(licenseId);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al obtener licencia: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
    
    public void setSelectionListener(LicenseSelectionListener listener) {
        this.selectionListener = listener;
    }
    
    public void refresh() {
        loadData();
    }
}