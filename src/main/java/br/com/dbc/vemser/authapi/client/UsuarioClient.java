package br.com.dbc.vemser.authapi.client;

import br.com.dbc.vemser.authapi.dto.LoginDTO;
import br.com.dbc.vemser.authapi.dto.TokenDTO;
import feign.Headers;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value="authentication", url="https://ecos.dbccompany.com.br/api/authentication/oauth")
@Headers("Content-Type: multipart/form-data")
@RequestMapping("/token")
public interface UsuarioClient {

    @RequestLine("POST /token")
    TokenDTO post(LoginDTO loginDTO);
}
