package br.com.dbc.usuarioapi.repository;

import br.com.dbc.usuarioapi.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {

   Optional<UsuarioEntity> findByLogin(String login);



}