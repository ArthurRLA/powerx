package br.ind.powerx.gestaoOperacional.repositories.specifications;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import br.ind.powerx.gestaoOperacional.model.Customer;

public class CustomerSpecifications {

    public static Specification<Customer> userIdIn(List<Long> userIds) {
        return (root, query, criteriaBuilder) -> 
            root.get("user").get("id").in(userIds);
    }

    public static Specification<Customer> groupIdIn(List<Long> groupIds) {
        return (root, query, criteriaBuilder) -> 
            root.get("group").get("id").in(groupIds);
    }

    public static Specification<Customer> industryIdIn(List<Long> industryIds) {
        return (root, query, criteriaBuilder) -> 
            root.get("industry").get("id").in(industryIds);
    }

    public static Specification<Customer> flagIdIn(List<Long> flagIds) {
        return (root, query, criteriaBuilder) -> 
            root.get("flag").get("id").in(flagIds);
    }

    public static Specification<Customer> isActive(boolean active){
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("active"), active);
    }
}
