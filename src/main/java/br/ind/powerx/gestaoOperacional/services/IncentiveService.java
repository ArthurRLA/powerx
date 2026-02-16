package br.ind.powerx.gestaoOperacional.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.model.Sale;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.enums.IncentiveStatus;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.IncentiveCustomerReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.IncentiveDateReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.IncentiveGroupReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.IncentiveUserReportInstructions;
import br.ind.powerx.gestaoOperacional.repositories.IncentiveRepository;
import br.ind.powerx.gestaoOperacional.repositories.SaleRepository;
import br.ind.powerx.gestaoOperacional.repositories.specifications.IncentiveSpecifications;

@Service
public class IncentiveService {

	@Autowired
	private IncentiveRepository incentiveRepository;
	
	@Autowired
	private SaleRepository saleRepository;
	
	@Autowired
	private ProductStockService productStockService;
	
	@Autowired
	private CurrentAccountService currentAccountService;

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
    
    @Transactional
    public void approveIncentiveByDocumentNumber(Integer saleDocumentNumber) {
        List<Incentive> incentives = incentiveRepository.findBySaleDocumentNumber(saleDocumentNumber);
        
        if (incentives.isEmpty()) {
            throw new IllegalArgumentException("Nenhum incentivo encontrado para o documento " + saleDocumentNumber);
        }
        
        boolean hasApproved = incentives.stream()
            .anyMatch(i -> i.getStatus() == IncentiveStatus.APPROVED);
        
        if (hasApproved) {
            throw new IllegalStateException("Alguns incentivos deste documento já estão aprovados");
        }
        
        List<Sale> sales = saleRepository.findByDocumentNumber(saleDocumentNumber);
        Customer customer = incentives.get(0).getCustomer();
        
        validateStockAvailability(customer, sales);
        
        for (Sale sale : sales) {
            productStockService.subtractFromStock(
                customer, 
                sale.getProduct(), 
                sale.getQuantity()
            );
        }
        
        currentAccountService.updateCurrentAccount(customer);
        
        for (Incentive incentive : incentives) {
            incentive.setStatus(IncentiveStatus.APPROVED);
            incentiveRepository.save(incentive);
        }
    }
    
    private void validateStockAvailability(Customer customer, List<Sale> sales) {
        if (customer.getProductStock() == null) {
            throw new IllegalStateException("Cliente não possui estoque cadastrado");
        }
        
        Map<Product, Integer> salesByProduct = sales.stream()
            .collect(Collectors.groupingBy(
                Sale::getProduct,
                Collectors.summingInt(Sale::getQuantity)
            ));
        
        for (Map.Entry<Product, Integer> entry : salesByProduct.entrySet()) {
            Product product = entry.getKey();
            Integer quantityToSubtract = entry.getValue();
            
            Integer currentStock = customer.getProductStock().getProductStockItems().stream()
                .filter(item -> item.getProduct().equals(product))
                .mapToInt(item -> item.getQuantity())
                .sum();
            
            if (currentStock < quantityToSubtract) {
                throw new IllegalStateException(
                    String.format("Estoque insuficiente para o produto '%s'. " +
                                "Estoque atual: %d, Quantidade necessária: %d", 
                                product.getProductName(), currentStock, quantityToSubtract)
                );
            }
        }
    }

}
