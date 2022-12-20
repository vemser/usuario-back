package br.com.dbc.vemser.authapi.service;


import br.com.dbc.vemser.authapi.dto.UsuarioDTO;
import br.com.dbc.vemser.authapi.entity.UsuarioEntity;
import br.com.dbc.vemser.authapi.exception.RegraDeNegocioException;
import br.com.dbc.vemser.authapi.repository.UsuarioRepository;
import br.com.dbc.vemser.authapi.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final ObjectMapper objectMapper;
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    public Integer getIdLoggedUser() {
        return Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    }

    public UsuarioDTO getLoggedUser() {
        Optional<UsuarioEntity> userLogged = findById(getIdLoggedUser());
        return objectMapper.convertValue(userLogged, UsuarioDTO.class);
    }

    public Optional<UsuarioEntity> findById(Integer idLoginUsuario) {
        return usuarioRepository.findById(idLoginUsuario);
    }

}