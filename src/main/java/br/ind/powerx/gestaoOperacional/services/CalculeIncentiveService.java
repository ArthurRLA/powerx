package br.ind.powerx.gestaoOperacional.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ind.powerx.gestaoOperacional.model.ApurationType;
import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Employee;
import br.ind.powerx.gestaoOperacional.model.Function;
import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.IncentiveValue;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.model.Sale;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.enums.IncentiveStatus;
import br.ind.powerx.gestaoOperacional.repositories.ApurationTypeRepository;
import br.ind.powerx.gestaoOperacional.repositories.FunctionRepository;
import br.ind.powerx.gestaoOperacional.repositories.IncentiveRepository;
import br.ind.powerx.gestaoOperacional.repositories.IncentiveValueRepository;
import br.ind.powerx.gestaoOperacional.repositories.SaleRepository;

@Service
public class CalculeIncentiveService {

    private static final Logger logger = LoggerFactory.getLogger(CalculeIncentiveService.class);

    private final SaleRepository saleRepository;
    private final IncentiveRepository incentiveRepository;
    private final IncentiveValueRepository incentiveValueRepository;
    private final ApurationTypeRepository apurationTypeRepository;
    private final AuthenticationService authenticationService;
    private final FunctionRepository functionRepository;
    private final ProductStockService productStockService;
    private final CurrentAccountService currentAccountService;

    @Autowired
    public CalculeIncentiveService(SaleRepository saleRepository, IncentiveRepository incentiveRepository,
            IncentiveValueRepository incentiveValueRepository, ApurationTypeRepository apurationTypeRepository,
            AuthenticationService authenticationService, FunctionRepository functionRepository,
            ProductStockService productStockService, CurrentAccountService currentAccountService) {
        this.saleRepository = saleRepository;
        this.incentiveRepository = incentiveRepository;
        this.incentiveValueRepository = incentiveValueRepository;
        this.apurationTypeRepository = apurationTypeRepository;
        this.authenticationService = authenticationService;
        this.functionRepository = functionRepository;
        this.productStockService = productStockService;
        this.currentAccountService = currentAccountService;
    }

    public List<Incentive> calculateIncentives(List<Sale> sales) {
        logger.info("Iniciando cálculo de incentivos para {} vendas.", sales.size());
        validateSales(sales);

        sales = sales.stream().filter(sale -> sale.getQuantity() > 0).toList();
        logger.debug("Vendas após filtro de quantidade positiva: {}", sales);

        User user = authenticationService.getUserAuthenticated();
        logger.debug("Usuário autenticado: {}", user);

        List<Incentive> incentives = new ArrayList<>();
        Customer customer = sales.get(0).getCustomer();
        logger.debug("Cliente da primeira venda: {}", customer);

        Map<String, ApurationType> apurationTypes = preloadApurationTypes();
        logger.debug("Tipos de apuração carregados do banco: {}", apurationTypes.values());

        for (Sale sale : sales) {
            logger.debug("Processando venda: {}", sale);
            List<Incentive> incSale = calculateIncentivesForSale(sale, apurationTypes, customer, user);
            logger.debug("Incentivos gerados para venda {}: {}", sale.getDocumentNumber(), incSale);
            incentives.addAll(incSale);
        }

        List<Incentive> roleIncentives = calculateIncentivesForRoles(customer, sales, apurationTypes, user);
        logger.debug("Incentivos gerados por função: {}", roleIncentives);
        incentives.addAll(roleIncentives);

        List<Incentive> incentivesCompacted = compactIncentives(incentives, apurationTypes);
        logger.info("Total de incentivos compactados: {}", incentivesCompacted.size());

        saleRepository.saveAll(sales);
        logger.debug("Vendas salvas no banco: {}", sales);

        incentiveRepository.saveAll(incentivesCompacted);
        logger.debug("Incentivos salvos no banco: {}", incentivesCompacted);

        return incentivesCompacted;
    }

    private void validateSales(List<Sale> sales) {
        if (sales == null || sales.isEmpty()) {
            throw new IllegalArgumentException("A lista de vendas não pode estar vazia.");
        }
        for (Sale sale : sales) {
            if (sale == null) {
                throw new IllegalArgumentException("A venda não pode ser nula.");
            }
            if (sale.getCustomer() == null || sale.getEmployee() == null || sale.getProduct() == null
                    || sale.getQuantity() == null) {
                throw new IllegalArgumentException("Venda contém informações obrigatórias nulas.");
            }
        }
        logger.debug("Validação das vendas concluída. Vendas válidas: {}", sales);
    }

