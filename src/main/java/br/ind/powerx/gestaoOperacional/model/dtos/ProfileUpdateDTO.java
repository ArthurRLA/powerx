package br.ind.powerx.gestaoOperacional.model.dtos;

public record ProfileUpdateDTO(
		String cpf,
		String rh,
		String vehicleBrand,
		String vehicleModel,
		Integer vehicleYear,
		String vehicleFuel
) {}
