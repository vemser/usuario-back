package br.com.dbc.usuarioapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class CargoLoginDTO {

    private List<String> nomes;
    private String login;
}
