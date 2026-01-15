package br.ind.powerx.gestaoOperacional.util.interfaces;

import java.util.Collection;

import br.ind.powerx.gestaoOperacional.model.ProductStockItem;
import br.ind.powerx.gestaoOperacional.util.MergeResult;

public interface StockMerger {
	MergeResult merge(Collection<ProductStockItem> current, Collection<ProductStockItem> incoming);
}
