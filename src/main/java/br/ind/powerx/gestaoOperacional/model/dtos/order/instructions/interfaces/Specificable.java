package br.ind.powerx.gestaoOperacional.model.dtos.order.instructions.interfaces;

import org.springframework.data.jpa.domain.Specification;

public interface Specificable<T> {

	Specification<T> getSpecification();
	String name();
}
