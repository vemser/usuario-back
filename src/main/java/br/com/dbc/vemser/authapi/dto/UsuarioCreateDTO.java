package br.com.dbc.vemser.authapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@AllArgsConstructor
public class UsuarioCreateDTO {
    @NotNull
    @NotBlank
    @Schema(description = "Login do usuário")
    private String login;
    @NotNull
    @NotEmpty
    @Schema(description = "Lista de cargos do usuário")
    private Set<CargoDTO> cargos;
}
