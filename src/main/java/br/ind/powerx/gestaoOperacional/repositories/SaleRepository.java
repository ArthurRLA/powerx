
package br.ind.powerx.gestaoOperacional.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.Sale;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long>, JpaSpecificationExecutor<Sale>{

	@Query("SELECT MAX(s.documentNumber) FROM Sale s")
    Integer findMaxDocumentNumber();

	@Query("SELECT DISTINCT s.documentNumber FROM Sale s")
	List<Integer> findDistinctDocumentNumbers();
	
	List<Sale> findByDocumentNumber(Integer documentNumber);

	void deleteByDocumentNumber(Integer documentNumber);
}
