package br.ind.powerx.gestaoOperacional.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Function;
import br.ind.powerx.gestaoOperacional.model.IncentiveValue;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.repositories.IncentiveValueRepository;
import br.ind.powerx.gestaoOperacional.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class IncentiveValueService {

	private final IncentiveValueRepository valueRepository;
	
	private final CustomerRepository customerRepository;
	
	private final ProductRepository productRepository;
	
	public IncentiveValueService(IncentiveValueRepository valueRepository, CustomerRepository customerRepository,
			ProductRepository productRepository) {
		this.valueRepository = valueRepository;
		this.customerRepository = customerRepository;
		this.productRepository = productRepository;
	}
	
	public void save(IncentiveValue value) {
		valueRepository.save(value);
	}
	
	public List<IncentiveValue> findAll(){
		return valueRepository.findAll();
	}

	public List<IncentiveValue> findAllByProductId(Long id) {
		return valueRepository.findAllByProductId(id);
	}

	public void delete(IncentiveValue v) {
		valueRepository.delete(v);
	}

	public Optional<IncentiveValue> findById(Long incentiveValueId) {
		return valueRepository.findById(incentiveValueId);
	}

	public List<IncentiveValue> findAllByProductAndCustomer(Long productId, Long customerId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new EntityNotFoundException("Cleinte não encontrado"));
		
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
		
		return valueRepository.findAllByProductAndCustomer(product, customer);
	}

	public List<IncentiveValue> findAllByCustomer(Long customerId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new EntityNotFoundException("Cleinte não encontrado"));
		
		return valueRepository.findAllByCustomer(customer);
	}

	public List<IncentiveValue> findAllByProductAndCustomerAndFunction(Product product, Customer customer,
			Function function) {
		return valueRepository.findAllByCustomerAndProductAndFunction(customer, product, function);
	}
}











