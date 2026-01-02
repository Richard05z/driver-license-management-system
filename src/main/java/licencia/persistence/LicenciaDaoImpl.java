package licencia.persistence;

import db.ConnectionPool;
import licencia.dtos.LicenciaDaoResponseDto;
import licencia.exception.InvalidLicenciaDataException;
import licencia.exception.LicenciaNotFoundException;
import licencia.model.Licencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LicenciaDaoImpl implements LicenciaDao {

    private Connection getConnection() throws SQLException {
        return ConnectionPool.getConnection();
    }

    @Override
    public Licencia guardar(Licencia licencia) throws InvalidLicenciaDataException, SQLException {
        String sql = "INSERT INTO licencia (id_conductor, tipo_licencia, categoria, fecha_emision, fecha_vencimiento," +
                " restricciones, renovada,puntos) VALUES(?,CAST(? AS tipo_licencia_enum),CAST(? AS categoria_licencia_enum)," +
                "?,?,?,?,?)";

        try (
                Connection cnn = getConnection();
                PreparedStatement pst = cnn.prepareStatement(sql);
        ) {
            pst.setLong(1, licencia.getConductor().getId());
            pst.setString(2, licencia.getTipoLicencia().name());
            pst.setString(3, licencia.getCategoriaLicencia().name());
            pst.setDate(4, new Date(licencia.getFechaEmision().getTime()));
            pst.setDate(5, new Date(licencia.getFechaVencimiento().getTime()));
            pst.setString(6, licencia.getRestricciones());
            pst.setBoolean(7, licencia.isRenovada());
            pst.setInt(8, licencia.getPuntos());

            int rows = pst.executeUpdate();
            if (rows > 0) {
                try (
                        ResultSet generatedKey = pst.getGeneratedKeys()) {
                    if (generatedKey.next()) {
                        licencia.setIdLicencia(generatedKey.getLong(1));
                    }
                }
            }
        }
        return licencia;
    }

    @Override
    public Licencia actualizar(Licencia licencia) throws SQLException, InvalidLicenciaDataException, LicenciaNotFoundException {
        String sql = "UPDATE licencia SET tipo_licencia = CAST(? AS tipo_licencia_enum)," +
                " categoria = CAST(? AS categoria_licencia_enum), fecha_emision = ?, fecha_vencimiento = ?," +
                " restricciones = ?, renovada = ?, puntos = ? WHERE id_licencia = ?";

        try (
                Connection conn = this.getConnection();
                PreparedStatement pstm = conn.prepareStatement(sql);
        ) {
            pstm.setString(1, licencia.getTipoLicencia().name());
            pstm.setString(2, licencia.getCategoriaLicencia().name());
            pstm.setDate(3, new Date(licencia.getFechaEmision().getTime()));
            pstm.setDate(4, new Date(licencia.getFechaVencimiento().getTime()));
            pstm.setString(5, licencia.getRestricciones());
            pstm.setBoolean(6, licencia.isRenovada());
            pstm.setInt(7, licencia.getPuntos());

            int affectedRows = pstm.executeUpdate();

            if (affectedRows == 0) {
                throw new LicenciaNotFoundException("Licencia con ID " + licencia.getIdLicencia() + " no encontrada para actualizar");
            }

            return licencia;
        }

    }

    @Override
    public LicenciaDaoResponseDto buscarPorId(Long id) throws SQLException, LicenciaNotFoundException {
        String sql = "SELECT * FROM licencia WHERE id_licencia=?";
        try (
                Connection cnn = this.getConnection();
                PreparedStatement pstm = cnn.prepareStatement(sql)
        ) {
            pstm.setLong(1, id);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return mapResult(rs);
                }
            }
        }
        throw new LicenciaNotFoundException("Licencia con ID " + id + " no encontrada");
    }

    @Override
    public List<LicenciaDaoResponseDto> listarLicencias() throws InvalidLicenciaDataException, SQLException {
        String sql = "SELECT * FROM licencia";
        List<LicenciaDaoResponseDto> licencias = new ArrayList<>();

        try (
                Connection cnn = this.getConnection();
                PreparedStatement pstm = cnn.prepareStatement(sql);
                ResultSet resultSet = pstm.executeQuery()
        ) {
            while (resultSet.next()) {
                LicenciaDaoResponseDto licenciaDaoResponseDto = mapResult(resultSet);
                licencias.add(licenciaDaoResponseDto);
            }
        }
        return licencias;
    }

    @Override
    public List<LicenciaDaoResponseDto> licenciasEmitidasPorRangoFecha(java.util.Date fechaInicial, java.util.Date fechaFinal) throws InvalidLicenciaDataException, SQLException {
        String sql = "SELECT * FROM generar_reporte_licencias_emitidas(?,?)";
        List<LicenciaDaoResponseDto> licencias = new ArrayList<>();
        try (
                Connection cnn = this.getConnection();
                PreparedStatement pstm = cnn.prepareCall(sql);
        ) {
            pstm.setDate(1, new Date(fechaInicial.getTime()));
            pstm.setDate(2, new Date(fechaFinal.getTime()));
            try (
                    ResultSet rs = pstm.executeQuery();
            ) {
                while (rs.next()) {
                    LicenciaDaoResponseDto licenciaDaoResponseDto = mapResult(rs);
                    licencias.add(licenciaDaoResponseDto);
                }
            }
        }
        return licencias;
    }

    @Override
    public boolean existePorId(Long id) throws SQLException {
        if (id == null) return false;

        String sql = "SELECT Count(*) FROM licencia WHERE id_licencia = ?";

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

    private LicenciaDaoResponseDto mapResult(ResultSet resultSet) throws SQLException {
        return new LicenciaDaoResponseDto(
                resultSet.getLong("id_licencia"),
                resultSet.getLong("id_conductor"),
                resultSet.getString("tipo_licencia"),
                resultSet.getString("categoria"),
                resultSet.getDate("fecha_emision"),
                resultSet.getDate("fecha_vencimiento"),
                resultSet.getString("restricciones"),
                resultSet.getBoolean("renovada"),
                resultSet.getInt("puntos")
        );
    }
}
