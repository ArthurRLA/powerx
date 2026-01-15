package br.ind.powerx.gestaoOperacional.services;

import java.math.BigDecimal;
import java.util.List;

import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.dtos.IncentiveCalculatedDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.IncentiveDTO;
import br.ind.powerx.gestaoOperacional.model.enums.State;

public class IncentiveMapper {

        public static IncentiveCalculatedDTO toIncentiveCalculated(List<Incentive> newIncentives) {
                Incentive incentiveReference = newIncentives.get(0);

                Integer saleDocumentNumber = incentiveReference.getSaleDocumentNumber();
                String referenceDate = MonthName.from(incentiveReference.getReferenceDate());
                State state = incentiveReference.getState();
                String customerName = incentiveReference.getCustomer().getFantasyName();
                String customerCnpj = incentiveReference.getCustomer().getCnpj();
                String paymentMethod = incentiveReference.getPaymentMethod().getName();

                List<IncentiveDTO> ccIncentives = newIncentives.stream()
                                .filter(i -> i.getApurationType().getName().equals("Conta Corrente"))
                                .map(incentive -> new IncentiveDTO(
                                                incentive.getCpf(),
                                                incentive.getEmployee().getName(),
                                                incentive.getIncentiveValue(),
                                                incentive.getEmployeeFunction().getName()))
                                .toList();

                List<IncentiveDTO> nfsIncentives = newIncentives.stream()
                                .filter(i -> i.getApurationType().getName().equals("NF Serviço"))
                                .map(incentive -> new IncentiveDTO(
                                                incentive.getCpf(),
                                                incentive.getEmployee().getName(),
                                                incentive.getIncentiveValue(),
                                                incentive.getEmployeeFunction().getName()))
                                .toList();

                BigDecimal totalCc = ccIncentives.stream()
                                .map(IncentiveDTO::getIncentiveValue)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalNfs = nfsIncentives.stream()
                                .map(IncentiveDTO::getIncentiveValue)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                var dto = new IncentiveCalculatedDTO();
                dto.setSaleDocumentNumber(saleDocumentNumber);
                dto.setReferenceDate(referenceDate);
                dto.setState(state);
                dto.setCustomerName(customerName);
                dto.setCustomerCnpj(customerCnpj);
                dto.setPaymentMethod(paymentMethod);
                dto.setCcIncentives(ccIncentives);
                dto.setNfsIncentives(nfsIncentives);
                dto.setTotalCc(totalCc);
                dto.setTotalNfs(totalNfs);

                return dto;
        }

}
