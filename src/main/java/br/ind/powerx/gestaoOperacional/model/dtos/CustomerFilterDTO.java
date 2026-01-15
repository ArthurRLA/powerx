package br.ind.powerx.gestaoOperacional.model.dtos;

import java.util.List;

public record CustomerFilterDTO(List<Long> users, List<Long> groups, List<Long> industries, List<Long> flags) {

}
