package br.ind.powerx.gestaoOperacional.controllers;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ind.powerx.gestaoOperacional.model.dtos.DeleteRevenueResponse;
import br.ind.powerx.gestaoOperacional.model.dtos.RevenueDetailsDto;
import br.ind.powerx.gestaoOperacional.model.dtos.RevenueDetailsEditDto;
import br.ind.powerx.gestaoOperacional.model.dtos.RevenueUpdateDto;
import br.ind.powerx.gestaoOperacional.services.RevenueService;
import br.ind.powerx.gestaoOperacional.util.ETagGenerator;

@RestController
@RequestMapping("/api/revenues")
public class RestRevenueController {

	private final RevenueService revenueService;
	
	@Autowired
	public RestRevenueController(RevenueService revenueService) {
		this.revenueService = revenueService;
	}
	
	@GetMapping("/details/{id}")
	public ResponseEntity<?> getRevenueDetails(@PathVariable Long id, 
			@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch){
		try {
			RevenueDetailsDto dto = revenueService.getRevenueDetailsDto(id);
			
			String etag = ETagGenerator.generateETag(dto);
			
			if(ifNoneMatch != null && ifNoneMatch.equals(etag)) {
				return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
						.eTag(etag)
						.build();
			}
			
			return ResponseEntity.ok()
					.eTag(etag)
					.cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES).cachePrivate())
					.body(dto);
					
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Não foi possível buscar informações para o faturamento de ID: " + id + "\n" + e.getMessage());
		}
	}
	
	@GetMapping("/edit/{id}")
	public ResponseEntity<?> getRevenueEditDetails(@PathVariable Long id, 
			@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch){
		try {
			RevenueDetailsEditDto dto = revenueService.getRevenueEditDetailsDto(id);
			
			String etag = ETagGenerator.generateETag(dto);
			
			if(ifNoneMatch != null && ifNoneMatch.equals(etag)) {
				return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
						.eTag(etag)
						.build();
			}
			
			return ResponseEntity.ok()
					.eTag(etag)
					.cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES).cachePrivate())
					.body(dto);
					
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Não foi possível buscar informações para o faturamento de ID: " + id + "\n" + e.getMessage());
		}
	}
	
	@PostMapping("/update/{id}")
	public ResponseEntity<?> updateRevenue(@PathVariable Long id,
			@RequestBody RevenueUpdateDto dto){
		try {
			revenueService.update(id, dto);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Não foi possível atualizar faturamento: " + e.getMessage());
		}
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteRevenue(@PathVariable Long id){
		try {
			DeleteRevenueResponse dto = revenueService.deleteRevenue(id);
			return ResponseEntity.ok(dto);
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Não foi possível deletar o faturamento: " + e.getMessage());
		}
	}
}

















