package br.ind.powerx.gestaoOperacional.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.Revenue;
import br.ind.powerx.gestaoOperacional.model.Sale;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.RevenueItemReportDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.IncentiveCustomerReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.IncentiveDateReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.IncentiveGroupReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.IncentiveUserReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.RevenueCustomerReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.RevenueDateReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.RevenueGroupReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.RevenueUserReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.SaleCustomerReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.SaleDateReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.SaleFlagReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.SaleGroupReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.SaleStateReportInstructions;
import br.ind.powerx.gestaoOperacional.model.enums.ReportFileFormat;
import br.ind.powerx.gestaoOperacional.model.enums.ReportType;
import br.ind.powerx.gestaoOperacional.services.AuthenticationService;
import br.ind.powerx.gestaoOperacional.services.IncentiveService;
import br.ind.powerx.gestaoOperacional.services.RevenueService;
import br.ind.powerx.gestaoOperacional.services.SaleService;
import br.ind.powerx.gestaoOperacional.services.order.XLSXOrderFactory;
import br.ind.powerx.gestaoOperacional.services.order.definition.XLSXReportDefinition; 
import br.ind.powerx.gestaoOperacional.services.order.generator.XLSXGenerator;
import br.ind.powerx.gestaoOperacional.services.report.definition.ReportDefinition;
import br.ind.powerx.gestaoOperacional.services.report.definition.factory.IncentiveReportDefinitionFactory;
import br.ind.powerx.gestaoOperacional.services.report.definition.factory.RevenueReportDefinitionFactory;
import br.ind.powerx.gestaoOperacional.services.report.definition.factory.SaleReportDefinitionFactory;
import br.ind.powerx.gestaoOperacional.services.report.generator.PDFReportGenerator;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final AuthenticationService authenticationService;
    private final IncentiveService incentiveService;
    private final SaleService saleService;
    private final RevenueService revenueService;
    private final IncentiveReportDefinitionFactory incentiveReportDefinitionFactory;
    private final SaleReportDefinitionFactory saleReportDefinitionFactory;
    private final RevenueReportDefinitionFactory revenueReportDefinitionFactory;
    private final PDFReportGenerator pdfReportGenerator;
    private final XLSXOrderFactory xlsxOrderFactory;
    private final XLSXGenerator xlsxGenerator;

    @Autowired
    public ReportController(AuthenticationService authenticationService, IncentiveService incentiveService,
            SaleService saleService, RevenueService revenueService,
            IncentiveReportDefinitionFactory incentiveReportDefinitionFactory,
            SaleReportDefinitionFactory saleReportDefinitionFactory,
            RevenueReportDefinitionFactory revenueReportDefinitionFactory,
            PDFReportGenerator pdfReportGenerator, XLSXOrderFactory xlsxOrderFactory,
            XLSXGenerator xlsxGenerator) {
        this.authenticationService = authenticationService;
        this.incentiveService = incentiveService;
        this.saleService = saleService;
        this.revenueService = revenueService;
        this.incentiveReportDefinitionFactory = incentiveReportDefinitionFactory;
        this.saleReportDefinitionFactory = saleReportDefinitionFactory;
        this.revenueReportDefinitionFactory = revenueReportDefinitionFactory;
        this.pdfReportGenerator = pdfReportGenerator;
        this.xlsxOrderFactory = xlsxOrderFactory;
        this.xlsxGenerator = xlsxGenerator;
    }

    @GetMapping
    public String getReportsPage(Model model) {
        User user = authenticationService.getUserAuthenticated();
        model.addAttribute("user", user);
        return "reports";
    }

    @GetMapping("/incentive-user")
    public String getIncentiveUserReport(Model model) {
        model.addAttribute("user", authenticationService.getUserAuthenticated());
        return "reports-incentive-user";
    }

    @GetMapping("/incentive-group")
    public String getIncentiveGroupReport(Model model) {
        model.addAttribute("user", authenticationService.getUserAuthenticated());
        return "reports-incentive-group";
    }

    @GetMapping("/incentive-customer")
    public String getIncentiveCustomerReport(Model model) {
        model.addAttribute("user", authenticationService.getUserAuthenticated());
        return "reports-incentive-customer";
    }

    @GetMapping("/incentive-date-between")
    public String getIncentiveDateBetweenReport(Model model) {
        model.addAttribute("user", authenticationService.getUserAuthenticated());
        return "reports-incentive-date-between";
    }

    @GetMapping("/incentive-user-details")
    public String getIncentiveUserDetailsReport(Model model) {
        model.addAttribute("user", authenticationService.getUserAuthenticated());
        return "reports-incentive-user-details";
    }

    @GetMapping("/incentive-group-details")
    public String getIncentiveGroupDetailsReport(Model model) {
        model.addAttribute("user", authenticationService.getUserAuthenticated());
        return "reports-incentive-group-details";
    }

    @GetMapping("/incentive-customer-details")
    public String getIncentiveCustomerDetailsReport(Model model) {
        model.addAttribute("user", authenticationService.getUserAuthenticated());
        return "reports-incentive-customer-details";
    }

    @GetMapping("/incentive-date-between-details")
    public String getIncentiveDateBetweenDetailsReport(Model model) {
        model.addAttribute("user", authenticationService.getUserAuthenticated());
        return "reports-incentive-date-between-details";
    }

    @GetMapping("/sale-state")
    public String getSaleStateReport(Model model) {
        model.addAttribute("user", authenticationService.getUserAuthenticated());
        return "reports-sale-state";
    }

    @GetMapping("/sale-flag")
    public String getSaleFlagReport(Model model) {
        model.addAttribute("user", authenticationService.getUserAuthenticated());
        return "reports-sale-flag";
    }

    @GetMapping("/sale-group")
    public String getSaleGroupReport(Model model) {
        model.addAttribute("user", authenticationService.getUserAuthenticated());
        return "reports-sale-group";
    }

    @GetMapping("/sale-customer")
    public String getSaleCustomerReport(Model model) {
        model.addAttribute("user", authenticationService.getUserAuthenticated());
        return "reports-sale-customer";
    }

    @GetMapping("/sale-date-between")
    public String getSaleDateBetweenReport(Model model) {
        model.addAttribute("user", authenticationService.getUserAuthenticated());
        return "reports-sale-date-between";
    }

    @GetMapping("/revenue-user")
    public String getRevenueUserReport(Model model) {
        model.addAttribute("user", authenticationService.getUserAuthenticated());
        return "reports-revenue-user";
    }

    @GetMapping("/revenue-group")
    public String getRevenueGroupReport(Model model) {
        model.addAttribute("user", authenticationService.getUserAuthenticated());
        return "reports-revenue-group";
    }

    @GetMapping("/revenue-customer")
    public String getRevenueCustomerReport(Model model) {
        model.addAttribute("user", authenticationService.getUserAuthenticated());
        return "reports-revenue-customer";
    }

    @GetMapping("/revenue-date-between")
    public String getRevenueDateBetweenReport(Model model) {
        model.addAttribute("user", authenticationService.getUserAuthenticated());
        return "reports-revenue-date-between";
    }

    @PostMapping("/incentive-user")
    public ResponseEntity<?> generateIncentiveUserReport(@RequestBody IncentiveUserReportInstructions instructions) {
        try {
            String date = instructions.getStartDate() + " - " + instructions.getEndDate();

            List<Incentive> incentivesFiltered = incentiveService.filter(instructions);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ReportFileFormat format = instructions.getFileFormat();
            switch (format) {
                case PDF:
                    ReportDefinition<Incentive> definition = incentiveReportDefinitionFactory
                            .incentivesByUserReport(date, ReportType.SIMPLE);
                    pdfReportGenerator.generatePDF(incentivesFiltered, definition, baos);
                    break;
                case XLSX:
                    XLSXReportDefinition<Incentive> xlsxDefinition = xlsxOrderFactory.incentiveDefinition();
                    xlsxGenerator.generate(incentivesFiltered, xlsxDefinition, baos);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Formato de arquivo inválido.");
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            InputStreamResource resource = new InputStreamResource(bais);

            String filename = "relatorio-incentivos." + (format == ReportFileFormat.PDF ? "pdf" : "xlsx");
            String disposition = (format == ReportFileFormat.PDF ? "inline" : "attachment");
            MediaType contentType = (format == ReportFileFormat.PDF ? MediaType.APPLICATION_PDF 
                    : MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            return ResponseEntity.ok()
                    .header("Content-Disposition", disposition + "; filename=" + filename)
                    .contentType(contentType)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível gerar o relatório: " + e.getMessage());
        }
    }

    @PostMapping("/incentive-group")
    public ResponseEntity<?> generateIncentiveGroupReport(@RequestBody IncentiveGroupReportInstructions instructions) {
        try {
            String date = instructions.getStartDate() + " - " + instructions.getEndDate();

            List<Incentive> incentivesFiltered = incentiveService.filter(instructions);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ReportFileFormat format = instructions.getFileFormat();
            switch (format) {
                case PDF:
                    ReportDefinition<Incentive> definition = incentiveReportDefinitionFactory
                            .incentivesByGroupReport(date, ReportType.SIMPLE);
                    pdfReportGenerator.generatePDF(incentivesFiltered, definition, baos);
                    break;
                case XLSX:
                    XLSXReportDefinition<Incentive> xlsxDefinition = xlsxOrderFactory.incentiveDefinition();
                    xlsxGenerator.generate(incentivesFiltered, xlsxDefinition, baos);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Formato de arquivo inválido.");
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            InputStreamResource resource = new InputStreamResource(bais);

            String filename = "relatorio-incentivos." + (format == ReportFileFormat.PDF ? "pdf" : "xlsx");
            String disposition = (format == ReportFileFormat.PDF ? "inline" : "attachment");
            MediaType contentType = (format == ReportFileFormat.PDF ? MediaType.APPLICATION_PDF 
                    : MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            return ResponseEntity.ok()
                    .header("Content-Disposition", disposition + "; filename=" + filename)
                    .contentType(contentType)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível gerar o relatório: " + e.getMessage());
        }
    }

    @PostMapping("/incentive-customer")
    public ResponseEntity<?> generateIncentiveCustomerReport(
            @RequestBody IncentiveCustomerReportInstructions instructions) {
        try {
            String date = instructions.getStartDate() + " - " + instructions.getEndDate();

            List<Incentive> incentivesFiltered = incentiveService.filter(instructions);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ReportFileFormat format = instructions.getFileFormat();
            switch (format) {
                case PDF:
                    ReportDefinition<Incentive> definition = incentiveReportDefinitionFactory
                            .incentivesByCustomerReport(date, ReportType.SIMPLE);
                    pdfReportGenerator.generatePDF(incentivesFiltered, definition, baos);
                    break;
                case XLSX:
                    XLSXReportDefinition<Incentive> xlsxDefinition = xlsxOrderFactory.incentiveDefinition();
                    xlsxGenerator.generate(incentivesFiltered, xlsxDefinition, baos);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Formato de arquivo inválido.");
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            InputStreamResource resource = new InputStreamResource(bais);

            String filename = "relatorio-incentivos." + (format == ReportFileFormat.PDF ? "pdf" : "xlsx");
            String disposition = (format == ReportFileFormat.PDF ? "inline" : "attachment");
            MediaType contentType = (format == ReportFileFormat.PDF ? MediaType.APPLICATION_PDF 
                    : MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            return ResponseEntity.ok()
                    .header("Content-Disposition", disposition + "; filename=" + filename)
                    .contentType(contentType)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível gerar o relatório: " + e.getMessage());
        }
    }

    @PostMapping("/incentive-date-between")
    public ResponseEntity<?> generateIncentiveDateReport(@RequestBody IncentiveDateReportInstructions instructions) {
        try {
            String date = instructions.getStartDate() + " - " + instructions.getEndDate();

            List<Incentive> incentivesFiltered = incentiveService.filter(instructions);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ReportFileFormat format = instructions.getFileFormat();
            switch (format) {
                case PDF:
                    ReportDefinition<Incentive> definition = incentiveReportDefinitionFactory
                            .incentivesByDateReport(date, ReportType.SIMPLE);
                    pdfReportGenerator.generatePDF(incentivesFiltered, definition, baos);
                    break;
                case XLSX:
                    XLSXReportDefinition<Incentive> xlsxDefinition = xlsxOrderFactory.incentiveDefinition();
                    xlsxGenerator.generate(incentivesFiltered, xlsxDefinition, baos);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Formato de arquivo inválido.");
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            InputStreamResource resource = new InputStreamResource(bais);

            String filename = "relatorio-incentivos." + (format == ReportFileFormat.PDF ? "pdf" : "xlsx");
            String disposition = (format == ReportFileFormat.PDF ? "inline" : "attachment");
            MediaType contentType = (format == ReportFileFormat.PDF ? MediaType.APPLICATION_PDF 
                    : MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            return ResponseEntity.ok()
                    .header("Content-Disposition", disposition + "; filename=" + filename)
                    .contentType(contentType)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível gerar o relatório: " + e.getMessage());
        }
    }

    @PostMapping("/sale-state")
    public ResponseEntity<?> generateSaleStateReport(@RequestBody SaleStateReportInstructions instructions) {
        try {
            String date = instructions.getStartDate() + " - " + instructions.getEndDate();
            ReportType type = instructions.isDetails() ? ReportType.WITH_DETAILS : ReportType.SIMPLE;

            List<Sale> salesFiltered = saleService.filter(instructions);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ReportFileFormat format = instructions.getFileFormat();
            switch (format) {
                case PDF:
                    ReportDefinition<Sale> definition = saleReportDefinitionFactory
                            .salesByStateReport(date, type);
                    pdfReportGenerator.generatePDF(salesFiltered, definition, baos);
                    break;
                case XLSX:
                    XLSXReportDefinition<Sale> xlsxDefinition = xlsxOrderFactory.saleDefinition();
                    xlsxGenerator.generate(salesFiltered, xlsxDefinition, baos);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Formato de arquivo inválido.");
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            InputStreamResource resource = new InputStreamResource(bais);

            String filename = "relatorio-vendas." + (format == ReportFileFormat.PDF ? "pdf" : "xlsx");
            String disposition = (format == ReportFileFormat.PDF ? "inline" : "attachment");
            MediaType contentType = (format == ReportFileFormat.PDF ? MediaType.APPLICATION_PDF 
                    : MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            return ResponseEntity.ok()
                    .header("Content-Disposition", disposition + "; filename=" + filename)
                    .contentType(contentType)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível gerar o relatório: " + e.getMessage());
        }
    }

    @PostMapping("/sale-flag")
    public ResponseEntity<?> generateSaleFlagReport(@RequestBody SaleFlagReportInstructions instructions) {
        try {
            String date = instructions.getStartDate() + " - " + instructions.getEndDate();
            ReportType type = instructions.isDetails() ? ReportType.WITH_DETAILS : ReportType.SIMPLE;

            List<Sale> salesFiltered = saleService.filter(instructions);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ReportFileFormat format = instructions.getFileFormat();
            switch (format) {
                case PDF:
                    ReportDefinition<Sale> definition = saleReportDefinitionFactory
                            .salesByFlagReport(date, type);
                    pdfReportGenerator.generatePDF(salesFiltered, definition, baos);
                    break;
                case XLSX:
                    XLSXReportDefinition<Sale> xlsxDefinition = xlsxOrderFactory.saleDefinition();
                    xlsxGenerator.generate(salesFiltered, xlsxDefinition, baos);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Formato de arquivo inválido.");
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            InputStreamResource resource = new InputStreamResource(bais);

            String filename = "relatorio-vendas." + (format == ReportFileFormat.PDF ? "pdf" : "xlsx");
            String disposition = (format == ReportFileFormat.PDF ? "inline" : "attachment");
            MediaType contentType = (format == ReportFileFormat.PDF ? MediaType.APPLICATION_PDF 
                    : MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            return ResponseEntity.ok()
                    .header("Content-Disposition", disposition + "; filename=" + filename)
                    .contentType(contentType)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível gerar o relatório: " + e.getMessage());
        }
    }

    @PostMapping("/sale-group")
    public ResponseEntity<?> generateSaleGroupReport(@RequestBody SaleGroupReportInstructions instructions) {
        try {
            String date = instructions.getStartDate() + " - " + instructions.getEndDate();
            ReportType type = instructions.isDetails() ? ReportType.WITH_DETAILS : ReportType.SIMPLE;

            List<Sale> salesFiltered = saleService.filter(instructions);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ReportFileFormat format = instructions.getFileFormat();
            switch (format) {
                case PDF:
                    ReportDefinition<Sale> definition = saleReportDefinitionFactory
                            .salesByGroupReport(date, type);
                    pdfReportGenerator.generatePDF(salesFiltered, definition, baos);
                    break;
                case XLSX:
                    XLSXReportDefinition<Sale> xlsxDefinition = xlsxOrderFactory.saleDefinition();
                    xlsxGenerator.generate(salesFiltered, xlsxDefinition, baos);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Formato de arquivo inválido.");
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            InputStreamResource resource = new InputStreamResource(bais);

            String filename = "relatorio-vendas." + (format == ReportFileFormat.PDF ? "pdf" : "xlsx");
            String disposition = (format == ReportFileFormat.PDF ? "inline" : "attachment");
            MediaType contentType = (format == ReportFileFormat.PDF ? MediaType.APPLICATION_PDF 
                    : MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            return ResponseEntity.ok()
                    .header("Content-Disposition", disposition + "; filename=" + filename)
                    .contentType(contentType)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível gerar o relatório: " + e.getMessage());
        }
    }

    @PostMapping("/sale-customer")
    public ResponseEntity<?> generateSaleCustomerReport(@RequestBody SaleCustomerReportInstructions instructions) {
        try {
            String date = instructions.getStartDate() + " - " + instructions.getEndDate();
            ReportType type = instructions.isDetails() ? ReportType.WITH_DETAILS : ReportType.SIMPLE;

            List<Sale> salesFiltered = saleService.filter(instructions);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ReportFileFormat format = instructions.getFileFormat();
            switch (format) {
                case PDF:
                    ReportDefinition<Sale> definition = saleReportDefinitionFactory
                            .salesByCustomerReport(date, type);
                    pdfReportGenerator.generatePDF(salesFiltered, definition, baos);
                    break;
                case XLSX:
                    XLSXReportDefinition<Sale> xlsxDefinition = xlsxOrderFactory.saleDefinition();
                    xlsxGenerator.generate(salesFiltered, xlsxDefinition, baos);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Formato de arquivo inválido.");
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            InputStreamResource resource = new InputStreamResource(bais);

            String filename = "relatorio-vendas." + (format == ReportFileFormat.PDF ? "pdf" : "xlsx");
            String disposition = (format == ReportFileFormat.PDF ? "inline" : "attachment");
            MediaType contentType = (format == ReportFileFormat.PDF ? MediaType.APPLICATION_PDF 
                    : MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            return ResponseEntity.ok()
                    .header("Content-Disposition", disposition + "; filename=" + filename)
                    .contentType(contentType)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível gerar o relatório: " + e.getMessage());
        }
    }

    @PostMapping("/sale-date-between")
    public ResponseEntity<?> generateSaleDateReport(@RequestBody SaleDateReportInstructions instructions) {
        try {
            String date = instructions.getStartDate() + " - " + instructions.getEndDate();
            ReportType type = instructions.isDetails() ? ReportType.WITH_DETAILS : ReportType.SIMPLE;

            List<Sale> salesFiltered = saleService.filter(instructions);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ReportFileFormat format = instructions.getFileFormat();
            switch (format) {
                case PDF:
                    ReportDefinition<Sale> definition = saleReportDefinitionFactory
                            .salesByDateReport(date, type); 
                    pdfReportGenerator.generatePDF(salesFiltered, definition, baos);
                    break;
                case XLSX:
                    XLSXReportDefinition<Sale> xlsxDefinition = xlsxOrderFactory.saleDefinition();
                    xlsxGenerator.generate(salesFiltered, xlsxDefinition, baos);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Formato de arquivo inválido.");
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            InputStreamResource resource = new InputStreamResource(bais);

            String filename = "relatorio-vendas." + (format == ReportFileFormat.PDF ? "pdf" : "xlsx");
            String disposition = (format == ReportFileFormat.PDF ? "inline" : "attachment");
            MediaType contentType = (format == ReportFileFormat.PDF ? MediaType.APPLICATION_PDF 
                    : MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            return ResponseEntity.ok()
                    .header("Content-Disposition", disposition + "; filename=" + filename)
                    .contentType(contentType)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível gerar o relatório: " + e.getMessage());
        }
    }

    @PostMapping("/revenue-user")
    public ResponseEntity<?> generateRevenueUserReport(@RequestBody RevenueUserReportInstructions instructions) {
        try {
            String date = instructions.getStartDate() + " - " + instructions.getEndDate();
            ReportType type = instructions.isDetails() ? ReportType.WITH_DETAILS : ReportType.SIMPLE;

            List<Revenue> revenuesFiltered = revenueService.filter(instructions);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ReportFileFormat format = instructions.getFileFormat();
            switch (format) {
                case PDF:
                    ReportDefinition<Revenue> definition = revenueReportDefinitionFactory
                            .revenuesByUserReport(date, type);
                    pdfReportGenerator.generatePDF(revenuesFiltered, definition, baos);
                    break;
                case XLSX:
                    List<RevenueItemReportDTO> flatList = revenueService.flattenRevenuesToReportDTOs(revenuesFiltered);
                    XLSXReportDefinition<RevenueItemReportDTO> xlsxDefinition = xlsxOrderFactory.revenueDefinition();
                    xlsxGenerator.generate(flatList, xlsxDefinition, baos);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Formato de arquivo inválido.");
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            InputStreamResource resource = new InputStreamResource(bais);

            String filename = "relatorio-faturamento." + (format == ReportFileFormat.PDF ? "pdf" : "xlsx");
            String disposition = (format == ReportFileFormat.PDF ? "inline" : "attachment");
            MediaType contentType = (format == ReportFileFormat.PDF ? MediaType.APPLICATION_PDF 
                    : MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            return ResponseEntity.ok()
                    .header("Content-Disposition", disposition + "; filename=" + filename)
                    .contentType(contentType)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível gerar o relatório: " + e.getMessage());
        }
    }

    @PostMapping("/revenue-group")
    public ResponseEntity<?> generateRevenueGroupReport(@RequestBody RevenueGroupReportInstructions instructions) {
        try {
            String date = instructions.getStartDate() + " - " + instructions.getEndDate();
            ReportType type = instructions.isDetails() ? ReportType.WITH_DETAILS : ReportType.SIMPLE;

            List<Revenue> revenuesFiltered = revenueService.filter(instructions);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ReportFileFormat format = instructions.getFileFormat();
            switch (format) {
                case PDF:
                    ReportDefinition<Revenue> definition = revenueReportDefinitionFactory
                            .revenuesByGroupReport(date, type);
                    pdfReportGenerator.generatePDF(revenuesFiltered, definition, baos);
                    break;
                case XLSX:
                    List<RevenueItemReportDTO> flatList = revenueService.flattenRevenuesToReportDTOs(revenuesFiltered);
                    XLSXReportDefinition<RevenueItemReportDTO> xlsxDefinition = xlsxOrderFactory.revenueDefinition();
                    xlsxGenerator.generate(flatList, xlsxDefinition, baos);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Formato de arquivo inválido.");
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            InputStreamResource resource = new InputStreamResource(bais);

            String filename = "relatorio-faturamento." + (format == ReportFileFormat.PDF ? "pdf" : "xlsx");
            String disposition = (format == ReportFileFormat.PDF ? "inline" : "attachment");
            MediaType contentType = (format == ReportFileFormat.PDF ? MediaType.APPLICATION_PDF 
                    : MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            return ResponseEntity.ok()
                    .header("Content-Disposition", disposition + "; filename=" + filename)
                    .contentType(contentType)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível gerar o relatório: " + e.getMessage());
        }
    }

    @PostMapping("/revenue-customer")
    public ResponseEntity<?> generateRevenueCustomerReport(
            @RequestBody RevenueCustomerReportInstructions instructions) {
        try {
            String date = instructions.getStartDate() + " - " + instructions.getEndDate();
            ReportType type = instructions.isDetails() ? ReportType.WITH_DETAILS : ReportType.SIMPLE;

            List<Revenue> revenuesFiltered = revenueService.filter(instructions);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ReportFileFormat format = instructions.getFileFormat();
            switch (format) {
                case PDF:
                    ReportDefinition<Revenue> definition = revenueReportDefinitionFactory
                            .revenuesByCustomerReport(date, type);
                    pdfReportGenerator.generatePDF(revenuesFiltered, definition, baos);
                    break;
                case XLSX:
                    List<RevenueItemReportDTO> flatList = revenueService.flattenRevenuesToReportDTOs(revenuesFiltered);
                    XLSXReportDefinition<RevenueItemReportDTO> xlsxDefinition = xlsxOrderFactory.revenueDefinition();
                    xlsxGenerator.generate(flatList, xlsxDefinition, baos);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Formato de arquivo inválido.");
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            InputStreamResource resource = new InputStreamResource(bais);

            String filename = "relatorio-faturamento." + (format == ReportFileFormat.PDF ? "pdf" : "xlsx");
            String disposition = (format == ReportFileFormat.PDF ? "inline" : "attachment");
            MediaType contentType = (format == ReportFileFormat.PDF ? MediaType.APPLICATION_PDF 
                    : MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            return ResponseEntity.ok()
                    .header("Content-Disposition", disposition + "; filename=" + filename)
                    .contentType(contentType)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível gerar o relatório: " + e.getMessage());
        }
    }

    @PostMapping("/revenue-date-between")
    public ResponseEntity<?> generateRevenueDateReport(@RequestBody RevenueDateReportInstructions instructions) {
        try {
            String date = instructions.getStartDate() + " - " + instructions.getEndDate();
            ReportType type = instructions.isDetails() ? ReportType.WITH_DETAILS : ReportType.SIMPLE;

            List<Revenue> revenuesFiltered = revenueService.filter(instructions);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ReportFileFormat format = instructions.getFileFormat();
            switch (format) {
                case PDF:
                    ReportDefinition<Revenue> definition = revenueReportDefinitionFactory
                            .revenuesByDateReport(date, type);
                    pdfReportGenerator.generatePDF(revenuesFiltered, definition, baos);
                    break;
                case XLSX:
                    List<RevenueItemReportDTO> flatList = revenueService.flattenRevenuesToReportDTOs(revenuesFiltered);
                    XLSXReportDefinition<RevenueItemReportDTO> xlsxDefinition = xlsxOrderFactory.revenueDefinition();
                    xlsxGenerator.generate(flatList, xlsxDefinition, baos);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Formato de arquivo inválido.");
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            InputStreamResource resource = new InputStreamResource(bais);

            String filename = "relatorio-faturamento." + (format == ReportFileFormat.PDF ? "pdf" : "xlsx");
            String disposition = (format == ReportFileFormat.PDF ? "inline" : "attachment");
            MediaType contentType = (format == ReportFileFormat.PDF ? MediaType.APPLICATION_PDF 
                    : MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            return ResponseEntity.ok()
                    .header("Content-Disposition", disposition + "; filename=" + filename)
                    .contentType(contentType)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível gerar o relatório: " + e.getMessage());
        }
    }

    @PostMapping("/incentive-user-details")
    public ResponseEntity<?> generateIncentiveUserWithDetailsReport(
            @RequestBody IncentiveUserReportInstructions instructions) {
        try {
            String date = instructions.getStartDate() + " - " + instructions.getEndDate();

            List<Incentive> incentivesFiltered = incentiveService.filter(instructions);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ReportFileFormat format = instructions.getFileFormat();
            switch (format) {
                case PDF:
                    ReportDefinition<Incentive> definition = incentiveReportDefinitionFactory
                            .incentivesByUserReport(date, ReportType.WITH_DETAILS);
                    pdfReportGenerator.generatePDF(incentivesFiltered, definition, baos);
                    break;
                case XLSX:
                    XLSXReportDefinition<Incentive> xlsxDefinition = xlsxOrderFactory.incentiveDefinition();
                    xlsxGenerator.generate(incentivesFiltered, xlsxDefinition, baos);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Formato de arquivo inválido.");
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            InputStreamResource resource = new InputStreamResource(bais);

            String filename = "relatorio-incentivos." + (format == ReportFileFormat.PDF ? "pdf" : "xlsx");
            String disposition = (format == ReportFileFormat.PDF ? "inline" : "attachment");
            MediaType contentType = (format == ReportFileFormat.PDF ? MediaType.APPLICATION_PDF 
                    : MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            return ResponseEntity.ok()
                    .header("Content-Disposition", disposition + "; filename=" + filename)
                    .contentType(contentType)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível gerar o relatório: " + e.getMessage());
        }
    }

    @PostMapping("/incentive-group-details")
    public ResponseEntity<?> generateIncentiveGroupWithDetailsReport(
            @RequestBody IncentiveGroupReportInstructions instructions) {
        try {
            String date = instructions.getStartDate() + " - " + instructions.getEndDate();

            List<Incentive> incentivesFiltered = incentiveService.filter(instructions);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ReportFileFormat format = instructions.getFileFormat();
            switch (format) {
                case PDF:
                    ReportDefinition<Incentive> definition = incentiveReportDefinitionFactory
                            .incentivesByGroupReport(date, ReportType.WITH_DETAILS);
                    pdfReportGenerator.generatePDF(incentivesFiltered, definition, baos);
                    break;
                case XLSX:
                    XLSXReportDefinition<Incentive> xlsxDefinition = xlsxOrderFactory.incentiveDefinition();
                    xlsxGenerator.generate(incentivesFiltered, xlsxDefinition, baos);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Formato de arquivo inválido.");
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            InputStreamResource resource = new InputStreamResource(bais);

            String filename = "relatorio-incentivos." + (format == ReportFileFormat.PDF ? "pdf" : "xlsx");
            String disposition = (format == ReportFileFormat.PDF ? "inline" : "attachment");
            MediaType contentType = (format == ReportFileFormat.PDF ? MediaType.APPLICATION_PDF 
                    : MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            return ResponseEntity.ok()
                    .header("Content-Disposition", disposition + "; filename=" + filename)
                    .contentType(contentType)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível gerar o relatório: " + e.getMessage());
        }
    }

    @PostMapping("/incentive-customer-details")
    public ResponseEntity<?> generateIncentiveCustomerWithDetailsReport(
            @RequestBody IncentiveCustomerReportInstructions instructions) {
        try {
            String date = instructions.getStartDate() + " - " + instructions.getEndDate();

            List<Incentive> incentivesFiltered = incentiveService.filter(instructions);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ReportFileFormat format = instructions.getFileFormat();
            switch (format) {
                case PDF:
                    ReportDefinition<Incentive> definition = incentiveReportDefinitionFactory
                            .incentivesByCustomerReport(date, ReportType.WITH_DETAILS);
                    pdfReportGenerator.generatePDF(incentivesFiltered, definition, baos);
                    break;
                case XLSX:
                    XLSXReportDefinition<Incentive> xlsxDefinition = xlsxOrderFactory.incentiveDefinition();
                    xlsxGenerator.generate(incentivesFiltered, xlsxDefinition, baos);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Formato de arquivo inválido.");
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            InputStreamResource resource = new InputStreamResource(bais);

            String filename = "relatorio-incentivos." + (format == ReportFileFormat.PDF ? "pdf" : "xlsx");
            String disposition = (format == ReportFileFormat.PDF ? "inline" : "attachment");
            MediaType contentType = (format == ReportFileFormat.PDF ? MediaType.APPLICATION_PDF 
                    : MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            return ResponseEntity.ok()
                    .header("Content-Disposition", disposition + "; filename=" + filename)
                    .contentType(contentType)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível gerar o relatório: " + e.getMessage());
        }
    }

    @PostMapping("/incentive-date-between-details")
    public ResponseEntity<?> generateIncentiveDateWithDetailsReport(
            @RequestBody IncentiveDateReportInstructions instructions) {
        try {
            String date = instructions.getStartDate() + " - " + instructions.getEndDate();

            List<Incentive> incentivesFiltered = incentiveService.filter(instructions);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ReportFileFormat format = instructions.getFileFormat();
            switch (format) {
                case PDF:
                    ReportDefinition<Incentive> definition = incentiveReportDefinitionFactory
                            .incentivesByDateReport(date, ReportType.WITH_DETAILS);
                    pdfReportGenerator.generatePDF(incentivesFiltered, definition, baos);
                    break;
                case XLSX:
                    XLSXReportDefinition<Incentive> xlsxDefinition = xlsxOrderFactory.incentiveDefinition();
                    xlsxGenerator.generate(incentivesFiltered, xlsxDefinition, baos);
                    break;
                default:
                    return ResponseEntity.badRequest().body("Formato de arquivo inválido.");
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            InputStreamResource resource = new InputStreamResource(bais);

            String filename = "relatorio-incentivos." + (format == ReportFileFormat.PDF ? "pdf" : "xlsx");
            String disposition = (format == ReportFileFormat.PDF ? "inline" : "attachment");
            MediaType contentType = (format == ReportFileFormat.PDF ? MediaType.APPLICATION_PDF 
                    : MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            return ResponseEntity.ok()
                    .header("Content-Disposition", disposition + "; filename=" + filename)
                    .contentType(contentType)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível gerar o relatório: " + e.getMessage());
        }
    }

    @PostMapping("/by-docNum/{num}")
    public ResponseEntity<?> generateByDocumentNumber(@PathVariable Integer num) {
        try {
            List<Incentive> incentivesFiltered = incentiveService.filter(num);
            String date = incentivesFiltered.get(0).getReferenceDate().toString();

            ReportDefinition<Incentive> definition = incentiveReportDefinitionFactory
                    .incentivesByCustomerReport(date, ReportType.WITH_DETAILS);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            pdfReportGenerator.generatePDF(incentivesFiltered, definition, baos);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

            InputStreamResource resource = new InputStreamResource(bais);

            return ResponseEntity.ok().header("Content-Disposition", "inline; filename=relatorio-incentivos.pdf")
                    .contentType(MediaType.APPLICATION_PDF).body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Não foi possível gerar o relatório: " + e.getMessage());
        }
    }
}