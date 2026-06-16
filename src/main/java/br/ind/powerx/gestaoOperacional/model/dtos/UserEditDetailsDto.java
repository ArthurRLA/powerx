package br.ind.powerx.gestaoOperacional.model.dtos;

import java.time.LocalDate;
import java.util.List;

import br.ind.powerx.gestaoOperacional.model.enums.Position;
import br.ind.powerx.gestaoOperacional.model.enums.State;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserEditDetailsDto {

	private String unysoftCode;
	private String name;
	private String cpf;
	private LocalDate birthDate;
	private String address;
	private String rh;
	private String vehicleBrand;
	private String vehicleModel;
	private Integer vehicleYear;
	private String vehicleFuel;
	private String phone;
	private String email;
	private boolean active;
	
	private Position currentPosition;
	private State currentState;
	private String currentRole;
	private List<Long> currentCustomers;
	
	private List<PositionDto> allPositions;
	private List<StateDto> allStates;
	private List<RoleDto> allRoles;
	private List<CustomerDto> allAvailableCustomers;
	
}
