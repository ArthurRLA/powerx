package br.ind.powerx.gestaoOperacional.model.enums;

public enum IncentiveStatus {
    PENDING("Pendente"),
    APPROVED("Aprovado"),
    APPROVED_NEGATIVE("Aprovado-Negativado");
    
    private final String displayName;
    
    IncentiveStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}