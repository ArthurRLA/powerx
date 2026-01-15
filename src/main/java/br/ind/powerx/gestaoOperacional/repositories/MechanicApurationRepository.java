package br.ind.powerx.gestaoOperacional.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.MechanicApuration;

@Repository
public interface MechanicApurationRepository extends JpaRepository<MechanicApuration, Long> {

	@Query("SELECT mc FROM MechanicApuration mc ORDER BY mc.name ASC")
	List<MechanicApuration> findAllOrderByNameAsc();

}
