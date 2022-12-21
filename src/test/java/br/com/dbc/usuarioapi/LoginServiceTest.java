package br.com.dbc.usuarioapi;

import br.com.dbc.usuarioapi.dto.UsuarioDTO;
import br.com.dbc.usuarioapi.entity.UsuarioEntity;
import br.com.dbc.usuarioapi.repository.UsuarioRepository;
import br.com.dbc.usuarioapi.security.TokenService;
import br.com.dbc.usuarioapi.service.LoginService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginServiceTest {

    @InjectMocks
    private LoginService loginService;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenService tokenService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ReflectionTestUtils.setField(loginService, "objectMapper", objectMapper);
    }

    @Test
    public void tstGetIdLoggedUser() {
        //SETUP
        UsernamePasswordAuthenticationToken dto = new UsernamePasswordAuthenticationToken(1,null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(dto);

        //ACT
        Integer idUsuarioLogado = loginService.getIdLoggedUser();

        //ASSERT
        assertEquals(1, idUsuarioLogado);

    }

    @Test
    public void testGetLoggedUser(){

        // SETUP
        UsernamePasswordAuthenticationToken dto = new UsernamePasswordAuthenticationToken(1,null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(dto);
        UsuarioEntity usuarioEntity = getUsuarioEntity();

        when(loginService.findById(anyInt())).thenReturn(Optional.of(usuarioEntity));

        // ACT
        UsuarioDTO usuarioDTO = loginService.getLoggedUser();

        assertNotNull(usuarioDTO);
    }

    @Test
    public void testFindById() {

        //SETUP
        Optional<UsuarioEntity> usuarioEntity = Optional.of(getUsuarioEntity());
        when(usuarioRepository.findById(anyInt())).thenReturn(usuarioEntity);

        //ACT
        Optional<UsuarioEntity> usuarioEntity1 = loginService.findById(1);

        assertNotNull(usuarioEntity1);

    }


    private static UsuarioEntity getUsuarioEntity() {
        UsuarioEntity usuarioEntity = new UsuarioEntity();
        usuarioEntity.setIdUsuario(1);
        usuarioEntity.setLogin("aaa");
        usuarioEntity.setFoto(null);
        usuarioEntity.setCargos(new HashSet<>());

        return usuarioEntity;
    }

}
