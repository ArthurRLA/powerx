package br.ind.powerx.gestaoOperacional.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.Industry;

@Repository
public interface IndustryRepository extends JpaRepository<Industry, Long>{

	@Query("SELECT i FROM Industry i ORDER BY i.name ASC")
	List<Industry> findAllOrderByNameAsc();

}
