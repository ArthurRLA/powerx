package br.ind.powerx.gestaoOperacional.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.IncentiveCustomerReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.IncentiveDateReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.IncentiveGroupReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.IncentiveUserReportInstructions;
import br.ind.powerx.gestaoOperacional.repositories.IncentiveRepository;
import br.ind.powerx.gestaoOperacional.repositories.specifications.IncentiveSpecifications;

@Service
public class IncentiveService {

	@Autowired
	private IncentiveRepository incentiveRepository;

	public List<Incentive> findAll() {
		return incentiveRepository.findAll();
	}

	public Optional<Incentive> findById(Long id) {
		return incentiveRepository.findById(id);
	}

	public void save(Incentive incentive) {
		incentiveRepository.save(incentive);
	}

	public List<Incentive> findByUser(User user) {
		return incentiveRepository.findByUser(user);
	}

	public Page<Incentive> findAll(Pageable pageable) {
		return incentiveRepository.findAll(pageable);
	}

	public Page<Incentive> findByUser(User user, Pageable pageable) {
		return incentiveRepository.findByUser(user, pageable);
	}

	public List<Incentive> filter(IncentiveUserReportInstructions instructions) {

		Specification<Incentive> spec = Specification.where(null);

		if (instructions.getStartDate() != null & instructions.getEndDate() != null) {
			spec = spec.and(IncentiveSpecifications.byReferenceDateBetween(instructions.getStartDate(),
					instructions.getEndDate()));
		}

		if (instructions.getUsers() != null && !instructions.getUsers().isEmpty()) {
			spec = spec.and(IncentiveSpecifications.hasUserIn(instructions.getUsers()));
		}

		if (instructions.getApurationTypes() != null && !instructions.getApurationTypes().isEmpty()) {
			spec = spec.and(IncentiveSpecifications.hasApurationTypeIn(instructions.getApurationTypes()));
		}

		return incentiveRepository.findAll(spec);
	}

	public List<Incentive> filter(IncentiveGroupReportInstructions instructions) {

		Specification<Incentive> spec = Specification.where(null);

		if (instructions.getStartDate() != null & instructions.getEndDate() != null) {
			spec = spec.and(IncentiveSpecifications.byReferenceDateBetween(instructions.getStartDate(),
					instructions.getEndDate()));
		}

		if (instructions.getGroups() != null && !instructions.getGroups().isEmpty()) {
			spec = spec.and(IncentiveSpecifications.hasGroupIn(instructions.getGroups()));
		}

		if (instructions.getApurationTypes() != null && !instructions.getApurationTypes().isEmpty()) {
			spec = spec.and(IncentiveSpecifications.hasApurationTypeIn(instructions.getApurationTypes()));
		}

		return incentiveRepository.findAll(spec);
	}

	public List<Incentive> filter(IncentiveCustomerReportInstructions instructions) {
		
		Specification<Incentive> spec = Specification.where(null);

		if (instructions.getStartDate() != null & instructions.getEndDate() != null) {
			spec = spec.and(IncentiveSpecifications.byReferenceDateBetween(instructions.getStartDate(),
					instructions.getEndDate()));
		}

		if (instructions.getCustomers() != null && !instructions.getCustomers().isEmpty()) {
			spec = spec.and(IncentiveSpecifications.hasCustomerIn(instructions.getCustomers()));
		}

		if (instructions.getApurationTypes() != null && !instructions.getApurationTypes().isEmpty()) {
			spec = spec.and(IncentiveSpecifications.hasApurationTypeIn(instructions.getApurationTypes()));
		}

		return incentiveRepository.findAll(spec);
	}

	public List<Incentive> filter(IncentiveDateReportInstructions instructions) {

		Specification<Incentive> spec = Specification.where(null);

		if (instructions.getStartDate() != null & instructions.getEndDate() != null) {
			spec = spec.and(IncentiveSpecifications.byReferenceDateBetween(instructions.getStartDate(),
					instructions.getEndDate()));
		}


		if (instructions.getApurationTypes() != null && !instructions.getApurationTypes().isEmpty()) {
			spec = spec.and(IncentiveSpecifications.hasApurationTypeIn(instructions.getApurationTypes()));
		}

		return incentiveRepository.findAll(spec);
	}

    public List<Incentive> filter(Integer documentNumber) {
        return incentiveRepository.findBySaleDocumentNumber(documentNumber);
    }

}
