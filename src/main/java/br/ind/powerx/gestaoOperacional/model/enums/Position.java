package br.ind.powerx.gestaoOperacional.model.enums;

public enum Position {

    MANAGER("MANAGER"),
    REGIONAL_MANAGER("REGIONAL_MANAGER"),
    COMMERCIAL_REPRESENTATIVE("COMMERCIAL_REPRESENTATIVE"),
    TI("TI");
    
    private String position;
    
    Position(String position) {
        this.position = position;
    }
    
    public String getPosition() {
        return this.position;
    }
    
    public static Position fromString(String position) {
        for (Position p : Position.values()) {
            if (p.getPosition().equalsIgnoreCase(position)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Nenhum valor conrrespondente de '" + position + "' encontrado");
    }

	public String getName() {
		if(this.equals(MANAGER)) {
			return "Gerente Geral";
		}
		
		if(this.equals(REGIONAL_MANAGER)) {
			return "Gerente Regional";
		}
		
		if(this.equals(COMMERCIAL_REPRESENTATIVE)) {
			return "Representante";
		}
		
		if(this.equals(TI)) {
			return "TI";
		}
		
		return "";
	}
}
