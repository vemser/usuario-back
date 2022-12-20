package br.com.dbc.usuarioapi.repository;

import br.com.dbc.usuarioapi.entity.CargoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CargoRepository extends JpaRepository<CargoEntity, Integer> {

    CargoEntity findByNome(String nome);

}