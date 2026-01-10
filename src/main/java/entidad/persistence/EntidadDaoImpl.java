package entidad.persistence;

import db.ConnectionPool;
import entidad.dto.EntidadDaoResponseDto;
import entidad.exception.EntidadNotFoundException;
import entidad.exception.InvalidEntidadDataException;
import entidad.model.Entidad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EntidadDaoImpl implements EntidadDao {

    private Connection getConnection() throws SQLException {
        return ConnectionPool.getConnection();
    }

    @Override
    public List<EntidadDaoResponseDto> listarEntidades() throws InvalidEntidadDataException, SQLException {
        String sql = "SELECT id_entidad, nombre, tipo_entidad, direccion, telefono, email, director, id_centro FROM entidad";

        List<EntidadDaoResponseDto> entidades = new ArrayList<>();
        try (
                Connection conn = this.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {
                EntidadDaoResponseDto entidadDaoResponseDto = mapResult(resultSet);
                entidades.add(entidadDaoResponseDto);
            }
        }
        return entidades;
    }

    @Override
    public List<EntidadDaoResponseDto> buscarEntidadesPorCentroId(Long idCentro) throws SQLException, EntidadNotFoundException, InvalidEntidadDataException {
        String sql = "SELECT id_entidad, nombre, tipo_entidad, direccion, telefono, email, director, id_centro FROM " +
                "entidad WHERE id_centro = ?";
        List<EntidadDaoResponseDto> entidades = new ArrayList<>();

        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql);) {
            statement.setLong(1, idCentro);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    EntidadDaoResponseDto entidadDaoResponseDto = mapResult(resultSet);
                    entidades.add(entidadDaoResponseDto);
                }
            }
        }
        return entidades;
    }

    @Override
    public Entidad guardar(Entidad entidad) throws InvalidEntidadDataException, SQLException {
        String sql = "INSERT INTO entidad (nombre,tipo_entidad,direccion,telefono," +
                "email,director,id_centro) VALUES(?,CAST(? AS tipo_entidad_enum),?,?,?,?,?) ";
        try (
                Connection conn = this.getConnection();
                PreparedStatement pstm = conn.prepareStatement(sql)
        ) {
            pstm.setString(1, entidad.getNombre());
            pstm.setString(2, entidad.getTipoEntidad().name());
            pstm.setString(3, entidad.getDireccion());
            pstm.setString(4, entidad.getTelefono());
            pstm.setString(5, entidad.getEmail());
            pstm.setString(6, entidad.getDirectorGeneral());
            pstm.setLong(7, entidad.getCentro().getIdCentro());

            int rows = pstm.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKey = pstm.getGeneratedKeys()) {
                    if (generatedKey.next()) {
                        entidad.setIdEntidad(generatedKey.getLong(1));
                    }
                }
            }
        }
        return entidad;
    }

    @Override
    public void eliminar(Long id) throws SQLException, EntidadNotFoundException {
        String sql = "DELETE FROM entidad WHERE id_entidad=?";
        try (
                Connection conn = this.getConnection();
                PreparedStatement pstm = conn.prepareStatement(sql);
        ) {

            pstm.setLong(1, id);

            int rows = pstm.executeUpdate();
            if (rows == 0) {
                throw new EntidadNotFoundException("La entidad con %s no fue encontrado".formatted(id));
            }
        }
    }

    @Override
    public boolean existePorId(Long id) throws SQLException {
        if (id == null) return false;

        String sql = "SELECT Count(*) FROM entidad WHERE id_centro = ?";

        try (
                Connection conn = this.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private EntidadDaoResponseDto mapResult(ResultSet resultSet) throws SQLException {
        return new EntidadDaoResponseDto(
                resultSet.getLong("id_entidad"),
                resultSet.getString("nombre"),
                resultSet.getString("tipo_entidad"),
                resultSet.getString("direccion"),
                resultSet.getString("telefono"),
                resultSet.getString("email"),
                resultSet.getString("director"),
                resultSet.getLong("id_centro"));
    }

    @Override
    public EntidadDaoResponseDto buscarPorId(Long id) throws SQLException, EntidadNotFoundException {
        if (id == null) {
            throw new EntidadNotFoundException("El ID no puede ser nulo");
        }

        String sql = "SELECT id_entidad, nombre, tipo_entidad, direccion, telefono, email, director, id_centro " +
                    "FROM entidad WHERE id_entidad = ?";
        
        try (
            Connection conn = this.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResult(resultSet);
                } else {
                    throw new EntidadNotFoundException("Entidad con ID " + id + " no encontrada");
                }
            }
        }
    }
}
