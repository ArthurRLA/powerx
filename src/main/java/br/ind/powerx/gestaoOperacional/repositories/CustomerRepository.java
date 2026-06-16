package br.ind.powerx.gestaoOperacional.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

	@Query("""
			SELECT DISTINCT c FROM Customer c
			LEFT JOIN FETCH c.user
			LEFT JOIN FETCH c.group
			LEFT JOIN FETCH c.industry
			LEFT JOIN FETCH c.flag
			LEFT JOIN FETCH c.mechanicApuration
			WHERE LOWER(c.fantasyName) LIKE LOWER(CONCAT('%', :q, '%'))
			ORDER BY c.fantasyName ASC
			""")
	List<Customer> searchForCrudTableByFantasyName(@Param("q") String q);

}
