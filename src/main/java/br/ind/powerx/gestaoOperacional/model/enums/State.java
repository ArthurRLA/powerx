package br.ind.powerx.gestaoOperacional.model.enums;

public enum State {

	SP1("SP1"),
	SP2("SP2"),
	SP3("SP3"),
	MG("MG"),
	RJ("RJ"),
	PB("PB"),
	PE("PE"),
	GO("GO"),
	PA("PA");
	
	private String state;
	
	State(String state){
		this.state = state;
	}

	public String getState() {
		return this.state;
	}

	public static State fromString(String state) {
		for(State s : State.values()) {
			if(s.getState().equalsIgnoreCase(state)) {
				return s;
			}
		}
		throw new IllegalArgumentException("Nenhum valor conrrespondente de '" + state + "' encontrado");
	}
	
	
}
