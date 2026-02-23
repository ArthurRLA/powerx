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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger logger = LoggerFactory.getLogger(IncentiveService.class);

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
    public String approveIncentiveByDocumentNumber(Integer saleDocumentNumber) {
        List<Incentive> incentives = incentiveRepository.findBySaleDocumentNumber(saleDocumentNumber);
        
        if (incentives.isEmpty()) {
            throw new IllegalArgumentException("Nenhum incentivo encontrado para o documento " + saleDocumentNumber);
        }
        
        // Verificar se já estão aprovados
        boolean hasApproved = incentives.stream()
            .anyMatch(i -> i.getStatus() == IncentiveStatus.APPROVED || i.getStatus() == IncentiveStatus.APPROVED_NEGATIVE);
        
        if (hasApproved) {
            throw new IllegalStateException("Alguns incentivos deste documento já estão aprovados");
        }
        
        // Buscar as vendas relacionadas ao documento
        List<Sale> sales = saleRepository.findByDocumentNumber(saleDocumentNumber);
        Customer customer = incentives.get(0).getCustomer();
        
        // Filtrar apenas vendas (não aplicações de mecânico)
        // Se o cliente tem apuração "Somente mecânicos", todas são vendas
        // Caso contrário, apenas Consultor Técnico e Consultor de Funilaria são vendas
        List<Sale> actualSales = filterActualSales(sales, customer);
        
        logger.info("Total de vendas: {}. Vendas que removem estoque: {}", sales.size(), actualSales.size());
        
        // Verificar se há estoque suficiente para as vendas reais
        boolean hasInsufficientStock = false;
        String stockMessage = "";
        try {
            validateStockAvailability(customer, actualSales);
        } catch (IllegalStateException e) {
            // Estoque insuficiente - vai aprovar como APPROVED_NEGATIVE
            hasInsufficientStock = true;
            stockMessage = e.getMessage();
            logger.warn("Estoque insuficiente detectado: {}. Aprovando como APPROVED_NEGATIVE", e.getMessage());
        }
        
        // Subtrair quantidades do estoque apenas das vendas reais (permite negativo)
        for (Sale sale : actualSales) {
            productStockService.subtractFromStock(
                customer, 
                sale.getProduct(), 
                sale.getQuantity()
            );
        }
        
        // Recalcular conta corrente (permite negativo)
        currentAccountService.updateCurrentAccount(customer);
        
        // Definir status baseado na disponibilidade de estoque
        IncentiveStatus newStatus = hasInsufficientStock ? IncentiveStatus.APPROVED_NEGATIVE : IncentiveStatus.APPROVED;
        
        // Atualizar status de todos os incentivos do documento
        for (Incentive incentive : incentives) {
            incentive.setStatus(newStatus);
            incentiveRepository.save(incentive);
        }
        
        logger.info("Documento {} aprovado com status: {}", saleDocumentNumber, newStatus);
        
        // Retornar mensagem apropriada
        if (hasInsufficientStock) {
            return "ATENÇÃO: Documento aprovado como NEGATIVADO! " + stockMessage + 
                   ". O estoque ficou negativo e precisa ser regularizado.";
        } else {
            return "Documento aprovado com sucesso!";
        }
    }
    
    /**
     * Filtra as vendas que devem remover do estoque.
     * Regra: Se cliente tem apuração "Somente mecânicos", todas são vendas.
     * Caso contrário, apenas Consultor Técnico e Consultor de Funilaria são vendas (Mecânico é aplicação).
     */
    private List<Sale> filterActualSales(List<Sale> sales, Customer customer) {
        if (sales.isEmpty()) {
            return sales;
        }
        
        // Se o cliente tem apuração "Somente mecânicos", todas as vendas removem estoque
        if (customer.getMechanicApuration() != null && 
            customer.getMechanicApuration().getName().equalsIgnoreCase("Somente mecânicos")) {
            logger.debug("Cliente com apuração 'Somente mecânicos' - todas as vendas removem estoque");
            return sales;
        }
        
        // Caso contrário, apenas vendas de Consultor Técnico e Consultor de Funilaria removem estoque
        List<Sale> actualSales = sales.stream()
            .filter(sale -> {
                String function = sale.getFunction();
                boolean isActualSale = function != null && 
                    (function.equalsIgnoreCase("Consultor Técnico") || 
                     function.equalsIgnoreCase("Consultor de Funilaria"));
                
                if (!isActualSale) {
                    logger.debug("Venda do produto {} com função '{}' é considerada aplicação - não remove estoque", 
                        sale.getProduct().getProductName(), function);
                }
                
                return isActualSale;
            })
            .collect(Collectors.toList());
        
        return actualSales;
    }
    
    private void validateStockAvailability(Customer customer, List<Sale> sales) {
        if (customer.getProductStock() == null) {
            throw new IllegalStateException("Cliente não possui estoque cadastrado");
        }
        
        // Agrupar vendas por produto para somar quantidades
        Map<Product, Integer> salesByProduct = sales.stream()
            .collect(Collectors.groupingBy(
                Sale::getProduct,
                Collectors.summingInt(Sale::getQuantity)
            ));
        
        // Verificar se há estoque suficiente para cada produto
        for (Map.Entry<Product, Integer> entry : salesByProduct.entrySet()) {
            Product product = entry.getKey();
            Integer quantityToSubtract = entry.getValue();
            
            // Buscar quantidade atual no estoque
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
