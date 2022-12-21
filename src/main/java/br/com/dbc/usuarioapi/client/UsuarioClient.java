package br.com.dbc.usuarioapi.client;

import br.com.dbc.usuarioapi.dto.CredenciaisDTO;
import br.com.dbc.usuarioapi.dto.TokenDTO;
import feign.Headers;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value="authentication", url="https://ecos.dbccompany.com.br/api/authentication/oauth")
@Headers("Content-Type: multipart/form-data")
@RequestMapping("/usuario")
public interface UsuarioClient {

    @RequestLine("POST /token")
    TokenDTO post(CredenciaisDTO credenciais);
}
