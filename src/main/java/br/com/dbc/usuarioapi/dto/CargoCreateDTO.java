package br.com.dbc.usuarioapi.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CargoCreateDTO {
    @NotNull
    @NotBlank
    private String nome;

    @NotNull
    @NotBlank
    private String descricao;
}
