package br.com.dbc.usuarioapi.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UsuarioFiltroDTO {
    private String login;
    private Set<CargoDTO> cargos;
}