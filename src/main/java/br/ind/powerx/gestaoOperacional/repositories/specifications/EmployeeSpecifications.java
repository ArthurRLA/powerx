package br.ind.powerx.gestaoOperacional.repositories.specifications;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import br.ind.powerx.gestaoOperacional.model.Employee;
import jakarta.persistence.criteria.Join;

public class EmployeeSpecifications {

	public static Specification<Employee> customersIn(List<Long> customers){
		return (root, query, criteriaBuilder) -> {
			Join<Object, Object> join = root.join("customers");
			return join.get("id").in(customers);
		};
	}
	
	public static Specification<Employee> functionsIn(List<Long> functions) {
        return (root, query, criteriaBuilder) -> {
            Join<Object, Object> join = root.join("functions");
            return join.get("id").in(functions);
        };
    }

	public static Specification<Employee> isActive(boolean active){
		return (root, query, criteriaBuilder) -> 
			criteriaBuilder.equal(root.get("active"), active);
	}
}
