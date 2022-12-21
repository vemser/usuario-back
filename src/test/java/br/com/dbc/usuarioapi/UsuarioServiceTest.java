package br.com.dbc.usuarioapi;

import br.com.dbc.usuarioapi.dto.*;
import br.com.dbc.usuarioapi.entity.CargoEntity;
import br.com.dbc.usuarioapi.entity.FotoEntity;
import br.com.dbc.usuarioapi.entity.UsuarioEntity;
import br.com.dbc.usuarioapi.exception.RegraDeNegocioException;
import br.com.dbc.usuarioapi.repository.UsuarioRepository;
import br.com.dbc.usuarioapi.service.CargoService;
import br.com.dbc.usuarioapi.service.LoginService;
import br.com.dbc.usuarioapi.service.UsuarioService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CargoService cargoService;

    @Mock
    private LoginService loginService;

    @Before
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ReflectionTestUtils.setField(usuarioService, "objectMapper", objectMapper);
    }

    @Test
    public void deveTestarListComSucesso(){
        // SETUP
        Integer pagina = 5;
        Integer quantidade = 3;

        UsuarioEntity usuarioEntity = getUsuarioEntity();
        Page<UsuarioEntity> paginaMock = new PageImpl<>(List.of(usuarioEntity));
        when(usuarioRepository.findAll(any(Pageable.class))).thenReturn(paginaMock);

        // ACT
        PageDTO<UsuarioDTO> usuarioDTO = usuarioService.list(pagina, quantidade);

        // ASSERT
        assertNotNull(usuarioDTO);
        assertEquals(1, usuarioDTO.getQuantidadePaginas());
        assertEquals(1, usuarioDTO.getTotalElementos());
    }

    @Test
    public void deveTestarListComUsuarioComFotoSucess() throws IOException {
        // SETUP
        Integer pagina = 5;
        Integer quantidade = 3;

        UsuarioEntity usuarioEntity = getUsuarioEntity();
        FotoEntity fotoEntity = getFotoEntity();
        usuarioEntity.setFoto(fotoEntity);
        fotoEntity.setUsuario(usuarioEntity);
        Page<UsuarioEntity> paginaMock = new PageImpl<>(List.of(usuarioEntity));
        when(usuarioRepository.findAll(any(Pageable.class))).thenReturn(paginaMock);

        // ACT
        PageDTO<UsuarioDTO> usuarioDTO = usuarioService.list(pagina, quantidade);

        // ASSERT
        assertNotNull(usuarioDTO);
        assertEquals(1, usuarioDTO.getQuantidadePaginas());
        assertEquals(1, usuarioDTO.getTotalElementos());
    }



//    @Test
//    public void deveTestarBuscarUsuarioLogado() throws RegraDeNegocioException {
//
//        // SETUP
//        UsuarioEntity usuarioEntity = getUsuarioEntity();
//        UsuarioDTO usuarioDTO = getUsuarioDTO();
//
//        when(usuarioRepository.findById(any())).thenReturn(Optional.of(usuarioEntity));
//        when(loginService.getLoggedUser()).thenReturn(usuarioDTO);
//
//        // ACT
//        UsuarioDTO usuarioDTO1 = usuarioService.buscarUsuarioLogado();
//
//        assertNotNull(usuarioDTO);
//        assertEquals(usuarioEntity.getEmail(), usuarioDTO.getEmail());
//
//    }

