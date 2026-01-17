import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

// Importar módulos existentes
import centro.controller.CentroController;
import centro.persistence.CentroDaoImpl;
import centro.repository.CentroRepository;
import centro.repository.CentroRepositoryImpl;
import centro.service.CentroService;
import centro.service.CentroServiceImpl;
import centro.view.CentroMainFrame;
import entidad.view.EntidadMainFrame;
import examen.view.ExamMainFrame;
import licencia.view.LicenseMainFrame;
import conductor.view.DriverMainFrame;

public class MainApplicationFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // Services para compartir entre módulos
    private CentroServiceImpl centroService;
    
    // Referencias a las vistas
    private JPanel welcomePanel;
    private JPanel centerModulePanel;
    private JPanel entityModulePanel;
    private JPanel driverModulePanel;
    private JPanel licenseModulePanel;
    private JPanel examModulePanel;
    private JPanel violationModulePanel;
    private JPanel reportModulePanel;
    
    public MainApplicationFrame() {
        initializeComponents();
        configureFrame();
        initializeServices();
    }
    
    private void initializeComponents() {
        // Configure main frame
        setTitle("Sistema de Gestión de Licencias de Conducción");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create CardLayout for switching between modules
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create welcome panel
        welcomePanel = createWelcomePanel();
        mainPanel.add(welcomePanel, "WELCOME");
        
        // Add components to frame
        add(mainPanel, BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
        
        // Add menu bar with all modules
        setJMenuBar(createMenuBar());
    }
    
    private void configureFrame() {
        setSize(1280, 720);
        setLocationRelativeTo(null); // Center on screen
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Start maximized
    }
    
    private void initializeServices() {
        try {
            // Initialize shared services
            CentroDaoImpl centroDao = new CentroDaoImpl();
            CentroRepositoryImpl centroRepository = new CentroRepositoryImpl(centroDao);
            centroService = new CentroServiceImpl(centroRepository);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al inicializar servicios: " + e.getMessage(),
                "Error de Base de Datos",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 245, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        // Title
        JLabel titleLabel = new JLabel("Sistema de Gestión de Licencias de Conducción", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 82, 155));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Seleccione un módulo del menú superior para comenzar", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(subtitleLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.setBackground(new Color(240, 240, 240));
        
        // Status label on left
        JLabel statusLabel = new JLabel(" Bienvenido al Sistema de Gestión de Licencias");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        // System info on right
        JLabel systemInfo = new JLabel("Sistema de Licencias v1.0 © 2024 ");
        systemInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        statusBar.add(systemInfo, BorderLayout.EAST);
        
        return statusBar;
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = new JMenu("Archivo");
        
        fileMenu.addSeparator();
        
        // Exit item
        JMenuItem exitItem = new JMenuItem("Salir");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Centers Menu
        JMenu centersMenu = new JMenu("Centros");
        JMenuItem manageCentersItem = new JMenuItem("Gestionar Centros");
        manageCentersItem.addActionListener(e -> openCenterModule());
        centersMenu.add(manageCentersItem);
        
        // Entities Menu
        JMenu entitiesMenu = new JMenu("Entidades");
        JMenuItem manageEntitiesItem = new JMenuItem("Gestionar Entidades");
        manageEntitiesItem.addActionListener(e -> openEntityModule());
        entitiesMenu.add(manageEntitiesItem);
        
        // Drivers Menu
        JMenu driversMenu = new JMenu("Conductores");
        JMenuItem manageDriversItem = new JMenuItem("Gestionar Conductores");
        manageDriversItem.addActionListener(e -> openDriverModule());
        driversMenu.add(manageDriversItem);
        
        // Licenses Menu
        JMenu licensesMenu = new JMenu("Licencias");
        JMenuItem manageLicensesItem = new JMenuItem("Gestionar Licencias");
        manageLicensesItem.addActionListener(e -> openLicenseModule());
        licensesMenu.add(manageLicensesItem);
        
        // Exams Menu
        JMenu examsMenu = new JMenu("Exámenes");
        JMenuItem manageExamsItem = new JMenuItem("Gestionar Exámenes");
        manageExamsItem.addActionListener(e -> openExamModule());
        examsMenu.add(manageExamsItem);
        
        // Violations Menu
        JMenu violationsMenu = new JMenu("Infracciones");
        JMenuItem manageViolationsItem = new JMenuItem("Gestionar Infracciones");
        manageViolationsItem.addActionListener(e -> openViolationModule());
        violationsMenu.add(manageViolationsItem);
        
        // Reports Menu
        /* JMenu reportsMenu = new JMenu("Reportes");
        JMenuItem generateReportsItem = new JMenuItem("Generar Reportes");
        generateReportsItem.addActionListener(e -> openReportModule());
        reportsMenu.add(generateReportsItem); */
        
        // Add all menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(centersMenu);
        menuBar.add(entitiesMenu);
        menuBar.add(driversMenu);
        menuBar.add(licensesMenu);
        menuBar.add(examsMenu);
        // menuBar.add(violationsMenu);
        // menuBar.add(reportsMenu);
        
        return menuBar;
    }
    
    private void showWelcomeScreen() {
        cardLayout.show(mainPanel, "WELCOME");
        updateStatusBar("Bienvenido al Sistema de Gestión de Licencias");
    }
    
    private void openCenterModule() {
        try {
            if (centerModulePanel == null) {
                // Initialize center module components
                CentroDaoImpl centroDao = new CentroDaoImpl();
                CentroRepository centroRepository = new CentroRepositoryImpl(centroDao);
                CentroService centroService = new CentroServiceImpl(centroRepository);
                CentroController centroController = new CentroController(centroService);
                
                // Create CentroMainFrame as a panel
                CentroMainFrame centroMainPanel = new CentroMainFrame(centroController);
                
                // Simple wrapper panel
                centerModulePanel = new JPanel(new BorderLayout());
                centerModulePanel.add(centroMainPanel, BorderLayout.CENTER);
                
                // Add to CardLayout
                mainPanel.add(centerModulePanel, "CENTERS");
            }
            
            // Show center module
            cardLayout.show(mainPanel, "CENTERS");
            updateStatusBar("Centros - Gestión de centros administrativos");
            
            // Load data when module is opened
            SwingUtilities.invokeLater(() -> {
                if (centerModulePanel.getComponentCount() > 0) {
                    Component comp = centerModulePanel.getComponent(0);
                    if (comp instanceof CentroMainFrame) {
                        ((CentroMainFrame) comp).loadData();
                    }
                }
            });
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al abrir módulo de Centros: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            showWelcomeScreen();
        }
    }
    
    private void openEntityModule() {
        try {
            // Limpiar el panel principal
            mainPanel.removeAll();
            
            // Crear y agregar el panel de entidades
            EntidadMainFrame entidadPanel = new EntidadMainFrame();
            mainPanel.add(entidadPanel, BorderLayout.CENTER);
            
            // Actualizar la interfaz
            mainPanel.revalidate();
            mainPanel.repaint();
            
            // Actualizar barra de estado
            updateStatusBar("Entidades - Gestión de clínicas y autoescuelas");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al abrir módulo de Entidades: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void openDriverModule() {
        try {
            if (driverModulePanel == null) {
                // Initialize driver module
                DriverMainFrame driverFrame = new DriverMainFrame();
                driverFrame.setSize(getSize());
                
                // Wrap the frame in a panel
                driverModulePanel = new JPanel(new BorderLayout());
                driverModulePanel.add(driverFrame.getContentPane(), BorderLayout.CENTER);
                mainPanel.add(driverModulePanel, "DRIVERS");
            }
            
            // Switch to driver module
            cardLayout.show(mainPanel, "DRIVERS");
            updateStatusBar("Conductores - Gestión de conductores y aspirantes");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al abrir módulo de Conductores: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void openLicenseModule() {
        try {
            if (licenseModulePanel == null) {
                // Initialize license module
                LicenseMainFrame licenseFrame = new LicenseMainFrame();
                
                // Don't call setVisible(true) or setSize() on the frame
                // Just get its content pane
                licenseModulePanel = (JPanel) licenseFrame.getContentPane();
                
                // Add to CardLayout
                mainPanel.add(licenseModulePanel, "LICENSES");
                
                // Remove any listeners that might interfere with our CardLayout
                // The frame instance will be garbage collected, we're only using its content pane
            }
            
            // Switch to license module
            cardLayout.show(mainPanel, "LICENSES");
            updateStatusBar("Licencias - Gestión de licencias emitidas");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al abrir módulo de Licencias: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void openExamModule() {
        try {
            if (examModulePanel == null) {
                // Initialize exam module
                ExamMainFrame examFrame = new ExamMainFrame();
                examFrame.setSize(getSize());
                
                // IMPORTANTE: No llamar a setVisible(true) aquí
                // Wrap the frame in a panel - tomar solo el contentPane
                examModulePanel = new JPanel(new BorderLayout());
                examModulePanel.add(examFrame.getContentPane(), BorderLayout.CENTER);
                mainPanel.add(examModulePanel, "EXAMS");
            }
            
            // Switch to exam module
            cardLayout.show(mainPanel, "EXAMS");
            updateStatusBar("Exámenes - Gestión de exámenes realizados");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al abrir módulo de Exámenes: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void openViolationModule() {
        try {
            if (violationModulePanel == null) {
                // TODO: Initialize violation module
                violationModulePanel = createPlaceholderPanel("Módulo de Infracciones", 
                    "Gestión de infracciones de tránsito");
                mainPanel.add(violationModulePanel, "VIOLATIONS");
            }
            
            // Switch to violation module
            cardLayout.show(mainPanel, "VIOLATIONS");
            updateStatusBar("Infracciones - Gestión de infracciones de tránsito");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al abrir módulo de Infracciones: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void openReportModule() {
        try {
            if (reportModulePanel == null) {
                // TODO: Initialize report module
                reportModulePanel = createPlaceholderPanel("Módulo de Reportes", 
                    "Generación de reportes del sistema");
                mainPanel.add(reportModulePanel, "REPORTS");
            }
            
            // Switch to report module
            cardLayout.show(mainPanel, "REPORTS");
            updateStatusBar("Reportes - Generación de reportes del sistema");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al abrir módulo de Reportes: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private JPanel createPlaceholderPanel(String title, String description) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 245, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        // Title
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 82, 155));
        
        // Description
        JLabel descLabel = new JLabel(description + " (En desarrollo)", SwingConstants.CENTER);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        descLabel.setForeground(new Color(100, 100, 100));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(descLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void updateStatusBar(String status) {
        // Update status bar text
        /* Container contentPane = getContentPane();
        for (Component comp : contentPane.getComponents()) {
            if (comp instanceof JPanel && comp.getComponentCount() > 0) {
                Component child = ((JPanel) comp).getComponent(0);
                if (child instanceof JLabel) {
                    ((JLabel) child).setText(" " + status);
                    break;
                }
            }
        } */
    }
}