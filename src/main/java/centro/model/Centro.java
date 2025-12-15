package centro.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Centro {

    private Long idCentro;
    private String nombre;
    private String codigo;
    private String direccionPostal;
    private String telefono;
    private String email;
    private String directorGeneral;
    private String jefeRRHH;
    private String jefeContabilidad;
    private String secretarioSindicato;
    private String logo;

    public Centro(String nombre, String codigo, String direccionPostal, String telefono, String email, String directorGeneral, String jefeRRHH, String jefeContabilidad, String secretarioSindicato, String logo) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.direccionPostal = direccionPostal;
        this.telefono = telefono;
        this.email = email;
        this.directorGeneral = directorGeneral;
        this.jefeRRHH = jefeRRHH;
        this.jefeContabilidad = jefeContabilidad;
        this.secretarioSindicato = secretarioSindicato;
        this.logo = logo;
    }

}
