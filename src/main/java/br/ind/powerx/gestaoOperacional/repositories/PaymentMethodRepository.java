package br.ind.powerx.gestaoOperacional.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.PaymentMethod;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
	@Query("SELECT pm FROM PaymentMethod pm ORDER BY pm.name ASC")
	List<PaymentMethod> findAllOrderByNameAsc();

	PaymentMethod findByName(String string);

	List<PaymentMethod> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

}