//    @Test
//    public void deveTestarBuscarUsuarioComFotoLogado() throws RegraDeNegocioException, IOException {
//
//        // SETUP
//        UsuarioEntity usuarioEntity = getUsuarioEntity();
//        UsuarioDTO usuarioDTO = getUsuarioDTO();
//        FotoEntity fotoEntity = getFotoEntity();
//        usuarioEntity.setFoto(fotoEntity);
//        fotoEntity.setUsuario(usuarioEntity);
//        usuarioDTO.setImagem(fotoEntity.getArquivo());
//        when(usuarioRepository.findById(any())).thenReturn(Optional.of(usuarioEntity));
//        when(loginService.getLoggedUser()).thenReturn(usuarioDTO);
//
//        // ACT
//        UsuarioDTO usuarioDTO1 = usuarioService.buscarUsuarioLogado();
//
//        assertNotNull(usuarioDTO);
//        assertEquals(usuarioEntity.getEmail(), usuarioDTO.getEmail());
//
//    }

    @Test
    public void testSalvarUsuarioSucess() {
        UsuarioEntity usuario = getUsuarioEntity();
        when(usuarioRepository.save(any())).thenReturn(usuario);

        UsuarioDTO usuarioDTO = usuarioService.salvarUsuario(usuario);

        verify(usuarioRepository, times(1)).save(any());
    }

    @Test
    public void deveTestarCreateComSucesso() throws RegraDeNegocioException, IOException {
        // SETUP
        String senhaCriptografada = "$oieufr9873he4j809fy43";
        UsuarioCreateDTO usuarioCreate = getUsuarioCreateDTO();


        when(usuarioRepository.save(any())).thenReturn(getUsuarioEntity());

        // ACT
        UsuarioDTO usuarioDTO = usuarioService.create(usuarioCreate);

        // ASSERT
        assertNotNull(usuarioDTO);
        assertEquals(1, usuarioDTO.getIdUsuario());
        verify(usuarioRepository, times(1)).save(any());
    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveTestarCreateFail() throws RegraDeNegocioException, IOException {

        // SETUP
        String senhaCriptografada = "$oieufr9873he4j809fy43";
        UsuarioCreateDTO usuarioCreate = getUsuarioCreateDTO();
        UsuarioEntity usuarioEntity = getUsuarioEntity();

        when(usuarioService.findByLogin(anyString())).thenReturn(usuarioEntity);

        // ACT
        usuarioService.create(usuarioCreate);

    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveTestarCreateFalha() throws RegraDeNegocioException, IOException {
        // SETUP
        Integer idUsuario = 1;
        UsuarioCreateDTO usuarioCreateDTO = getUsuarioCreateDTO();
        UsuarioEntity usuarioEntity = getUsuarioEntity();

        Set<CargoEntity> cargoEntitiesSet = new HashSet<>();
        cargoEntitiesSet.add(getCargoEntityInstrutor());
        cargoEntitiesSet.add(getCargoEntity());

        Set<CargoCreateDTO> cargoCreateDTOS  = new HashSet<>();

        cargoCreateDTOS.add(getCargoCreateDTOFalse());

        usuarioCreateDTO.setCargos(cargoCreateDTOS);

        usuarioEntity.setCargos(cargoEntitiesSet);

        // ACT
        UsuarioDTO usuarioDTO = usuarioService.create(usuarioCreateDTO);

        // ASSERT
        assertNotNull(usuarioDTO);
        assertEquals(1, usuarioDTO.getIdUsuario());
        assertEquals("Luiz Martins", usuarioDTO.getLogin());
        verify(usuarioRepository, times(1)).save(any());
    }

    @Test
    public void deveTestarDeleteComSucesso() throws RegraDeNegocioException {
        // SETUP
        Integer idUsuario = 1;
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        usuarioEntity.setIdUsuario(1);
        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioEntity));

        // Ação (ACT)
        usuarioService.delete(idUsuario);

        // Verificação (ASSERT)
        verify(usuarioRepository, times(1)).delete(any());
    }

    @Test
    public void deveTestarFindByLoginComSucesso() throws RegraDeNegocioException {
        // Criar variaveis (SETUP)
        String login = "gustavo.linck";
        UsuarioEntity usuarioRecuperado = getUsuarioEntity();
        usuarioRecuperado.setLogin(login);
        when(usuarioRepository.findByLogin(anyString())).thenReturn(Optional.of(usuarioRecuperado));

        // Ação (ACT)
        UsuarioEntity usuarioEntity = usuarioService.findByLogin(login);

        // Verificação (ASSERT)
        assertNotNull(usuarioEntity);
        assertEquals(login, usuarioEntity.getLogin());
    }

    private static UsuarioEntity getUsuarioEntity() {
        UsuarioEntity usuarioEntity = new UsuarioEntity();
        usuarioEntity.setIdUsuario(1);
        usuarioEntity.setLogin("gustavo.ferreira");
        usuarioEntity.setCargos(new HashSet<>());

        return usuarioEntity;
    }

    private static UsuarioCreateDTO getUsuarioCreateDTO() {
        UsuarioCreateDTO usuarioCreateDTO = new UsuarioCreateDTO();
        usuarioCreateDTO.setLogin("gustavo.ferreira");
        usuarioCreateDTO.setCargos(new HashSet<>());

        return usuarioCreateDTO;
    }

    private static UsuarioDTO getUsuarioDTO() {
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setLogin("gustavo.ferreira");
        usuarioDTO.setIdUsuario(10);

        usuarioDTO.setCargos(new HashSet<>());

        return usuarioDTO;
    }

    private static FotoEntity getFotoEntity() throws IOException {
        FotoEntity fotoEntity = new FotoEntity();
        byte[] imagemBytes = new byte[5*1024];
        MultipartFile imagem = new MockMultipartFile("imagem", imagemBytes);
        String nomeFoto = StringUtils.cleanPath((imagem.getOriginalFilename()));
        fotoEntity.setIdFoto(1);
        fotoEntity.setTipo(imagem.getContentType());
        fotoEntity.setArquivo(imagem.getBytes());
        fotoEntity.setNome(nomeFoto);
        return fotoEntity;
    }


    private static CargoEntity getCargoEntity() {
        CargoEntity cargoEntity = new CargoEntity();
        cargoEntity.setNome("ROLE_ADMIN");
        return cargoEntity;
    }

    private static CargoEntity getCargoEntityInstrutor() {
        CargoEntity cargoEntity = new CargoEntity();
        cargoEntity.setNome("ROLE_INSTRUTOR");
        return cargoEntity;
    }

    private static CargoEntity getCargoEntityFalse() {
        CargoEntity cargoEntity = new CargoEntity();
        cargoEntity.setNome("ROLE_HIHIHI");
        return cargoEntity;
    }

    private static CargoCreateDTO getCargoCreateDTOFalse() {
        CargoCreateDTO cargoCreateDTO = new CargoCreateDTO();
        cargoCreateDTO.setNome("ROLE_HAHA");
        return cargoCreateDTO;
    }

    private static CargoCreateDTO getCargoCreateDTOAdmin() {
        CargoCreateDTO cargoCreateDTO = new CargoCreateDTO();
        cargoCreateDTO.setNome("ROLE_ADMIN");
        return cargoCreateDTO;
    }

    private static CargoCreateDTO getCargoCreateDTOInstrutor() {
        CargoCreateDTO cargoCreateDTO = new CargoCreateDTO();
        cargoCreateDTO.setNome("ROLE_INSTRUTOR");
        return cargoCreateDTO;
    }

}
