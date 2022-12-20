package br.com.dbc.usuarioapi.security;

import br.com.dbc.usuarioapi.entity.UsuarioEntity;
import br.com.dbc.usuarioapi.exception.RegraDeNegocioException;
import br.com.dbc.usuarioapi.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {

    private final UsuarioService usuarioService;
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UsuarioEntity usuario = null;
        try {
            usuario = usuarioService.findByLogin(login);
        } catch (RegraDeNegocioException e) {
            throw new RuntimeException(e);
        }
        if(usuario == null) {
            throw new UsernameNotFoundException("Usuario inválido");
        } else {
            return usuario;
        }
    }
}
