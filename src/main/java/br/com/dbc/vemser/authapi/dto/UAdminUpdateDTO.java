package br.com.dbc.vemser.authapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
public class UAdminUpdateDTO {

    @NotEmpty
    @Schema(description = "Cargos do usuário")
    private Set<CargoCreateDTO> cargos;
}
