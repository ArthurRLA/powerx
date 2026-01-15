package br.ind.powerx.gestaoOperacional.services.report.renderer;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface DetailsRenderer<T> {
	List<Map<String, Object>> renderRules(List<T> list);
}
