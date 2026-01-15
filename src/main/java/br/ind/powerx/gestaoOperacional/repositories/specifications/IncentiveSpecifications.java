package br.ind.powerx.gestaoOperacional.repositories.specifications;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.enums.State;

public class IncentiveSpecifications {

	public static Specification<Incentive> byReferenceDateBetween(LocalDate start, LocalDate end) {
		return (root, query, cb) -> cb.between(root.get("referenceDate"), start, end);
	}

	public static Specification<Incentive> hasCurrentAccount() {
		return (root, query, cb) -> cb.equal(root.get("apurationType").get("name"), "Conta Corrente");
	}

	public static Specification<Incentive> hasState(State state) {
		return (root, query, cb) -> state == null ? cb.conjunction() : cb.equal(root.get("state"), state);
	}

	public static Specification<Incentive> hasUser(Long userId) {
		return (root, query, cb) -> userId == null ? cb.conjunction() : cb.equal(root.get("user").get("id"), userId);
	}

	public static Specification<Incentive> hasCustomer(Long customerId) {
		return (root, query, cb) -> customerId == null ? cb.conjunction()
				: cb.equal(root.get("customer").get("id"), customerId);
	}

	public static Specification<Incentive> hasGroup(Long groupId) {
		return (root, query, cb) -> groupId == null ? cb.conjunction()
				: cb.equal(root.get("customer").get("group").get("id"), groupId);
	}

	public static Specification<Incentive> hasUserIn(Collection<Long> userIds) {
		return (root, query, cb) -> {
			if (userIds == null || userIds.isEmpty()) {
				return null; 
			}
			return root.get("user").get("id").in(userIds);
		};
	}

	public static Specification<Incentive> hasCustomerIn(Collection<Long> customerIds) {
		return (root, query, cb) -> {
			if (customerIds == null || customerIds.isEmpty()) {
				return null;
			}
			return root.get("customer").get("id").in(customerIds);
		};
	}

	public static Specification<Incentive> hasApurationTypeIn(List<Long> apurationTypes) {
		return (root, query, cb) -> root.get("apurationType").get("id").in(apurationTypes);
	}
	
	public static Specification<Incentive> hasStateIn(List<State> states) {
		return (root, query, cb) -> root.get("customer").get("user").get("state").in(states);
	}

	public static Specification<Incentive> hasGroupIn(List<Long> groups) {
		return (root, query, cb) -> {
			if (groups == null || groups.isEmpty()) {
				return null;
			}
			return root.get("customer").get("group").get("id").in(groups);
		};
	}

	public static Specification<Incentive> hasDocumentNumber(Integer documentNumber) {
		return (root, query, cb) -> {
			return cb.equal(root.get("saleDocumentNumber"), documentNumber);
		};
	}
}
