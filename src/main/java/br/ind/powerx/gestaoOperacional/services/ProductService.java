package br.ind.powerx.gestaoOperacional.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Function;
import br.ind.powerx.gestaoOperacional.model.IncentiveValue;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.model.dtos.AddIncentiveValueDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.IncentiveValueDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.IncentiveValueSpreadsheet;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.repositories.FunctionRepository;
import br.ind.powerx.gestaoOperacional.repositories.ProductRepository;
import br.ind.powerx.gestaoOperacional.util.Spreadsheets;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private IncentiveValueService valueService;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private FunctionRepository functionRepository;

	public void save(Product product) {
		if (product.getUnysoftCode() != null) {
			if (product.getUnysoftCode().isBlank() || product.getUnysoftCode().isEmpty()
					|| product.getUnysoftCode().equals("")) {
				product.setUnysoftCode(null);
			}
		}
		productRepository.save(product);
	}

	public List<Product> findAll() {
		return productRepository.findAll();
	}

	@Transactional
	public void addIncentiveValue(AddIncentiveValueDTO incentiveDTO) {
		Product product = productRepository.findById(incentiveDTO.productId())
				.orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

		List<IncentiveValueDTO> incentiveValueDTO = incentiveDTO.incentives();
		List<IncentiveValue> incentiveValues = new ArrayList<>();

		for (int i = 0; i < incentiveValueDTO.size(); i++) {

			Long customerId = incentiveValueDTO.get(i).customerId();
			Customer customer = customerRepository.findById(customerId)
					.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

			Long functionId = incentiveValueDTO.get(i).functionId();
			Function function = functionRepository.findById(functionId)
					.orElseThrow(() -> new EntityNotFoundException("Função não encontrado"));

			BigDecimal ccValue = incentiveValueDTO.get(i).ccValue();

			BigDecimal nfsValue = incentiveValueDTO.get(i).nfsValue();

			BigDecimal overValue = incentiveValueDTO.get(i).overValue();

			IncentiveValue incentiveValue = new IncentiveValue();
			incentiveValue.setCustomer(customer);
			incentiveValue.setFunction(function);
			incentiveValue.setId(null);
			incentiveValue.setProduct(product);
			incentiveValue.setCcValue(ccValue);
			incentiveValue.setCcValue(nfsValue);
			incentiveValue.setOverValue(overValue);

			incentiveValues.add(incentiveValue);
		}

		for (IncentiveValue i : incentiveValues) {
			i.setProduct(product);
			valueService.save(i);
			product.addIncentiveValue(i);
		}

		productRepository.save(product);
	}

	@Transactional
	public void addIncentiveValue(Long id, List<Long> customers, List<Long> functions, List<Double> ccValues,
			List<Double> nfsValues, List<Double> overValues) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

		Long customerId = customers.get(0);

		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

		List<IncentiveValue> currentValues = valueService.findAllByProductAndCustomer(product.getId(),
				customer.getId());

		currentValues.forEach(v -> {
			product.removeIncentiveValue(v);
			valueService.delete(v);
		});

		for (int i = 0; i < functions.size(); i++) {
			Long functionId = functions.get(i);
			Double ccValueD = ccValues.get(i);
			Double nfsValueD = nfsValues.get(i);
			Double overValueD = overValues.get(i);

			Function function = functionRepository.findById(functionId)
					.orElseThrow(() -> new EntityNotFoundException("Função não encontrada"));

			BigDecimal ccValue = new BigDecimal(ccValueD);

			BigDecimal nfsValue = new BigDecimal(nfsValueD);

			BigDecimal overValue = new BigDecimal(overValueD);

			IncentiveValue incentive = new IncentiveValue(null, customer, function, product, ccValue, nfsValue,
					overValue);
			valueService.save(incentive);
			product.addIncentiveValue(incentive);
		}

		productRepository.save(product);
	}

	@Transactional
	public void removeIncentiveValue(Long productId, Long incentiveValueId) {

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

		IncentiveValue incentive = valueService.findById(incentiveValueId)
				.orElseThrow(() -> new EntityNotFoundException("Incentivo não encontrado"));

		List<IncentiveValue> currentValues = product.getIncentiveValues();

		if (currentValues.contains(incentive)) {
			product.removeIncentiveValue(incentive);
			valueService.delete(incentive);
		}

	}

	public Page<Product> findAll(Pageable pageable) {
		return productRepository.findAll(pageable);
	}

	public Product findById(Long productId) {
		return productRepository.findById(productId)
				.orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
	}

	public void saveBySpreadsheet(MultipartFile file) throws IOException {
		try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
			Sheet sheet = workbook.getSheetAt(0);
			int lastRowWithData = Spreadsheets.getLastRowWithData(sheet);

			Iterator<Row> rowIterator = sheet.iterator();
			boolean isHeader = true;

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();

				if (isHeader) {
					isHeader = false;
					continue;
				}

				if (row.getRowNum() > lastRowWithData) {
					break;
				}

				if (Spreadsheets.isRowEmpty(row)) {
					System.out.println("Linha " + row.getRowNum() + " vazia - pulando");
					continue;
				}

				try {
					String customerCnpj = Spreadsheets.getStringCellValue(row.getCell(0));
					String functionName = Spreadsheets.getStringCellValue(row.getCell(1));
					String productCode = Spreadsheets.getStringCellValue(row.getCell(2));
					Double cc = Spreadsheets.getDoubleCellValue(row.getCell(3));
					Double nfs = Spreadsheets.getDoubleCellValue(row.getCell(4));
					Double over = Spreadsheets.getDoubleCellValue(row.getCell(5));

					IncentiveValueSpreadsheet valueSpreadsheet  = new IncentiveValueSpreadsheet(customerCnpj, functionName, 
							productCode, cc, nfs, over);

					IncentiveValue value = valueFromDto(valueSpreadsheet);
					
					if(validateIncentiveValue(value)) {
						valueService.save(value);
					}

				} catch (Exception e) {
					System.err.println("Erro crítico na linha " + row.getRowNum() + ": " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	private IncentiveValue valueFromDto(IncentiveValueSpreadsheet valueSpreadsheet) {
		IncentiveValue value = new IncentiveValue();
		Customer customer = customerRepository.findByCnpj(valueSpreadsheet.customerCnpj());
		Function function = functionRepository.findByName(valueSpreadsheet.functionName()).get();
		Product product = productRepository.findByProductCode(valueSpreadsheet.productCode());
		BigDecimal cc = new BigDecimal(valueSpreadsheet.cc());
		BigDecimal nfs = new BigDecimal(valueSpreadsheet.nfs());
		BigDecimal over = new BigDecimal(valueSpreadsheet.over());
		
		value.setCustomer(customer);
		value.setFunction(function);
		value.setProduct(product);
		value.setCcValue(cc);
		value.setNfsValue(nfs);
		value.setOverValue(over);
		
		return value;
	}
	
	private boolean validateIncentiveValue(IncentiveValue value) {
		
		if(value.getProduct() == null || value.getFunction() == null || value.getCustomer() == null) {
			return false;
		}
		
		List<IncentiveValue> values = valueService.findAllByProductAndCustomerAndFunction(value.getProduct(), value.getCustomer(), value.getFunction());
	
		if(values.size() == 0 || values == null) {
			return true;
		}
		return false;
	
	}

	public List<Product> findAllOrderByProductCodeAsc() {
		return productRepository.findAllOrderByProductCodeAsc();
	}

}

















































