package br.com.dbc.usuarioapi.dto;

import lombok.Data;

@Data
public class LoginDTO {


    private String client_id;

    private String client_secret;

    private String grant_type;

    private String username;

    private String password;

}
