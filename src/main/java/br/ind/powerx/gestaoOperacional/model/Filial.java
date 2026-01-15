package br.ind.powerx.gestaoOperacional.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "filial")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Filial {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "name", nullable = false, unique = true, length = 50)
	private String name;
	
	@OneToMany(mappedBy = "filial", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<State> states = new ArrayList<>();
	
	public void addState(State state) {
		if(state != null && !states.contains(state)) {
			states.add(state);
			if(state.getFilial() != this) {
				state.setFilial(this);
			}
		}
	}
	
	public void removeState(State state) {
		if(states.remove(state)) {
			if(state.getFilial() == this) {
				state.setFilial(null);
			}
		}
	}
}