    private int calculateConsultantTotalQuantity(List<Sale> sales, Product product, Customer customer) {
        String mechanicApurationName = customer.getMechanicApuration().getName();
        int totalQuantity = 0;

        for (Sale sale : sales) {
            if (sale.getProduct().equals(product) && !mechanicApurationName.equalsIgnoreCase("Somente Mecânicos")) {
                if (sale.getEmployee().getFunctions().stream()
                        .anyMatch(f -> f.getName().equalsIgnoreCase("Consultor Técnico"))
                        && (sale.getFunction().equals("Consultor Técnico"))) {
                    totalQuantity += sale.getQuantity();
                }
            }
        }
        logger.debug("Quantidade total para o produto '{}' (Consultores): {}", product.getProductName(), totalQuantity);
        return totalQuantity;
    }

    private int calculateTinkerTotalQuantity(List<Sale> sales, Product product, Customer customer) {
        String mechanicApurationName = customer.getMechanicApuration().getName();
        int totalQuantity = 0;

        for (Sale sale : sales) {
            if (sale.getProduct().equals(product) && !mechanicApurationName.equalsIgnoreCase("Somente Mecânicos")) {
                if (sale.getEmployee().getFunctions().stream()
                        .anyMatch(f -> f.getName().equalsIgnoreCase("Consultor de Funilaria"))
                        && (sale.getFunction().equals("Consultor de Funilaria"))) {
                    totalQuantity += sale.getQuantity();
                }
            }
        }
        logger.debug("Quantidade total para o produto '{}' (funilaria): {}", product.getProductName(), totalQuantity);
        return totalQuantity;
    }

    private int calculateMechanicTotalQuantity(List<Sale> sales, Product product, Customer customer) {
        String mechanicApurationName = customer.getMechanicApuration().getName();
        int totalQuantity = 0;

        for (Sale sale : sales) {
            if (sale.getProduct().equals(product) && mechanicApurationName.equalsIgnoreCase("Somente Mecânicos")) {
                if (sale.getEmployee().getFunctions().stream().anyMatch(f -> f.getName().equalsIgnoreCase("Mecânico"))
                        && sale.getFunction().equals("Mecânico")) {
                    totalQuantity += sale.getQuantity();
                }
            }
        }
        logger.debug("Quantidade total para o produto '{}' (Mecânicos): {}", product.getProductName(), totalQuantity);
        return totalQuantity;
    }

    private Map<String, ApurationType> preloadApurationTypes() {
        List<ApurationType> types = apurationTypeRepository.findAll();
        logger.debug("ApuraçãoTypes recuperados do banco: {}", types);
        return types.stream()
                .collect(Collectors.toMap(ApurationType::getName, t -> t));
    }

    private List<Incentive> calculateIncentivesForSale(Sale sale, Map<String, ApurationType> apurationTypes,
            Customer customer, User user) {
        logger.info("------CALCULANDO INCENTIVO POR VENDA------");
        List<Incentive> incentives = new ArrayList<>();
        Employee employee = sale.getEmployee();
        Product product = sale.getProduct();
        int quantity = sale.getQuantity();
        logger.debug("Documento: {}, Cliente: {}, Premiado: {}, Produto: {} - {}, Quantidade: {}",
                sale.getDocumentNumber(), customer.getFantasyName(), employee.getName(),
                product.getProductCode(), product.getProductName(), quantity);

        for (Function function : employee.getFunctions()) {
            if (isRelevantFunction(function)) {
                if ((function.getName().equals("Consultor Técnico") && sale.getFunction().equals("Consultor Técnico"))
                        || (function.getName().equals("Mecânico") && sale.getFunction().equals("Mecânico"))
                        || (function.getName().equals("Consultor de Funilaria")
                                && sale.getFunction().equals("Consultor de Funilaria"))) {

                    Function functionToUse = function;

                    if (function.getName().equals("Consultor de Funilaria")) {
                        functionToUse = functionRepository.findByName("Consultor Técnico").get();
                    }

                    logger.debug("Processando função relevante: {}", function.getName());
                    IncentiveValue value = incentiveValueRepository.findByCustomerAndProductAndFunction(customer,
                            product, functionToUse);
                    logger.debug("Valor do incentivo recuperado: {}", value);

                    if (value != null) {
                        BigDecimal ccValue = value.getCcValue().multiply(BigDecimal.valueOf(quantity));
                        BigDecimal nfsValue = value.getNfsValue().multiply(BigDecimal.valueOf(quantity));
                        logger.debug("Cálculo: ccValue = {}, nfsValue = {}", ccValue, nfsValue);
                        incentives.addAll(createIncentives(ccValue, nfsValue, employee, function, customer,
                                sale.getDocumentNumber(), user, apurationTypes));
                    }
                }
            }
        }
        logger.info("------FIM DO CÁLCULO POR VENDA------");
        return incentives;
    }

