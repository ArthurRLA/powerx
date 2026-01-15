package br.ind.powerx.gestaoOperacional.controllers;


import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ind.powerx.gestaoOperacional.model.dtos.StateDto;
import br.ind.powerx.gestaoOperacional.model.enums.State;
import br.ind.powerx.gestaoOperacional.util.ETagGenerator;

@RestController
@RequestMapping("/api/states")
public class RestStateController {

	
	@GetMapping("/all")
	public ResponseEntity<?> getAllApurationTypes(
			@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch){
		
		try {
			List<StateDto> dto = 
					Arrays.asList(State.values()).stream()
						.map(s -> {
							var item = new StateDto();
							item.setId(s);
							item.setName(s.getState());
							
							return item;
						})
						.toList();
			
			String eTag = ETagGenerator.generateETag(dto);
			
			if(ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
				return ResponseEntity
						.status(HttpStatus.NOT_MODIFIED)
						.eTag(eTag)
						.build();
			}
			
			return ResponseEntity
					.ok()
					.cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS).cachePrivate())
					.eTag(eTag)
					.body(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
			
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erro ao buscar usuários: " + e.getMessage());
		}
	}
}




























