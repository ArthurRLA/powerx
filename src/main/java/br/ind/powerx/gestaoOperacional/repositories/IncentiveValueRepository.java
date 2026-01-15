package br.ind.powerx.gestaoOperacional.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Function;
import br.ind.powerx.gestaoOperacional.model.IncentiveValue;
import br.ind.powerx.gestaoOperacional.model.Product;

@Repository
public interface IncentiveValueRepository extends JpaRepository<IncentiveValue, Long>{
	List<IncentiveValue> findAllByProductId(Long id);
	
	IncentiveValue findByCustomerAndProductAndFunction(Customer customer, Product product, Function function);

	List<IncentiveValue> findAllByCustomerAndProductAndFunction(Customer customer, Product product, Function function);

	List<IncentiveValue> findByCustomer(Customer customer);

	List<IncentiveValue> findAllByProductAndCustomer(Product product, Customer customer);

	List<IncentiveValue> findAllByCustomer(Customer customer);

}
