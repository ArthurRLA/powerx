package br.ind.powerx.gestaoOperacional.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Group;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.repositories.GroupRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PartnerGroupService {

	@Autowired 
	private GroupRepository groupRepository;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private ProductService productService;
	
	public List<Group> findAll() {
		return groupRepository.findAll();
	}

	public void save(Group groupToSave) {
		Group group = new Group();
		group.setName(groupToSave.getName());
		
		for(Customer c : groupToSave.getCustomers()) {
			group.addCustomer(c);
		}
		
		for(Product p : groupToSave.getProducts()) {
			group.addProduct(p);
		}
		
		groupRepository.save(group);
		
	}
	
	public void update(Group groupToUpdate) { 
	    Group existingGroup = groupRepository.findById(groupToUpdate.getId())
	            .orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));

	    existingGroup.setName(groupToUpdate.getName());

	    updateCustomers(existingGroup, groupToUpdate.getCustomers());

	    updateProducts(existingGroup, groupToUpdate.getProducts());

	    groupRepository.save(existingGroup);
	}

	private void updateCustomers(Group existingGroup, List<Customer> updatedCustomers) {
	    List<Customer> currentCustomers = existingGroup.getCustomers();
	    for (Customer customer : currentCustomers) {
	        customer.setGroup(null);
	        customerService.save(customer);
	    }
	    currentCustomers.clear();

	    if (updatedCustomers != null) {
	        for (Customer customer : updatedCustomers) {
	            existingGroup.addCustomer(customer);
	            customerService.save(customer);
	        }
	    }
	}


	private void updateProducts(Group existingGroup, List<Product> updatedProducts) {
	    List<Product> currentProducts = existingGroup.getProducts();
	    for (Product product : currentProducts) {
	        product.removeGroup(existingGroup);
	        productService.save(product);
	    }
	    currentProducts.clear();

	    for (Product product : updatedProducts) {
	        existingGroup.addProduct(product);
	        productService.save(product);
	    }
	}


	public Page<Group> findAll(Pageable pageable) {
		return groupRepository.findAll(pageable);
	}

	public List<Group> findAllOrderByNameAsc() {
		return groupRepository.findAllOrderByNameAsc();
	}
	


}



















