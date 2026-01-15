package br.ind.powerx.gestaoOperacional.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

	List<Customer> findAllByActiveTrueOrderByFantasyNameAsc();

	List<Customer> findAllByUserIdNullOrderByFantasyNameAsc();

	List<Customer> findAllByGroupIdNullOrderByFantasyNameAsc();

	Customer findByCnpj(String customerCnpj);

	@Query("SELECT c FROM Customer c ORDER BY c.fantasyName ASC")
	List<Customer> findAllOrderByFantasyNameAsc();


}
