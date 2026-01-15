package br.ind.powerx.gestaoOperacional.services.order.orcherstrator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.PaymentMethod;
import br.ind.powerx.gestaoOperacional.model.dtos.order.instructions.interfaces.OrderStrategy;
import br.ind.powerx.gestaoOperacional.model.dtos.order.instructions.interfaces.Specificable;
import br.ind.powerx.gestaoOperacional.repositories.IncentiveRepository;
import br.ind.powerx.gestaoOperacional.repositories.PaymentMethodRepository;
import br.ind.powerx.gestaoOperacional.services.order.definition.GeneratedFile;
import br.ind.powerx.gestaoOperacional.services.order.definition.XLSXReportDefinition;
import br.ind.powerx.gestaoOperacional.services.order.generator.XLSXGenerator;
import br.ind.powerx.gestaoOperacional.services.order.strategy.impl.PicPontosOrderStrategy;
import br.ind.powerx.gestaoOperacional.services.order.strategy.impl.SwileOrderStrategy;
import br.ind.powerx.gestaoOperacional.services.order.strategy.impl.YouCardOrderStrategy;

@Service
public class XLSXOrchestrator {

	private final IncentiveRepository incentiveRepository;
	private final Map<PaymentMethod, OrderStrategy<Incentive>> strategies;
	private final XLSXGenerator xlsxGenerator;
	
	
	@Autowired
	public XLSXOrchestrator(IncentiveRepository incentiveRepository,
			XLSXGenerator xlsxGenerator, PaymentMethodRepository paymentMethodRepository, 
			SwileOrderStrategy swileOrderStrategy, YouCardOrderStrategy youCardOrderStrategy, 
			PicPontosOrderStrategy picPontosOrderStrategy) {
		
		this.incentiveRepository = incentiveRepository;
		this.xlsxGenerator = xlsxGenerator;
		
		strategies = Map.of(
					paymentMethodRepository.findByName("Swile"), swileOrderStrategy, 
					paymentMethodRepository.findByName("You Card"), youCardOrderStrategy,
					paymentMethodRepository.findByName("Pic Pontos"), picPontosOrderStrategy
				);
	}
	
	public List<Incentive> findIncentives(Specificable<Incentive> specificable){
		Specification<Incentive> spec = specificable.getSpecification();
		return incentiveRepository.findAll(spec);
	}
	
	public Map<PaymentMethod, List<Incentive>> incentivesGroupedByPaymentMethod(List<Incentive> incentives){
		return incentives.stream()
				.collect(Collectors.groupingBy(Incentive::getPaymentMethod));
	}
	
	public GeneratedFile generate(PaymentMethod method, List<Incentive> incentives) throws IOException {
		OrderStrategy<Incentive> strategy = strategies.get(method);
		XLSXReportDefinition<Incentive> definition = strategy.definition();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		xlsxGenerator.generate(incentives, definition, baos);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		
		InputStreamResource resource = new InputStreamResource(bais);
		
		GeneratedFile file = new GeneratedFile(method.getName() + strategy.format(), resource);
		
		return file;
	}
}
