package br.ind.powerx.gestaoOperacional.repositories.specifications;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import br.ind.powerx.gestaoOperacional.model.Sale;
import br.ind.powerx.gestaoOperacional.model.enums.State;

public class SaleSpecifications {

	public static Specification<Sale> byReferenceDateBetween(LocalDate start, LocalDate end) {
		return (root, query, cb) -> cb.between(root.get("referenceDate"), start, end);
	}
	
	public static Specification<Sale> hasStateIn(List<State> states) {
		return (root, query, cb) -> root.get("customer").get("user").get("state").in(states);
	}
	
	public static Specification<Sale> hasGroupIn(List<Long> groups) {
		return (root, query, cb) -> root.get("customer").get("group").get("id").in(groups);
	}
	
	public static Specification<Sale> hasFlagIn(List<Long> flags) {
		return (root, query, cb) -> root.get("customer").get("flag").get("id").in(flags);
	}
	
	public static Specification<Sale> hasCustomerIn(List<Long> customers) {
		return (root, query, cb) -> root.get("customer").get("id").in(customers);
	}
}
