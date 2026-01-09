package entidad.view;

import centro.exception.CentroNotFoundException;
import entidad.controller.EntidadController;
import entidad.dto.EntidadRequestDto;
import entidad.exception.EntidadNotFoundException;
import entidad.exception.InvalidEntidadDataException;
import entidad.model.Entidad;
import entidad.vo.TipoEntidad;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

// TODO: ESTA IMPLEMENTACION ES SOLO PARA PRUEBA EN DESARROLLO-CAMBIAR POSTERIORMENTE POR LA VISTA OFICIAL DE LA ENTIDAD
public class EntidadView {
    private final EntidadController entidadController;
    private final Scanner scanner;

    public EntidadView(EntidadController entidadController) {
        this.entidadController = entidadController;
        scanner = new Scanner(System.in);
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n--- Gestión de Entidades ---");
            System.out.println("1. Agregar Entidad");
            System.out.println("2. Mostrar Todas las Entidades");
            System.out.println("3. Buscar Entidades por ID de Centro");
            System.out.println("4. Eliminar Entidad por ID");
            System.out.println("5. Salir");
            System.out.print("Opción: ");

            int option = readValidInteger("Opción:", 1);
            scanner.nextLine(); // Consumir nueva línea

            switch (option) {
                case 1 -> addEntidadView();
                case 2 -> showAllEntidadesView();
                case 3 -> findByCentroIdView();
                case 4 -> deleteEntidadView();
                case 5 -> {
                    scanner.close();
                    return;
                }
                default -> System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
    }

    private void addEntidadView() {
        try {
            System.out.println("\n--- Agregar Nueva Entidad ---");
            String nombre = readNonEmptyString("Ingrese el nombre de la entidad:");
            TipoEntidad tipo = readValidTipoEntidad("Ingrese el tipo de entidad (CLINICA/AUTOESCUELA):");
            String direccion = readNonEmptyString("Ingrese la dirección:");
            String telefono = readNonEmptyString("Ingrese el teléfono:");
            String email = readNonEmptyString("Ingrese el email:");
            String directorGeneral = readNonEmptyString("Ingrese el Director General:");
            Long idCentro = readValidLong("Ingrese el ID del Centro al que pertenece:", 1);

            EntidadRequestDto requestDto = new EntidadRequestDto(
                    nombre,
                    tipo,
                    direccion,
                    telefono,
                    email,
                    directorGeneral,
                    idCentro
            );

            Entidad entidadGuardada = entidadController.anadirEntidad(requestDto);
            System.out.printf("✅ Entidad '%s' guardada con ID: %d%n", entidadGuardada.getNombre(), entidadGuardada.getIdEntidad());

        } catch (InvalidEntidadDataException e) {
            System.err.println("❌ Error de Validación: " + e.getMessage());
        } catch (CentroNotFoundException e) {
            System.err.println("❌ Error: " + e.getMessage() + ". No se pudo asociar la entidad.");
        } catch (SQLException e) {
            System.err.println("❌ Error de Base de Datos: " + e.getMessage());
            // En una aplicación real, se manejaría de forma más robusta
            throw new RuntimeException(e);
        }
    }

    private void showAllEntidadesView() {
        try {
            System.out.println("\n--- Listado de Todas las Entidades ---");
            List<Entidad> entidades = entidadController.obtenerEntidades();
            if (entidades.isEmpty()) {
                System.out.println("No hay entidades registradas.");
                return;
            }
            entidades.forEach(this::showEntidad);
        } catch (InvalidEntidadDataException | SQLException e) {
            System.err.println("❌ Error al mostrar entidades: " + e.getMessage());
        }
    }

    private void findByCentroIdView() {
        try {
            System.out.println("\n--- Buscar Entidades por Centro ID ---");
            Long centroId = readValidLong("Ingrese el ID del Centro para listar sus entidades:", 1);

            List<Entidad> entidades = entidadController.obtenerEntidadesPorCentroId(centroId);
            if (entidades.isEmpty()) {
                System.out.printf("No se encontraron entidades asociadas al Centro ID %d.%n", centroId);
                return;
            }
            System.out.printf("✅ Entidades encontradas para el Centro ID %d:%n", centroId);
            entidades.forEach(this::showEntidad);

        } catch (EntidadNotFoundException | CentroNotFoundException e) {
            System.err.println("❌ Error: " + e.getMessage());
        } catch (InvalidEntidadDataException | SQLException e) {
            System.err.println("❌ Error en la búsqueda: " + e.getMessage());
        }
    }

    private void deleteEntidadView() {
        try {
            System.out.println("\n--- Eliminar Entidad ---");
            Long id = readValidLong("Ingrese el ID de la Entidad a eliminar:", 1);

            entidadController.eliminarEntidad(id);
            System.out.printf("✅ Entidad con ID %d eliminada correctamente.%n", id);

        } catch (InvalidEntidadDataException e) {
            System.err.println("❌ Error de Validación: " + e.getMessage());
        } catch (EntidadNotFoundException e) {
            System.err.println("❌ Error: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Error de Base de Datos: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // NOTA: No se implementa updateCentroView() ni showMenu case 5, ya que no tienes un método de actualización
    // en tu EntidadController, pero se deja el esqueleto de la clase listo.

    private void showEntidad(Entidad entidad) {
        System.out.println("\n🔹 Entidad ID: " + entidad.getIdEntidad());
        System.out.println("nombre: " + entidad.getNombre());
        System.out.println("tipoEntidad: " + entidad.getTipoEntidad());
        System.out.println("direccion: " + entidad.getDireccion());
        System.out.println("telefono: " + entidad.getTelefono());
        System.out.println("email: " + entidad.getEmail());
        System.out.println("directorGeneral: " + entidad.getDirectorGeneral());
        System.out.println("Centro Asociado ID: " + (entidad.getCentro() != null ? entidad.getCentro().getIdCentro() : "N/A"));
        System.out.println("_________________________");
    }


    // =================================================================
    // MÉTODOS DE LECTURA DE DATOS (Adaptados de CentroView)
    // =================================================================

    // Método para leer cadenas no vacías (mínimo 3 caracteres)
    private String readNonEmptyString(String message) {
        String input;
        do {
            System.out.println(message);
            input = scanner.nextLine().trim();
            if (input.length() < 3) {
                System.out.println("El valor no puede estar vacío o es muy corto.");
            }
        } while (input.length() < 3);
        return input;
    }

    // Método para leer un Long válido con un mínimo opcional
    private long readValidLong(String message, long min) {
        long value;
        do {
            System.out.println(message);
            String input = scanner.nextLine().trim();
            try {
                value = Long.parseLong(input);
                if (value < min) {
                    System.out.println("Debe ser al menos " + min + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
            }
        } while (true);
    }

    // Método para leer un Integer válido con un mínimo opcional
    private int readValidInteger(String message, int min) {
        int value;
        do {
            System.out.println(message);
            String input = scanner.nextLine().trim();
            try {
                value = Integer.parseInt(input);
                if (value < min) {
                    System.out.println("Debe ser al menos " + min + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero válido.");
            }
        } while (true);
    }

    // Método específico para leer TipoEntidad
    private TipoEntidad readValidTipoEntidad(String message) {
        TipoEntidad tipo = null;
        boolean valido = false;
        do {
            System.out.println(message);
            String input = scanner.nextLine().trim().toLowerCase();
            try {
                tipo = TipoEntidad.valueOf(input);
                valido = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Tipo de entidad inválido. Use CLINICA o AUTOESCUELA.");
            }
        } while (!valido);
        return tipo;
    }
}