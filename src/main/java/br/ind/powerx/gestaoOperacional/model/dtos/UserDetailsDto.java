package br.ind.powerx.gestaoOperacional.model.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.ind.powerx.gestaoOperacional.model.enums.State;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDetailsDto {

	private Long id;
	private String unysoftCode;
	private String name;
	private LocalDate birthDate;
	private String cpf;
	private String address;
	private String rh;
	private String vehicleBrand;
	private String vehicleModel;
	private Integer vehicleYear;
	private String vehicleFuel;
	private String email;
	private String position;
	private State state;
	private String phone;
	private LocalDateTime creationDate;
	private LocalDate startOfActivities;
	private LocalDateTime lastUpdate;
	private String active;
	private String role;
}
