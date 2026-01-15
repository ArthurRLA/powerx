package br.ind.powerx.gestaoOperacional.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.ApurationType;
import br.ind.powerx.gestaoOperacional.model.dtos.ApurationTypeSelectDto;
import br.ind.powerx.gestaoOperacional.repositories.ApurationTypeRepository;

@Service
public class ApurationTypeService {
	
	@Autowired
	private ApurationTypeRepository apurationTypeRepository;

	public List<ApurationType> findAll() {
		return apurationTypeRepository.findAll();
	}

	public Optional<ApurationType> findById(Long apurationTypeId) {
		return apurationTypeRepository.findById(apurationTypeId);
	}
	
	public ApurationType findByName(String apurationTypeName) {
		return apurationTypeRepository.findByName(apurationTypeName);
	}

	public List<ApurationType> findAllOrderByNameAsc() {
		return apurationTypeRepository.findAllOrderByNameAsc();
	}

	public List<ApurationTypeSelectDto> getApurationTypesSelect() {
		return apurationTypeRepository.findAllOrderByNameAsc().stream()
				.map(a -> new ApurationTypeSelectDto(a.getId(), a.getName()))
				.toList();
	}

}
