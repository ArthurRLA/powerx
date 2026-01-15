package br.ind.powerx.gestaoOperacional.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.CurrentAccount;
import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.ProductStock;
import br.ind.powerx.gestaoOperacional.model.Revenue;
import br.ind.powerx.gestaoOperacional.repositories.CurrentAccountRepository;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import jakarta.transaction.Transactional;

@Service
public class CurrentAccountService {

	private final CurrentAccountRepository currentAccountRepository;
	private final CustomerRepository customerRepository;

	@Autowired
	public CurrentAccountService(CurrentAccountRepository currentAccountRepository,
			CustomerRepository customerRepository) {
		this.currentAccountRepository = currentAccountRepository;
		this.customerRepository = customerRepository;
	}

	@Transactional
	public void updateCurrentAccount(List<Revenue> revenues) {
		for (Revenue revenue : revenues) {
			Customer customer = revenue.getCustomer();

			ProductStock stock = customer.getProductStock();

			if (stock == null) {
				stock = new ProductStock();
				stock.setCustomer(customer);
			}

			stock.setCustomer(customer);

			BigDecimal totalBalance = stock.getTotalBalance();

			CurrentAccount currentAccount = customer.getCurrentAccount();

			if (currentAccount == null) {
				currentAccount = new CurrentAccount();
				customer.setCurrentAccount(currentAccount);
				currentAccount.setCustomer(customer);
			}

			currentAccount.setBalance(totalBalance);

			currentAccountRepository.save(currentAccount);

			customerRepository.save(customer);
		}
	}

	public void updateCurrentAccount(Customer customer) {
		ProductStock stock = customer.getProductStock();

		if (stock == null) {
			stock = new ProductStock();
			stock.setCustomer(customer);
		}

		stock.setCustomer(customer);

		BigDecimal totalBalance = stock.getTotalBalance();

		CurrentAccount currentAccount = customer.getCurrentAccount();

		if (currentAccount == null) {
			currentAccount = new CurrentAccount();
			customer.setCurrentAccount(currentAccount);
			currentAccount.setCustomer(customer);
		}

		currentAccount.setBalance(totalBalance);

		currentAccountRepository.save(currentAccount);

		customerRepository.save(customer);

	}

}
