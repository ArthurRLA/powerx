package br.ind.powerx.gestaoOperacional.services;

import java.time.LocalDate;
import java.time.Month;

public class MonthName {

    public static String from(LocalDate referenceDate) {
        if (referenceDate != null) {
            Month month = referenceDate.getMonth();
            int year = referenceDate.getYear();

            switch (month) {
                case JANUARY: return "Janeiro de " + year;
                case FEBRUARY: return "Fevereiro de " + year;
                case MARCH: return "Março de " + year;
                case APRIL: return "Abril de " + year;
                case MAY: return "Maio de " + year;
                case JUNE: return "Junho de " + year;
                case JULY: return "Julho de " + year;
                case AUGUST: return "Agosto de " + year;
                case SEPTEMBER: return "Setembro de " + year;
                case OCTOBER: return "Outubro de " + year;
                case NOVEMBER: return "Novembro de " + year;
                case DECEMBER: return "Dezembro de " + year;

                default: return "";
            }
        }else {
            throw new IllegalArgumentException("Data nula ou inválida");
        }

    }

}
