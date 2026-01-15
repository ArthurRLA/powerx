package br.ind.powerx.gestaoOperacional.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

	Product findByProductCode(String productCode);
	
	@Query("SELECT p FROM Product p ORDER BY p.productCode ASC")
	List<Product> findAllOrderByProductCodeAsc();


}
