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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;
    private final CargoService cargoService;
    private final LoginService loginService;
    private final UsuarioClient usuarioClient;
    private final TokenService tokenService;


    public String post(LoginDTO login) {
        if (login.getUsername().trim().contains("@dbccompany.com.br")) {
            login.setUsername(login.getUsername().trim().replace("@dbccompany.com.br", ""));
        }
        TokenDTO token = (usuarioClient.post(login));
        UsuarioEntity usuarioEntity;
        try {
            usuarioEntity = findByLogin(token.getUsername());
        } catch (RegraDeNegocioException e) {
            UsuarioEntity usuarioEntity1 = new UsuarioEntity();
            usuarioEntity1.setCargos(Collections.emptySet());
            usuarioEntity1.setLogin(token.getUsername());
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

    public UsuarioDTO buscarUsuarioLogado() throws RegraDeNegocioException {
        UsuarioDTO loggedUser = loginService.getLoggedUser();
        UsuarioEntity usuarioEntity = findById(loggedUser.getIdUsuario());
        return toDto(usuarioEntity);
    }

    public UsuarioDTO create(UsuarioCreateDTO usuarioCreateDTO) throws RegraDeNegocioException {

        UsuarioEntity usuarioEntity = new UsuarioEntity();

        Set<CargoEntity> cargos = usuarioCreateDTO.getCargos().stream()
                .map(cargo -> cargoService.findByNome(cargo.getNome())).collect(Collectors.toSet());
        usuarioEntity.setCargos(cargos);
        UsuarioDTO usuarioDTO = toDto(usuarioRepository.save(usuarioEntity));
        Set<CargoDTO> cargosDTO = cargoService.toDtos(cargos);
        usuarioDTO.setCargos(cargosDTO);
        return usuarioDTO;
    }

//    public UsuarioDTO updatePerfil(UsuarioUpdateDTO usuarioUpdate) throws RegraDeNegocioException {
//        Integer idLoggedUser = loginService.getIdLoggedUser();
//
//        UsuarioEntity usuarioRecover = findById(idLoggedUser);
//    }

    public UsuarioDTO updateAdmin(Integer id, UAdminUpdateDTO usuarioUpdate) throws RegraDeNegocioException {

        if (!cargoValido(usuarioUpdate.getCargos())) {
            throw new RegraDeNegocioException("Insira um cargo válido!");
        }

        UsuarioEntity usuarioRecover = findById(id);
        Set<CargoEntity> cargos = usuarioUpdate.getCargos().stream()
                .map(cargo -> (cargoService.findByNome(cargo.getNome()))).collect(Collectors.toSet());
        usuarioRecover.setCargos(cargos);

        return toDto(usuarioRepository.save(usuarioRecover));
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


    public void delete(Integer idUsuario) throws RegraDeNegocioException {
        UsuarioEntity usuarioEntity = this.findById(idUsuario);
        usuarioRepository.delete(usuarioEntity);
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
}