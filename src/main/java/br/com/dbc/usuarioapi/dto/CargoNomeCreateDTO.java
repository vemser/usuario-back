package br.com.dbc.usuarioapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class CargoNomeCreateDTO {

    private List<String> nome;
}
