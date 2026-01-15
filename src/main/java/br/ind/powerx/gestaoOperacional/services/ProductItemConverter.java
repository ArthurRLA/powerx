package br.ind.powerx.gestaoOperacional.services;

import java.util.List;

import br.ind.powerx.gestaoOperacional.model.ProductStockItem;
import br.ind.powerx.gestaoOperacional.model.Revenue;
import br.ind.powerx.gestaoOperacional.model.RevenueItem;
import br.ind.powerx.gestaoOperacional.model.enums.OperationType;

public class ProductItemConverter {
	public static List<ProductStockItem> toStockItem(Revenue revenue){
		return revenue.getRevenueItems().stream()
				.filter(r -> r.getQuantity() != 0)
				.map(r -> {
					if(!r.getRevenue().getCustomer().getGroup().getProducts().contains(r.getProduct())) {
						r.getRevenue().getCustomer().getGroup().addProduct(r.getProduct());
					}
					
					var item = new ProductStockItem(null, r.getProduct(), r.getQuantity(), null);
					
					if(revenue.getOperationType().equals(OperationType.RETURN)) {
						item.setQuantity(item.getQuantity() * -1);
					}
					
					return item;
				})
				.toList();
	}
	
	public static List<ProductStockItem> toStockItem(List<RevenueItem> items, OperationType opType) {
	    return items.stream()
	        .filter(r -> r.getQuantity() != 0)
	        .map(r -> {
	            if (!r.getRevenue().getCustomer().getGroup().getProducts().contains(r.getProduct())) {
	                r.getRevenue().getCustomer().getGroup().addProduct(r.getProduct());
	            }
	            var item = new ProductStockItem(null, r.getProduct(), r.getQuantity(), null);

	            if (opType == OperationType.SALE) {
	                item.setQuantity(-item.getQuantity());
	            }

	            return item;
	        })
	        .toList();
	}
}
