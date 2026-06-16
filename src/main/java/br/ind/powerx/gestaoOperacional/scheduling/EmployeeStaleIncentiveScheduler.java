package br.ind.powerx.gestaoOperacional.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.ind.powerx.gestaoOperacional.services.EmployeeService;

@Component
public class EmployeeStaleIncentiveScheduler {

	private static final Logger log = LoggerFactory.getLogger(EmployeeStaleIncentiveScheduler.class);

	private static final int DAYS_WITHOUT_INCENTIVE = 90;

	private final EmployeeService employeeService;

	public EmployeeStaleIncentiveScheduler(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@Scheduled(cron = "${app.scheduler.employee-stale-incentive-cron:0 30 3 * * *}")
	public void deactivateStaleWinners() {
		int updated = employeeService.deactivateEmployeesWithoutRecentIncentive(DAYS_WITHOUT_INCENTIVE);
		if (updated > 0) {
			log.info("Rotina de inatividade por falta de incentivo: {} premiado(s) marcado(s) como inativo(s) (sem recebimento há {} dias ou mais).",
					updated, DAYS_WITHOUT_INCENTIVE);
		}
	}
}
