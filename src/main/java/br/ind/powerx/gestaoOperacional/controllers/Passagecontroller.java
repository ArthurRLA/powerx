package br.ind.powerx.gestaoOperacional.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.PassageDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.PassageDetailsDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.PassageSummaryDTO;
import br.ind.powerx.gestaoOperacional.services.AuthenticationService;
import br.ind.powerx.gestaoOperacional.services.CustomerService;
import br.ind.powerx.gestaoOperacional.services.PassageService;

@Controller
@RequestMapping("/passages")
public class PassageController {

    private final PassageService passageService;
    private final CustomerService customerService;
    private final AuthenticationService authenticationService;

    @Autowired
    public PassageController(PassageService passageService, CustomerService customerService,
            AuthenticationService authenticationService) {
        this.passageService = passageService;
        this.customerService = customerService;
        this.authenticationService = authenticationService;
    }

    @GetMapping
    public String index(Model model) {
        User user = authenticationService.getUserAuthenticated();
        boolean isAdmin = user.getRole().equals("ROLE_ADMIN");

        Map<Integer, PassageSummaryDTO> passages;
        if (isAdmin) {
            passages = passageService.getAllPassageSummaries();
        } else {
            passages = passageService.getPassageSummariesByUser(user);
        }

        List<Customer> customers = isAdmin
                ? customerService.findAllByActiveTrueOrderByFantasyNameAsc()
                : user.getCustomers();

        model.addAttribute("passages", passages);
        model.addAttribute("customers", customers);
        model.addAttribute("user", user);
        model.addAttribute("isAdmin", isAdmin);

        return "passages";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createPassage(@RequestBody PassageDTO passageDTO) {
        try {
            passageService.createPassage(passageDTO);
            return ResponseEntity.ok().body("Passagem criada com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar passagem: " + e.getMessage());
        }
    }

    @GetMapping("/{passageNumber}")
    @ResponseBody
    public ResponseEntity<?> getPassageDetails(@PathVariable Integer passageNumber) {
        try {
            PassageDetailsDTO details = passageService.getPassageDetails(passageNumber);
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar detalhes da passagem: " + e.getMessage());
        }
    }

    @PutMapping("/{passageNumber}")
    @ResponseBody
    public ResponseEntity<?> updatePassage(@PathVariable Integer passageNumber, @RequestBody PassageDTO passageDTO) {
        try {
            passageService.updatePassage(passageNumber, passageDTO);
            return ResponseEntity.ok().body("Passagem atualizada com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar passagem: " + e.getMessage());
        }
    }

    @DeleteMapping("/{passageNumber}")
    @ResponseBody
    public ResponseEntity<?> deletePassage(@PathVariable Integer passageNumber) {
        try {
            passageService.deletePassage(passageNumber);
            return ResponseEntity.ok().body("Passagem deletada com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao deletar passagem: " + e.getMessage());
        }
    }

    @GetMapping("/filter")
    @ResponseBody
    public ResponseEntity<?> filterPassages(
            @RequestParam(value = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(value = "customerId", required = false) Long customerId) {
        try {
            Map<Integer, PassageSummaryDTO> passages = passageService.filterPassages(start, end, customerId);
            return ResponseEntity.ok(passages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao filtrar passagens: " + e.getMessage());
        }
    }
}
