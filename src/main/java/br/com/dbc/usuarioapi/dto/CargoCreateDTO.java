package br.com.dbc.usuarioapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CargoCreateDTO {
    @NotNull
    @NotBlank
    private String nome;

    @NotNull
    @NotBlank
    private String descricao;
}