    private List<Incentive> calculateIncentivesForRoles(Customer customer, List<Sale> sales,
            Map<String, ApurationType> apurationTypes, User user) {
        logger.info("------CALCULANDO INCENTIVO POR FUNÇÃO------");
        Integer documentNumber = sales.get(0).getDocumentNumber();
        logger.debug("Documento para incentivos por função: {}", documentNumber);

        List<Incentive> incentives = new ArrayList<>();

        List<String> roles = functionRepository.findAll().stream()
                .filter(function -> !function.getName().equalsIgnoreCase("Mecânico")
                        && !function.getName().equalsIgnoreCase("Consultor Técnico")
                        && !function.getName().equalsIgnoreCase("Consultor de Funilaria"))
                .map(Function::getName)
                .collect(Collectors.toList());
        logger.debug("Funções recuperadas para incentivo (excluindo 'Mecânico' e 'Consultor Técnico'): {}", roles);

        List<Employee> relevantEmployees = customer.getEmployees().stream()
                .filter(emp -> emp.getFunctions().stream().anyMatch(func -> roles.contains(func.getName()))
                        && emp.isActive())
                .collect(Collectors.toList());
        logger.debug("Funcionários relevantes para incentivo por função: {}", relevantEmployees);

        logger.info("------CALCULANDO INCENTIVO POR GERÊNCIA------");
        for (Employee emp : relevantEmployees) {
            for (Product product : customer.getGroup().getProducts()) {
                for (Function function : emp.getFunctions()) {
                    if (!function.getName().equalsIgnoreCase("Mecânico")
                            && !function.getName().equalsIgnoreCase("Consultor Técnico")
                            && !function.getName().equalsIgnoreCase("Consultor de Funilaria")) {
                        int totalQuantity = 0;
                        if (!customer.getMechanicApuration().getName().equalsIgnoreCase("Somente Mecânicos")) {
                            totalQuantity = calculateConsultantTotalQuantity(sales, product, customer);
                        } else {
                            totalQuantity = calculateMechanicTotalQuantity(sales, product, customer);
                        }

                        if (!function.getName().equals("Chefe de Oficina")) {
                            totalQuantity += calculateTinkerTotalQuantity(sales, product, customer);
                        }

                        Function funtionToUse = function;

                        if (function.getName().equals("Supervisor de Funilaria")) {
                            funtionToUse = functionRepository.findByName("Chefe de Oficina").get();
                            totalQuantity = calculateTinkerTotalQuantity(sales, product, customer);
                        }

                        int currentFunctionQuantity = (int) customer.getEmployees().stream()
                                .filter(e -> e.getFunctions().stream()
                                        .anyMatch(f -> f.getName().equalsIgnoreCase(function.getName()))
                                        && e.isActive())
                                .count();
                        logger.debug(
                                "Para o funcionário: {}, Função: {}, Produtos vendidos: {} - {}, Quantidade total: {}, Quantidade de pessoas com a função: {}",
                                emp.getName(), function.getName(), product.getProductCode(), product.getProductName(),
                                totalQuantity, currentFunctionQuantity);

                        IncentiveValue value = incentiveValueRepository.findByCustomerAndProductAndFunction(customer,
                                product, funtionToUse);
                        logger.debug("Valor do incentivo para {} - {}: {}", emp.getName(), function.getName(), value);

                        if (value != null) {
                            BigDecimal ccValue = value.getCcValue()
                                    .divide(new BigDecimal(currentFunctionQuantity), 2, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(totalQuantity));
                            BigDecimal nfsValue = value.getNfsValue()
                                    .divide(new BigDecimal(currentFunctionQuantity), 2, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(totalQuantity));
                            logger.debug("Cálculo para {} - {}: cc = {}, nfs = {}", emp.getName(), function.getName(),
                                    ccValue, nfsValue);
                            incentives.addAll(createIncentives(ccValue, nfsValue, emp, function, customer,
                                    documentNumber, user, apurationTypes));
                        }
                    }
                }
            }
        }

        logger.info("------CALCULANDO INCENTIVO POR MECÂNICO------");
        if (customer.getMechanicApuration() != null
                && customer.getMechanicApuration().getName().equalsIgnoreCase("Linear")) {
            List<Employee> mechanics = customer.getEmployees().stream()
                    .filter(emp -> emp.getFunctions().stream()
                            .anyMatch(func -> func.getName().equalsIgnoreCase("Mecânico")))
                    .collect(Collectors.toList());
            logger.debug("Mecânicos encontrados: {}", mechanics);

            for (Employee emp : mechanics) {
                for (Product product : customer.getGroup().getProducts()) {
                    for (Function function : emp.getFunctions()) {
                        int totalQuantity = calculateConsultantTotalQuantity(sales, product, customer)
                                + calculateTinkerTotalQuantity(sales, product, customer);
                        BigDecimal mechanicQuantity = new BigDecimal(mechanics.size());
                        logger.debug("Para Mecânico: {}, Produto: {} - {}, Quantidade: {}, Número de mecânicos: {}",
                                emp.getName(), product.getProductCode(), product.getProductName(), totalQuantity,
                                mechanics.size());

                        IncentiveValue value = incentiveValueRepository.findByCustomerAndProductAndFunction(customer,
                                product, function);
                        logger.debug("Valor do incentivo para Mecânico {}: {}", emp.getName(), value);

                        if (value != null) {
                            BigDecimal ccValue = value.getCcValue()
                                    .divide(mechanicQuantity, 2, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(totalQuantity));
                            BigDecimal nfsValue = value.getNfsValue()
                                    .divide(mechanicQuantity, 2, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(totalQuantity));
                            logger.debug("Cálculo para Mecânico {} - {}: cc = {}, nfs = {}", emp.getName(),
                                    function.getName(), ccValue, nfsValue);
                            incentives.addAll(createIncentives(ccValue, nfsValue, emp, function, customer,
                                    documentNumber, user, apurationTypes));
                        }
                    }
                }
            }
        }
        logger.info("------FIM DO CÁLCULO POR FUNÇÃO------");
        return incentives;
    }

