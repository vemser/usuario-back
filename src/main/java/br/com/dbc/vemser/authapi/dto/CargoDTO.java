package br.com.dbc.vemser.authapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class CargoDTO {
    @JsonIgnore
    private Integer idCargo;
    private String nome;
    private String descricao;
}