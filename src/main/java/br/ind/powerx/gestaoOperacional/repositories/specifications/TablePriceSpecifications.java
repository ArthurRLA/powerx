package br.ind.powerx.gestaoOperacional.repositories.specifications;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import br.ind.powerx.gestaoOperacional.model.TablePrice;

public class TablePriceSpecifications {

	public static Specification<TablePrice> customersIn(List<Long> customers){
		return (root, query, cb) -> 
			root.get("customer").get("id").in(customers);
		
	}
}
