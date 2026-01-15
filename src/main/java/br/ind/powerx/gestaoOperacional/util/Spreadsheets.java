package br.ind.powerx.gestaoOperacional.util;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class Spreadsheets {

	public static String getStringCellValue(Cell cell) {
		if (cell == null)
			return null;

		DataFormatter formatter = new DataFormatter();
		String value = formatter.formatCellValue(cell).trim();
		return value.isEmpty() ? null : value;
	}

	public static Double getDoubleCellValue(Cell cell) {
		if (cell == null) {
			return null;
		}

		try {
			switch (cell.getCellType()) {
				case NUMERIC:
					return cell.getNumericCellValue();

				case STRING:
					return parseDouble(cell.getStringCellValue());

				case FORMULA:
					switch (cell.getCachedFormulaResultType()) {
						case NUMERIC:
							return cell.getNumericCellValue();
						case STRING:
							return parseDouble(cell.getStringCellValue());
						default:
							return null;
					}

				default:
					return null;
			}

		} catch (Exception e) {
			System.err.println("Erro ao tentar ler valor numérico da célula: " + e.getMessage());
			return null;
		}
	}

	private static Double parseDouble(String value) {
		if (value == null) return null;
		value = value.trim().replace(",", ".");
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	
	public static Integer getIntegerCellValue(Cell cell) {
		if (cell == null) {
			return null;
		}

		DataFormatter formatter = new DataFormatter();

		String cellValue = formatter.formatCellValue(cell);

		if (cellValue == null || cellValue.trim().isEmpty()) {
			return null;
		}

		try {
			return Integer.parseInt(cellValue);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static Long parseLongFromCell(Cell cell) {
		if (cell == null) {
			return null;
		}
		if (cell.getCellType() == CellType.NUMERIC) {
			return (long) cell.getNumericCellValue();
		} else if (cell.getCellType() == CellType.STRING) {
			try {
				return Long.valueOf(cell.getStringCellValue());
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}

	public static int getLastRowWithData(Sheet sheet) {
		int lastRowNum = sheet.getLastRowNum();
		System.out.println("lastrownum - " + lastRowNum);
		for (int i = lastRowNum; i >= 0; i--) {
			Row row = sheet.getRow(i);
			if (row != null && !isRowEmpty(row)) {
				return i;
			}
		}
		return -1;
	}

	public static boolean isRowEmpty(Row row) {
		if (row == null) {
			return true;
		}

		Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			if (!isCellEmpty(cell)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isCellEmpty(Cell cell) {
		if (cell == null || cell.getCellType() == CellType.BLANK) {
			return true;
		}

		if (cell.getCellType() == CellType.STRING) {
			String value = cell.getStringCellValue().trim();
			return value.isEmpty();
		}
		
		return false;
	}
}
