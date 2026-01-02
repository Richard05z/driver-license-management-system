package licencia.service;

import conductor.exception.ConductorNotFoundException;
import conductor.model.Conductor;
import conductor.service.ConductorService;
import licencia.dtos.LicenciaDaoResponseDto;
import licencia.dtos.LicenciaRequestDto;
import licencia.dtos.LicenciaUpdateRequestDto;
import licencia.exception.InvalidLicenciaDataException;
import licencia.exception.LicenciaNotFoundException;
import licencia.mapper.LicenciaMapper;
import licencia.model.Licencia;
import licencia.repository.LicenciaRepository;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Slf4j
public class LicenciaServiceImpl implements LicenciaService {

    private final LicenciaRepository repository;
    private final ConductorService conductorService;

    public LicenciaServiceImpl(LicenciaRepository repository, ConductorService conductorService) {
        this.repository = repository;
        this.conductorService = conductorService;
    }

    @Override
    public Licencia guardar(LicenciaRequestDto licencia) throws InvalidLicenciaDataException, SQLException, ConductorNotFoundException {
        LicenciaValidator.validate(licencia);
        Conductor conductor = this.conductorService.getById(licencia.idConductor());
        return this.repository.guardar(LicenciaMapper.toDomain(licencia, conductor));
    }

    @Override
    public Licencia actualizar(Long id, LicenciaUpdateRequestDto dataParaActualizar) throws SQLException, InvalidLicenciaDataException, LicenciaNotFoundException, ConductorNotFoundException {
        Licencia licenciaExistente = this.buscarPorId(id);
        Licencia licenciaActualizada = this.establecerDataActualizar(licenciaExistente, dataParaActualizar);
        return this.repository.actualizar(licenciaActualizada);
    }

    private Licencia establecerDataActualizar(Licencia licenciaExistente, LicenciaUpdateRequestDto dataParaActualizar) {
        licenciaExistente.setCategoriaLicencia(dataParaActualizar.categoriaLicencia());
        licenciaExistente.setTipoLicencia(dataParaActualizar.tipoLicencia());
        licenciaExistente.setFechaEmision(dataParaActualizar.fechaEmision());
        licenciaExistente.setFechaVencimiento(dataParaActualizar.fechaVencimiento());
        licenciaExistente.setRestricciones(dataParaActualizar.restricciones());
        licenciaExistente.setRenovada(dataParaActualizar.renovada());
        licenciaExistente.setPuntos(dataParaActualizar.puntos());
        return licenciaExistente;
    }

    @Override
    public List<Licencia> listarLicencias() throws InvalidLicenciaDataException, SQLException {
        List<LicenciaDaoResponseDto> respuesta = this.repository.listarLicencias();
        return transformarRespuesta(respuesta);
    }

    @Override
    public List<Licencia> licenciasEmitidasPorRangoFecha(Date fechaInicial, Date fechaFinal) throws InvalidLicenciaDataException, SQLException {
        List<LicenciaDaoResponseDto> respuesta = this.repository.licenciasEmitidasPorRangoFecha(fechaInicial, fechaFinal);
        return transformarRespuesta(respuesta);
    }

    @Override
    public Licencia buscarPorId(Long id) throws SQLException, LicenciaNotFoundException, ConductorNotFoundException {
        this.existePorId(id);
        LicenciaDaoResponseDto respuesta = this.repository.buscarPorId(id);
        Conductor conductor = this.conductorService.getById(respuesta.idConductor());
        return LicenciaMapper.toDomain(respuesta, conductor);
    }

    private List<Licencia> transformarRespuesta(List<LicenciaDaoResponseDto> respuesta) {
        return respuesta.stream()
                .map(dto -> {
                    try {
                        Conductor conductor = this.conductorService.getById(dto.idConductor());
                        return LicenciaMapper.toDomain(dto, conductor);
                    } catch (SQLException | ConductorNotFoundException e) {
                        log.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    @Override
    public boolean existePorId(Long id) throws SQLException, LicenciaNotFoundException {
        if (!this.repository.existePorId(id)) {
            throw new LicenciaNotFoundException("La licencia con id %s no fue encontrado".formatted(id));
        }
        return true;
    }
}
