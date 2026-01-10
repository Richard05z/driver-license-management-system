package entidad.view;

import javax.swing.*;

import centro.persistence.CentroDaoImpl;
import centro.repository.CentroRepositoryImpl;
import centro.service.CentroServiceImpl;

import java.awt.*;
import java.awt.event.*;

import entidad.controller.EntidadController;
import entidad.persistence.EntidadDaoImpl;
import entidad.repository.EntidadRepositoryImpl;
import entidad.service.EntidadServiceImpl;

public class EntidadMainFrame extends JPanel {
    private EntidadController entidadController;
    private EntidadTable entidadTable;
    private EntidadForm entidadForm;
    private JLabel statusLabel;
    
    public EntidadMainFrame() {
        initComponents();
        setupListeners();
    }
    
    private void initComponents() {
        try {
            // Initialize the full chain: DAO → Repository → Service → Controller
            EntidadDaoImpl entidadDao = new EntidadDaoImpl();
            EntidadRepositoryImpl entidadRepository = new EntidadRepositoryImpl(entidadDao);

            CentroDaoImpl centroDao = new CentroDaoImpl();
            CentroRepositoryImpl centroRepository = new CentroRepositoryImpl(centroDao);
            CentroServiceImpl centroService = new CentroServiceImpl(centroRepository);

            EntidadServiceImpl entidadService = new EntidadServiceImpl(entidadRepository, centroService);
            entidadController = new EntidadController(entidadService);
            
            setLayout(new BorderLayout());
            
            // Main split panel
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setDividerLocation(600);
            splitPane.setResizeWeight(0.5);
            splitPane.setOneTouchExpandable(true);
            
            // Left panel: Entidad table
            entidadTable = new EntidadTable(entidadController);
            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.add(entidadTable, BorderLayout.CENTER);
            
            // Right panel: Form
            entidadForm = new EntidadForm(entidadController);
            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.add(entidadForm, BorderLayout.CENTER);
            
            splitPane.setLeftComponent(new JScrollPane(leftPanel));
            splitPane.setRightComponent(new JScrollPane(rightPanel));
            
            // Toolbar
            JToolBar toolBar = createToolBar();
            
            // Status bar
            JPanel statusPanel = createStatusBar();
            
            // Configure main layout
            add(toolBar, BorderLayout.NORTH);
            add(splitPane, BorderLayout.CENTER);
            add(statusPanel, BorderLayout.SOUTH);
            
            // Configure callbacks
            entidadForm.setOnSaveCallback(() -> {
                entidadTable.refresh();
                updateStatus("Entidad guardada exitosamente");
            });
            
            entidadForm.setOnDeleteCallback(() -> {
                entidadTable.refresh();
                updateStatus("Entidad eliminada exitosamente");
            });
            
            // Configure table selection listener
            entidadTable.setSelectionListener(entidadId -> {
                if (entidadId != null) {
                    updateStatus("Entidad seleccionada ID: " + entidadId);
                    
                    // Intenta cargar la entidad en el formulario
                    try {
                        // Si tienes un método en el controller para buscar por ID
                        // entidadForm.loadEntidad(entidadController.buscarEntidadPorId(entidadId));
                        
                        // Por ahora, muestra un mensaje informativo
                        JOptionPane.showMessageDialog(this,
                            "Para cargar una entidad existente, implementa el método buscarEntidadPorId en EntidadController",
                            "Información",
                            JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this,
                            "Error al cargar entidad: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al inicializar el módulo: " + e.getMessage(), 
                "Error de Inicialización", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createEtchedBorder());
        
        JButton btnNew = new JButton("Nueva Entidad");
        btnNew.addActionListener(e -> {
            entidadForm.newEntidad();
            updateStatus("Listo para crear nueva entidad");
        });
        
        JButton btnRefresh = new JButton("Actualizar");
        btnRefresh.addActionListener(e -> {
            entidadTable.refresh();
            updateStatus("Lista actualizada");
        });
        
        toolBar.add(btnNew);
        toolBar.addSeparator();
        toolBar.add(btnRefresh);
        
        return toolBar;
    }
    
    private JPanel createStatusBar() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.setBackground(new Color(240, 240, 240));
        statusLabel = new JLabel("Módulo de Entidades - Listo");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusPanel.add(statusLabel);
        
        return statusPanel;
    }
    
    private void setupListeners() {
        // Double click on table
        entidadTable.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Long id = entidadTable.getSelectedEntidadId();
                    if (id != null) {
                        updateStatus("Entidad seleccionada para edición ID: " + id);
                    }
                }
            }
        });
        
        // Simple click to show in status bar
        entidadTable.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Long id = entidadTable.getSelectedEntidadId();
                if (id != null) {
                    updateStatus("Entidad seleccionada ID: " + id);
                }
            }
        });
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(" " + message);
    }
    
    public void refreshData() {
        entidadTable.refresh();
    }
}