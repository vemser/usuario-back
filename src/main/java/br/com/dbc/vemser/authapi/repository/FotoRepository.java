package br.com.dbc.vemser.authapi.repository;

import br.com.dbc.vemser.authapi.entity.FotoEntity;
import br.com.dbc.vemser.authapi.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FotoRepository extends JpaRepository<FotoEntity, Integer> {
    FotoEntity findByUsuario(UsuarioEntity usuario);
}