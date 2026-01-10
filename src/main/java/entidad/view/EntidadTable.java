package entidad.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import entidad.controller.EntidadController;
import entidad.model.Entidad;

public class EntidadTable extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterCombo;
    private EntidadController entidadController;
    private EntidadSelectionListener selectionListener;
    
    public interface EntidadSelectionListener {
        void onEntidadSelected(Long entidadId);
    }
    
    public EntidadTable(EntidadController controller) {
        this.entidadController = controller;
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel searchLabel = new JLabel("Buscar:");
        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
        });
        
        JLabel filterLabel = new JLabel("Filtrar por Tipo:");
        filterCombo = new JComboBox<>(new String[]{"Todos", "clinica", "autoescuela"});
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
        
        // Columnas SIN ID
        String[] columnNames = {"Nombre", "Tipo", "Dirección", "Teléfono", "Email", "Director", "Centro ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6) return Long.class; // Solo Centro ID es Long
                return String.class;
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        
        // Ajustar ancho de columnas (SIN columna ID)
        table.getColumnModel().getColumn(0).setPreferredWidth(150);  // Nombre
        table.getColumnModel().getColumn(1).setPreferredWidth(80);   // Tipo
        table.getColumnModel().getColumn(2).setPreferredWidth(200);  // Dirección
        table.getColumnModel().getColumn(3).setPreferredWidth(100);  // Teléfono
        table.getColumnModel().getColumn(4).setPreferredWidth(150);  // Email
        table.getColumnModel().getColumn(5).setPreferredWidth(120);  // Director
        table.getColumnModel().getColumn(6).setPreferredWidth(80);   // Centro ID
        
        // Renderer para columna de Tipo
        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                        isSelected, hasFocus, row, column);
                
                if (value != null) {
                    String tipo = value.toString();
                    if (tipo.equals("clinica")) {
                        c.setForeground(new Color(0, 100, 0)); // Verde oscuro
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else if (tipo.equals("autoescuela")) {
                        c.setForeground(new Color(0, 0, 150)); // Azul
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    }
                }
                
                return c;
            }
        });
        
        // Mapa para guardar el ID de cada fila
        table.putClientProperty("idMap", new java.util.HashMap<Integer, Long>());
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && selectionListener != null) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        // Obtener el ID del mapa
                        java.util.Map<Integer, Long> idMap = (java.util.Map<Integer, Long>) table.getClientProperty("idMap");
                        Long entidadId = idMap.get(row);
                        if (entidadId != null) {
                            selectionListener.onEntidadSelected(entidadId);
                        }
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel countLabel = new JLabel("Total: 0 entidades");
        countLabel.setName("countLabel");
        bottomPanel.add(countLabel);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    public void loadData() {
        SwingUtilities.invokeLater(() -> {
            try {
                List<Entidad> entidades = entidadController.obtenerEntidades();
                updateTable(entidades);
                updateCounter(entidades.size());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al cargar entidades: " + e.getMessage(), 
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
                List<Entidad> allEntidades = entidadController.obtenerEntidades();
                List<Entidad> results = allEntidades.stream()
                    .filter(entidad -> 
                        entidad.getNombre().toLowerCase().contains(criteria.toLowerCase()) ||
                        entidad.getDireccion().toLowerCase().contains(criteria.toLowerCase()) ||
                        entidad.getEmail().toLowerCase().contains(criteria.toLowerCase()) ||
                        entidad.getDirectorGeneral().toLowerCase().contains(criteria.toLowerCase()))
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
                List<Entidad> allEntidades = entidadController.obtenerEntidades();
                List<Entidad> results = allEntidades.stream()
                    .filter(entidad -> entidad.getTipoEntidad().name().toLowerCase().equals(filter))
                    .toList();
                updateTable(results);
                updateCounter(results.size());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al filtrar: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateTable(List<Entidad> entidades) {
        tableModel.setRowCount(0);
        
        // Limpiar y crear nuevo mapa de IDs
        java.util.Map<Integer, Long> idMap = new java.util.HashMap<>();
        table.putClientProperty("idMap", idMap);
        
        for (int i = 0; i < entidades.size(); i++) {
            Entidad entidad = entidades.get(i);
            
            // Guardar el ID en el mapa usando el índice de la fila
            idMap.put(i, entidad.getIdEntidad());
            
            // Agregar fila SIN el ID
            Object[] row = {
                entidad.getNombre(),
                entidad.getTipoEntidad().name().toLowerCase(),
                entidad.getDireccion(),
                entidad.getTelefono(),
                entidad.getEmail(),
                entidad.getDirectorGeneral(),
                entidad.getCentro() != null ? entidad.getCentro().getIdCentro() : ""
            };
            tableModel.addRow(row);
        }
    }
    
    private void updateCounter(int count) {
        Component[] comps = getComponents();
        for (Component comp : comps) {
            if (comp instanceof JPanel) {
                Component[] subComps = ((JPanel) comp).getComponents();
                for (Component subComp : subComps) {
                    if (subComp instanceof JLabel && subComp.getName() != null && subComp.getName().equals("countLabel")) {
                        ((JLabel) subComp).setText("Total: " + count + " entidades");
                        return;
                    }
                }
            }
        }
    }
    
    public JTable getTable() {
        return table;
    }
    
    public Long getSelectedEntidadId() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            java.util.Map<Integer, Long> idMap = (java.util.Map<Integer, Long>) table.getClientProperty("idMap");
            return idMap.get(row);
        }
        return null;
    }
    
    public void setSelectionListener(EntidadSelectionListener listener) {
        this.selectionListener = listener;
    }
    
    public void refresh() {
        loadData();
    }
}