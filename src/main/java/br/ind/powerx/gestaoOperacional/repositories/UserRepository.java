package br.ind.powerx.gestaoOperacional.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>{

	User findByEmail(String email);

	List<User> findAllByActiveTrue();

	List<User> findAllByActiveTrueOrderByNameAsc();

	@Query("SELECT u FROM User u ORDER BY u.name ASC")
	List<User> findAllOrderByNameAsc();
	
}
