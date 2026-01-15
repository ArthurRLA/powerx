package br.ind.powerx.gestaoOperacional.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ind.powerx.gestaoOperacional.model.ProductStock;

public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {

}
