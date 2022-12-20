package br.com.dbc.vemser.authapi.service;

import br.com.dbc.vemser.authapi.client.UsuarioClient;
import br.com.dbc.vemser.authapi.dto.LoginDTO;
import br.com.dbc.vemser.authapi.dto.TokenDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UsuarioService {

    private final UsuarioClient usuarioClient;

    public String post(LoginDTO login) {
        if (login.getUsername().contains("@dbccompany.com.br")) {
            login.setUsername(login.getUsername().replace("@dbccompany.com.br", ""));
        }
        TokenDTO token = (usuarioClient.post(login));
        return token.getUsername();
    }
}