package entidad.controller;

import centro.exception.CentroNotFoundException;
import entidad.dto.EntidadRequestDto;
import entidad.exception.EntidadNotFoundException;
import entidad.exception.InvalidEntidadDataException;
import entidad.model.Entidad;
import entidad.service.EntidadService;
import entidad.utils.Validates;

import java.sql.SQLException;
import java.util.List;

public class EntidadController {

    private final EntidadService entidadService;

    public EntidadController(EntidadService entidadService) {
        this.entidadService = entidadService;
    }

    public Entidad anadirEntidad(EntidadRequestDto entidadRequestDto) throws InvalidEntidadDataException, SQLException, CentroNotFoundException {
        Validates.validateObject(entidadRequestDto, "entidadRequestDto no puede ser nula");
        return this.entidadService.guardar(entidadRequestDto);
    }

    public List<Entidad> obtenerEntidades() throws InvalidEntidadDataException, SQLException {
        return this.entidadService.listarEntidades();
    }

    public List<Entidad> obtenerEntidadesPorCentroId(Long centroId) throws InvalidEntidadDataException, SQLException, EntidadNotFoundException, CentroNotFoundException {
        return this.entidadService.listarEntidadesPorCentroId(centroId);
    }

    public void eliminarEntidad(Long id) throws InvalidEntidadDataException, SQLException, EntidadNotFoundException {
        Validates.validate(id, "El id no puede ser nulo");
        this.entidadService.eliminar(id);
    }

}
