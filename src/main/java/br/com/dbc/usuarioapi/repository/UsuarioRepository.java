package br.com.dbc.usuarioapi.repository;


import br.com.dbc.usuarioapi.entity.UsuarioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {

   Optional<UsuarioEntity> findByLogin(String login);

   @Query(" SELECT distinct obj " +
           " from USUARIO obj " +
           " left JOIN obj.cargos c " +
           " WHERE (:login is null or UPPER(obj.login) LIKE UPPER(concat('%', :login, '%'))) AND " +
           " (UPPER(c.nome) IN (:nomeCargo)) " +
           " ORDER BY obj.login ")
   Page<UsuarioEntity> findAllByFiltro(Pageable pageable, String login, List<String> nomeCargo);

   Page<UsuarioEntity> findUsuariosEntitiesByLoginContainingIgnoreCase(Pageable pageable, String login);
}