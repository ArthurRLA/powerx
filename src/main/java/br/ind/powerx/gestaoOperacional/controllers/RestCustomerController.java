package br.ind.powerx.gestaoOperacional.controllers;

import java.util.List;
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

import br.ind.powerx.gestaoOperacional.model.dtos.CustomerBasicInfosDto;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerCurrentAccountInfosDto;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerDto;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerEditDto;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerEmployeeBasicInfosDto;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerProductStockItemsDto;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerSetCurrentAccountDto;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerTablePriceInfosDto;
import br.ind.powerx.gestaoOperacional.model.dtos.DataNewSales;
import br.ind.powerx.gestaoOperacional.model.dtos.EmployeeBasicDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.UserCustomersDto;
import br.ind.powerx.gestaoOperacional.services.CustomerService;
import br.ind.powerx.gestaoOperacional.util.ETagGenerator;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/customers")
public class RestCustomerController {
	private final CustomerService customerService;

	@Autowired
	public RestCustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@GetMapping("/details/{id}")
	public ResponseEntity<?> getCustomerBasicInfos(@PathVariable Long id,
			@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {
		try {
			CustomerBasicInfosDto dto = customerService.customersBasicInfosById(id);

			String eTag = ETagGenerator.generateETag(dto);

			if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
				return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
						.eTag(eTag)
						.build();
			}

			return ResponseEntity.ok()
					.cacheControl(CacheControl.maxAge(2, TimeUnit.MINUTES).cachePrivate())
					.eTag(eTag)
					.body(dto);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Cliente com id: '" + id + "' não encontrado");
		}
	}

	@GetMapping("/by-user/{id}")
	public ResponseEntity<?> getCustomerByUser(@PathVariable Long id,
			@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {
		try {

			UserCustomersDto dto = customerService.customersUniqueInfosByUserId(id);

			String eTag = ETagGenerator.generateETag(dto);

			if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
				return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
						.eTag(eTag)
						.build();
			}

			return ResponseEntity.ok()
					.cacheControl(CacheControl.maxAge(25, TimeUnit.SECONDS).cachePrivate())
					.eTag(eTag)
					.body(dto);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Cliente com id: '" + id + "' não encontrado");
		}
	}

	@GetMapping("/all")
	public ResponseEntity<?> getCustomerSelect() {
		try {
			List<CustomerDto> dto = customerService.getCustomerSelect();

			return ResponseEntity.ok(dto);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Erro: " + e.getMessage());
		}
	}

	@GetMapping("/employees/{id}")
	public ResponseEntity<?> getCustomerEmployeesInfos(
			@PathVariable(name = "id") Long id,
			@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {
		try {
			List<CustomerEmployeeBasicInfosDto> dtos = customerService.getCustomerEmployeesBasicInfos(id);

			String eTag = ETagGenerator.generateETag(dtos);

			if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
				return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
						.eTag(eTag)
						.build();
			}

			return ResponseEntity.ok()
					.cacheControl(CacheControl.maxAge(2, TimeUnit.MINUTES).cachePrivate())
					.eTag(eTag)
					.body(dtos);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Cliente com id: '" + id + "' não encontrado");
		}
	}

	@GetMapping("/table-price/{id}")
	public ResponseEntity<?> getCustomerTablePriceInfos(
			@PathVariable(name = "id") Long id,
			@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {
		try {
			List<CustomerTablePriceInfosDto> dtos = customerService.getCustomerTablePriceInfos(id);

			String eTag = ETagGenerator.generateETag(dtos);

			if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
				return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
						.eTag(eTag)
						.build();
			}

			return ResponseEntity.ok()
					.cacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES).cachePrivate())
					.eTag(eTag)
					.body(dtos);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("Cliente com id: '" + id + "' não encontrado");
		}
	}

	@GetMapping("/product-stock/{id}")
	public ResponseEntity<?> getCustomerProductStock(@PathVariable(name = "id") Long id) {
		try {
			List<CustomerProductStockItemsDto> dtos = customerService.getCustomerProductStockItems(id);
			return ResponseEntity.ok(dtos);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente com id: '" + id + "' não encontrado");
		}
	}

	@GetMapping("/current-account/{id}")
	public ResponseEntity<?> getCustomerCurrentAccountInfos(@PathVariable(name = "id") Long id) {
		try {
			CustomerCurrentAccountInfosDto dto = customerService.getCustomerCurrentAccountInfosDto(id);
			return ResponseEntity.ok(dto);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente com id: '" + id + "' não encontrado");
		}
	}

	@GetMapping("/set-current-account/{id}")
	public ResponseEntity<?> getCustomerSetCurrentAccount(@PathVariable(name = "id") Long id) {
		try {
			CustomerSetCurrentAccountDto dto = customerService.getCustomerSetCurrentAccountDto(id);
			return ResponseEntity.ok(dto);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente com id: '" + id + "' não encontrado");
		}
	}

	@GetMapping("/edit-data/{id}")
	public ResponseEntity<?> getEditData(@PathVariable(name = "id") Long id,
			@RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {
		try {
			CustomerEditDto dto = customerService.getCustomerEditData(id);
			String eTag = "\"" + Integer.toHexString(dto.hashCode()) + "\"";

			if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
				return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
						.eTag(eTag)
						.build();
			}

			return ResponseEntity.ok()
					.cacheControl(CacheControl.maxAge(2, TimeUnit.MINUTES).cachePrivate())
					.eTag(eTag)
					.body(dto);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente com id: '" + id + "' não encontrado");
		}
	}

	@GetMapping("/{id}/{function}")
	public ResponseEntity<?> getEmployeesByFunction(
			@PathVariable(name = "id") Long id,
			@PathVariable(name = "function") String functionName) {
		try {
			DataNewSales datas = customerService.getDataNewSales(id, functionName);
			return ResponseEntity.ok(datas);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cliente com id: '" + id + "' não encontrado");
		}
	}

	@GetMapping("/{id}/employees-for-passage")
	public ResponseEntity<?> getEmployeesForPassage(@PathVariable(name = "id") Long id) {
		try {
			List<EmployeeBasicDTO> employees = customerService.getEmployeesForPassage(id);
			return ResponseEntity.ok(employees);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erro ao buscar funcionários: " + e.getMessage());
		}
	}

}
