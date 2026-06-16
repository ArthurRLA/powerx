package br.ind.powerx.gestaoOperacional.util;

import java.util.Locale;

public final class DbTextUpper {

	private DbTextUpper() {
	}

	public static String upper(String value) {
		if (value == null || value.isBlank()) {
			return value;
		}
		return value.trim().toUpperCase(Locale.ROOT);
	}
}
