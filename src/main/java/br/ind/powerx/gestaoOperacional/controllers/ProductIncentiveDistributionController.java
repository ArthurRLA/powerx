package br.ind.powerx.gestaoOperacional.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.ind.powerx.gestaoOperacional.model.ApurationType;
import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Function;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.services.ApurationTypeService;
import br.ind.powerx.gestaoOperacional.services.AuthenticationService;
import br.ind.powerx.gestaoOperacional.services.CustomerService;
import br.ind.powerx.gestaoOperacional.services.FunctionService;
import br.ind.powerx.gestaoOperacional.services.ProductService;

@Controller
@RequestMapping("/product-incentive-distribution")
public class ProductIncentiveDistributionController {

	@Autowired
	private ProductService productService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ApurationTypeService apurationTypeService;

	@Autowired
	private FunctionService functionService;

	@Autowired
	private AuthenticationService authenticationService;

	@GetMapping
	public String page(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size,
			Model model) {
		User user = authenticationService.getUserAuthenticated();
		Page<Product> products = productService
				.findAll(PageRequest.of(page, size, Sort.by(Sort.Order.asc("productCode"))));
		List<Customer> customers = customerService.findAllByActiveTrueOrderByFantasyNameAsc();
		List<ApurationType> apurationTypes = apurationTypeService.findAllOrderByNameAsc();
		List<Function> functions = functionService.findAllOrderByNameAsc();

		model.addAttribute("products", products.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", products.getTotalPages());
		model.addAttribute("user", user);
		model.addAttribute("customers", customers);
		model.addAttribute("apurationTypes", apurationTypes);
		model.addAttribute("functions", functions);

		return "product-incentive-distribution";
	}
}
