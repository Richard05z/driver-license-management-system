package licencia.mapper;

import conductor.model.Conductor;
import licencia.dtos.LicenciaDaoResponseDto;
import licencia.dtos.LicenciaRequestDto;
import licencia.model.Licencia;
import licencia.vo.CategoriaLicencia;
import licencia.vo.TipoLicencia;

public class LicenciaMapper {

    public static Licencia toDomain(LicenciaRequestDto requestDto, Conductor conductor) {
        return new Licencia(
                conductor,
                requestDto.tipoLicencia(),
                requestDto.categoriaLicencia(),
                requestDto.fechaEmision(),
                requestDto.fechaVencimiento(),
                requestDto.restricciones(),
                requestDto.renovada(),
                requestDto.puntos()
        );
    }

    public static Licencia toDomain(LicenciaDaoResponseDto licenciaDaoResponseDto, Conductor conductor) {
        return new Licencia(
                conductor,
                TipoLicencia.valueOf(licenciaDaoResponseDto.tipoLicencia()),
                CategoriaLicencia.valueOf(licenciaDaoResponseDto.categoriaLicencia()),
                licenciaDaoResponseDto.fechaEmision(),
                licenciaDaoResponseDto.fechaVencimiento(),
                licenciaDaoResponseDto.restricciones(),
                licenciaDaoResponseDto.renovada(),
                licenciaDaoResponseDto.puntos()
        );
    }
}
