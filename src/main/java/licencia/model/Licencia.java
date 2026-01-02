package licencia.model;

import conductor.model.Conductor;
import licencia.vo.CategoriaLicencia;
import licencia.vo.TipoLicencia;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
public class Licencia {
    private Long idLicencia;
    private Conductor conductor;
    private TipoLicencia tipoLicencia;
    private CategoriaLicencia categoriaLicencia;
    private Date fechaEmision;
    private Date fechaVencimiento;
    private String restricciones;
    private boolean renovada;
    private int puntos;

    public Licencia(Conductor conductor, TipoLicencia tipoLicencia, CategoriaLicencia categoriaLicencia,
                    Date fechaEmision, Date fechaVencimiento, String restricciones,
                    boolean renovada, int puntos) {
        this.conductor = conductor;
        this.tipoLicencia = tipoLicencia;
        this.categoriaLicencia = categoriaLicencia;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.restricciones = restricciones;
        this.renovada = renovada;
        this.puntos = puntos;
    }
}
