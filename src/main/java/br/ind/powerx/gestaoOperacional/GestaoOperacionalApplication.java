package br.ind.powerx.gestaoOperacional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GestaoOperacionalApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestaoOperacionalApplication.class, args);
	}
}
