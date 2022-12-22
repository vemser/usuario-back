package br.com.dbc.usuarioapi.repository;



import br.com.dbc.usuarioapi.entity.CargoEntity;
import br.com.dbc.usuarioapi.entity.UsuarioEntity;
import org.hibernate.validator.constraints.ParameterScriptAssert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {

   Optional<UsuarioEntity> findByLogin(String login);

   @Query(" SELECT distinct obj " +
           " from USUARIO obj " +
           " inner JOIN obj.cargos c " +
           " WHERE (:login is null or UPPER(obj.login) LIKE UPPER(concat('%', :login, '%'))) AND " +
           " (:nomeCargo is null or UPPER(c.nome) in (:nomeCargo)) " +
           " ORDER BY obj.login ")
   Page<UsuarioEntity> findAllByFiltro(Pageable pageable, String login, List<String> nomeCargo);
}