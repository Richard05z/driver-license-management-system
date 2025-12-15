package centro.service;

import centro.exception.CentroNotFoundException;
import centro.exception.InvalidCentroDataException;
import centro.model.Centro;
import centro.repository.CentroRepository;

import java.sql.SQLException;
import java.util.List;

public class CentroServiceImpl implements  CentroService {

    private final CentroRepository centroRepository;

    public CentroServiceImpl(CentroRepository centroRepository){
        this.centroRepository = centroRepository;
    }

    @Override
    public List<Centro> listarCentros() throws InvalidCentroDataException, SQLException {
        return this.centroRepository.listarCentros();
    }

    @Override
    public Centro listarPorCodigo(String codigo) throws SQLException, CentroNotFoundException {
        return this.centroRepository.listarPorCodigo(codigo);
    }

    @Override
    public Centro guardar(Centro centro) throws InvalidCentroDataException, SQLException {
        CentroValidator.validate(centro);
        return this.centroRepository.guardar(centro);
    }

    @Override
    public void eliminar(Long id) throws SQLException, CentroNotFoundException {
        this.existePorId(id);
        this.centroRepository.eliminar(id);
    }

    @Override
    public Centro actualizar(Centro centro) throws CentroNotFoundException, SQLException {
        this.existePorId(centro.getIdCentro());
        return this.centroRepository.actualizar(centro);
    }

    @Override
    public boolean existePorId(Long id) throws SQLException, CentroNotFoundException {
         if(!this.centroRepository.existePorId(id)){
             throw new CentroNotFoundException("El centro con %s no fue encontrado".formatted(id));
         }
         return true;
    }
}
