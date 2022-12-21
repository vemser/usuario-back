package br.com.dbc.usuarioapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableFeignClients
@SpringBootApplication
public class UsuarioApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsuarioApplication.class, args);
	}

}
