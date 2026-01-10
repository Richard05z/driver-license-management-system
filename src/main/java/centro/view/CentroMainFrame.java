package centro.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import centro.controller.CentroController;
import centro.persistence.CentroDao;
import centro.repository.CentroRepository;
import centro.service.CentroService;

public class CentroMainFrame extends JPanel {
    private CentroController centroController;
    private CentroTable centroTable;
    private CentroForm centroForm;
    private JLabel statusLabel;
    
    public CentroMainFrame(CentroController controller) {
        this.centroController = controller;
        initComponents();
        setupListeners();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Main split panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(650);
        splitPane.setResizeWeight(0.5);
        
        // Left panel: Center table
        centroTable = new CentroTable(centroController);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(centroTable, BorderLayout.CENTER);
        
        // Right panel: Form
        centroForm = new CentroForm(centroController);
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(centroForm, BorderLayout.CENTER);
        
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
        centroForm.setOnSaveCallback(() -> {
            centroTable.refresh();
            updateStatus("Centro guardado exitosamente");
        });
        
        centroForm.setOnDeleteCallback(() -> {
            centroTable.refresh();
            updateStatus("Centro eliminado exitosamente");
        });
        
        // Configure table selection listener
        centroTable.setSelectionListener(centroId -> {
            if (centroId != null) {
                centroForm.loadCentro(centroId);
                updateStatus("Centro seleccionado ID: " + centroId);
            }
        });
    }
    
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton btnNew = new JButton("Nuevo Centro");
        btnNew.addActionListener(e -> {
            centroForm.newCentro();
            updateStatus("Listo para crear nuevo centro");
        });
        
        JButton btnRefresh = new JButton("Actualizar");
        btnRefresh.addActionListener(e -> {
            centroTable.refresh();
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
        statusLabel = new JLabel("Sistema de Gestión de Centros - Listo");
        statusPanel.add(statusLabel);
        return statusPanel;
    }
    
    private void setupListeners() {
        // Double click on table to edit
        centroTable.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Long id = centroTable.getSelectedCentroId();
                    if (id != null) {
                        centroForm.loadCentro(id);
                        updateStatus("Editando centro ID: " + id);
                    }
                }
            }
        });
        
        // Simple click to show in status bar
        centroTable.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Long id = centroTable.getSelectedCentroId();
                if (id != null) {
                    updateStatus("Centro seleccionado ID: " + id);
                }
            }
        });
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
    
    // Method to load data when panel becomes visible
    public void loadData() {
        centroTable.refresh();
    }
    
    // Method to clear form
    public void clearForm() {
        centroForm.newCentro();
    }
}