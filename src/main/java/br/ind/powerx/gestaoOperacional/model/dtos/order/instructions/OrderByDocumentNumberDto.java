package br.ind.powerx.gestaoOperacional.model.dtos.order.instructions;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.dtos.order.instructions.interfaces.Specificable;
import br.ind.powerx.gestaoOperacional.repositories.specifications.IncentiveSpecifications;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderByDocumentNumberDto implements Specificable<Incentive> {

	private Integer documentNumber;
	private List<Long> apurationTypes;
	
	@Override
	public Specification<Incentive> getSpecification() {
		Specification<Incentive> spec = Specification.where(null);

		if (this.documentNumber != null) {
			spec = spec.and(IncentiveSpecifications.hasDocumentNumber(documentNumber));
		}


		if (this.apurationTypes != null && !this.apurationTypes.isEmpty()) {
			spec = spec.and(IncentiveSpecifications.hasApurationTypeIn(apurationTypes));
		}

		return spec;
	
	}

	@Override
	public String name() {
		return this.documentNumber.toString();
	}
}
