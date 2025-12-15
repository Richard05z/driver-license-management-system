package centro.view;

import centro.controller.CentroController;
import centro.dto.CentroResponseDto;
import centro.exception.CentroNotFoundException;
import centro.exception.InvalidCentroDataException;
import centro.model.Centro;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class CentroView {
    private final CentroController centroController;
    private final Scanner scanner;

    public CentroView(CentroController centroController) {
        this.centroController = centroController;
        scanner = new Scanner(System.in);
    }

    public void showMenu() {
        while (true) {
            System.out.println("\nSeleccione una opción:");
            System.out.println("1. Agregar Centro");
            System.out.println("2. Mostrar Centros");
            System.out.println("3. Buscar Centro por ID");
            System.out.println("4. Eliminar Centro por ID");
            System.out.println("5. Modificar Centro por ID");
            System.out.println("6. Salir");
            System.out.print("Opción: ");

            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1 -> addCentroView();
                case 2 -> showAllView();
                case 3 -> findByCodigoView();
                case 4 -> deleteCentroView();
                case 5 -> updateCentroView();
                case 6 -> {
                    scanner.close();
                    return;
                }
                default -> System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
    }

    private void addCentroView() {
        try {
            String nombre = readNonEmptyString("Ingrese el nombre del centro:");
            String codigo = readNonEmptyString("Ingrese el codigo del centro:");
            String direccionPostal = readNonEmptyString("Ingrese el direccionPostal del centro:");
            String telefono = readNonEmptyString("Ingrese el telefono del centro:");
            String email = readNonEmptyString("Ingrese el email del centro:");
            String directorGeneral = readNonEmptyString("Ingrese el directorGeneral del centro:");
            String jefeRRHH = readNonEmptyString("Ingrese el jefeRRHH del centro:");
            String jefeContabilidad = readNonEmptyString("Ingrese el jefeContabilidad del centro:");
            String secretarioSindicato = readNonEmptyString("Ingrese el secretarioSindicato del centro:");
            String logo = readNonEmptyString("Ingrese el logo del centro:");

            Centro centro = new Centro(
                    nombre,
                    codigo,
                    direccionPostal,
                    telefono,
                    email,
                    directorGeneral,
                    jefeRRHH,
                    jefeContabilidad,
                    secretarioSindicato,
                    logo
            );
            centroController.anadirCentro(centro);
        } catch (InvalidCentroDataException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void findByCodigoView() {
        try {
            String codigo = readNonEmptyString("Ingrese el codigo del centro a buscar:");
            CentroResponseDto centro = centroController.obtenerCentroPorCodigo(codigo);
            showCentro(centro);
        } catch (InvalidCentroDataException | CentroNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteCentroView() {
        try {
            long id = readValidLong("Ingrese el ID del centro a buscar:", 1);
            centroController.eliminarCentro(id);
        } catch (InvalidCentroDataException | CentroNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateCentroView() {

    }

    private void showAllView() {
        try {
            List<CentroResponseDto> products = centroController.obtenerCentros();
            products.forEach(this::showCentro);
        } catch (InvalidCentroDataException | SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void showCentro(CentroResponseDto centro) {
        System.out.println("\n🔹 Centro:");
        System.out.println("nombre: " + centro.nombre());
        System.out.println("direccionPostal: " + centro.direccionPostal());
        System.out.println("telefono: " + centro.telefono());
        System.out.println("email: " + centro.email());
        System.out.println("directorGeneral: " + centro.directorGeneral());
        System.out.println("jefeRRHH: " + centro.jefeRRHH());
        System.out.println("jefeContabilidad: " + centro.jefeContabilidad());
        System.out.println("secretarioSindicato: " + centro.secretarioSindicato());
        System.out.println("logo: " + centro.logo());
        System.out.println("_________________________");
    }

    // Método para leer cadenas no vacías
    private String readNonEmptyString(String message) {
        String input;
        do {
            System.out.println(message);
            input = scanner.nextLine().trim();
            if (input.length() < 3) {
                System.out.println("El valor no puede estar vacío o el nombre es muy corto.");
            }
        } while (input.length() < 3);
        return input;
    }

    // Método para leer enteros válidos con un mínimo opcional
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
                System.out.println("Ingrese un número válido.");
            }
        } while (true);
    }

    private double readValidDouble(String message, double min) {
        double value;
        do {
            System.out.println(message);
            String input = scanner.nextLine().trim();
            try {
                value = Double.parseDouble(input);
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

    // Método sobrecargado para leer enteros sin restricciones
    private int readValidInteger() {
        return readValidInteger(" Ingrese un número:", Integer.MIN_VALUE);
    }
}
