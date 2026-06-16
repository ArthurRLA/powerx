package br.ind.powerx.gestaoOperacional.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.ind.powerx.gestaoOperacional.model.dtos.crud.CrudSearchCustomerRow;
import br.ind.powerx.gestaoOperacional.model.dtos.crud.CrudSearchEmployeeRow;
import br.ind.powerx.gestaoOperacional.model.dtos.crud.CrudSearchFlagRow;
import br.ind.powerx.gestaoOperacional.model.dtos.crud.CrudSearchGroupRow;
import br.ind.powerx.gestaoOperacional.model.dtos.crud.CrudSearchIndustryRow;
import br.ind.powerx.gestaoOperacional.model.dtos.crud.CrudSearchPaymentRow;
import br.ind.powerx.gestaoOperacional.model.dtos.crud.CrudSearchProductRow;
import br.ind.powerx.gestaoOperacional.model.dtos.crud.CrudSearchTablePriceRow;
import br.ind.powerx.gestaoOperacional.model.dtos.crud.CrudSearchUserRow;
import br.ind.powerx.gestaoOperacional.services.CrudTableSearchService;

@RestController
@RequestMapping("/api/crud-table-search")
public class CrudTableSearchRestController {

	private final CrudTableSearchService crudTableSearchService;

	public CrudTableSearchRestController(CrudTableSearchService crudTableSearchService) {
		this.crudTableSearchService = crudTableSearchService;
	}

	@GetMapping("/users")
	public List<CrudSearchUserRow> users(@RequestParam("q") String q) {
		return crudTableSearchService.searchUsers(trimQ(q));
	}

	@GetMapping("/customers")
	public List<CrudSearchCustomerRow> customers(@RequestParam("q") String q) {
		return crudTableSearchService.searchCustomers(trimQ(q));
	}

	@GetMapping("/products")
	public List<CrudSearchProductRow> products(@RequestParam("q") String q) {
		return crudTableSearchService.searchProducts(trimQ(q));
	}

	@GetMapping("/employees")
	public List<CrudSearchEmployeeRow> employees(@RequestParam("q") String q) {
		return crudTableSearchService.searchEmployees(trimQ(q));
	}

	@GetMapping("/groups")
	public List<CrudSearchGroupRow> groups(@RequestParam("q") String q) {
		return crudTableSearchService.searchGroups(trimQ(q));
	}

	@GetMapping("/payments")
	public List<CrudSearchPaymentRow> payments(@RequestParam("q") String q) {
		return crudTableSearchService.searchPayments(trimQ(q));
	}

	@GetMapping("/industries")
	public List<CrudSearchIndustryRow> industries(@RequestParam("q") String q) {
		return crudTableSearchService.searchIndustries(trimQ(q));
	}

	@GetMapping("/flags")
	public List<CrudSearchFlagRow> flags(@RequestParam("q") String q) {
		return crudTableSearchService.searchFlags(trimQ(q));
	}

	@GetMapping("/table-prices")
	public List<CrudSearchTablePriceRow> tablePrices(@RequestParam("q") String q) {
		return crudTableSearchService.searchTablePrices(trimQ(q));
	}

	private static String trimQ(String q) {
		return q == null ? "" : q.trim();
	}
}
