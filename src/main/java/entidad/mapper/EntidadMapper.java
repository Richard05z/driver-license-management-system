package entidad.mapper;

import centro.model.Centro;
import entidad.dto.EntidadDaoResponseDto;
import entidad.dto.EntidadRequestDto;
import entidad.model.Entidad;
import entidad.vo.TipoEntidad;

public class EntidadMapper {

    public static Entidad toDomain(EntidadRequestDto requestDto, Centro centro){
        return new Entidad(
                requestDto.nombre(),
                requestDto.tipoEntidad(),
                requestDto.direccion(),
                requestDto.telefono(),
                requestDto.email(),
                requestDto.directorGeneral(),
                centro
        );
    }
    public static Entidad toDomain(EntidadDaoResponseDto entidadDaoResponseDto, Centro centro){
        return new Entidad(
                entidadDaoResponseDto.nombre(),
                TipoEntidad.valueOf(entidadDaoResponseDto.tipoEntidad()),
                entidadDaoResponseDto.direccion(),
                entidadDaoResponseDto.telefono(),
                entidadDaoResponseDto.email(),
                entidadDaoResponseDto.directorGeneral(),
                centro
        );
    }
}
