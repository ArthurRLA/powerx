package br.ind.powerx.gestaoOperacional.util;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.dtos.SaleDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.UpdateSaleDto;
import br.ind.powerx.gestaoOperacional.model.dtos.UpdateSaleItemDTO;

public class SaleQuantityValidator {

    private static final Logger logger = LoggerFactory.getLogger(SaleQuantityValidator.class);

    public static boolean check(List<SaleDTO> salesDtos, Customer customer) {
        if (customer.getMechanicApuration().getName().equals("Somente mecânicos"))
            return true;

        logger.info("Iniciando verificação de quantidades por função...");

        Map<Long, Map<String, Integer>> productPerFunction = salesDtos.stream()
                .flatMap(sale -> sale.getProducts().stream()
                        .map(p -> {
                            String originalFunction = sale.getFunction();
                            String groupedFunction;
                            if (originalFunction.equalsIgnoreCase("Consultor Técnico")
                                    || originalFunction.equalsIgnoreCase("Consultor de Funilaria")) {
                                groupedFunction = "Consultores";
                            } else if (originalFunction.equalsIgnoreCase("Mecânico")) {
                                groupedFunction = "Mecânico";
                            } else {
                                return null;
                            }
                            return new AbstractMap.SimpleEntry<>(
                                    p.getProductId(),
                                    new AbstractMap.SimpleEntry<>(groupedFunction, p.getQuantity()));
                        })
                        .filter(entry -> entry != null))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.toMap(
                                e -> e.getValue().getKey(),
                                e -> e.getValue().getValue(),
                                Integer::sum,
                                HashMap::new)));

        logger.debug("Mapa de vendas acumuladas por produto e função (agrupadas):");
        productPerFunction.forEach((productId, functionMap) -> {
            logger.debug("Produto ID: {}", productId);
            functionMap.forEach((function, quantity) -> logger.debug("  Função agrupada: {}, Quantidade total: {}",
                    function, quantity));
        });

        boolean mecanicoLiderouAlgumProduto = productPerFunction.entrySet().stream()
                .map(entry -> {
                    Long productId = entry.getKey();
                    Map<String, Integer> functionMap = entry.getValue();

                    String topFunction = functionMap.entrySet().stream()
                            .max(Comparator.comparingInt(Map.Entry::getValue))
                            .map(Map.Entry::getKey)
                            .orElse("");

                    logger.info("Produto {} - função agrupada que mais vendeu: {}", productId, topFunction);
                    return topFunction;
                })
                .anyMatch(topFunction -> topFunction.equalsIgnoreCase("Mecânico"));

        if (mecanicoLiderouAlgumProduto) {
            logger.warn("Mecânico foi a função que mais vendeu em pelo menos um produto (considerando agrupamento).");
        } else {
            logger.info("Nenhum produto teve Mecânico como função com maior quantidade (considerando agrupamento).");
        }

        return !mecanicoLiderouAlgumProduto;
    }

    public static boolean check(UpdateSaleDto updateSaleDto, Customer customer) {
        if (customer.getMechanicApuration().getName().equals("Somente mecânicos"))
            return true;

        boolean result = true;

        logger.info("Iniciando verificação de quantidades por função...");

        List<UpdateSaleItemDTO> consultants = updateSaleDto.getConsultantSales();
        consultants.addAll(updateSaleDto.getTinkerSales());
        List<UpdateSaleItemDTO> mechanics = updateSaleDto.getMechanicSales();

        Map<Long, Integer> consultantProductPerQuantity = consultants.stream()
                .collect(Collectors.toMap(UpdateSaleItemDTO::getProductId, UpdateSaleItemDTO::getQuantity, Integer::sum));

        Map<Long, Integer> mechanicProductPerQuantity = mechanics.stream()
                .collect(Collectors.toMap(UpdateSaleItemDTO::getProductId, UpdateSaleItemDTO::getQuantity, Integer::sum));

        StringBuilder sb = new StringBuilder();

        for(Map.Entry<Long, Integer> entry : mechanicProductPerQuantity.entrySet()){
            Long productId = entry.getKey();
            Integer mecQuantity = entry.getValue();

            Integer consQuantity = consultantProductPerQuantity.get(productId);

            if(mecQuantity > consQuantity){
                result = false;
                sb.append("\nId produto: " + productId + ", com diferença de: " + (mecQuantity - consQuantity));
            }
        }

        
        if (!result) {
            logger.warn("Mecânico foi a função que mais vendeu em : {}", sb.toString());
        } else {
            logger.info("Nenhum produto teve Mecânico como função com maior quantidade (considerando agrupamento).");
        }

        return result;
    }
}
