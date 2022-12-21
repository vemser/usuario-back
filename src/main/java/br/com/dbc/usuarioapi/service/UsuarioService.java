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
        }else if (!cargoValido(usuarioCreateDTO.getCargos())){
            throw new RegraDeNegocioException("Insira um cargo válido!");
        }
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
        if (!cargoValido(cargoUpdate.getCargos())) {
            throw new RegraDeNegocioException("Insira um cargo válido!");
        }

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

    public boolean cargoValido(Set<CargoCreateDTO> cargoEntities) {

        String admin = "ROLE_ADMIN";
        String coordenador = "ROLE_GESTOR";
        String gestao = "ROLE_GESTAO_DE_PESSOAS";
        String instrutor = "ROLE_INSTRUTOR";
        String aluno = "ROLE_ALUNO";
        String colaborador = "ROLE_COLABORADOR";

        for (CargoCreateDTO cargo : cargoEntities) {
            if (!Objects.equals(cargo.getNome().trim(), admin) &&
                    !Objects.equals(cargo.getNome().trim(), coordenador) &&
                    !Objects.equals(cargo.getNome().trim(), gestao) &&
                    !Objects.equals(cargo.getNome().trim(), instrutor) &&
                    !Objects.equals(cargo.getNome().trim(), aluno) &&
                    !Objects.equals(cargo.getNome().trim(), colaborador)) {
                return false;
            }
        }
        return true;
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

    private UsuarioEntity toEntity(UsuarioDTO usuario) {
        UsuarioEntity usuarioEntity = objectMapper.convertValue(usuario, UsuarioEntity.class);
        usuarioEntity.setCargos(cargoService.toEntities(usuario.getCargos()));
        return usuarioEntity;
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
        credenciais.setClient_id("ECOS-Client");
        credenciais.setClient_secret("DBC-ECOS");
        credenciais.setGrant_type("password");
        credenciais.setUsername(login.getUsername());
        credenciais.setPassword(login.getPassword());
        return credenciais;
    }
}