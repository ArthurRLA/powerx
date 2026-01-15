package br.ind.powerx.gestaoOperacional.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Industry;
import br.ind.powerx.gestaoOperacional.model.dtos.IndustryDTO;
import br.ind.powerx.gestaoOperacional.repositories.IndustryRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class IndustryService {

	@Autowired
	private IndustryRepository industryRepository;
	
	public List<Industry> findAll(){
		return industryRepository.findAll();
	}

	public void save(IndustryDTO industryDTO) {
		Industry industry = new Industry();
		industry.setName(industryDTO.name());
		industryRepository.save(industry);
	}

	public void update(Long id, Industry industry) {
		Industry existingIndustry = industryRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Seguimento não encontrado"));
		existingIndustry.setName(industry.getName());
		industryRepository.save(existingIndustry);
	}

	public Page<Industry> findAll(Pageable pageable) {
		return industryRepository.findAll(pageable);
	}

	public List<Industry> findAllOrderByNameAsc() {
		return industryRepository.findAllOrderByNameAsc();
	}
	
}