package br.com.dbc.usuarioapi.controller;

import br.com.dbc.usuarioapi.dto.LoginDTO;
import br.com.dbc.usuarioapi.dto.PageDTO;
import br.com.dbc.usuarioapi.dto.UsuarioCreateDTO;
import br.com.dbc.usuarioapi.dto.UsuarioDTO;
import br.com.dbc.usuarioapi.exception.RegraDeNegocioException;
import br.com.dbc.usuarioapi.service.FotoService;
import br.com.dbc.usuarioapi.service.UsuarioService;
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
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final FotoService fotoService;
    private final UsuarioService usuarioService;


    @Operation(summary = "Realizar login", description = "Realiza o login do usuário")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Token de acesso retornado com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso"),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO login) {
        String user = (usuarioService.post(login));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @Operation(summary = "Listar usuários", description = "Lista os usuários cadastrados no sistema")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso"),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção")
            }
    )
    @GetMapping
    public ResponseEntity<PageDTO<UsuarioDTO>> list(Integer pagina, Integer tamanho) {
        return new ResponseEntity<>(usuarioService.list(pagina, tamanho), HttpStatus.OK);
    }

    @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso"),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção")
            }
    )
    @PostMapping
    public ResponseEntity<UsuarioDTO> create(@Valid @RequestBody UsuarioCreateDTO usuarioCreateDTO) throws RegraDeNegocioException {
        UsuarioDTO usuarioDTO = usuarioService.create(usuarioCreateDTO);
        return new ResponseEntity<>(usuarioDTO, HttpStatus.OK);
    }

    @Operation(summary = "Deletar usuário", description = "Deleta um usuário do sistema")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Usuário deletado com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso"),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção")
            }
    )
    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<Void> delete(@Valid @PathVariable("idUsuario") Integer idUsuario) throws RegraDeNegocioException {
        usuarioService.delete(idUsuario);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar imagem de perfil", description = "Atualiza a imagem de perfil do usuário")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Imagem atualizada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso"),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção")
            }
    )
    @PutMapping(value = "/upload-image-perfil", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<UsuarioDTO> uploadImagePerfil(@Valid @RequestPart("imagem") MultipartFile imagem) throws RegraDeNegocioException, IOException {
        return new ResponseEntity<>(fotoService.uploadImagePerfil(imagem), HttpStatus.OK);
    }
}
