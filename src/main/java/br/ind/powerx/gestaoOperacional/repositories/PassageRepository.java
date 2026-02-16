package br.ind.powerx.gestaoOperacional.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Passage;
import br.ind.powerx.gestaoOperacional.model.User;

@Repository
public interface PassageRepository extends JpaRepository<Passage, Long>, JpaSpecificationExecutor<Passage> {

    @Query("SELECT DISTINCT p.passageNumber FROM Passage p ORDER BY p.passageNumber DESC")
    List<Integer> findDistinctPassageNumbers();

    @Query("SELECT MAX(p.passageNumber) FROM Passage p")
    Integer findMaxPassageNumber();

    List<Passage> findByPassageNumber(Integer passageNumber);

    @Query("SELECT DISTINCT p.passageNumber FROM Passage p WHERE p.user = :user ORDER BY p.passageNumber DESC")
    List<Integer> findDistinctPassageNumbersByUser(@Param("user") User user);

    List<Passage> findByReferenceDateBetweenAndCustomer(LocalDate start, LocalDate end, Customer customer);

    @Modifying
    @Transactional
    @Query("DELETE FROM Passage p WHERE p.passageNumber = :passageNumber")
    void deleteAllByPassageNumber(@Param("passageNumber") Integer passageNumber);
}
