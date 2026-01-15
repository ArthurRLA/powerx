package br.ind.powerx.gestaoOperacional.model.dtos;

import java.util.List;

public record EmployeeFilterDTO(List<Long> customers, List<Long> functions ) {

}
