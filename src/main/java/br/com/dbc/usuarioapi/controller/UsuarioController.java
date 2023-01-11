package br.com.dbc.usuarioapi.controller;

import br.com.dbc.usuarioapi.dto.*;
import br.com.dbc.usuarioapi.exception.RegraDeNegocioException;
import br.com.dbc.usuarioapi.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/usuario")
@Validated
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    @Operation(summary = "Buscar usuário logado", description = "Busca o usuário que está logado no sistema")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Busca do usuário logado realizada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso"),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção")
            }
    )
    @GetMapping("/logged-user")
    public ResponseEntity<UsuarioDTO> getLoggedUser() throws RegraDeNegocioException {
        return new ResponseEntity<>(usuarioService.buscarUsuarioLogado(), HttpStatus.OK);
    }

    @Operation(summary = "Realizar login", description = "Realiza o login do usuário")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Token de acesso retornado com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso"),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDTO login) throws RegraDeNegocioException {
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
    public ResponseEntity<Void> delete(@PathVariable("idUsuario") Integer idUsuario) throws RegraDeNegocioException {
        usuarioService.delete(idUsuario);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar cargos", description = "Atualiza os cargos do usuário")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Cargos atualizados com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso"),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção")
            }
    )
    @PutMapping(value = "/update-cargos/{idUsuario}")
    public ResponseEntity<UsuarioDTO> updateCargos(@PathVariable("idUsuario") Integer idUsuario,
                                                   @Valid @RequestBody CargoUpdateDTO cargoUpdateDTO) throws RegraDeNegocioException {
        UsuarioDTO usuarioDTO = usuarioService.updateCargos(idUsuario, cargoUpdateDTO);

        return new ResponseEntity<>(usuarioDTO, HttpStatus.OK);
    }

    @Operation(summary = "Filtar por login e cargo", description = "Filtar por login e cargo do usuário")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Filtrado com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso"),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção")
            }
    )
    @GetMapping("/filtro-login-cargo")
    public ResponseEntity<PageDTO<UsuarioDTO>> filtrarLoginCargo(Integer pagina, Integer tamanho,
                                                                 CargoLoginDTO nomeCargo) throws RegraDeNegocioException {
        return ResponseEntity.ok(usuarioService.filtrar(pagina, tamanho, nomeCargo));
    }

    @Operation(summary = "Filtar por login e cargo", description = "Filtar por login e cargo do usuário")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Usuario encontrado com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Você não tem permissão para acessar este recurso"),
                    @ApiResponse(responseCode = "500", description = "Foi gerada uma exceção")
            }
    )
    @GetMapping("/filtro-usuario-login-cargo")
    public ResponseEntity<PageDTO<UsuarioFiltroDTO>> filtrarUsuarioPorLoginCargo(Integer pagina, Integer tamanho,
                                                                                 CargoLoginDTO nomeCargo) throws RegraDeNegocioException {
        return ResponseEntity.ok(usuarioService.filtroUsuarioNomeCargo(pagina, tamanho, nomeCargo));
    }


}