    private boolean isRelevantFunction(Function function) {
        return function.getName().equalsIgnoreCase("Mecânico")
                || function.getName().equalsIgnoreCase("Consultor Técnico")
                || function.getName().equalsIgnoreCase("Consultor de Funilaria");
    }

    private List<Incentive> createIncentives(BigDecimal ccValue, BigDecimal nfsValue, Employee employee,
            Function function,
            Customer customer, Integer saleOrdem, User user, Map<String, ApurationType> apurationTypes) {
        if (saleOrdem == null) {
            saleOrdem = 0;
        }

        List<Incentive> incentives = new ArrayList<>();
        logger.debug("Criando incentivos para funcionário: {}, função: {}", employee.getName(), function.getName());

        if (ccValue.compareTo(BigDecimal.ZERO) > 0) {
            Incentive incCc = new Incentive(null, LocalDate.now().minusMonths(1), user.getState(),
                    employee.getPaymentMethod(),
                    apurationTypes.get("Conta Corrente"), employee, employee.getCpf(), ccValue, function, customer,
                    saleOrdem, IncentiveStatus.PENDING, user);
            incentives.add(incCc);
            logger.debug("Incentivo CC criado: {}", incCc);
        }
        if (nfsValue.compareTo(BigDecimal.ZERO) > 0) {
            Incentive incNfs = new Incentive(null, LocalDate.now().minusMonths(1), user.getState(),
                    employee.getPaymentMethod(),
                    apurationTypes.get("NF Serviço"), employee, employee.getCpf(), nfsValue, function, customer,
                    saleOrdem, IncentiveStatus.PENDING, user);
            incentives.add(incNfs);
            logger.debug("Incentivo NFS criado: {}", incNfs);
        }
        return incentives;
    }

    public List<Incentive> compactIncentives(List<Incentive> incentives, Map<String, ApurationType> apurationTypes) {
        logger.info("Iniciando compactação de {} incentivos.", incentives.size());
        Map<String, Map<String, BigDecimal>> groupedIncentives = incentives.stream()
                .collect(Collectors.groupingBy(
                        incentive -> incentive.getEmployee().getName(),
                        Collectors.groupingBy(
                                incentive -> incentive.getApurationType().getName(),
                                Collectors.mapping(
                                        Incentive::getIncentiveValue,
                                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)))));

