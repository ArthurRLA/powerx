package br.ind.powerx.gestaoOperacional.controllers;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.dtos.IncentiveCalculatedDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.SaleDetailsToUpdateDto;
import br.ind.powerx.gestaoOperacional.model.dtos.SaleDto;
import br.ind.powerx.gestaoOperacional.model.dtos.UpdateSaleDto;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.services.IncentiveMapper;
import br.ind.powerx.gestaoOperacional.services.SaleService;
import br.ind.powerx.gestaoOperacional.util.SaleQuantityValidator;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/sales")
public class RestSaleController {

    private final SaleService saleService;
    private final CustomerRepository customerRepository;
    private static final Logger logger = LoggerFactory.getLogger(RestSaleController.class);

    @Autowired
    public RestSaleController(SaleService saleService, CustomerRepository customerRepository) {
        this.saleService = saleService;
        this.customerRepository = customerRepository;
    }

    @PostMapping
    public ResponseEntity<?> saveSales(
            @RequestBody(required = true) List<SaleDto> salesDtos) {
        logger.info("Recebida requisição para salvar vendas com {} registros.", salesDtos.size());

        try {
            Long customerId = salesDtos.get(0).getCustomer();
            logger.debug("Buscando cliente com ID: {}", customerId);
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new EntityNotFoundException("Id do cliente não encontrado: " + customerId));
            boolean valid = SaleQuantityValidator.check(salesDtos, customer);
            if (!valid) {
                logger.warn("Validação falhou: Existem quantidades de aplicações maiores que as quantidades de vendas.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Existem quantidades de aplicações maiores que as quantidades de vendas.");
            }

            logger.info("Salvando vendas...");
            List<Incentive> newIncentives = saleService.saveSales(salesDtos);

            IncentiveCalculatedDTO dto = IncentiveMapper.toIncentiveCalculated(newIncentives);

            logger.info("Vendas salvas com sucesso.");
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            logger.error("Erro ao salvar vendas: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar usuários: " + e.getMessage());
        }
    }

    @GetMapping("/details/{num}")
    public ResponseEntity<?> findSalesByDocNum(
            @PathVariable Integer num) {
        logger.info("Recebida requisição para buscar detalhes da venda do documento: {}", num);
        try {
            SaleDetailsToUpdateDto dto = saleService.findSalesByDocNum(num);
            logger.info("Detalhes encontrados para documento: {}", num);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            logger.error("Erro ao buscar detalhes da venda: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar usuários: " + e.getMessage());
        }
    }

    @PostMapping("/update/{documentNumber}")
    public ResponseEntity<?> updateSales(
            @PathVariable Integer documentNumber,
            @RequestBody UpdateSaleDto updateSaleDto) {
        logger.info("Recebida requisição para atualizar vendas do documento: {}", documentNumber);
        try {
            Customer customer = saleService.findByDocumentNumber(documentNumber).get(0).getCustomer();
            logger.debug("Buscando cliente com ID: {}", customer.getId());
            boolean valid = SaleQuantityValidator.check(updateSaleDto, customer);

             if (!valid) {
                logger.warn("Validação falhou: Existem quantidades de aplicações maiores que as quantidades de vendas.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Existem quantidades de aplicações maiores que as quantidades de vendas.");
            }
            
            saleService.updateSales(updateSaleDto);
            logger.info("Vendas atualizadas com sucesso para documento: {}", documentNumber);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Erro ao atualizar vendas: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar usuários: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{documentNumber}")
    public ResponseEntity<?> deleteByDocumentNumber(
            @PathVariable Integer documentNumber) {
        logger.info("Recebida requisição para deletar vendas/incentivos do documento: {}", documentNumber);
        try {
            saleService.deleteByDocumentNumber(documentNumber);
            logger.info("Vendas/incentivos deletados com sucesso para documento: {}", documentNumber);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Erro ao deletar vendas/incentivos do documento {}: {}", documentNumber, e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar usuários: " + e.getMessage());
        }
    }

}