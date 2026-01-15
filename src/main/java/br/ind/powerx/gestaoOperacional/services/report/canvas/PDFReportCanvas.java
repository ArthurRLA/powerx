package br.ind.powerx.gestaoOperacional.services.report.canvas;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import lombok.Getter;

@Getter
public class PDFReportCanvas implements Closeable{

	private final PDDocument doc;
	private PDPage page;
	private PDPageContentStream content;
	private float currentY;
	private float currentX;
	
	private static final float DEFAULT_Y_FOR_NEW_PAGE = 780;
	private static final float DEFAULT_X_MARGIN = 50;
	private static final float SPACE = 5;
	
	public PDFReportCanvas(PDDocument doc) throws IOException {
		this.doc = doc;
		this.currentX = DEFAULT_X_MARGIN;
		createNewPage();
	}

	public void createNewPage() throws IOException {
		if (this.content != null) {
			this.content.close();
		}
		this.page = new PDPage(PDRectangle.A4);
		this.doc.addPage(this.page);
		this.content = new PDPageContentStream(this.doc, this.page);
		this.currentY = DEFAULT_Y_FOR_NEW_PAGE;
	}

	private void subtractY(float num) {
		this.currentY -= num;
	}
	
	public void checkPageBreak(float threshold) throws IOException {
		if (this.getCurrentY() < threshold) {
			this.createNewPage();
		}
	}

	public void writeOnNewLine(PDFont family, float size, float x, float y, String text)
			throws IOException {
		this.content.beginText();
		this.content.setFont(family, size);
		this.content.newLineAtOffset(x, y);
		this.content.showText(text);
		this.content.endText();
	}
	
	public void writeOnSameLine(PDFont font, float fontSize, float x, float y, String text) throws IOException {
	    if (content == null) {
	        throw new IOException("O stream de conteúdo (content) não foi inicializado.");
	    }

	    this.content.beginText();
	    this.content.setFont(font, fontSize);
	    this.content.newLineAtOffset(x, y);
	    this. content.showText(text);
	    this.content.endText();
	}

	public void changeYForLineBelow(float num) {
		subtractY(num);
	}
	
	public void drawLine() throws IOException {    
		this.content.setLineWidth(0.5f);
		this.content.moveTo(DEFAULT_X_MARGIN, this.currentY - SPACE);
	    this.content.lineTo(this.page.getMediaBox().getWidth() - DEFAULT_X_MARGIN, this.currentY - SPACE);
	    this.content.stroke();
	}

	public String formatCurrency(BigDecimal value) {
		return value.setScale(2, RoundingMode.HALF_UP).toString().replace('.', ',');
	}

	@Override
	public void close() throws IOException {
		if (this.content != null) {
            this.content.close();
        }
	}
}
