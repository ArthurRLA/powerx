package br.ind.powerx.gestaoOperacional.services.report.definition;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GroupingLevel<T, K> {

	private String title;
	private Function<T, String> id;
	private Function<T, K> classifier;
	private Function<List<T>, BigDecimal> subtotalCalculator;
}
