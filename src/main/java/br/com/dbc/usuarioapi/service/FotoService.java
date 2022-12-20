package br.com.dbc.usuarioapi.service;

import br.com.dbc.usuarioapi.dto.UsuarioDTO;
import br.com.dbc.usuarioapi.entity.FotoEntity;
import br.com.dbc.usuarioapi.entity.UsuarioEntity;
import br.com.dbc.usuarioapi.exception.RegraDeNegocioException;
import br.com.dbc.usuarioapi.repository.FotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FotoService {

    private final UsuarioService usuarioService;
    private final FotoRepository fotoRepository;
    private final LoginService loginService;

    private final CargoService cargoService;

    public FotoEntity findById(Integer idPerfil) throws RegraDeNegocioException {
        return fotoRepository.findById(idPerfil)
                .orElseThrow(() -> new RegraDeNegocioException("Imagem não encontrada!"));
    }

    public UsuarioDTO uploadImage(Integer idUsuario, MultipartFile imagem) throws RegraDeNegocioException, IOException {
        UsuarioEntity usuario = usuarioService.findById(idUsuario);
        return salvarUsuarioComFotoDTO(imagem, usuario);
    }

    public UsuarioDTO uploadImagePerfil(MultipartFile imagem) throws RegraDeNegocioException, IOException {
        Integer idLoggedUser = loginService.getIdLoggedUser();

        UsuarioEntity usuario = usuarioService.findById(idLoggedUser);
        return salvarUsuarioComFotoDTO(imagem, usuario);
    }

    public UsuarioDTO salvarUsuarioComFotoDTO(MultipartFile imagem, UsuarioEntity usuario) throws IOException {
        FotoEntity fotoRecuperada = fotoRepository.findByUsuario(usuario);
        return getUsuarioDTO(imagem, usuario, fotoRecuperada != null ? fotoRecuperada : new FotoEntity());
    }

    private UsuarioDTO getUsuarioDTO(MultipartFile imagem, UsuarioEntity usuario, FotoEntity fotoEntity) throws IOException {
        UsuarioDTO usuarioDTO;
        String nomeFoto = StringUtils.cleanPath((imagem.getOriginalFilename()));
        fotoEntity.setArquivo(imagem.getBytes());
        fotoEntity.setTipo(imagem.getContentType());
        fotoEntity.setNome(nomeFoto);
        fotoEntity.setUsuario(usuario);
        usuario.setFoto(fotoEntity);
        FotoEntity fotoSaved = fotoRepository.save(fotoEntity);
        usuarioDTO = usuarioService.salvarUsuario(usuario);
        usuarioDTO.setCargos(cargoService.toDtos(usuario.getCargos()));
        usuarioDTO.setImagem(fotoSaved.getArquivo());
        return usuarioDTO;
    }
}