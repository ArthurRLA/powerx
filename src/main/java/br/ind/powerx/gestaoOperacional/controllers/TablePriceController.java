package br.ind.powerx.gestaoOperacional.controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.model.TablePrice;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.TablePriceSaveDTO;
import br.ind.powerx.gestaoOperacional.services.AuthenticationService;
import br.ind.powerx.gestaoOperacional.services.CustomerService;
import br.ind.powerx.gestaoOperacional.services.ProductService;
import br.ind.powerx.gestaoOperacional.services.TablePriceService;

@Controller
@RequestMapping("/table-prices")
public class TablePriceController {

	private final TablePriceService tableService;

	private final ProductService productService;

	private final CustomerService customerService;

	private final AuthenticationService authenticationService;

	@Autowired
	public TablePriceController(TablePriceService tableService, ProductService productService,
			CustomerService customerService, AuthenticationService authenticationService) {
		this.tableService = tableService;
		this.productService = productService;
		this.customerService = customerService;
		this.authenticationService = authenticationService;
	}

	@GetMapping
	public String findAll(
			@RequestParam(defaultValue = "0") int page, 
			@RequestParam(defaultValue = "50") int size,
			Model model) {
		User user = authenticationService.getUserAuthenticated();
		Page<TablePrice> tables = tableService.findAll(PageRequest.of(page, size));
		
		List<TablePrice> orderedList = new ArrayList<>(tables.getContent());
	    orderedList.sort(
	        Comparator.comparing(
	            t -> t.getCustomer() != null ? t.getCustomer().getFantasyName() : null,
	            Comparator.nullsFirst(Comparator.naturalOrder())
	        )
	    );
		
		List<Customer> customers = customerService.findAllByActiveTrueOrderByFantasyNameAsc();
		List<Product> products = productService.findAllOrderByProductCodeAsc();

		model.addAttribute("tablePrices", orderedList);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", tables.getTotalPages());
		model.addAttribute("user", user);
		model.addAttribute("products", products);
		model.addAttribute("customers", customers);

		return "table-price";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute TablePriceSaveDTO table) {
		tableService.save(table);

		return "redirect:/table-prices";
	}

	@PostMapping("/update/{id}")
	public String update(@PathVariable Long id, @ModelAttribute TablePriceSaveDTO table) {
		tableService.update(id, table);
		return "redirect:/table-prices";
	}
	
	@PostMapping("save/spreadsheet")
	public String saveBySpreadSheet(@RequestParam(name = "file") MultipartFile file, Model model) {
		try {
			tableService.saveBySpreadsheet(file);
			return "redirect:/table-prices";
		}
		catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
			model.addAttribute("error", e);
			return "error";
		
		}
	}
	
	@GetMapping("/filter")
	public String filterTablePrice(
			@RequestParam(defaultValue = "0") int page, 
			@RequestParam(defaultValue = "50") int size,
			@RequestParam(value = "customers", required = false) List<Long> customerIds,
			Model model) {
		Page<TablePrice> tables = tableService.filter(customerIds, PageRequest.of(page, size));

		model.addAttribute("tablePrices", tables.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", tables.getTotalPages());
		
		model.addAttribute("customerIds", customerIds);
		model.addAttribute("size", size);

		return "fragments/filteredTablePrices :: filtered-table-prices";
	}
	
	@GetMapping("/clearFilters")
	public String clearFilters(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size,
			Model model) {

		Page<TablePrice> tables = tableService.findAllByCustomerFantasyNameAsc(PageRequest.of(page, size));

		model.addAttribute("tablePrices", tables.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", tables.getTotalPages());

		return "fragments/filteredTablePrices :: filtered-table-prices";
	}
}

















