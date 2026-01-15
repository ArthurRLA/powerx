package br.ind.powerx.gestaoOperacional.model.enums;

public enum Fuel {

	GASOLINE("gasolina"),
	ETHANOL("etanol");
	
	private String fuel;
	
	Fuel(String fuel){
		this.fuel = fuel;
	}

	public String getFuel() {
		return fuel;
	}
	
	public Fuel fromString(String fuel) {
		for(Fuel f : Fuel.values()) {
			if(f.getFuel().equalsIgnoreCase(fuel)){
				return f;
			}
		}
		throw new IllegalArgumentException("Nenhum valor conrrespondente de '" + fuel + "' encontrado");
	}
	
}
