package br.ind.powerx.gestaoOperacional.controllers;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.services.AuthenticationService;

@Controller
@RequestMapping("/home")
public class HomeController {
	
	@Autowired
    private AuthenticationService authenticationService;

    @GetMapping
    public String home(Model model) {
       User user = authenticationService.getUserAuthenticated();
    		   
       List<Incentive> allIncentives = user.getCustomers().stream()
               .flatMap(customer -> customer.getIncentives().stream())
               .collect(Collectors.toList());

       List<Incentive> last10Incentives = allIncentives.stream()
               .sorted(Comparator.comparing(Incentive::getReferenceDate).reversed())
               .limit(10)
               .collect(Collectors.toList());
       
       if(user != null) {
    	   model.addAttribute("user", user);
    	   model.addAttribute("incentives", last10Incentives);
       }
       
        return "home";
    }
}
