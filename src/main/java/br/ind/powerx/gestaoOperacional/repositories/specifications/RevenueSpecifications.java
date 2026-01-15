package br.ind.powerx.gestaoOperacional.repositories.specifications;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import br.ind.powerx.gestaoOperacional.model.Revenue;
import br.ind.powerx.gestaoOperacional.model.enums.OperationType;

public class RevenueSpecifications {

	public static Specification<Revenue> byReferenceDateBetween(LocalDate start, LocalDate end) {
		return (root, query, cb) -> cb.between(root.get("date"), start, end);
	}
	
	public static Specification<Revenue> usersIn(List<Long> userIds){
		return (root, query, cb) -> 
			root.get("user").get("id").in(userIds);	
	}
	
	public static Specification<Revenue> customersIn(List<Long> customerIds){
		return (root, query, cb) -> 
			root.get("customer").get("id").in(customerIds);	
	}
	
	public static Specification<Revenue> groupsIn(List<Long> groupIds){
		return (root, query, cb) -> 
			root.get("customer").get("group").get("id").in(groupIds);	
	}
	
	public static Specification<Revenue> operationTypesIn(List<OperationType> operationTypes){
		return (root, query, cb) -> 
			root.get("operationType").in(operationTypes);	
	}
}
