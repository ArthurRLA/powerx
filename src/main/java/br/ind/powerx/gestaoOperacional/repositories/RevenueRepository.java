package br.ind.powerx.gestaoOperacional.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Revenue;
import br.ind.powerx.gestaoOperacional.model.enums.OperationType;

public interface RevenueRepository extends JpaRepository<Revenue, Long>, JpaSpecificationExecutor<Revenue> {

	Optional<Revenue> findByInvoiceNumber(Integer invoiceNumber);

	Optional<Revenue> findByInvoiceNumberAndCustomer(Integer invoiceNumber, Customer customer);

	Optional<Revenue> findByInvoiceNumberAndCustomerAndOperationType(Integer invoiceNumber, Customer customer,
			OperationType operationType);

	Page<Revenue> findAllByInvoiceNumber(Integer invoiceNumber, Pageable pageable);

}
