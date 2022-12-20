package br.com.dbc.vemser.authapi.repository;

import br.com.dbc.vemser.authapi.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {

   Optional<UsuarioEntity> findByLogin(String login);



}