package centro.controller;

import centro.dto.CentroResponseDto;
import centro.exception.CentroNotFoundException;
import centro.exception.InvalidCentroDataException;
import centro.mapper.CentroMapper;
import centro.model.Centro;
import centro.service.CentroService;
import centro.utils.Validates;

import java.sql.SQLException;
import java.util.List;

public class CentroController {
    private final CentroService centroService;

    public CentroController(CentroService centroService) {
        this.centroService = centroService;
    }

    public Centro anadirCentro(Centro centro) throws InvalidCentroDataException, SQLException {
        Validates.validateObject(centro, "El centro no puede ser nulo");
        return this.centroService.guardar(centro);
    }

    public List<CentroResponseDto> obtenerCentros() throws InvalidCentroDataException, SQLException {
        return this.centroService.listarCentros().stream()
                .map((CentroMapper::toCentroResponseDto))
                .toList();
    }

    public CentroResponseDto obtenerCentroPorCodigo(String codigoCentro) throws InvalidCentroDataException, SQLException, CentroNotFoundException {
        Validates.validateText(codigoCentro, "El codigo no puede ser nulo");
        return CentroMapper.toCentroResponseDto(this.centroService.listarPorCodigo(codigoCentro));
    }

    public Centro actualizarCentro(Centro centro) throws InvalidCentroDataException, SQLException, CentroNotFoundException {
        Validates.validateObject(centro, "El centro no puede ser nulo");
        return this.centroService.actualizar(centro);
    }

    public void eliminarCentro(Long id) throws InvalidCentroDataException, SQLException, CentroNotFoundException {
        Validates.validate(id, "El id no puede ser nulo");
        centroService.eliminar(id);
    }

}
