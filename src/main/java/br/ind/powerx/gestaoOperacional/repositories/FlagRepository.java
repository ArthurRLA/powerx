package br.ind.powerx.gestaoOperacional.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.Flag;

@Repository
public interface FlagRepository extends JpaRepository<Flag, Long>{
	@Query("SELECT f FROM Flag f ORDER BY f.name ASC")
	List<Flag> findAllOrderByNameAsc();

	List<Flag> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

}
