package br.ind.powerx.gestaoOperacional.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.model.ProductStockItem;
import br.ind.powerx.gestaoOperacional.util.MergeResult;
import br.ind.powerx.gestaoOperacional.util.interfaces.StockMerger;

@Component
public class DefaultStockMerger implements StockMerger {

	@Override
	public MergeResult merge(Collection<ProductStockItem> current, Collection<ProductStockItem> incoming) {
		Map<Product, ProductStockItem> currentMap = current.stream()
				.collect(Collectors.toMap(ProductStockItem::getProduct, i -> i));
		
		List<ProductStockItem> updated = new ArrayList<>();
		List<ProductStockItem> toAdd = new ArrayList<>();
		
		for(ProductStockItem inc : incoming) {
			ProductStockItem existingItem = currentMap.get(inc.getProduct());
			if(existingItem != null) {
				existingItem.setQuantity(existingItem.getQuantity() + inc.getQuantity());
				updated.add(existingItem);
			}
			else {
				toAdd.add(inc);
			}
		}
		
		return new MergeResult(updated, toAdd);
	}

}
