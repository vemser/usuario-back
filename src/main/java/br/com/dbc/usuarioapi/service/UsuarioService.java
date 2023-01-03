package br.com.dbc.usuarioapi.service;

import br.com.dbc.usuarioapi.client.UsuarioClient;
import br.com.dbc.usuarioapi.dto.*;
import br.com.dbc.usuarioapi.entity.CargoEntity;
import br.com.dbc.usuarioapi.entity.FotoEntity;
import br.com.dbc.usuarioapi.entity.UsuarioEntity;
import br.com.dbc.usuarioapi.exception.RegraDeNegocioException;
import br.com.dbc.usuarioapi.repository.UsuarioRepository;
import br.com.dbc.usuarioapi.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;
    private final CargoService cargoService;
    private final UsuarioClient usuarioClient;
    private final TokenService tokenService;
    private final LoginService loginService;

    @Value("${credentials.client-id}")
    private String clientId;
    @Value("${credentials.client-secret}")
    private String clientSecret;
    @Value("${credentials.grant-type}")
    private String grantType;

    public UsuarioDTO buscarUsuarioLogado() throws RegraDeNegocioException {
        UsuarioDTO loggedUser = loginService.getLoggedUser();
        UsuarioEntity usuarioEntity = findById(loggedUser.getIdUsuario());
        return toDto(usuarioEntity);
    }

    public String post(LoginDTO login) throws RegraDeNegocioException {
        String loginValidado = validarLogin(login.getUsername());
        login.setUsername(loginValidado);

        CredenciaisDTO credenciaisDTO = gerarCredenciais(login);
        ResponseEcosDTO response = (usuarioClient.post(credenciaisDTO));
        UsuarioEntity usuarioEntity;
        try {
            usuarioEntity = findByLogin(response.getUsername());
        } catch (RegraDeNegocioException e) {
            UsuarioEntity usuarioEntity1 = new UsuarioEntity();
            usuarioEntity1.setCargos(Collections.emptySet());
            usuarioEntity1.setLogin(response.getUsername());
            usuarioEntity1.setFoto(null);
            usuarioEntity = usuarioRepository.save(usuarioEntity1);
        }
        return tokenService.getToken(usuarioEntity);
    }

    public PageDTO<UsuarioDTO> list(Integer pagina, Integer tamanho) {
        PageRequest pageRequest = PageRequest.of(pagina, tamanho);
        Page<UsuarioEntity> paginaDoRepositorio = usuarioRepository.findAll(pageRequest);
        List<UsuarioDTO> usuarios = paginaDoRepositorio.getContent().stream()
                .map(this::toDto)
                .toList();
        return new PageDTO<>(paginaDoRepositorio.getTotalElements(),
                paginaDoRepositorio.getTotalPages(),
                pagina,
                tamanho,
                usuarios
        );
    }

    public UsuarioDTO create(UsuarioCreateDTO usuarioCreateDTO) throws RegraDeNegocioException {
        String loginValidado = validarLogin(usuarioCreateDTO.getLogin());
        usuarioCreateDTO.setLogin(loginValidado);

        Optional<UsuarioEntity> byLogin = usuarioRepository.findByLogin(usuarioCreateDTO.getLogin());
        if (byLogin.isPresent()) {
            throw new RegraDeNegocioException("Login informado já existe!");
        }

        validarCargos(usuarioCreateDTO.getCargos());
        UsuarioEntity usuarioEntity = new UsuarioEntity();
        usuarioEntity.setLogin(usuarioCreateDTO.getLogin());

        Set<CargoEntity> cargos = usuarioCreateDTO.getCargos().stream()
                .map(cargo -> cargoService.findByNome(cargo.getNome())).collect(Collectors.toSet());
        usuarioEntity.setCargos(cargos);
        UsuarioDTO usuarioDTO = toDto(usuarioRepository.save(usuarioEntity));
        Set<CargoDTO> cargosDTO = cargoService.toDtos(cargos);
        usuarioDTO.setCargos(cargosDTO);
        return usuarioDTO;
    }

    public UsuarioDTO updateCargos(Integer id, CargoUpdateDTO cargoUpdate) throws RegraDeNegocioException {
        validarCargos(cargoUpdate.getCargos());

        UsuarioEntity usuarioRecover = findById(id);
        Set<CargoEntity> cargos = cargoUpdate.getCargos().stream()
                .map(cargo -> (cargoService.findByNome(cargo.getNome()))).collect(Collectors.toSet());
        usuarioRecover.setCargos(cargos);

        return toDto(usuarioRepository.save(usuarioRecover));
    }

    public void delete(Integer idUsuario) throws RegraDeNegocioException {
        UsuarioEntity usuarioEntity = this.findById(idUsuario);
        usuarioRepository.delete(usuarioEntity);
    }

    public void validarCargos(Set<CargoCreateDTO> cargosUsuario) throws RegraDeNegocioException {
        List<CargoEntity> cargosBanco = cargoService.list();
        List<String> nomesCargos = cargosBanco.stream()
                .map(CargoEntity::getNome)
                .toList();
        boolean equals = true;

        for(CargoCreateDTO cargoUsuario : cargosUsuario) {
            if(!nomesCargos.contains(cargoUsuario.getNome())) {
                equals = false;
            }
        }
        if(!equals) {
            throw new RegraDeNegocioException("Cargo inválido!");
        }
    }

    public UsuarioEntity findById(Integer idUsuario) throws RegraDeNegocioException {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado!"));
    }

    public UsuarioEntity findByLogin(String login) throws RegraDeNegocioException {
        return usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado!"));
    }

    public UsuarioDTO salvarUsuario(UsuarioEntity usuario) {
        return objectMapper.convertValue(usuarioRepository.save(usuario), UsuarioDTO.class);
    }

    private UsuarioDTO toDto(UsuarioEntity usuario) {
        UsuarioDTO usuarioDTO = objectMapper.convertValue(usuario, UsuarioDTO.class);
        usuarioDTO.setCargos(cargoService.toDtos(usuario.getCargos()));
        FotoEntity foto = usuario.getFoto();
        usuarioDTO.setImagem(foto == null ? null : foto.getArquivo());
        return usuarioDTO;
    }

    private String validarLogin(String login) throws RegraDeNegocioException {
        if(login.trim().contains("@")) {
            if (login.trim().endsWith("@dbccompany.com.br")) {
                login = login.trim().replace("@dbccompany.com.br", "");
            } else {
                throw new RegraDeNegocioException("Login informado não segue o padrão @dbccompany.com.br");
            }
        }
        return login;
    }

    private CredenciaisDTO gerarCredenciais(LoginDTO login) {
        CredenciaisDTO credenciais = new CredenciaisDTO();
        credenciais.setClient_id(clientId);
        credenciais.setClient_secret(clientSecret);
        credenciais.setGrant_type(grantType);
        credenciais.setUsername(login.getUsername());
        credenciais.setPassword(login.getPassword());
        return credenciais;
    }

    public PageDTO<UsuarioDTO> filtrar(Integer pagina, Integer tamanho, CargoLoginDTO cargoLogin) throws RegraDeNegocioException {
        PageRequest pageRequest = PageRequest.of(pagina, tamanho);

        Page<UsuarioEntity> usuarioEntityPage;

        if (cargoLogin.getNomes() == null){
            usuarioEntityPage = usuarioRepository
                    .findUsuariosByLoginContainingIgnoreCaseOrderByLogin(pageRequest, cargoLogin.getLogin());
        }else {
            Set<CargoCreateDTO> validar = cargoLogin.getNomes().stream()
                    .map(x -> new CargoCreateDTO(x, null)).collect(Collectors.toSet());

            validarCargos(validar);

            usuarioEntityPage = usuarioRepository
                    .findAllByFiltro(pageRequest, cargoLogin.getLogin(), cargoLogin.getNomes());
        }

        List<UsuarioDTO> usuarioDTOList = getUsuarioDtos(usuarioEntityPage);

        return new PageDTO<>(usuarioEntityPage.getTotalElements(),
                usuarioEntityPage.getTotalPages(),
                pagina,
                tamanho,
                usuarioDTOList);
    }

    @NotNull
    private List<UsuarioDTO> getUsuarioDtos(Page<UsuarioEntity> usuarioEntities) {
        List<UsuarioDTO> usuarioDTOList = usuarioEntities
                .getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return usuarioDTOList;
    }
}