        List<Incentive> compactedList = new ArrayList<>();

        for (Map.Entry<String, Map<String, BigDecimal>> employeeEntry : groupedIncentives.entrySet()) {
            String employeeName = employeeEntry.getKey();
            Map<String, BigDecimal> apurationTotals = employeeEntry.getValue();
            logger.debug("Compactando incentivos para funcionário: {} com totais: {}", employeeName, apurationTotals);

            Incentive reference = incentives.stream()
                    .filter(incentive -> employeeName.equals(incentive.getEmployee().getName()))
                    .findFirst()
                    .orElse(null);

            if (reference != null) {
                for (Map.Entry<String, BigDecimal> apurationEntry : apurationTotals.entrySet()) {
                    BigDecimal totalValue = apurationEntry.getValue();
                    String currentApurationKey = apurationEntry.getKey();
                    logger.debug("Para apuração '{}' total compactado: {}", currentApurationKey, totalValue);

                    Incentive compactedIncentive = new Incentive();
                    compactedIncentive.setState(reference.getState());
                    compactedIncentive.setPaymentMethod(reference.getPaymentMethod());
                    compactedIncentive.setEmployeeFunction(reference.getEmployeeFunction());
                    compactedIncentive.setSaleDocumentNumber(reference.getSaleDocumentNumber());
                    compactedIncentive.setUser(reference.getUser());
                    compactedIncentive.setEmployee(reference.getEmployee());
                    compactedIncentive.setCpf(reference.getCpf());
                    compactedIncentive.setReferenceDate(reference.getReferenceDate());
                    compactedIncentive.setCustomer(reference.getCustomer());
                    compactedIncentive.setApurationType(apurationTypes.get(apurationEntry.getKey()));
                    compactedIncentive.setIncentiveValue(totalValue);

                    compactedList.add(compactedIncentive);
                    logger.debug("Incentivo compactado criado: {}", compactedIncentive);
                }
            }
        }

