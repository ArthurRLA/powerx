package br.ind.powerx.gestaoOperacional.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.model.TablePrice;

@Repository
public interface TablePriceRepository extends JpaRepository<TablePrice, Long>, JpaSpecificationExecutor<TablePrice>{

	@Query("SELECT tp FROM TablePrice tp JOIN tp.customer c ORDER BY c.fantasyName ASC")
	List<TablePrice> findAllOrderByCustomerFantasyName();

	Optional<TablePrice> findByCustomerAndProduct(Customer customer, Product product);

	@Query("SELECT tp FROM TablePrice tp JOIN tp.customer c ORDER BY c.fantasyName ASC")
	Page<TablePrice> findAllOrderByCustomerFantasyName(Pageable pageable);

}
