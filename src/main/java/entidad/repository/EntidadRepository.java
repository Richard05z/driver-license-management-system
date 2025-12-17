package entidad.repository;

import entidad.dto.EntidadDaoResponseDto;
import entidad.exception.EntidadNotFoundException;
import entidad.exception.InvalidEntidadDataException;
import entidad.model.Entidad;

import java.sql.SQLException;
import java.util.List;

public interface EntidadRepository {
    List<EntidadDaoResponseDto> listarEntidades() throws InvalidEntidadDataException, SQLException;

    List<EntidadDaoResponseDto> buscarEntidadesPorCentroId(Long idCentro) throws SQLException, EntidadNotFoundException, InvalidEntidadDataException;

    Entidad guardar(Entidad entidad) throws InvalidEntidadDataException, SQLException;

    void eliminar(Long id) throws SQLException, EntidadNotFoundException;

    boolean existePorId(Long id) throws SQLException;
}
