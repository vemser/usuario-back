package br.com.dbc.usuarioapi.controller;

import br.com.dbc.usuarioapi.dto.UsuarioDTO;
import br.com.dbc.usuarioapi.exception.RegraDeNegocioException;
import br.com.dbc.usuarioapi.service.FotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/foto")
public class FotoController {

    private final FotoService fotoService;

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
