package br.ind.powerx.gestaoOperacional.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.model.ProductStock;
import br.ind.powerx.gestaoOperacional.model.ProductStockItem;
import br.ind.powerx.gestaoOperacional.model.Revenue;
import br.ind.powerx.gestaoOperacional.model.RevenueItem;
import br.ind.powerx.gestaoOperacional.model.enums.OperationType;
import br.ind.powerx.gestaoOperacional.repositories.ProductStockRepository;
import br.ind.powerx.gestaoOperacional.util.MergeResult;
import br.ind.powerx.gestaoOperacional.util.interfaces.StockMerger;
import jakarta.transaction.Transactional;

@Service
public class ProductStockService {
	private static final Logger logger = LoggerFactory.getLogger(ProductStockService.class);

	private final ProductStockRepository productStockRepository;
	private final StockMerger merger;

	@Autowired
	public ProductStockService(ProductStockRepository productStockRepository, StockMerger merger) {
		this.productStockRepository = productStockRepository;
		this.merger = merger;
	}

	@Transactional
	public void updateStock(List<Revenue> revenues) {
		for (Revenue revenue : revenues) {
			Customer customer = revenue.getCustomer();
			ProductStock stock = customer.getProductStock();

			if (stock == null) {
				stock = new ProductStock();
				stock.setCustomer(customer);
			}

			List<ProductStockItem> incoming = ProductItemConverter.toStockItem(revenue);

			MergeResult result = merger.merge(stock.getProductStockItems(), incoming);

			result.getNewItems().forEach(stock::addProductStockItem);

			productStockRepository.save(stock);
		}

	}

	@Transactional
	public void adjustStockOnUpdate(List<RevenueItem> oldItems, OperationType oldOpType, Customer oldCustomer,
			List<RevenueItem> newItems, OperationType newOpType, Customer newCustomer) {

		Map<Product, Integer> oldEffect = computeEffect(oldItems, oldOpType);
		Map<Product, Integer> revertOld = oldEffect.entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, e -> -e.getValue()));
		applyDeltas(oldCustomer, revertOld);

		Map<Product, Integer> newEffect = computeEffect(newItems, newOpType);
		applyDeltas(newCustomer, newEffect);

		logger.info("Estoque ajustado: oldClient={} ↔ newClient={}", oldCustomer.getId(), newCustomer.getId());
	}

	private Map<Product, Integer> computeEffect(
	        List<RevenueItem> items,
	        OperationType opType) {
	    int sign = (opType == OperationType.SALE ? +1 : -1);
	    return items.stream()
	        .collect(Collectors.toMap(
	            RevenueItem::getProduct,
	            i -> sign * i.getQuantity(),
	            Integer::sum
	        ));
	}

	private void applyDeltas(Customer customer, Map<Product, Integer> deltas) {
		
		if(customer.getProductStock() == null) {
			customer.setProductStock(new ProductStock());
		}
		
	    List<ProductStockItem> stockDeltas = deltas.entrySet().stream()
	        .filter(e -> e.getValue() != 0)
	        .map(e -> new ProductStockItem(null, e.getKey(), e.getValue(), null))
	        .toList();

	    MergeResult result = merger.merge(
	        customer.getProductStock().getProductStockItems(),
	        stockDeltas
	    );
	    result.getNewItems()
	          .forEach(customer.getProductStock()::addProductStockItem);
	    productStockRepository.save(customer.getProductStock());
	}

	
	public void removeFromStock(ProductStock currentStock, Collection<ProductStockItem>  removeFromStock) {
		removeFromStock.stream().forEach(i -> i.setQuantity(-i.getQuantity()));
		
		MergeResult result = merger.merge(currentStock.getProductStockItems(), removeFromStock);
		
		result.getNewItems().forEach(currentStock::addProductStockItem);
	}
	
	@Transactional
	public void subtractFromStock(Customer customer, Product product, Integer quantity) {
		ProductStock stock = customer.getProductStock();
		
		if (stock == null) {
			stock = new ProductStock();
			stock.setCustomer(customer);
			customer.setProductStock(stock);
		}
		
		// Criar item com quantidade negativa para subtrair do estoque
		ProductStockItem itemToSubtract = new ProductStockItem(null, product, -quantity, null);
		
		MergeResult result = merger.merge(stock.getProductStockItems(), List.of(itemToSubtract));
		
		result.getNewItems().forEach(stock::addProductStockItem);
		
		productStockRepository.save(stock);
	}
	
	@Transactional
	public void addToStock(Customer customer, Product product, Integer quantity) {
		ProductStock stock = customer.getProductStock();
		
		if (stock == null) {
			stock = new ProductStock();
			stock.setCustomer(customer);
			customer.setProductStock(stock);
		}
		
		// Criar item com quantidade positiva para adicionar ao estoque
		ProductStockItem itemToAdd = new ProductStockItem(null, product, quantity, null);
		
		MergeResult result = merger.merge(stock.getProductStockItems(), List.of(itemToAdd));
		
		result.getNewItems().forEach(stock::addProductStockItem);
		
		productStockRepository.save(stock);
	}


}
