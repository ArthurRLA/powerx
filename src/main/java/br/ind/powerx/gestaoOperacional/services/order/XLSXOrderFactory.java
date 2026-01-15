package br.ind.powerx.gestaoOperacional.services.order;

import java.time.LocalDate;
import java.util.function.Function;
import org.springframework.stereotype.Component;

import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.Sale;
import br.ind.powerx.gestaoOperacional.model.dtos.RevenueItemReportDTO;
import br.ind.powerx.gestaoOperacional.services.order.definition.XLSXReportDefinition;

@Component
public class XLSXOrderFactory {

	private Function<Incentive, String> nameExtractor = i -> i.getEmployee().getName();
	private Function<Incentive, String> cpfExtractor = Incentive::getCpf;
	private Function<Incentive, String> totalExtractor = i -> i.getIncentiveValue().toString().replace(",", ".");
	private Function<Incentive, String> emailExtractor = i -> i.getEmployee().getEmail();
	private Function<Incentive, LocalDate> monthExtractor = i -> i.getReferenceDate();
	private Function<Incentive, String> apTypeExtractor = i -> i.getApurationType().getName();
	private Function<Incentive, String> paymentMethodExtractor = i -> i.getPaymentMethod().getName();
	private Function<Incentive, String> functionExtractor = i -> i.getEmployeeFunction().getName();
	private Function<Incentive, String> cnpjExtractor = i -> i.getCustomer().getCnpj();

	private Function<Sale, LocalDate> saleMonthExtractor = s -> s.getReferenceDate();
	private Function<Sale, Integer> docNumExtractor = s -> s.getDocumentNumber();
	private Function<Sale, String> saleCustomerExtractor = s -> s.getCustomer().getFantasyName();
	private Function<Sale, String> saleCnpjExtractor = s -> s.getCustomer().getCnpj();
	private Function<Sale, String> empNameExtractor = s -> s.getEmployee().getName();
	private Function<Sale, String> empCpfExtractor = s -> s.getEmployee().getCpf();
	private Function<Sale, String> productCodeExtractor = s -> s.getProduct().getProductCode();
	private Function<Sale, String> productNameExtractor = s -> s.getProduct().getProductName();
	private Function<Sale, Integer> quantityExtractor = s -> s.getQuantity();
	private Function<Sale, String> saleFunctionExtractor = s -> s.getFunction();

	private Function<Incentive, String> customerExtractor = i -> {
		String nome = i.getCustomer() != null ? i.getCustomer().getFantasyName() : null;
		return (nome != null && nome.length() > 30) ? nome.substring(0, 30) : nome;
	};

	public XLSXReportDefinition<Incentive> swileOrderDefinition() {
		return new XLSXReportDefinition.Builder<Incentive>()
				.addColumn("Nome", nameExtractor)
				.addColumn("CPF", cpfExtractor)
				.addColumn("Total", totalExtractor)
				.build();
	}

	public XLSXReportDefinition<Incentive> youCardOrderDefinition() {
		return new XLSXReportDefinition.Builder<Incentive>()
				.addColumn("Documento", cpfExtractor)
				.addColumn("Nome", nameExtractor)
				.addColumn("Valor", totalExtractor)
				.addColumn("Observacao", customerExtractor)
				.build();
	}

	public XLSXReportDefinition<Incentive> picPontosOrderDefinition() {
		return new XLSXReportDefinition.Builder<Incentive>()
				.addColumn("Documento", cpfExtractor)
				.addColumn("Nome", nameExtractor)
				.addColumn("Valor", totalExtractor)
				.addColumn("Email", emailExtractor)
				.addColumn("Observacao", customerExtractor)
				.build();
	}

	public XLSXReportDefinition<Incentive> incentiveDefinition() {
		return new XLSXReportDefinition.Builder<Incentive>()
				.addColumn("Mês", monthExtractor)
				.addColumn("Apuração", apTypeExtractor)
				.addColumn("Método de Pagamento", paymentMethodExtractor)
				.addColumn("Nome", nameExtractor)
				.addColumn("CPF", cpfExtractor)
				.addColumn("Valor", Incentive::getIncentiveValue	)
				.addColumn("Função", functionExtractor)
				.addColumn("Cliente", customerExtractor)
				.addColumn("CNPJ", cnpjExtractor)
				.build();
	}

	public XLSXReportDefinition<Sale> saleDefinition() {
		return new XLSXReportDefinition.Builder<Sale>()
				.addColumn("Mês", saleMonthExtractor)
				.addColumn("N° Doc", docNumExtractor)
				.addColumn("Cliente", saleCustomerExtractor)
				.addColumn("CNPJ", saleCnpjExtractor)
				.addColumn("Vendedor", empNameExtractor)
				.addColumn("CPF", empCpfExtractor)
				.addColumn("Produto", productCodeExtractor)
				.addColumn("Descrição", productNameExtractor)
				.addColumn("Quantidade", quantityExtractor)
				.addColumn("Função", saleFunctionExtractor)
				.build();
	}

	public XLSXReportDefinition<RevenueItemReportDTO> revenueDefinition() {
		return new XLSXReportDefinition.Builder<RevenueItemReportDTO>()
				.addColumn("Mês", RevenueItemReportDTO::getDate)
				.addColumn("N° da Nota", RevenueItemReportDTO::getInvoiceNumber)
				.addColumn("Natureza da Operação", RevenueItemReportDTO::getOperationType)
				.addColumn("Cliente", RevenueItemReportDTO::getCustomer)
				.addColumn("CNPJ", RevenueItemReportDTO::getCnpj)
				.addColumn("Usuário", RevenueItemReportDTO::getUser)
				.addColumn("CPF", RevenueItemReportDTO::getCpf)
				.addColumn("Produto", RevenueItemReportDTO::getProductCode)
				.addColumn("Descrição", RevenueItemReportDTO::getProductName)
				.addColumn("Quantidade", RevenueItemReportDTO::getQuantity)
				.addColumn("Valor Total", RevenueItemReportDTO::getSubTotal)
				.build();
	}

}
