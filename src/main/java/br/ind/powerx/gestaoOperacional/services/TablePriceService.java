package br.ind.powerx.gestaoOperacional.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.model.TablePrice;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerDto;
import br.ind.powerx.gestaoOperacional.model.dtos.ProductBasicDto;
import br.ind.powerx.gestaoOperacional.model.dtos.TablePriceDto;
import br.ind.powerx.gestaoOperacional.model.dtos.TablePriceEditDto;
import br.ind.powerx.gestaoOperacional.model.dtos.TablePriceSaveDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.TablePriceSpreadsheetDto;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.repositories.ProductRepository;
import br.ind.powerx.gestaoOperacional.repositories.TablePriceRepository;
import br.ind.powerx.gestaoOperacional.repositories.specifications.TablePriceSpecifications;
import br.ind.powerx.gestaoOperacional.util.Spreadsheets;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class TablePriceService {

	private static final Logger logger = LoggerFactory.getLogger(TablePriceService.class); 

	private final TablePriceRepository tableRepository;
	private final CustomerRepository customerRepository;
	private final ProductRepository productRepository;

	@Autowired
	public TablePriceService(TablePriceRepository tableRepository, CustomerRepository customerRepository,
			ProductRepository productRepository) {
		this.tableRepository = tableRepository;
		this.customerRepository = customerRepository;
		this.productRepository = productRepository;
	}

	public List<TablePrice> findAll() {
		return tableRepository.findAll();
	}

	public List<TablePrice> findAllByCustomerFantasyNameAsc() {
		return tableRepository.findAllOrderByCustomerFantasyName();
	}
	
	public Page<TablePrice> findAllByCustomerFantasyNameAsc(Pageable pageable) {
		return tableRepository.findAllOrderByCustomerFantasyName(pageable);
	}

	public Page<TablePrice> findAll(Pageable pageable) {
		return tableRepository.findAll(pageable);
	}

	@Transactional
	public void save(TablePriceSaveDTO tableDTO) {
		TablePrice table = new TablePrice();
		Customer customer = findCustomerById(tableDTO.customer());
		Product product = findProductById(tableDTO.product());

		var checkTable = tableRepository.findByCustomerAndProduct(customer, product);

		if (!checkTable.isPresent()) {
			table.setCustomer(customer);
			table.setProduct(product);
			table.setPrice(tableDTO.price());
			tableRepository.save(table);
		}
	}

	@Transactional
	public void update(Long id, TablePriceSaveDTO tableDTO) {
		TablePrice table = tableRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Tabela de Preço não encontrada"));

		Customer customer = findCustomerById(tableDTO.customer());
		Product product = findProductById(tableDTO.product());

		if (!table.getCustomer().equals(customer)) {
			table.setCustomer(customer);
		}

		if (!table.getProduct().equals(product)) {
			table.setProduct(product);
		}

		table.setPrice(tableDTO.price());

		tableRepository.save(table);
	}

	private Customer findCustomerById(Long customerId) {
		return customerRepository.findById(customerId)
				.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
	}

	private Product findProductById(Long productId) {
		return productRepository.findById(productId)
				.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
	}
	
	private Optional<TablePrice> findById(Long id){
		return tableRepository.findById(id);
	}

	@Transactional
	public void saveBySpreadsheet(MultipartFile file) throws IOException {
	    logger.info("Iniciando o processamento da planilha: {}", file.getOriginalFilename());

	    List<TablePrice> tables = new ArrayList<>();
	    int totalLinhas = 0;
	    int linhasVazias = 0;

	    try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
	        Sheet sheet = workbook.getSheetAt(0);
	        Iterator<Row> rowIterator = sheet.iterator();

	        boolean isHeader = true;

	        while (rowIterator.hasNext()) {
	            Row row = rowIterator.next();

	            if (isHeader) {
	                isHeader = false;
	                continue;
	            }

	            totalLinhas++;

	            if (Spreadsheets.isRowEmpty(row)) {
	                linhasVazias++;
	                logger.warn("Linha {} vazia. Ignorando...", row.getRowNum());
	                continue;
	            }

	            String cnpj = Spreadsheets.getStringCellValue(row.getCell(0));
	            String productCode = Spreadsheets.getStringCellValue(row.getCell(1));
	            Double price = Spreadsheets.getDoubleCellValue(row.getCell(2));

	            TablePriceSpreadsheetDto dto = new TablePriceSpreadsheetDto(cnpj, productCode, price);
	            
	            logger.debug("Lendo linha {}: {}", row.getRowNum(), dto);

	            TablePrice table = fromDto(dto);
	            tables.add(table);
	        }
	    }

	    logger.info("Total de linhas lidas: {}", totalLinhas);
	    logger.info("Linhas vazias: {}", linhasVazias);
	    logger.info("Entradas válidas: {}", tables.size());

	    tableRepository.saveAll(tables);
	    logger.info("Dados salvos com sucesso. Total de registros: {}", tables.size());
	}

	private TablePrice fromDto(TablePriceSpreadsheetDto dto) {
	    Customer customer = customerRepository.findByCnpj(dto.getCnpj());
	    if (customer == null) {
	        throw new EntityNotFoundException("Cliente com CNPJ " + dto.getCnpj() + " não encontrado");
	    }

	    Product product = productRepository.findByProductCode(dto.getProductCode());
	    if (product == null) {
	        throw new EntityNotFoundException("Produto com código " + dto.getProductCode() + " não encontrado");
	    }

	    if (dto.getPrice() == null || dto.getPrice() <= 0) {
	        throw new IllegalArgumentException("Preço inválido para o produto " + dto.getProductCode());
	    }
	    
	    if(tableRepository.findByCustomerAndProduct(customer, product).isPresent()) {
	    	throw new IllegalArgumentException("Produto já incluso na tabela de preço desse cliente");
	    }

	    return new TablePrice(null, customer, product, new BigDecimal(dto.getPrice()));
	}

	public TablePriceDto getTablePriceDto(Long id) {
		return findById(id).stream()
				.map(t -> {
					var dto = new TablePriceDto();
					dto.setId(t.getId());
					dto.setCustomer(t.getCustomer().getFantasyName());
					dto.setProduct(t.getProduct().getProductCode());
					dto.setPrice(t.getPrice());
					
					return dto;
				})
				.findFirst()
				.orElseThrow(() -> new EntityNotFoundException("Tabela não encontrada"));
	}

	public TablePriceEditDto getTablePriceEditDto(Long id) {
		return findById(id).stream()
				.map(t -> {
					var dto = new TablePriceEditDto();
					dto.setId(t.getId());
					dto.setCustomer(t.getCustomer().getId());
					dto.setProduct(t.getProduct().getId());
					dto.setPrice(t.getPrice());
					
					var customers = customerRepository.findAllByActiveTrueOrderByFantasyNameAsc();
					var products = productRepository.findAllOrderByProductCodeAsc();
					
					dto.setCustomers(
							customers.stream()
								.map(c -> {
									var cus = new CustomerDto();
									cus.setId(c.getId());
									cus.setName(c.getFantasyName());
									
									return cus;
								})
								.toList()
							);
					
					dto.setProducts(
							products.stream()
								.map(p -> {
									var prod = new ProductBasicDto();
									prod.setId(p.getId());
									prod.setCode(p.getProductCode());
									
									return prod;
								})
								.toList()
							);
					
					return dto;
				})
				.findFirst()
				.orElseThrow(() -> new EntityNotFoundException("Tabela não encontrada"));
	}

	public Page<TablePrice> filter(List<Long> customerIds, Pageable pagable) {
		Specification<TablePrice> spec = Specification.where(null);
		
		if (customerIds != null && !customerIds.isEmpty()) {
			spec = spec.and(TablePriceSpecifications.customersIn(customerIds));
		}
		
		return tableRepository.findAll(spec, pagable);
	}
}





















