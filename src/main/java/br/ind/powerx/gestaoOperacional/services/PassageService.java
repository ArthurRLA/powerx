package br.ind.powerx.gestaoOperacional.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Employee;
import br.ind.powerx.gestaoOperacional.model.Passage;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.PassageDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.PassageDetailsDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.PassageEmployeeDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.PassageItemDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.PassageSummaryDTO;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.repositories.EmployeeRepository;
import br.ind.powerx.gestaoOperacional.repositories.PassageRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PassageService {

    private static final Logger logger = LoggerFactory.getLogger(PassageService.class);

    private final PassageRepository passageRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final AuthenticationService authenticationService;

    @Autowired
    public PassageService(PassageRepository passageRepository, CustomerRepository customerRepository,
            EmployeeRepository employeeRepository, AuthenticationService authenticationService) {
        this.passageRepository = passageRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
        this.authenticationService = authenticationService;
    }

    @Transactional
    public void createPassage(PassageDTO passageDTO) {
        logger.info("Criando nova passagem");

        Customer customer = customerRepository.findById(passageDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        User user = authenticationService.getUserAuthenticated();

        Integer maxPassageNumber = passageRepository.findMaxPassageNumber();
        Integer newPassageNumber = (maxPassageNumber != null ? maxPassageNumber : 0) + 1;

        List<Passage> passages = new ArrayList<>();

        for (PassageItemDTO item : passageDTO.getItems()) {
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                continue;
            }

            Employee employee = employeeRepository.findById(item.getEmployeeId())
                    .orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado"));

            Passage passage = new Passage();
            passage.setReferenceDate(passageDTO.getReferenceDate());
            passage.setCustomer(customer);
            passage.setEmployee(employee);
            passage.setQuantity(item.getQuantity());
            passage.setUser(user);
            passage.setPassageNumber(newPassageNumber);

            passages.add(passage);
        }

        if (passages.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma passagem válida para salvar");
        }

        passageRepository.saveAll(passages);
        logger.info("Passagem {} criada com sucesso", newPassageNumber);
    }

    public Map<Integer, PassageSummaryDTO> getAllPassageSummaries() {
        List<Integer> passageNumbers = passageRepository.findDistinctPassageNumbers();
        return getPassageSummariesByNumbers(passageNumbers);
    }

    public Map<Integer, PassageSummaryDTO> getPassageSummariesByUser(User user) {
        List<Integer> passageNumbers = passageRepository.findDistinctPassageNumbersByUser(user);
        return getPassageSummariesByNumbers(passageNumbers);
    }

    private Map<Integer, PassageSummaryDTO> getPassageSummariesByNumbers(List<Integer> passageNumbers) {
        Map<Integer, PassageSummaryDTO> summaries = new LinkedHashMap<>();

        for (Integer passageNumber : passageNumbers) {
            List<Passage> passages = passageRepository.findByPassageNumber(passageNumber);

            if (!passages.isEmpty()) {
                Passage first = passages.get(0);
                Integer totalQuantity = passages.stream()
                        .mapToInt(Passage::getQuantity)
                        .sum();

                PassageSummaryDTO summary = new PassageSummaryDTO(
                        passageNumber,
                        first.getReferenceDate(),
                        first.getCustomer().getFantasyName(),
                        totalQuantity);

                summaries.put(passageNumber, summary);
            }
        }

        return summaries;
    }

    public PassageDetailsDTO getPassageDetails(Integer passageNumber) {
        List<Passage> passages = passageRepository.findByPassageNumber(passageNumber);

        if (passages.isEmpty()) {
            throw new EntityNotFoundException("Passagem não encontrada");
        }

        Passage first = passages.get(0);
        Integer totalQuantity = passages.stream()
                .mapToInt(Passage::getQuantity)
                .sum();

        List<PassageEmployeeDTO> employees = passages.stream()
                .map(p -> new PassageEmployeeDTO(
                        p.getEmployee().getId(),
                        p.getEmployee().getName(),
                        p.getEmployee().getCpf(),
                        p.getQuantity()))
                .collect(Collectors.toList());

        return new PassageDetailsDTO(
                passageNumber,
                first.getReferenceDate(),
                first.getCustomer().getFantasyName(),
                first.getCustomer().getId(),
                totalQuantity,
                employees);
    }

    @Transactional
    public void updatePassage(Integer passageNumber, PassageDTO passageDTO) {
        logger.info("Atualizando passagem {}", passageNumber);

        List<Passage> oldPassages = passageRepository.findByPassageNumber(passageNumber);

        if (oldPassages.isEmpty()) {
            throw new EntityNotFoundException("Passagem não encontrada");
        }

        Customer customer = customerRepository.findById(passageDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        User user = authenticationService.getUserAuthenticated();

        // Deletar passagens antigas
        passageRepository.deleteAllByPassageNumber(passageNumber);

        // Criar novas passagens
        List<Passage> newPassages = new ArrayList<>();

        for (PassageItemDTO item : passageDTO.getItems()) {
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                continue;
            }

            Employee employee = employeeRepository.findById(item.getEmployeeId())
                    .orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado"));

            Passage passage = new Passage();
            passage.setReferenceDate(passageDTO.getReferenceDate());
            passage.setCustomer(customer);
            passage.setEmployee(employee);
            passage.setQuantity(item.getQuantity());
            passage.setUser(user);
            passage.setPassageNumber(passageNumber);

            newPassages.add(passage);
        }

        if (newPassages.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma passagem válida para salvar");
        }

        passageRepository.saveAll(newPassages);
        logger.info("Passagem {} atualizada com sucesso", passageNumber);
    }

    @Transactional
    public void deletePassage(Integer passageNumber) {
        logger.info("Deletando passagem {}", passageNumber);

        List<Passage> passages = passageRepository.findByPassageNumber(passageNumber);

        if (passages.isEmpty()) {
            throw new EntityNotFoundException("Passagem não encontrada");
        }

        passageRepository.deleteAllByPassageNumber(passageNumber);
        logger.info("Passagem {} deletada com sucesso", passageNumber);
    }

    public Map<Integer, PassageSummaryDTO> filterPassages(LocalDate start, LocalDate end, Long customerId) {
        List<Integer> allPassageNumbers;

        if (start != null && end != null && customerId != null) {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

            List<Passage> filteredPassages = passageRepository.findByReferenceDateBetweenAndCustomer(start, end,
                    customer);

            allPassageNumbers = filteredPassages.stream()
                    .map(Passage::getPassageNumber)
                    .distinct()
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());
        } else if (start != null && end != null) {
            List<Passage> allPassages = passageRepository.findAll();
            allPassageNumbers = allPassages.stream()
                    .filter(p -> !p.getReferenceDate().isBefore(start) && !p.getReferenceDate().isAfter(end))
                    .map(Passage::getPassageNumber)
                    .distinct()
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());
        } else if (customerId != null) {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

            List<Passage> filteredPassages = passageRepository.findAll().stream()
                    .filter(p -> p.getCustomer().equals(customer))
                    .collect(Collectors.toList());

            allPassageNumbers = filteredPassages.stream()
                    .map(Passage::getPassageNumber)
                    .distinct()
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());
        } else {
            allPassageNumbers = passageRepository.findDistinctPassageNumbers();
        }

        return getPassageSummariesByNumbers(allPassageNumbers);
    }
}
