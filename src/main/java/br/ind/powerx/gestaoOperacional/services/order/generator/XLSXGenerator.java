package br.ind.powerx.gestaoOperacional.services.order.generator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.services.order.definition.XLSXColumn;
import br.ind.powerx.gestaoOperacional.services.order.definition.XLSXReportDefinition;

@Service
public class XLSXGenerator {

	public <T> void generate(List<T> list, XLSXReportDefinition<T> definition, OutputStream outputStream) throws IOException{
		
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		
		Row header = sheet.createRow(0);
		
		List<XLSXColumn<T>> columns = definition.getColumnDefinitions();
		
		for (int i = 0; i < columns.size(); i++) {
			Cell cell = header.createCell(i);
			cell.setCellValue(columns.get(i).getHeader());
		}
		
		int rowIndex = 1;
		
		for(T item : list) {
			Row row = sheet.createRow(rowIndex);
			for(int i = 0; i < columns.size(); i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(columns.get(i).getValueExtractor().apply(item).toString());
			}
			
			rowIndex++;
		}
		
		for (int i = 0; i < columns.size(); i++) {
            sheet.autoSizeColumn(i);
        }
		
		workbook.write(outputStream);
		workbook.close();
		
	}

}







































