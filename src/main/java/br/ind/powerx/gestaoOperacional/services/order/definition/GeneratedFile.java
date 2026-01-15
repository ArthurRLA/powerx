package br.ind.powerx.gestaoOperacional.services.order.definition;

import org.springframework.core.io.InputStreamResource;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneratedFile {

	private String filename;
	private InputStreamResource resource;
}
