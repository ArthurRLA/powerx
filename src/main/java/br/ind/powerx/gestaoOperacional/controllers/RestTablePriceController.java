package br.ind.powerx.gestaoOperacional.controllers;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ind.powerx.gestaoOperacional.model.dtos.TablePriceDto;
import br.ind.powerx.gestaoOperacional.model.dtos.TablePriceEditDto;
import br.ind.powerx.gestaoOperacional.services.TablePriceService;
import br.ind.powerx.gestaoOperacional.util.ETagGenerator;

@RestController
@RequestMapping("/api/table-prices")
public class RestTablePriceController {

	private final TablePriceService tablePriceService;

	@Autowired
	public RestTablePriceController(TablePriceService tabelPriceService) {
		this.tablePriceService = tabelPriceService;
	}

	@GetMapping("/details/{id}")
	public ResponseEntity<?> getTablePriceDetails(@PathVariable Long id,
			@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {
		try {
			TablePriceDto dto = tablePriceService.getTablePriceDto(id);

			String eTag = ETagGenerator.generateETag(dto);

			if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
				return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(eTag).build();
			}

			return ResponseEntity.ok().cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS).cachePrivate()).eTag(eTag)
					.body(dto);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	@GetMapping("/edit/{id}")
	public ResponseEntity<?> getTablePriceInfosToEdit(@PathVariable Long id,
			@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch){
		try {
			TablePriceEditDto dto = tablePriceService.getTablePriceEditDto(id);
			
			String eTag = ETagGenerator.generateETag(dto);
			
			if(ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
				return ResponseEntity
						.status(HttpStatus.NOT_MODIFIED)
						.eTag(eTag)
						.build();
			}
			
			return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES).cachePrivate())
					.eTag(eTag).body(dto);
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
}






