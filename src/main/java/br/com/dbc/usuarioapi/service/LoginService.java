package br.com.dbc.usuarioapi.service;

import br.com.dbc.usuarioapi.dto.UsuarioDTO;
import br.com.dbc.usuarioapi.entity.UsuarioEntity;
import br.com.dbc.usuarioapi.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final ObjectMapper objectMapper;
    private final UsuarioRepository usuarioRepository;

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