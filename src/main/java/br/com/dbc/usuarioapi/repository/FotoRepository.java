package br.com.dbc.usuarioapi.repository;

import br.com.dbc.usuarioapi.entity.FotoEntity;
import br.com.dbc.usuarioapi.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FotoRepository extends JpaRepository<FotoEntity, Integer> {
    FotoEntity findByUsuario(UsuarioEntity usuario);
}