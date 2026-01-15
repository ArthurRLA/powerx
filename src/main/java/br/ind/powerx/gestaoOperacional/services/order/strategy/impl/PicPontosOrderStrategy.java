package br.ind.powerx.gestaoOperacional.services.order.strategy.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.dtos.order.instructions.interfaces.OrderStrategy;
import br.ind.powerx.gestaoOperacional.services.order.XLSXOrderFactory;
import br.ind.powerx.gestaoOperacional.services.order.definition.XLSXReportDefinition;

@Component
public class PicPontosOrderStrategy implements OrderStrategy<Incentive>{

private final XLSXOrderFactory orderFactory;
	
	@Autowired
	public PicPontosOrderStrategy(XLSXOrderFactory orderFactory) {
		this.orderFactory = orderFactory;
	}
	
	@Override
	public XLSXReportDefinition<Incentive> definition(){
		return orderFactory.picPontosOrderDefinition();
	}
	
	@Override
	public String format() {
		return ".xslx";
	}
}
