package entidad.service;

import centro.exception.CentroNotFoundException;
import entidad.dto.EntidadRequestDto;
import entidad.exception.EntidadNotFoundException;
import entidad.exception.InvalidEntidadDataException;
import entidad.model.Entidad;

import java.sql.SQLException;
import java.util.List;

public interface EntidadService {

    List<Entidad> listarEntidades() throws InvalidEntidadDataException, SQLException;

    List<Entidad> listarEntidadesPorCentroId(Long idCentro) throws InvalidEntidadDataException, SQLException, CentroNotFoundException, EntidadNotFoundException;

    Entidad guardar(EntidadRequestDto entidadRequestDto) throws InvalidEntidadDataException, SQLException, CentroNotFoundException;

    void eliminar(Long id) throws SQLException, EntidadNotFoundException;

    boolean existePorId(Long id) throws SQLException, EntidadNotFoundException;

}
