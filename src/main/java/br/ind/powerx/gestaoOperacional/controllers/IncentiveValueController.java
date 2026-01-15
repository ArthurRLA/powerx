package br.ind.powerx.gestaoOperacional.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import br.ind.powerx.gestaoOperacional.model.Function;
import br.ind.powerx.gestaoOperacional.model.IncentiveValue;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.services.IncentiveValueService;
import br.ind.powerx.gestaoOperacional.services.ProductService;

@Controller
@RequestMapping("/incentive-value")
public class IncentiveValueController {

	private final IncentiveValueService incentiveValueService;
	
	private final ProductService productService;
	
	@Autowired
	public IncentiveValueController(IncentiveValueService incentiveValueService, 
			ProductService productService) {
		this.incentiveValueService = incentiveValueService;
		this.productService = productService;
	}
	
	@GetMapping("/{productId}/{customerId}")
	public String getIncentiveValueByCustomerAndproduct(
	        @PathVariable(name = "productId") Long productId, 
	        @PathVariable(name = "customerId") Long customerId, 
	        Model model) {
	    
	    List<IncentiveValue> incentiveValues = incentiveValueService.findAllByProductAndCustomer(productId, customerId);
	    
	    Product product = productService.findById(productId);
	    
	    BigDecimal ccTotal = getTotalCcValue(incentiveValues);
	    BigDecimal nfsTotal = getTotalNfsValue(incentiveValues);
	    BigDecimal overTotal = getTotalOverValue(incentiveValues);
	    
	    if (incentiveValues.isEmpty()) {
	        model.addAttribute("incentiveValues", new ArrayList<>());
	        model.addAttribute("product", product);
	    } else {
	        model.addAttribute("product", incentiveValues.get(0).getProduct());
	    }

	    model.addAttribute("incentiveValues", incentiveValues);
	    model.addAttribute("nfsTotal", nfsTotal);
	    model.addAttribute("ccTotal", ccTotal);
	    model.addAttribute("overTotal", overTotal);
	    
	    return "fragments/incentiveValueTable :: incentive-values";
	}
	
	@GetMapping("/a/{customerId}/{apurationType}")
	public String getIncentiveValuesByCustomerAndApurationType(
	        @PathVariable Long customerId, 
	        @PathVariable String apurationType, 
	        Model model) {

	    List<IncentiveValue> incentiveValues = incentiveValueService.findAllByCustomer(customerId);
	    
	    List<Function> sortedFunctions = incentiveValues.stream()
	            .map(IncentiveValue::getFunction)
	            .distinct()
	            .sorted(Comparator.comparing(Function::getName)) 
	            .collect(Collectors.toList());
	    
	    Map<Product, Map<Function, BigDecimal>> productFunctionValueMap = new LinkedHashMap<>();
	    
	    Map<Product, BigDecimal> productTotalMap = new LinkedHashMap<>();
	    
	    Map<Function, Boolean> functionHasPositiveValueMap = new LinkedHashMap<>();
	    
	    sortedFunctions.forEach(function -> functionHasPositiveValueMap.put(function, false));
	    
	    for (IncentiveValue iv : incentiveValues) {
	        Product product = iv.getProduct();
	        Function function = iv.getFunction();
	        BigDecimal value = getValueByApurationType(iv, apurationType); 
	        
	        productFunctionValueMap
	            .computeIfAbsent(product, k -> new LinkedHashMap<>())
	            .put(function, value);
	        
	        productTotalMap.put(product, productTotalMap.getOrDefault(product, BigDecimal.ZERO).add(value));
	        
	        if (value.compareTo(BigDecimal.ZERO) > 0) {
	            functionHasPositiveValueMap.put(function, true);
	        }
	    }
	    
	    List<Function> filteredFunctions = sortedFunctions.stream()
	            .filter(functionHasPositiveValueMap::get)
	            .collect(Collectors.toList());
	    
	    List<Product> sortedProducts = productFunctionValueMap.keySet().stream()
	            .sorted(Comparator.comparing(Product::getProductCode))
	            .collect(Collectors.toList());
	    
	    model.addAttribute("apurationType", apurationType);
	    model.addAttribute("sortedFunctions", filteredFunctions);
	    model.addAttribute("sortedProducts", sortedProducts);
	    model.addAttribute("productFunctionValueMap", productFunctionValueMap);
	    model.addAttribute("productTotalMap", productTotalMap);
	    
	    return "fragments/incentiveValueTableCustomer :: incentiveValueTableFragment";
	}

	private BigDecimal getValueByApurationType(IncentiveValue iv, String apurationType) {
	    switch (apurationType) {
	        case "Conta Corrente":
	            return iv.getCcValue();
	        case "NF Serviço":
	            return iv.getNfsValue();
	        case "Over":
	            return iv.getOverValue() != null ? iv.getOverValue() : BigDecimal.ZERO;
	        default:
	            return BigDecimal.ZERO;
	    }
	}
	
	public BigDecimal getTotalCcValue(List<IncentiveValue> incentives) {
        return incentives.stream()
                .map(IncentiveValue::getCcValue)
                .filter(Objects::nonNull)        
                .reduce(BigDecimal.ZERO, BigDecimal::add); 
    }

    public BigDecimal getTotalNfsValue(List<IncentiveValue> incentives) {
        return incentives.stream()
                .map(IncentiveValue::getNfsValue) 
                .filter(Objects::nonNull)        
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public BigDecimal getTotalOverValue(List<IncentiveValue> incentives) {
        return incentives.stream()
                .map(IncentiveValue::getOverValue) 
                .filter(Objects::nonNull)        
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}






























