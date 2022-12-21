package br.com.dbc.usuarioapi;

import br.com.dbc.usuarioapi.entity.CargoEntity;
import br.com.dbc.usuarioapi.exception.RegraDeNegocioException;
import br.com.dbc.usuarioapi.repository.CargoRepository;
import br.com.dbc.usuarioapi.service.CargoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CargoServiceTest {

    @InjectMocks
    private CargoService cargoService;
    @Mock
    private CargoRepository cargoRepository;

    @Test
    public void testFindByIdSuccess() throws RegraDeNegocioException {
        // SETUP
        CargoEntity cargoEntity = getCargoEntity();
        when(cargoRepository.findById(anyInt())).thenReturn(Optional.of(cargoEntity));

        // ACT
        CargoEntity cargo = cargoService.findById(cargoEntity.getIdCargo());

        // ASSERT
        assertNotNull(cargo);
        assertEquals(10, cargoEntity.getIdCargo());

    }

    @Test
    public void testFindByNome() throws RegraDeNegocioException {
        // SETUP
        CargoEntity cargoEntity = getCargoEntity();
        when(cargoRepository.findByNome(anyString())).thenReturn(cargoEntity);

        // ACT
        CargoEntity cargo = cargoService.findByNome(cargoEntity.getNome());

        // ASSERT
        assertNotNull(cargo);
        assertEquals("ADMIN", cargoEntity.getNome());

    }

    private CargoEntity getCargoEntity(){
        CargoEntity cargoEntity = new CargoEntity();
        cargoEntity.setIdCargo(10);
        cargoEntity.setNome("ADMIN");
        cargoEntity.setDescricao("DESCRICAO");

        cargoEntity.setUsuarios(new HashSet<>());

        return cargoEntity;
    }


}
