package br.ind.powerx.gestaoOperacional.services.report.generator;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.services.report.canvas.PDFReportCanvas;
import br.ind.powerx.gestaoOperacional.services.report.definition.GroupingLevel;
import br.ind.powerx.gestaoOperacional.services.report.definition.ReportDefinition;

@Service
public class PDFReportGenerator {

	private static final float MARGIN_X = 50;
	private static final float MARGIN_Y_TITLE = 820;
	private static final float MARGIN_Y_DATE = 800;
	private static final float MIN_Y_BEFORE_BREAK_PAGE = 120;
	private static final float Y_TO_SUBTRACT_FOR_NEW_LINE = 22;
	private static final float FONT_SIZE_TITLE = 16;
	private static final float FONT_SIZE_DATE = 12;
	private static final float FONT_SIZE_GROUP_HEADER = 12;
	private static final float FONT_SIZE_SUBTOTAL = 10;
	private static final float FONT_SIZE_DETAIL_HEADER = 10;
	private static final float FONT_SIZE_DETAIL_CONTENT = 9;
	private static final float INDENTATION = 10;

	public <T> void generatePDF(List<T> list, ReportDefinition<T> definition, OutputStream outputStream)
			throws IOException {
		try (PDDocument doc = new PDDocument()) {
			PDFReportCanvas canvas = new PDFReportCanvas(doc);

			writeHeader(definition, canvas);
			canvas.changeYForLineBelow(Y_TO_SUBTRACT_FOR_NEW_LINE * 2);

			writeGroupedData(list, definition, definition.getGroupingLevels(), 0, canvas);

			BigDecimal grandTotal = definition.getGroupingLevels().get(0).getSubtotalCalculator().apply(list);
			writeSubtotal(canvas, "Total Geral: ", grandTotal, 14);

			canvas.close();
			doc.save(outputStream);
		}
	}

	private <T> void writeHeader(ReportDefinition<T> definition, PDFReportCanvas canvas) throws IOException {
		canvas.writeOnNewLine(PDType1Font.HELVETICA_BOLD, FONT_SIZE_TITLE, MARGIN_X, MARGIN_Y_TITLE,
				definition.getTitle());
		canvas.writeOnNewLine(PDType1Font.HELVETICA, FONT_SIZE_DATE, MARGIN_X, MARGIN_Y_DATE,
				"Período: " + definition.getDate());
	}

	private <T> void writeGroupedData(List<T> items, ReportDefinition<T> definition,
			List<GroupingLevel<T, ?>> groupingLevels, int levelIndex, PDFReportCanvas canvas) throws IOException {

		if (levelIndex >= groupingLevels.size()) {
			writeDetails(items, definition, canvas);
			return;
		}

		GroupingLevel<T, ?> currentGroup = groupingLevels.get(levelIndex);

		Map<?, List<T>> groupedMap = items.stream().collect(Collectors.groupingBy(currentGroup.getClassifier()));

		for (Map.Entry<?, List<T>> entry : groupedMap.entrySet()) {
			List<T> groupItems = entry.getValue();

			canvas.checkPageBreak(MIN_Y_BEFORE_BREAK_PAGE);

			canvas.drawLine();

			String groupTitle = currentGroup.getTitle() + currentGroup.getId().apply(groupItems.get(0));
			canvas.writeOnNewLine(PDType1Font.HELVETICA_BOLD, FONT_SIZE_GROUP_HEADER, MARGIN_X + (levelIndex * 15),
					canvas.getCurrentY(), groupTitle);
			canvas.changeYForLineBelow(Y_TO_SUBTRACT_FOR_NEW_LINE);

			writeGroupedData(groupItems, definition, groupingLevels, levelIndex + 1, canvas);

			BigDecimal subtotal = currentGroup.getSubtotalCalculator().apply(groupItems);
			writeSubtotal(canvas, "Subtotal " + currentGroup.getTitle(), subtotal, FONT_SIZE_SUBTOTAL);
			canvas.changeYForLineBelow(Y_TO_SUBTRACT_FOR_NEW_LINE * 1.5f);
		}
	}

	private <T> void writeDetails(List<T> items, ReportDefinition<T> definition, PDFReportCanvas canvas) throws IOException {

		List<Float> columnWidths = definition.getColumnWidths();
	    List<String> headers = definition.getDetailmentHeaders();
	    
	    if (columnWidths.size() != headers.size()) {
	        throw new IOException("A quantidade de cabeçalhos não corresponde à quantidade de larguras de coluna na definição do relatório.");
	    }
	    
	    float initialX = MARGIN_X + (definition.getGroupingLevels().size() * INDENTATION);

	    float currentX = initialX;
	    
	    if(headers != null && !headers.isEmpty()) {
	    	for (int i = 0; i < headers.size(); i++) {
		        canvas.writeOnSameLine(PDType1Font.HELVETICA_BOLD, FONT_SIZE_DETAIL_HEADER, currentX, canvas.getCurrentY(), headers.get(i));
		        currentX += columnWidths.get(i); 
		    }
	    }
	    canvas.changeYForLineBelow(Y_TO_SUBTRACT_FOR_NEW_LINE);

	    List<Map<String, Object>> renderedDetails = definition.getDetailsRenderer().renderRules(items);

	    if(renderedDetails != null && !renderedDetails.isEmpty()) {
	    	for (Map<String, Object> detailRow : renderedDetails) {
	    		
		        canvas.checkPageBreak(MIN_Y_BEFORE_BREAK_PAGE);
		        
		        currentX = initialX;
		        for (int i = 0; i < headers.size(); i++) {
		            String headerName = headers.get(i);
		            String cellText = detailRow.get(headerName).toString();
		            
		            canvas.writeOnSameLine(PDType1Font.HELVETICA, FONT_SIZE_DETAIL_CONTENT, currentX, canvas.getCurrentY(), cellText);
		            currentX += columnWidths.get(i);
		        }
		        canvas.changeYForLineBelow(Y_TO_SUBTRACT_FOR_NEW_LINE - 4);
		    }
	    }
	}

	private void writeSubtotal(PDFReportCanvas canvas, String title, BigDecimal value, float fontSize)
			throws IOException {
		String text = title + canvas.formatCurrency(value);
		float textWidth = (PDType1Font.HELVETICA_BOLD.getStringWidth(text) / 1000f) * fontSize;
		float startX = canvas.getPage().getMediaBox().getWidth() - MARGIN_X - textWidth;

		canvas.writeOnNewLine(PDType1Font.HELVETICA_BOLD, fontSize, startX, canvas.getCurrentY(), text);
	}
}
