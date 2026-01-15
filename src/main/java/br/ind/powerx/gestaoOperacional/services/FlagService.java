package br.ind.powerx.gestaoOperacional.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Flag;
import br.ind.powerx.gestaoOperacional.model.dtos.FlagDTO;
import br.ind.powerx.gestaoOperacional.repositories.FlagRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class FlagService {

	@Autowired
	private FlagRepository flagRepository;
	
	public List<Flag> findAll(){
		return flagRepository.findAll();
	}

	public void save(FlagDTO flagDTO) {
		Flag flag = new Flag();
		flag.setName(flagDTO.name());
		flagRepository.save(flag);
	}

	public void update(Long id, Flag flag) {
		Flag existingFlag = flagRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Bandeira não encontrada"));
		existingFlag.setName(flag.getName());
		flagRepository.save(existingFlag);
	}

	public Page<Flag> findAll(Pageable pageable) {
		return flagRepository.findAll(pageable);
	}

	public List<Flag> findAllOrderByNameAsc() {
		return flagRepository.findAllOrderByNameAsc();
	}

	public List<FlagDTO> getFlagsSelect() {
		return flagRepository.findAllOrderByNameAsc().stream()
				.map(f -> new FlagDTO(f.getId(), f.getName()))
				.toList();
						
	}
	
	
}
