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

import br.ind.powerx.gestaoOperacional.model.dtos.EmployeeDetailsDto;
import br.ind.powerx.gestaoOperacional.model.dtos.EmployeeEditDto;
import br.ind.powerx.gestaoOperacional.services.EmployeeService;
import br.ind.powerx.gestaoOperacional.util.ETagGenerator;

@RestController
@RequestMapping("/api/employees")
public class RestEmployeeController {

	private final EmployeeService employeeService;

	@Autowired
	public RestEmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@GetMapping("/details/{id}")
	public ResponseEntity<?> getEmployeeDetails(@PathVariable Long id,
			@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {
		try {
			EmployeeDetailsDto dto = employeeService.getEmployeeDetails(id);

			String eTag = ETagGenerator.generateETag(dto);

			if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
				return ResponseEntity
						.status(HttpStatus.NOT_MODIFIED)
						.eTag(eTag)
						.build();
			}

			return ResponseEntity
					.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES).cachePrivate())
					.eTag(eTag)
					.body(dto);
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	@GetMapping("/edit-data/{id}")
	public ResponseEntity<?> getEmployeeEdit(@PathVariable Long id,
			@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {
		try {
			EmployeeEditDto dto = employeeService.getEmployeeEdit(id);

			String eTag = ETagGenerator.generateETag(dto);

			if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
				return ResponseEntity
						.status(HttpStatus.NOT_MODIFIED)
						.eTag(eTag)
						.build();
			}

			return ResponseEntity
					.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.MINUTES).cachePrivate())
					.eTag(eTag)
					.body(dto);
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
}
