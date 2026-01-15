package br.ind.powerx.gestaoOperacional.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Function;
import br.ind.powerx.gestaoOperacional.repositories.FunctionRepository;

@Service
public class FunctionService {

	@Autowired 
	private FunctionRepository functionRepository;
	
	public List<Function> findAll() {
		return functionRepository.findAll();
	}

	public Optional<Function> findById(Long functionId) {
		return functionRepository.findById(functionId);
	}

	public List<Function> findAllOrderByNameAsc() {
		return functionRepository.findAllOrderByNameAsc();
	}

}
