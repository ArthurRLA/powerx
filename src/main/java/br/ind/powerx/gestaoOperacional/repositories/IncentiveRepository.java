package br.ind.powerx.gestaoOperacional.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.ind.powerx.gestaoOperacional.model.ApurationType;
import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.PaymentMethod;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.enums.State;
import br.ind.powerx.gestaoOperacional.repositories.specifications.IncentiveSpecifications;

@Repository
public interface IncentiveRepository extends JpaRepository<Incentive, Long>, JpaSpecificationExecutor<Incentive> {

    @Query("SELECT DISTINCT i.saleDocumentNumber FROM Incentive i")
    List<Integer> findDistinctDocumentNumbers();

    List<Incentive> findBySaleDocumentNumber(Integer saleDocumentNumber);

    List<Incentive> findByReferenceDateBetweenAndCustomerAndApurationType(
            LocalDate dataInicial, LocalDate dataFinal, Customer customer, ApurationType apurationType);

    List<Incentive> findByReferenceDateBetweenAndApurationTypeAndState(LocalDate dataInicial, LocalDate dataFinal,
            ApurationType apurationType, State state);

    List<Incentive> findByUser(User user);

    @Query("SELECT DISTINCT i.saleDocumentNumber FROM Incentive i WHERE i.user.id = :userId")
    List<Integer> findDistinctDocumentNumbersByUserId(@Param("userId") Long userId);

    Page<Incentive> findByUser(User user, Pageable pageable);

    List<Incentive> findByReferenceDateBetweenAndApurationTypeAndStateAndPaymentMethod(LocalDate inicio, LocalDate fim,
            ApurationType apurationType, State state, PaymentMethod paymentMethod);

    default List<Integer> findDistinctDocumentNumbersBySpec(Specification<Incentive> spec) {
        return findAll(spec).stream()
                .map(Incentive::getSaleDocumentNumber)
                .distinct()
                .collect(Collectors.toList());
    }

    default List<Integer> findDistinctDocumentNumbersByUserIdAndSpec(Long userId, Specification<Incentive> spec) {
        Specification<Incentive> userSpec = IncentiveSpecifications.hasUser(userId);
        return findAll(userSpec.and(spec)).stream()
                .map(Incentive::getSaleDocumentNumber)
                .distinct()
                .collect(Collectors.toList());
    }

    @Modifying
    @Transactional
    @Query("DELETE FROM Incentive i WHERE i.saleDocumentNumber = :saleDocumentNumber")
    void deleteAllBySaleDocumentNumber(@Param("saleDocumentNumber") Integer saleDocumentNumber);

}
