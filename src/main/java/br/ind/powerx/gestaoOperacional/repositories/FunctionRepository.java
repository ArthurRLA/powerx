package br.ind.powerx.gestaoOperacional.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.Function;

@Repository
public interface FunctionRepository extends JpaRepository<Function, Long> {

	Optional<Function> findByName(String functionName);

	@Query("SELECT f FROM Function f ORDER BY f.name ASC")
	List<Function> findAllOrderByNameAsc();

}
