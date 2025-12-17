package entidad.model;

import centro.model.Centro;
import entidad.vo.TipoEntidad;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Entidad {
    private Long idEntidad;
    private String nombre;
    private TipoEntidad tipoEntidad;
    private String direccion;
    private String telefono;
    private String email;
    private String directorGeneral;
    private Centro centro;

    public Entidad(String nombre, TipoEntidad tipoEntidad, String direccion, String telefono, String email,
                   String directorGeneral, Centro centro) {
        this.nombre = nombre;
        this.tipoEntidad = tipoEntidad;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.directorGeneral = directorGeneral;
        this.centro = centro;
    }
}
