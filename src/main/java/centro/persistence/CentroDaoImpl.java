package centro.persistence;

import centro.exception.CentroNotFoundException;
import centro.exception.InvalidCentroDataException;
import centro.model.Centro;
import db.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CentroDaoImpl implements CentroDao {

    private Connection getConnection() throws SQLException {
        return ConnectionPool.getConnection();
    }

    @Override
    public List<Centro> listarCentros() throws InvalidCentroDataException, SQLException {
        String sql = "SELECT * from centro";
        List<Centro> centros = new ArrayList<>();
        try (
                Connection conn = this.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {
                Centro centro = mapResult(resultSet);
                centros.add(centro);
            }
        }
        return centros;

    }

    @Override
    public Centro listarPorCodigo(String codigo) throws SQLException, CentroNotFoundException {
        String sql = "SELECT * FROM obtener_ficha_del_centro_por_codigo(?)";
        try (
                Connection conn = this.getConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, codigo);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResult(resultSet);
                }
            }
        }
        throw new CentroNotFoundException("El centro con %s no fue encontrado".formatted(codigo));
    }

    @Override
    public Centro obtenerCentroPorId(Long id) throws SQLException, CentroNotFoundException {
        String sql = "SELECT * FROM centro WHERE id_centro = ?";

        try (
                Connection conn = this.getConnection();
                PreparedStatement pstm = conn.prepareStatement(sql);
        ) {
            pstm.setLong(1, id);
            try(ResultSet resultSet = pstm.executeQuery()){
                if(resultSet.next()){
                    return this.mapResult(resultSet);
                }
            }
        }
        throw new CentroNotFoundException("El centro con %s no fue encontrado".formatted(id));
    }

    @Override
    public Centro guardar(Centro centro) throws InvalidCentroDataException, SQLException {
        String sql = "INSERT INTO centro (nombre,codigo,direccion_postal,telefono,email,director_general," +
                "jefe_rrhh,jefe_contabilidad,secretario_sindicato,logo) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (
                Connection conn = this.getConnection();
                PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, centro.getNombre());
            pstm.setString(2, centro.getCodigo());
            pstm.setString(3, centro.getDireccionPostal());
            pstm.setString(4, centro.getTelefono());
            pstm.setString(5, centro.getEmail());
            pstm.setString(6, centro.getDirectorGeneral());
            pstm.setString(7, centro.getJefeRRHH());
            pstm.setString(8, centro.getJefeContabilidad());
            pstm.setString(9, centro.getSecretarioSindicato());
            pstm.setString(10, centro.getLogo());

            int rows = pstm.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKey = pstm.getGeneratedKeys()) {
                    if (generatedKey.next()) {
                        centro.setIdCentro(generatedKey.getLong(1));
                    }
                }
            }
        }
        return centro;
    }

    @Override
    public void eliminar(Long id) throws CentroNotFoundException, SQLException {
        String sql = "DELETE FROM centro WHERE id_centro=?";
        try (
                Connection conn = this.getConnection();
                PreparedStatement pstm = conn.prepareStatement(sql);
        ) {

            pstm.setLong(1, id);
            pstm.setLong(2, id);

            int rows = pstm.executeUpdate();
            if (rows == 0) {
                throw new CentroNotFoundException("El centro con %s no fue encontrado".formatted(id));
            }
        }
    }

    @Override
    public Centro actualizar(Centro centro) throws CentroNotFoundException, SQLException {
        String sql = "UPDATE centro SET nombre=?,codigo=?,direccion_postal=?,telefono=?,email=?,director_general=?," +
                "jefe_rrhh=?,jefe_contabilidad=?,secretario_sindicato=?,logo=? WHERE id_centro=?";
        try (
                Connection conn = this.getConnection();
                PreparedStatement pstm = conn.prepareStatement(sql);
        ) {
            pstm.setString(1, centro.getNombre());
            pstm.setString(2, centro.getCodigo());
            pstm.setString(3, centro.getDireccionPostal());
            pstm.setString(4, centro.getTelefono());
            pstm.setString(5, centro.getEmail());
            pstm.setString(6, centro.getDirectorGeneral());
            pstm.setString(7, centro.getJefeRRHH());
            pstm.setString(8, centro.getJefeContabilidad());
            pstm.setString(9, centro.getSecretarioSindicato());
            pstm.setString(10, centro.getLogo());
            pstm.setLong(11, centro.getIdCentro());

            int rows = pstm.executeUpdate();
            if (rows == 0) {
                throw new CentroNotFoundException("El centro con %s no fue encontrado".formatted(centro.getIdCentro()));
            }
            return centro;
        }
    }

    @Override
    public boolean existePorId(Long id) throws SQLException {
        if (id == null) return false;

        String sql = "SELECT Count(*) FROM centro WHERE id_centro = ?";

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


    private Centro mapResult(ResultSet resultSet) throws SQLException {
        return new Centro(
            resultSet.getLong("id_centro"),
            resultSet.getString("nombre"),
            resultSet.getString("codigo"),
            resultSet.getString("direccion_postal"),
            resultSet.getString("telefono"),
            resultSet.getString("email"),
            resultSet.getString("director_general"),
            resultSet.getString("jefe_rrhh"),
            resultSet.getString("jefe_contabilidad"),
            resultSet.getString("secretario_sindicato"),
            resultSet.getString("logo")
        );
    }
}