        printCompactIncentives(compactedList);
        return compactedList;
    }

    public void printCompactIncentives(List<Incentive> compactedIncentives) {
        logger.info("Incentivos Compactados: ");
        compactedIncentives.forEach(i -> logger.info("{}", i));
    }

    @Transactional
    public void updateIncentives(List<Sale> allNewSales, Integer documentNumber) {
        validateSales(allNewSales);

        allNewSales = allNewSales.stream().filter(sale -> sale.getQuantity() > 0).toList();

        Customer customer = allNewSales.get(0).getCustomer();
        User user = customer.getUser();

        // Buscar vendas antigas e incentivos para controle de estoque
        List<Sale> oldSales = saleRepository.findByDocumentNumber(documentNumber);
        List<Incentive> oldIncentives = incentiveRepository.findBySaleDocumentNumber(documentNumber);

        // Verificar se os incentivos estavam aprovados para reverter estoque
        boolean wasApproved = oldIncentives.stream()
                .anyMatch(i -> i.getStatus() == IncentiveStatus.APPROVED
                        || i.getStatus() == IncentiveStatus.APPROVED_NEGATIVE);

        // Se estava aprovado, adicionar as quantidades antigas de volta ao estoque
        // Mas apenas das vendas reais (não aplicações de mecânico)
        if (wasApproved && !oldSales.isEmpty()) {
            logger.info("Revertendo estoque das vendas antigas (documento estava aprovado)");
            List<Sale> oldActualSales = filterActualSales(oldSales, customer);
            logger.info("Total de vendas antigas: {}. Vendas que removeram estoque: {}",
                    oldSales.size(), oldActualSales.size());

            for (Sale oldSale : oldActualSales) {
                productStockService.addToStock(customer, oldSale.getProduct(), oldSale.getQuantity());
            }
            logger.info("Estoque revertido com sucesso");
        }

        // Não validar estoque - permitir edição mesmo com estoque insuficiente
        // A validação será feita na próxima aprovação

        List<Incentive> incentives = new ArrayList<>();

        Map<String, ApurationType> apurationTypes = preloadApurationTypes();

        for (Sale sale : allNewSales) {
            List<Incentive> incSale = updateIncentivesForSale(sale, apurationTypes, customer, user);
            incentives.addAll(incSale);
        }

        List<Incentive> roleIncentives = updateIncentivesForRoles(customer, allNewSales, apurationTypes, user);
        incentives.addAll(roleIncentives);

        List<Incentive> incentivesCompacted = compactIncentives(incentives, apurationTypes);

        // Definir status como PENDING para todos os novos incentivos
        incentivesCompacted.forEach(incentive -> incentive.setStatus(IncentiveStatus.PENDING));

        saleRepository.deleteByDocumentNumber(documentNumber);
        incentiveRepository.deleteAllBySaleDocumentNumber(documentNumber);

        saleRepository.saveAll(allNewSales);
        incentiveRepository.saveAll(incentivesCompacted);

        // Recalcular conta corrente
        currentAccountService.updateCurrentAccount(customer);

        logger.info("Incentivos atualizados com sucesso. Status: PENDING");
    }

    /**
     * Filtra as vendas que devem remover do estoque.
     * Regra: Se cliente tem apuração "Somente mecânicos", todas são vendas.
     * Caso contrário, apenas Consultor Técnico e Consultor de Funilaria são vendas
     * (Mecânico é aplicação).
     */
    private List<Sale> filterActualSales(List<Sale> sales, Customer customer) {
        if (sales.isEmpty()) {
            return sales;
        }

        // Se o cliente tem apuração "Somente mecânicos", todas as vendas removem
        // estoque
        if (customer.getMechanicApuration() != null &&
                customer.getMechanicApuration().getName().equalsIgnoreCase("Somente mecânicos")) {
            logger.debug("Cliente com apuração 'Somente mecânicos' - todas as vendas removem estoque");
            return sales;
        }

        // Caso contrário, apenas vendas de Consultor Técnico e Consultor de Funilaria
        // removem estoque
        List<Sale> actualSales = sales.stream()
                .filter(sale -> {
                    String function = sale.getFunction();
                    boolean isActualSale = function != null &&
                            (function.equalsIgnoreCase("Consultor Técnico") ||
                                    function.equalsIgnoreCase("Consultor de Funilaria"));

                    if (!isActualSale) {
                        logger.debug("Venda do produto {} com função '{}' é considerada aplicação - não remove estoque",
                                sale.getProduct().getProductName(), function);
                    }

                    return isActualSale;
                })
                .collect(Collectors.toList());

        return actualSales;
    }

    private void validateStockForNewSales(Customer customer, List<Sale> newSales) {
        if (customer.getProductStock() == null) {
            throw new IllegalStateException("Cliente não possui estoque cadastrado");
        }

        // Agrupar vendas por produto para somar quantidades
        Map<Product, Integer> salesByProduct = newSales.stream()
                .collect(Collectors.groupingBy(
                        Sale::getProduct,
                        Collectors.summingInt(Sale::getQuantity)));

        // Verificar se há estoque suficiente para cada produto
        for (Map.Entry<Product, Integer> entry : salesByProduct.entrySet()) {
            Product product = entry.getKey();
            Integer quantityNeeded = entry.getValue();

            // Buscar quantidade atual no estoque
            Integer currentStock = customer.getProductStock().getProductStockItems().stream()
                    .filter(item -> item.getProduct().equals(product))
                    .mapToInt(item -> item.getQuantity())
                    .sum();

            if (currentStock < quantityNeeded) {
                throw new IllegalStateException(
                        String.format("Estoque insuficiente para o produto '%s'. " +
                                "Estoque atual: %d, Quantidade necessária: %d",
                                product.getProductName(), currentStock, quantityNeeded));
            }
        }
    }

    private List<Incentive> updateIncentivesForSale(Sale sale, Map<String, ApurationType> apurationTypes,
            Customer customer, User user) {
        logger.info("------ATUALIZANDO INCENTIVO POR VENDA------");
        List<Incentive> incentives = new ArrayList<>();
        Employee employee = sale.getEmployee();
        Product product = sale.getProduct();
        int quantity = sale.getQuantity();
        logger.debug("Documento: {}, Cliente: {}, Premiado: {}, Produto: {} - {}, Quantidade: {}",
                sale.getDocumentNumber(), customer.getFantasyName(), employee.getName(),
                product.getProductCode(), product.getProductName(), quantity);

        for (Function function : employee.getFunctions()) {
            if (isRelevantFunction(function)) {
                if ((function.getName().equals("Consultor Técnico") && sale.getFunction().equals("Consultor Técnico"))
                        || (function.getName().equals("Mecânico") && sale.getFunction().equals("Mecânico")
                                || (function.getName().equals("Consultor de Funilaria")
                                        && sale.getFunction().equals("Consultor de Funilaria")))) {

                    Function functionToUse = function;

                    if (function.getName().equals("Consultor de Funilaria")) {
                        functionToUse = functionRepository.findByName("Consultor Técnico").get();
                    }

                    logger.debug("Processando função relevante: {}", function.getName());
                    IncentiveValue value = incentiveValueRepository.findByCustomerAndProductAndFunction(customer,
                            product, functionToUse);
                    logger.debug("Valor do incentivo recuperado: {}", value);

                    if (value != null) {
                        BigDecimal ccValue = value.getCcValue().multiply(BigDecimal.valueOf(quantity));
                        BigDecimal nfsValue = value.getNfsValue().multiply(BigDecimal.valueOf(quantity));
                        logger.debug("Cálculo: ccValue = {}, nfsValue = {}", ccValue, nfsValue);
                        incentives.addAll(updateIncentives(ccValue, nfsValue, employee, function, customer,
                                sale.getDocumentNumber(), user, apurationTypes, sale.getReferenceDate()));
                    }
                }
            }
        }
        logger.info("------FIM DO CÁLCULO POR VENDA------");
        return incentives;
    }

    private List<Incentive> updateIncentivesForRoles(Customer customer, List<Sale> sales,
            Map<String, ApurationType> apurationTypes, User user) {
        logger.info("------CALCULANDO INCENTIVO POR FUNÇÃO------");
        Integer documentNumber = sales.get(0).getDocumentNumber();
        LocalDate referenceDate = sales.get(0).getReferenceDate();
        logger.debug("Documento para incentivos por função: {}", documentNumber);

        List<Incentive> incentives = new ArrayList<>();

        List<String> roles = functionRepository.findAll().stream()
                .filter(function -> !function.getName().equalsIgnoreCase("Mecânico")
                        && !function.getName().equalsIgnoreCase("Consultor Técnico")
                        && !function.getName().equalsIgnoreCase("Consultor de Funilaria"))
                .map(Function::getName)
                .collect(Collectors.toList());
        logger.debug("Funções recuperadas para incentivo (excluindo 'Mecânico' e 'Consultor Técnico'): {}", roles);

        List<Employee> relevantEmployees = customer.getEmployees().stream()
                .filter(emp -> emp.getFunctions().stream().anyMatch(func -> roles.contains(func.getName()))
                        && emp.isActive())
                .collect(Collectors.toList());
        logger.debug("Funcionários relevantes para incentivo por função: {}", relevantEmployees);

        logger.info("------CALCULANDO INCENTIVO POR GERÊNCIA------");
        for (Employee emp : relevantEmployees) {
            for (Product product : customer.getGroup().getProducts()) {
                for (Function function : emp.getFunctions()) {
                    if (!function.getName().equalsIgnoreCase("Mecânico")
                            && !function.getName().equalsIgnoreCase("Consultor Técnico")
                            && !function.getName().equalsIgnoreCase("Consultor de Funilaria")) {
                        int totalQuantity = 0;
                        if (!customer.getMechanicApuration().getName().equalsIgnoreCase("Somente Mecânicos")) {
                            totalQuantity = calculateConsultantTotalQuantity(sales, product, customer);
                        } else {
                            totalQuantity = calculateMechanicTotalQuantity(sales, product, customer);
                        }

                        if (!function.getName().equals("Chefe de Oficina")) {
                            totalQuantity += calculateTinkerTotalQuantity(sales, product, customer);
                        }

                        Function funtionToUse = function;

                        if (function.getName().equals("Supervisor de Funilaria")) {
                            funtionToUse = functionRepository.findByName("Chefe de Oficina").get();
                            totalQuantity = calculateTinkerTotalQuantity(sales, product, customer);
                        }

                        int currentFunctionQuantity = (int) customer.getEmployees().stream()
                                .filter(e -> e.getFunctions().stream()
                                        .anyMatch(f -> f.getName().equalsIgnoreCase(function.getName()))
                                        && e.isActive())
                                .count();
                        logger.debug(
                                "Para o funcionário: {}, Função: {}, Produtos vendidos: {} - {}, Quantidade total: {}, Quantidade de pessoas com a função: {}",
                                emp.getName(), function.getName(), product.getProductCode(), product.getProductName(),
                                totalQuantity, currentFunctionQuantity);

                        IncentiveValue value = incentiveValueRepository.findByCustomerAndProductAndFunction(customer,
                                product, funtionToUse);
                        logger.debug("Valor do incentivo para {} - {}: {}", emp.getName(), function.getName(), value);

                        if (value != null) {
                            BigDecimal ccValue = value.getCcValue()
                                    .divide(new BigDecimal(currentFunctionQuantity), 2, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(totalQuantity));
                            BigDecimal nfsValue = value.getNfsValue()
                                    .divide(new BigDecimal(currentFunctionQuantity), 2, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(totalQuantity));
                            logger.debug("Cálculo para {} - {}: cc = {}, nfs = {}", emp.getName(), function.getName(),
                                    ccValue, nfsValue);
                            incentives.addAll(updateIncentives(ccValue, nfsValue, emp, function, customer,
                                    documentNumber, user, apurationTypes, referenceDate));
                        }
                    }
                }
            }
        }

        logger.info("------CALCULANDO INCENTIVO POR MECÂNICO------");
        if (customer.getMechanicApuration() != null
                && customer.getMechanicApuration().getName().equalsIgnoreCase("Linear")) {
            List<Employee> mechanics = customer.getEmployees().stream()
                    .filter(emp -> emp.getFunctions().stream()
                            .anyMatch(func -> func.getName().equalsIgnoreCase("Mecânico")))
                    .collect(Collectors.toList());
            logger.debug("Mecânicos encontrados: {}", mechanics);

            for (Employee emp : mechanics) {
                for (Product product : customer.getGroup().getProducts()) {
                    for (Function function : emp.getFunctions()) {
                        int totalQuantity = calculateConsultantTotalQuantity(sales, product, customer)
                                + calculateTinkerTotalQuantity(sales, product, customer);
                        BigDecimal mechanicQuantity = new BigDecimal(mechanics.size());
                        logger.debug("Para Mecânico: {}, Produto: {} - {}, Quantidade: {}, Número de mecânicos: {}",
                                emp.getName(), product.getProductCode(), product.getProductName(), totalQuantity,
                                mechanics.size());

                        IncentiveValue value = incentiveValueRepository.findByCustomerAndProductAndFunction(customer,
                                product, function);
                        logger.debug("Valor do incentivo para Mecânico {}: {}", emp.getName(), value);

                        if (value != null) {
                            BigDecimal ccValue = value.getCcValue()
                                    .divide(mechanicQuantity, 2, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(totalQuantity));
                            BigDecimal nfsValue = value.getNfsValue()
                                    .divide(mechanicQuantity, 2, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(totalQuantity));
                            logger.debug("Cálculo para Mecânico {} - {}: cc = {}, nfs = {}", emp.getName(),
                                    function.getName(), ccValue, nfsValue);
                            incentives.addAll(updateIncentives(ccValue, nfsValue, emp, function, customer,
                                    documentNumber, user, apurationTypes, referenceDate));
                        }
                    }
                }
            }
        }
        logger.info("------FIM DO CÁLCULO POR FUNÇÃO------");
        return incentives;
    }

    private List<Incentive> updateIncentives(BigDecimal ccValue, BigDecimal nfsValue, Employee employee,
            Function function,
            Customer customer, Integer saleOrdem, User user, Map<String, ApurationType> apurationTypes,
            LocalDate referenceDate) {
        List<Incentive> incentives = new ArrayList<>();
        logger.debug("Criando incentivos para funcionário: {}, função: {}", employee.getName(), function.getName());

        if (ccValue.compareTo(BigDecimal.ZERO) > 0) {
            Incentive incCc = new Incentive(null, referenceDate, user.getState(), employee.getPaymentMethod(),
                    apurationTypes.get("Conta Corrente"), employee, employee.getCpf(), ccValue, function, customer,
                    saleOrdem, IncentiveStatus.PENDING, user);
            incentives.add(incCc);
            logger.debug("Incentivo CC criado: {}", incCc);
        }
        if (nfsValue.compareTo(BigDecimal.ZERO) > 0) {
            Incentive incNfs = new Incentive(null, referenceDate, user.getState(), employee.getPaymentMethod(),
                    apurationTypes.get("NF Serviço"), employee, employee.getCpf(), nfsValue, function, customer,
                    saleOrdem, IncentiveStatus.PENDING, user);
            incentives.add(incNfs);
            logger.debug("Incentivo NFS criado: {}", incNfs);
        }
        return incentives;
    }
}
