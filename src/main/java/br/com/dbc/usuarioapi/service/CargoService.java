package br.com.dbc.usuarioapi.service;

import br.com.dbc.usuarioapi.dto.CargoDTO;
import br.com.dbc.usuarioapi.entity.CargoEntity;
import br.com.dbc.usuarioapi.exception.RegraDeNegocioException;
import br.com.dbc.usuarioapi.repository.CargoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CargoService {
    private final CargoRepository cargoRepository;
    private final ObjectMapper objectMapper;

    public CargoEntity findById(Integer id) throws RegraDeNegocioException {
        return cargoRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Cargo não encontrado!"));
    }

    public CargoEntity findByNome(String nome) {
        return cargoRepository.findByNome(nome);
    }

    public CargoEntity toEntity(CargoDTO cargoDTO) {
        return objectMapper.convertValue(cargoDTO, CargoEntity.class);
    }

    public CargoDTO toDto(CargoEntity cargoEntity) {
        return objectMapper.convertValue(cargoEntity, CargoDTO.class);
    }

    public List<CargoEntity> list() {
        return cargoRepository.findAll();
    }
    public Set<CargoDTO> toDtos(Set<CargoEntity> cargos) {
        return cargos.stream()
                .map(cargoEntity -> toDto(cargoEntity))
                .collect(Collectors.toSet());
    }

    public Set<CargoEntity> toEntities(Set<CargoDTO> cargos) {
        return cargos.stream()
                .map(cargoDTO -> findByNome(cargoDTO.getNome()))
                .collect(Collectors.toSet());
    }

}