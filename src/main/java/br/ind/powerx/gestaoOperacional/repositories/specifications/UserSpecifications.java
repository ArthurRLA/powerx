package br.ind.powerx.gestaoOperacional.repositories.specifications;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.enums.Position;
import br.ind.powerx.gestaoOperacional.model.enums.State;

public class UserSpecifications {

	public static Specification<User> positionsIn(List<Position> positions) {
        return (root, query, criteriaBuilder) -> 
            root.get("position").in(positions);
    }

    public static Specification<User> statesIn(List<State> states) {
        return (root, query, criteriaBuilder) -> 
            root.get("state").in(states);
    }

    public static Specification<User> isActive(boolean active){
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("active"), active);
    } 
}
