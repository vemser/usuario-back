package br.com.dbc.vemser.authapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class TokenDTO {
    private String access_token;

    private Integer expires_in;

    private String refresh_token;

    private List<String> roles;

    private String scope;

    private String token_type;

    private String username;

}
