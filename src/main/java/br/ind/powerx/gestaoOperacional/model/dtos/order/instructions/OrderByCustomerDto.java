package br.ind.powerx.gestaoOperacional.model.dtos.order.instructions;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.dtos.order.instructions.interfaces.Specificable;
import br.ind.powerx.gestaoOperacional.repositories.specifications.IncentiveSpecifications;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderByCustomerDto implements Specificable<Incentive> {

	private LocalDate startDate;
	private LocalDate endDate;
	private List<Long> customers;
	private List<Long> apurationTypes;
	
	@Override
	public Specification<Incentive> getSpecification() {
		
		Specification<Incentive> spec = Specification.where(null);

		if (startDate != null & endDate != null) {
			spec = spec.and(IncentiveSpecifications.byReferenceDateBetween(startDate, endDate));
		}

		if (customers != null && !customers.isEmpty()) {
			spec = spec.and(IncentiveSpecifications.hasCustomerIn(customers));
		}

		if (apurationTypes != null && !apurationTypes.isEmpty()) {
			spec = spec.and(IncentiveSpecifications.hasApurationTypeIn(apurationTypes));
		}

		return spec;
	}
	
	@Override
	public String name() {
		return startDate + "-" + endDate;
	}
	
}
