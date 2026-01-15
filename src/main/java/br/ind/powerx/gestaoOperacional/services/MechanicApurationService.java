package br.ind.powerx.gestaoOperacional.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.MechanicApuration;
import br.ind.powerx.gestaoOperacional.repositories.MechanicApurationRepository;

@Service
public class MechanicApurationService {

	@Autowired
	private MechanicApurationRepository mechanicApurationRepository;
	
	public List<MechanicApuration> findAll(){
		return mechanicApurationRepository.findAll();
	}

	public List<MechanicApuration> findAllOrderByNameAsc() {
		return mechanicApurationRepository.findAllOrderByNameAsc();
	}
}
