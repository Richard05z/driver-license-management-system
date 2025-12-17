package centro.persistence;

import centro.exception.CentroNotFoundException;
import centro.exception.InvalidCentroDataException;
import centro.model.Centro;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CentroDao {
    List<Centro> listarCentros() throws InvalidCentroDataException, SQLException;

    Centro listarPorCodigo(String codigo) throws SQLException, CentroNotFoundException;

    Centro guardar(Centro centro) throws InvalidCentroDataException, SQLException;

    void eliminar(Long id) throws SQLException, CentroNotFoundException;

    Centro actualizar(Centro centro) throws CentroNotFoundException, SQLException;

    boolean existePorId(Long id) throws SQLException;

    Centro obtenerCentroPorId(Long id)throws SQLException, CentroNotFoundException;

}
