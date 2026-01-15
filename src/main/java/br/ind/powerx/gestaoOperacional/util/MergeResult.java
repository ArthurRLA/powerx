package br.ind.powerx.gestaoOperacional.util;

import java.util.List;

import br.ind.powerx.gestaoOperacional.model.ProductStockItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MergeResult {

	private final List<ProductStockItem> updatedItems;;
	private final List<ProductStockItem> newItems;
}
