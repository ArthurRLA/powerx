package br.ind.powerx.gestaoOperacional.services.order.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.PaymentMethod;
import br.ind.powerx.gestaoOperacional.model.dtos.order.instructions.interfaces.Specificable;
import br.ind.powerx.gestaoOperacional.services.order.definition.GeneratedFile;
import br.ind.powerx.gestaoOperacional.services.order.orcherstrator.XLSXOrchestrator;
import br.ind.powerx.gestaoOperacional.util.ZipUtil;

@Service
public class OrderService {
	
	private static final String XLSX_MEDIA_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	private static final String CSV_MEDIA_TYPE = "text/csv";

	private final XLSXOrchestrator xlsxOrcherstrator;

	@Autowired
	public OrderService(XLSXOrchestrator xlsxOrcherstrator) {
		this.xlsxOrcherstrator = xlsxOrcherstrator;
	}

	public ResponseEntity<?> generateFile(Specificable<Incentive> specificable) throws IOException {

		List<Incentive> incentives = xlsxOrcherstrator.findIncentives(specificable);
		Map<PaymentMethod, List<Incentive>> groupedIncentives = xlsxOrcherstrator.incentivesGroupedByPaymentMethod(incentives);
		List<GeneratedFile> files = new ArrayList<>();
		
		for(Map.Entry<PaymentMethod, List<Incentive>> entry : groupedIncentives.entrySet()) {
			files.add(xlsxOrcherstrator.generate(entry.getKey(), entry.getValue()));
		}
		
		if(files.size() > 1) {
			InputStreamResource resource = ZipUtil.createZip(files);
			
			String filename = "pedidos_" + specificable.name() + ".zip";
            
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
		} else {
			GeneratedFile file = files.get(0);
            String filename = file.getFilename();
            MediaType mediaType = filename.endsWith(".xlsx")
                ? MediaType.parseMediaType(XLSX_MEDIA_TYPE)
                : MediaType.parseMediaType(CSV_MEDIA_TYPE);

            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .contentType(mediaType)
                .body(file.getResource());
		}
	}

}
