package br.com.dbc.vemser.authapi.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UsuarioDTO {
    private Integer IdUsuario;
    private String login;
    private Set<CargoDTO> cargos;
    private byte[] imagem;
}