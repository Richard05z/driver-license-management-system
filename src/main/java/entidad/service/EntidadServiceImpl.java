package entidad.service;

import centro.exception.CentroNotFoundException;
import centro.model.Centro;
import centro.service.CentroService;
import entidad.dto.EntidadDaoResponseDto;
import entidad.dto.EntidadRequestDto;
import entidad.exception.EntidadNotFoundException;
import entidad.exception.InvalidEntidadDataException;
import entidad.mapper.EntidadMapper;
import entidad.model.Entidad;
import entidad.repository.EntidadRepository;

import java.sql.SQLException;
import java.util.List;

public class EntidadServiceImpl implements EntidadService {

    private final EntidadRepository entidadRepository;
    private final CentroService centroService;

    public EntidadServiceImpl(EntidadRepository entidadRepository, CentroService centroService) {
        this.entidadRepository = entidadRepository;
        this.centroService = centroService;
    }

    @Override
    public List<Entidad> listarEntidades() throws InvalidEntidadDataException, SQLException {
        List<EntidadDaoResponseDto> respuesta = this.entidadRepository.listarEntidades();
        return respuesta.stream()
                .map(dto -> {
                    try {
                        Centro centro = this.centroService.buscarCentroPorId(dto.idCentro());
                        return EntidadMapper.toDomain(dto, centro);
                    } catch (SQLException | CentroNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

    @Override
    public List<Entidad> listarEntidadesPorCentroId(Long idCentro) throws InvalidEntidadDataException, SQLException,
            CentroNotFoundException, EntidadNotFoundException {
        Centro centro = this.centroService.buscarCentroPorId(idCentro);
        List<EntidadDaoResponseDto> respuesta = this.entidadRepository.buscarEntidadesPorCentroId(idCentro);
        return respuesta.stream()
                .map(dto -> EntidadMapper.toDomain(dto, centro))
                .toList();
    }

    @Override
    public Entidad guardar(EntidadRequestDto entidadRequestDto) throws InvalidEntidadDataException, SQLException, CentroNotFoundException {
        EntidadValidator.validate(entidadRequestDto);
        Centro centro = this.centroService.buscarCentroPorId(entidadRequestDto.idCentro());
        Entidad entidad = EntidadMapper.toDomain(entidadRequestDto, centro);
        return this.entidadRepository.guardar(entidad);
    }

    @Override
    public void eliminar(Long id) throws SQLException, EntidadNotFoundException {
        this.existePorId(id);
        this.entidadRepository.eliminar(id);
    }

    @Override
    public boolean existePorId(Long id) throws SQLException, EntidadNotFoundException {
        if (!this.entidadRepository.existePorId(id)) {
            throw new EntidadNotFoundException("La entidad con %s no fue encontrado".formatted(id));
        }
        return true;
    }
}
