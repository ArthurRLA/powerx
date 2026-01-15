package br.ind.powerx.gestaoOperacional.model.enums;

public enum OperationType {
	
	SALE("SALE"), 
	RETURN("RETURN");
	
	private String name;
	
	OperationType(String name){
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}
