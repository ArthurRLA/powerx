package br.ind.powerx.gestaoOperacional.model.enums;

public enum ReportFileFormat {

	PDF("PDF"),
	XLSX("XLSX");
	
	private String name;

	ReportFileFormat(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}

	public static ReportFileFormat fromString(String value) {
        for (ReportFileFormat format : values()) {
            if (format.name.equalsIgnoreCase(value)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Formato inválido: " + value);
    }
}
