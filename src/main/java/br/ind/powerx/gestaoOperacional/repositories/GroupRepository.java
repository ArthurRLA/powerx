package br.ind.powerx.gestaoOperacional.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long>{
	
	@Query("SELECT g FROM Group g ORDER BY g.name ASC")
	List<Group> findAllOrderByNameAsc();

	List<Group> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

}