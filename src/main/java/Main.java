import centro.controller.CentroController;
import centro.persistence.CentroDao;
import centro.persistence.CentroDaoImpl;
import centro.repository.CentroRepository;
import centro.repository.CentroRepositoryImpl;
import centro.service.CentroService;
import centro.service.CentroServiceImpl;
import centro.view.CentroFrame;
import entidad.controller.EntidadController;
import entidad.persistence.EntidadDao;
import entidad.persistence.EntidadDaoImpl;
import entidad.repository.EntidadRepository;
import entidad.repository.EntidadRepositoryImpl;
import entidad.service.EntidadService;
import entidad.service.EntidadServiceImpl;
import entidad.view.EntidadView;

import javax.swing.*;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        // TODO: ESTO ES SOLO PARA PRUEBAS CORREGIR POSTERIORMENTE CON LLAMADAS A VISTAS OFICIALES DE LA APP
//        try {
//
//            CentroDao centroDao = new CentroDaoImpl();
//            CentroRepository centroRepository = new CentroRepositoryImpl((CentroDaoImpl) centroDao);
//            CentroService centroService = new CentroServiceImpl(centroRepository);
//            CentroController centroController = new CentroController(centroService);
//            CentroView view = new CentroView(centroController);
//            view.showMenu();
//        EntidadDao entidadDao = new EntidadDaoImpl();
//        EntidadRepository entidadRepository = new EntidadRepositoryImpl(entidadDao);
//        EntidadService entidadService = new EntidadServiceImpl(entidadRepository,centroService);
//        EntidadController entidadController = new EntidadController(entidadService);
//        EntidadView view = new EntidadView(entidadController);
//        view.showMenu();
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
        // Ejecución en el hilo de despacho de eventos de Swing (ES OBLIGATORIO)
//        SwingUtilities.invokeLater(() -> {
//            try {
//                // INYECCIÓN DE DEPENDENCIAS (Configuración de la Arquitectura Limpia)
//
//                // 1. Persistencia (DAO)
//                CentroDaoImpl centroDao = new CentroDaoImpl();
//
//                // 2. Repository (Mantenemos esta capa según tu código, aunque sea delegación)
//                CentroRepositoryImpl centroRepository = new CentroRepositoryImpl(centroDao);
//
//                // 3. Servicio (Lógica de Negocio)
//                CentroServiceImpl centroService = new CentroServiceImpl(centroRepository);
//
//                // 4. Controlador (Punto de entrada de la UI)
//                CentroController controller = new CentroController(centroService);
//
//                // 5. VISTA
//                CentroFrame view = new CentroFrame(controller);
//                view.setVisible(true);
//                view.setResizable(false);
//                view.setBounds(20, 20, 1289, 790);
//
//            } catch (SQLException e) {
//                JOptionPane.showMessageDialog(null,
//                        "Error fatal al inicializar la base de datos: " + e.getMessage(),
//                        "Error de Conexión", JOptionPane.ERROR_MESSAGE);
//                System.exit(1);
//            }
//        });
    }
}
