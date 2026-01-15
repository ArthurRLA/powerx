package br.ind.powerx.gestaoOperacional.services.report.definition;

import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DetailmentData<T, K> {

	private String identifier;
	private Function<T, K> data;
	
}
