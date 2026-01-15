package br.ind.powerx.gestaoOperacional.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.ApurationType;

@Repository
public interface ApurationTypeRepository extends JpaRepository<ApurationType, Long> {

	ApurationType findByName(String string);

	@Query("SELECT at FROM ApurationType at ORDER BY at.name ASC")
	List<ApurationType> findAllOrderByNameAsc();

}
