package br.com.dbc.vemser.authapi.repository;

import br.com.dbc.vemser.authapi.entity.CargoEntity;
import br.com.dbc.vemser.authapi.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CargoRepository extends JpaRepository<CargoEntity, Integer> {

    CargoEntity findByNome(String nome);

}