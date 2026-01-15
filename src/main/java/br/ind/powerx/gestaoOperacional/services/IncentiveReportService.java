package br.ind.powerx.gestaoOperacional.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.enums.State;
import br.ind.powerx.gestaoOperacional.repositories.IncentiveRepository;
import br.ind.powerx.gestaoOperacional.repositories.specifications.IncentiveSpecifications;

@Service
public class IncentiveReportService {

	private final IncentiveRepository incentiveRepository;

	public IncentiveReportService(IncentiveRepository incentiveRepository) {
		this.incentiveRepository = incentiveRepository;
	}

	public byte[] generateIncentiveReport(LocalDate startDate, LocalDate endDate, State state, Long userId,
			Long customerId, Long groupId, boolean detalhar) throws IOException {

		Specification<Incentive> spec = Specification.where(null);

		spec = spec.and(IncentiveSpecifications.byReferenceDateBetween(startDate, endDate));
		spec = spec.and(IncentiveSpecifications.hasState(state));
		spec = spec.and(IncentiveSpecifications.hasUser(userId));
		spec = spec.and(IncentiveSpecifications.hasCustomer(customerId));
		spec = spec.and(IncentiveSpecifications.hasGroup(groupId));
		spec = spec.and(IncentiveSpecifications.hasCurrentAccount());

		List<Incentive> incentives = incentiveRepository.findAll(spec);

		Map<String, Map<String, Map<String, List<Incentive>>>> grouped = incentives.stream()
				.collect(Collectors.groupingBy(inc -> inc.getUser().getName(),
						Collectors.groupingBy(inc -> inc.getCustomer().getGroup().getName(),
								Collectors.groupingBy(inc -> inc.getCustomer().getFantasyName()))));

		BigDecimal grandTotal = BigDecimal.ZERO;

		try (PDDocument doc = new PDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			PDPage page = new PDPage(PDRectangle.A4);
			doc.addPage(page);
			PDPageContentStream content = new PDPageContentStream(doc, page);

			content.setFont(PDType1Font.HELVETICA_BOLD, 16);
			content.beginText();
			content.newLineAtOffset(50, 820);
			content.showText("Relatório de Incentivos");
			content.endText();
			content.setFont(PDType1Font.HELVETICA, 12);
			content.beginText();
			content.newLineAtOffset(50, 800);
			content.showText(String.format("Período: %s a %s", startDate, endDate));
			content.endText();

			float margin = 50;
			float y = 780;

			for (String userName : grouped.keySet()) {

				if (y < 120) {
					content.close();
					page = new PDPage(PDRectangle.A4);
					doc.addPage(page);
					content = new PDPageContentStream(doc, page);
					y = 780;
				}

				content.setFont(PDType1Font.HELVETICA_BOLD, 12);
				content.beginText();
				content.newLineAtOffset(margin, y);
				content.showText("RCA: " + userName);
				content.endText();
				y -= 22;

				Map<String, Map<String, List<Incentive>>> byGroup = grouped.get(userName);
				BigDecimal totalPerUser = BigDecimal.ZERO;

				for (String groupName : byGroup.keySet()) {
					if (y < 150) {
						content.close();
						page = new PDPage(PDRectangle.A4);
						doc.addPage(page);
						content = new PDPageContentStream(doc, page);
						y = 780;
					}

					content.setLineWidth(0.5f);
					content.moveTo(margin + 10, y);
					content.lineTo(page.getMediaBox().getWidth() - margin, y);
					content.stroke();
					y -= 12;

					content.setFont(PDType1Font.HELVETICA_BOLD, 11);
					content.beginText();
					content.newLineAtOffset(margin + 20, y);
					content.showText("Grupo: " + groupName);
					content.endText();
					y -= 18;

					Map<String, List<Incentive>> byCustomer = byGroup.get(groupName);
					BigDecimal totalPerGroup = BigDecimal.ZERO;

					if (!detalhar) {
						content.setFont(PDType1Font.HELVETICA_BOLD, 10);
						float[] colX = { margin + 40, margin + 100, margin + 260, margin + 420 };
						String[] hdr = { "ID", "CNPJ", "Cliente", "Valor" };
						for (int i = 0; i < hdr.length; i++) {
							content.beginText();
							content.newLineAtOffset(colX[i], y);
							content.showText(hdr[i]);
							content.endText();
						}
						y -= 14;
						content.setFont(PDType1Font.HELVETICA, 9);
					}

					for (String custName : byCustomer.keySet()) {
						if (y < 120) {
							content.close();
							page = new PDPage(PDRectangle.A4);
							doc.addPage(page);
							content = new PDPageContentStream(doc, page);
							y = 780;
						}
						List<Incentive> incs = byCustomer.get(custName);
						BigDecimal totalPerCustomer = incs.stream().map(Incentive::getIncentiveValue)
								.reduce(BigDecimal.ZERO, BigDecimal::add);
						totalPerGroup = totalPerGroup.add(totalPerCustomer);

						if (detalhar) {

							content.setFont(PDType1Font.HELVETICA_BOLD, 10);
							content.beginText();
							content.newLineAtOffset(margin + 40, y);
							content.showText("Cliente: " + custName);
							content.endText();
							y -= 16;
							content.setFont(PDType1Font.HELVETICA_BOLD, 9);
							content.beginText();
							content.newLineAtOffset(margin + 60, y);
							content.showText("CPF        Nome           Valor       Função");
							content.endText();
							y -= 12;
							content.setFont(PDType1Font.HELVETICA, 9);
							for (Incentive inc : incs) {
								if (y < 120) {
									content.close();
									page = new PDPage(PDRectangle.A4);
									doc.addPage(page);
									content = new PDPageContentStream(doc, page);
									y = 780;
								}
								content.beginText();
								content.newLineAtOffset(margin + 60, y);
								content.showText(String.format("%s  %s  %s  %s", inc.getCpf(),
										inc.getEmployee().getName(), formatCurrency(inc.getIncentiveValue()),
										inc.getEmployeeFunction().getName()));
								content.endText();
								y -= 10;
							}

							y -= 8;
							String textCust = "Subtotal Cliente: " + formatCurrency(totalPerCustomer);
							float fsCust = 9;
							float twCust = (PDType1Font.HELVETICA_BOLD.getStringWidth(textCust) / 1000f) * fsCust;
							float startXCust = page.getMediaBox().getWidth() - margin - twCust;
							content.setFont(PDType1Font.HELVETICA_BOLD, fsCust);
							content.beginText();
							content.newLineAtOffset(startXCust, y);
							content.showText(textCust);
							content.endText();
							y -= 16;
						} else {

							float[] colX = { margin + 40, margin + 100, margin + 260, margin + 420 };
							content.setFont(PDType1Font.HELVETICA, 9);
							content.beginText();
							content.newLineAtOffset(colX[0], y);
							content.showText(incs.get(0).getCustomer().getId().toString());
							content.endText();
							content.beginText();
							content.newLineAtOffset(colX[1], y);
							content.showText(incs.get(0).getCustomer().getCnpj());
							content.endText();
							content.beginText();
							content.newLineAtOffset(colX[2], y);
							content.showText(custName);
							content.endText();
							content.beginText();
							content.newLineAtOffset(colX[3], y);
							content.showText(formatCurrency(totalPerCustomer));
							content.endText();
							y -= 12;
						}
					}

					y -= 8;
					String textGrp = "Subtotal Grupo: " + formatCurrency(totalPerGroup);
					float fsGrp = 10;
					float twGrp = (PDType1Font.HELVETICA_BOLD.getStringWidth(textGrp) / 1000f) * fsGrp;
					float startXGrp = page.getMediaBox().getWidth() - margin - twGrp;
					content.setFont(PDType1Font.HELVETICA_BOLD, fsGrp);
					content.beginText();
					content.newLineAtOffset(startXGrp, y);
					content.showText(textGrp);
					content.endText();
					y -= 22;

					totalPerUser = totalPerUser.add(totalPerGroup);
				}

				y -= 8;
				String textUser = "Subtotal RCA: " + formatCurrency(totalPerUser);
				float fsUser = 11;
				float twUser = (PDType1Font.HELVETICA_BOLD.getStringWidth(textUser) / 1000f) * fsUser;
				float startXUser = page.getMediaBox().getWidth() - margin - twUser;
				content.setFont(PDType1Font.HELVETICA_BOLD, fsUser);
				content.beginText();
				content.newLineAtOffset(startXUser, y);
				content.showText(textUser);
				content.endText();
				y -= 26;

				grandTotal = grandTotal.add(totalPerUser);
			}

			if (y < 120) {
				content.close();
				page = new PDPage(PDRectangle.A4);
				doc.addPage(page);
				content = new PDPageContentStream(doc, page);
				y = 780;
			}

			String textAll = "Total Geral: " + formatCurrency(grandTotal);
			float fsAll = 14;
			float twAll = (PDType1Font.HELVETICA_BOLD.getStringWidth(textAll) / 1000f) * fsAll;
			float startXAll = page.getMediaBox().getWidth() - margin - twAll;
			content.setFont(PDType1Font.HELVETICA_BOLD, fsAll);
			content.beginText();
			content.newLineAtOffset(startXAll, y);
			content.showText(textAll);
			content.endText();

			content.close();
			doc.save(baos);
			return baos.toByteArray();
		}
	}

	private String formatCurrency(BigDecimal value) {
		return "R$ " + value.setScale(2, RoundingMode.HALF_UP).toString().replace('.', ',');
	}
}
