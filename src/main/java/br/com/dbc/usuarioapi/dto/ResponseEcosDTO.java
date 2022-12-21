package br.com.dbc.usuarioapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResponseEcosDTO {
    private String access_token;

    private Integer expires_in;

    private String refresh_token;

    private List<String> roles;

    private String scope;

    private String token_type;

    private String username;

}
