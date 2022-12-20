package br.com.dbc.vemser.authapi.controller;

import br.com.dbc.vemser.authapi.dto.*;
import br.com.dbc.vemser.authapi.exception.RegraDeNegocioException;
import br.com.dbc.vemser.authapi.service.FotoService;
import br.com.dbc.vemser.authapi.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;


@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class UsuarioController {

    private final FotoService fotoService;
    private final UsuarioService usuarioService;


    @Operation(summary = "Faz o login", description = "Login no sistema")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna o token de acesso."),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso"),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO login) {
        String user = (usuarioService.post(login));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<PageDTO<UsuarioDTO>> list(Integer pagina, Integer tamanho) {
        return new ResponseEntity<>(usuarioService.list(pagina, tamanho), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> create(@Valid @RequestBody UsuarioCreateDTO usuarioCreateDTO) throws RegraDeNegocioException {
        UsuarioDTO usuarioDTO = usuarioService.create(usuarioCreateDTO);
        return new ResponseEntity<>(usuarioDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<Void> delete(@Valid @PathVariable("idUsuario") Integer idUsuario) throws RegraDeNegocioException {
        usuarioService.delete(idUsuario);
        return ResponseEntity.noContent().build();
    }


    @PutMapping(value = "/upload-image/{idUsuario}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<UsuarioDTO> uploadImage(@Valid @PathVariable("idUsuario") Integer idUsuario,
                                                  @Valid @RequestPart("imagem") MultipartFile imagem) throws RegraDeNegocioException, IOException {
        return new ResponseEntity<>(fotoService.uploadImage(idUsuario, imagem), HttpStatus.OK);
    }

    @PutMapping(value = "/upload-image-perfil", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<UsuarioDTO> uploadImagePerfil(@Valid @RequestPart("imagem") MultipartFile imagem) throws RegraDeNegocioException, IOException {
        return new ResponseEntity<>(fotoService.uploadImagePerfil(imagem), HttpStatus.OK);
    }

}
