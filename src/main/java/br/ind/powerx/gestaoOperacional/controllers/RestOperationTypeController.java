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

import br.ind.powerx.gestaoOperacional.model.dtos.OperationTypeDto;
import br.ind.powerx.gestaoOperacional.model.enums.OperationType;
import br.ind.powerx.gestaoOperacional.util.ETagGenerator;

@RestController
@RequestMapping("/api/operationTypes")
public class RestOperationTypeController {

	
	@GetMapping("/all")
	public ResponseEntity<?> getAllApurationTypes(
			@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch){
		
		try {
			List<OperationTypeDto> dto = 
					Arrays.asList(OperationType.values()).stream()
						.map(o -> {
							var item = new OperationTypeDto();
							item.setId(o);
							item.setName(o.getName());
							
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




























