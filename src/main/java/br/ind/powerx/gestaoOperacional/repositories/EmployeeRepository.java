package br.ind.powerx.gestaoOperacional.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee>{

	List<Employee> findAllByActiveTrue();

	Employee findByCpf(String cpf);

	List<Employee> findAllByActiveTrueOrderByNameAsc();

	Optional<Employee> findById(Long id);

}
