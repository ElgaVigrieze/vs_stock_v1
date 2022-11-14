package com.company.vs_stock;
import com.company.vs_stock.data.*;
import com.company.vs_stock.data.enums.DocType;

import com.company.vs_stock.data.invoice.Invoice;
import com.company.vs_stock.data.invoice.InvoiceLine;
import com.company.vs_stock.data.project.Project;
import com.company.vs_stock.data.utilities.PdfFileExporter;
import com.company.vs_stock.dto.CustomerDto;
import com.company.vs_stock.dto.InvoiceLineSaveDto;
import com.company.vs_stock.dto.InvoiceSaveDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static com.company.vs_stock.data.utilities.DateConverter.convertStringToDate;
import static com.company.vs_stock.data.utilities.DateConverter.formatDate;
import static com.company.vs_stock.data.utilities.PreparePdfData.setPdfDataInvoice;

@Controller
public class InvoiceController {
    private InvoiceRepository repo;
    private ProjectRepository repo1;
    private CustomerRepository repo2;

    public InvoiceController() {
        repo = new InvoiceRepository();
        repo1 = new ProjectRepository();
        repo2 = new CustomerRepository();
    }

    @GetMapping("/new_invoice")
    public String addInvoice(Model model) {
        DocType[] docTypes = DocType.values();
        List<String> types = new ArrayList<>();
        for (var t : docTypes) {
            types.add(t.getValue());
        }
        types.remove(DocType.DELIVERY_INVOICE.getValue());
        Iterable<Customer> customers = repo2.getCustomers();
        var lastInvoice = (Invoice)repo.getLastInvoice();
        var invoiceId = lastInvoice.getId()+1;
        model.addAttribute("title", "Izveidot jaunu rēķinu");
        model.addAttribute("types", types);
        model.addAttribute("invoiceId", invoiceId);
        model.addAttribute("customers", customers);
        return "invoices/new_invoice";
    }

    @PostMapping("/new_invoice/{invoiceId}")
    public ModelAndView addInvoice1(InvoiceSaveDto dto) {
        var invoiceId = repo.addInvoice(dto);
        return new ModelAndView ("redirect:/invoices/{invoiceId}/add");
    }


    @GetMapping("/invoices/{invoiceId}/add")
    public String addProject(Model model, @PathVariable long invoiceId) {
        var invoice = (Invoice)repo.getInvoice(invoiceId);
        var type = invoice.getType().getValue();
        switch (invoice.getType()) {
            case SERVICE_INVOICE -> {
                var projects = repo1.getProjects();
                model.addAttribute("projects", projects);
                model.addAttribute("invoice", invoice);
                model.addAttribute("type", type);
                return "invoices/new_invoice_line";
            }
            case GOODS_INVOICE -> {
                model.addAttribute("type", type);
                boolean addressPresent = true;
                if(invoice.getDeliveryAddress()==null){
                    addressPresent=false;
                }
                model.addAttribute("addressPresent", addressPresent);
                return "invoices/new_invoice_line2a";
            }
        }
        return "";
    }

    @PostMapping("/invoices/{invoiceId}/new_line")
    public ModelAndView saveInvoiceLine(InvoiceLineSaveDto dto, Model model,@PathVariable long invoiceId){
            var invoice = (Invoice)repo.getInvoice(invoiceId);
        model.addAttribute("invoice", invoice);
        if(dto.getProjectId()==0){
          return new ModelAndView("invoices/new_invoice_line2a");
        }
        else{
            repo.addInvoiceLine(dto,invoiceId);
            var invoice1 = (Invoice)repo.getInvoice(invoiceId);
            var lines = invoice1.getLines();
            invoice1.setTotal(invoice1.fetchTotal(lines));
            repo.updateInvoice(invoice1);
            Collections.reverse(lines);
            var lastLine = lines.get(0);
            model.addAttribute("lastLine", lastLine);
        }
        return new ModelAndView("redirect:/invoices/{invoiceId}");
    }

    @PostMapping("/invoices/{invoiceId}/new_line/save")
    public ModelAndView saveInvoice1(InvoiceLineSaveDto dto, Model model,@PathVariable long invoiceId){
        var invoice = (Invoice)repo.getInvoice(invoiceId);
        model.addAttribute("invoice", invoice);
        repo.addInvoiceLine(dto,invoiceId);
        var invoice1 = (Invoice)repo.getInvoice(invoiceId);
        var lines = invoice1.getLines();
        invoice1.setTotal(invoice1.fetchTotal(lines));
        invoice1.setDeliveryAddress(dto.getDeliveryAddress());
        repo.updateInvoice(invoice1);
        return new ModelAndView("redirect:/invoices/{invoiceId}");
    }

    @PostMapping("/invoices/{invoiceId}/new_line/add")
    public ModelAndView saveInvoice2(InvoiceLineSaveDto dto, Model model,@PathVariable long invoiceId){
        var invoice = (Invoice)repo.getInvoice(invoiceId);
        model.addAttribute("invoice", invoice);
        return new ModelAndView("redirect:/invoices/{invoiceId}");
    }

    @GetMapping("/invoices/{invoiceId}/preview")
    public ResponseEntity<InputStreamResource> viewPdf(@PathVariable long invoiceId, Model model) throws IOException {
        var invoice = (Invoice) repo.getInvoice(invoiceId);
        model.addAttribute("invoice", invoice);
        var number = invoice.getNumber().replace("/", "_");
        PdfFileExporter pdfFileExporter = new PdfFileExporter();
        Map<String, Object> data = setPdfDataInvoice(invoiceId);
        String pdfFileName = "C:\\jar\\pdf\\invoices\\invoice_" + number + ".pdf";
        try {
            if (invoice.getType() == DocType.DELIVERY_INVOICE) {
                return pdfFileExporter.showPdfFilePreview("delivery_invoice", data, pdfFileName);
            }
            if (invoice.getType() == DocType.SERVICE_INVOICE) {
                return pdfFileExporter.showPdfFilePreview("service_invoice", data, pdfFileName);
            }
            if (invoice.getType() == DocType.GOODS_INVOICE) {
                return pdfFileExporter.showPdfFilePreview("goods_invoice", data, pdfFileName);
            }
        }catch(Exception ex){
            System.out.println("Kļūda");
        }
        return null;
    }

    @GetMapping("/invoices/{invoiceId}/invoice")
    public String viewPdf1(@PathVariable long invoiceId, Model model) {
        var invoice = (Invoice)repo.getInvoice(invoiceId);
        var lines = repo.getLines(invoiceId);
        var date = invoice.getDate();
        model.addAttribute("invoice",invoice);
        model.addAttribute("lines", lines);
        model.addAttribute("date", date);
        return "pdf_templates/goods_invoice";
    }

    @GetMapping("/invoices/{invoiceId}/lines")
    public String viewLines(@PathVariable long invoiceId,Model model){
        var invoice = (Invoice)repo.getInvoice(invoiceId);
        var lines = invoice.getLines();
        model.addAttribute("invoice", invoice);
        model.addAttribute("lines", lines);
        model.addAttribute("title2", "Rindas");
        return "invoices/invoice_detail_lines";
    }

    @GetMapping("/invoices/{invoiceId}/edit")
    public String editInvoice(@PathVariable long invoiceId, Model model){
        var invoice = (Invoice)repo.getInvoice(invoiceId);
        var customers = repo2.getCustomers();
        model.addAttribute("invoice", invoice);
        model.addAttribute("customers", customers);
        model.addAttribute("title", "Rediģēt rēķinu");
        return "invoices/invoice_detail_edit";
    }
    @PostMapping("/invoices/{invoiceId}/edit")
    public ModelAndView editInvoiceSave(@PathVariable long invoiceId, InvoiceSaveDto dto,Model model){
        var invoice = (Invoice)repo.getInvoice(invoiceId);
        var customer = (Customer)repo2.getCustomer(dto.getCustomerId());
        var lines = invoice.getLines();
        var invoiceUpdated = new Invoice(invoice.getId(),invoice.getType(),invoice.getNumber(),convertStringToDate(dto.getDate()),convertStringToDate(dto.getDueDate()),customer, invoice.getTotal(),dto.getComment(), lines);
        repo.updateInvoice(invoiceUpdated);
        model.addAttribute("invoice", invoice);
        return new ModelAndView("redirect:/invoices/{invoiceId}");
    }

    @GetMapping("/invoices/{invoiceId}/lines/{lineId}/delete")
    public String deleteLineConfirm(@PathVariable long invoiceId, @PathVariable long lineId, Model model){
        var invoice = (Invoice)repo.getInvoice(invoiceId);
        var line = (InvoiceLine)repo.getLine(lineId);
        model.addAttribute("invoice", invoice);
        model.addAttribute("line", line);
        return "invoices/invoice_detail_lines_delete";
    }

    @PostMapping("/invoices/{invoiceId}/lines/{lineId}/delete")
    public ModelAndView deleteLine(@PathVariable long invoiceId, @PathVariable long lineId, Model model){
        repo.deleteInvoiceLine(lineId);
        var invoice = (Invoice)repo.getInvoice(invoiceId);
        var lines = invoice.getLines();
        invoice.setTotal(invoice.fetchTotal(lines));
        repo.updateInvoice(invoice);
        return new ModelAndView("redirect:/invoices/{invoiceId}/lines");
    }

    @GetMapping("/invoices")
    public String invoices(Model model) {
        var invoices = repo.getInvoices();
        var invoice = new Invoice();
        var totalTurnover = repo.getTotalTurnover();
        var repo1 = new CustomerRepository();
        var customers = repo1.getCustomers();
        model.addAttribute("customers", customers);
        model.addAttribute("invoices", invoices);
        model.addAttribute("invoice", invoice);
        model.addAttribute("totalTurnover", totalTurnover);
        return "invoices/invoices";
    }

    @PostMapping("/invoices")
    public String invoicesPerCustomer(Model model, CustomerDto dto) {
        var customerId = dto.getCustomerId();
        var invoices = repo.getAllInvoicesPerCustomer(customerId);
//        var totalTurnover = repo.getTotalTurnoverPer();
        var repo1 = new CustomerRepository();
        var customers = repo1.getCustomers();
        model.addAttribute("customers", customers);
        model.addAttribute("customerId", customerId);
        model.addAttribute("invoices", invoices);
//        model.addAttribute("invoice", invoice);
//        model.addAttribute("totalTurnover", totalTurnover);
        return "invoices/invoices_filtered";
    }

//    @GetMapping("/invoices/customers/{customerId}")
//    public String invoicesPerCustomer1(@PathVariable long customerId, Model model) {
//        var invoices = repo.getAllInvoicesPerCustomer(customerId);
////        var totalTurnover = repo.getTotalTurnoverPer();
//        var repo1 = new CustomerRepository();
//        var customers = repo1.getCustomers();
//        model.addAttribute("customers", customers);
//        model.addAttribute("customerId", customerId);
//        model.addAttribute("invoices", invoices);
////        model.addAttribute("invoice", invoice);
////        model.addAttribute("totalTurnover", totalTurnover);
//        return "invoices/invoices_filtered";
//    }

    @GetMapping("/unpaid")
    public String invoicesUnpaid(Model model) {
        var invoices = repo.getUnpaidInvoices();
        model.addAttribute("invoices", invoices);
        return "invoices/unpaid";
    }

    @GetMapping("/invoices/{invoiceId}")
    public String invoiceDetail(Model model,@PathVariable long invoiceId) {
        var invoice = (Invoice)repo.getInvoice(invoiceId);
        var lines = repo.getLines(invoiceId);
        var type = invoice.getType().getValue();
        var line = ((Collection<?>) lines).size();
        System.out.println(line);
        model.addAttribute("invoice", invoice);
        model.addAttribute("lines", lines);
        model.addAttribute("line", line);
        model.addAttribute("type", type);
        model.addAttribute("title", invoice.getType().getValue().replaceFirst("p","P")+" Nr. "+invoice.getNumber());
        return "invoices/invoice_detail";
    }

    @GetMapping("/invoices/{invoiceId}/confirm")
    public String invoiceDelete(Model model,@PathVariable long invoiceId) {
        var invoice = (Invoice)repo.getInvoice(invoiceId);
        var lines = repo.getLines(invoiceId);
        model.addAttribute("invoice", invoice);
        model.addAttribute("lines", lines);
        model.addAttribute("confirmDelete",invoice);
        return "invoices/invoice_detail";
    }

    @GetMapping("/invoices/{invoiceId}/delivery")
    public ModelAndView createDeliveryInvoice(Model model,@PathVariable long invoiceId) {
        var invoice = (Invoice)repo.getInvoice(invoiceId);
        repo.addDeliveryInvoice(invoice);
        model.addAttribute("invoice", invoice);
        return new ModelAndView ("redirect:/invoices");
    }

    @GetMapping("/invoices/{invoiceId}/delete")
    public ModelAndView  invoiceDeleteConfirmed(Model model,@PathVariable long invoiceId) {
        repo.deleteInvoice(invoiceId);
        return new ModelAndView ("redirect:/invoices");
    }

    @GetMapping("/invoices/{invoiceId}/lines/{lineId}/edit")
    public String editLines(@PathVariable long invoiceId, @PathVariable long lineId, Model model){
        var projects = repo1.getProjects();
        var line = (InvoiceLine)repo.getLine(lineId);
        model.addAttribute("line", line);
        model.addAttribute("projects", projects);
        model.addAttribute("title", "Rediģēt rindu");
        return "invoices/invoice_detail_lines_edit";
    }

    @PostMapping("/invoices/{invoiceId}/lines/{lineId}/edit")
    public ModelAndView  editLinesSave(@PathVariable long invoiceId, @PathVariable long lineId, InvoiceLineSaveDto dto){
        var line = (InvoiceLine)repo.getLine(lineId);
        var updatedLine = new InvoiceLine(lineId,line.getInvoice(),dto.getDescription(),dto.getPrice(),dto.getQuantity(),dto.getPrice()*dto.getQuantity(),(Project)repo1.getProject(dto.getProjectId()));
        repo.updateInvoiceLine(updatedLine);
        return new ModelAndView ("redirect:/invoices/{invoiceId}/lines");
    }

    @PostMapping("/invoices/paid")
    public ModelAndView  markAsPaid(InvoiceSaveDto dto, Model model){
        var invoices = repo.getInvoices();
        model.addAttribute("invoices", invoices);
        System.out.println(dto.getPaid());
        for (var i: repo.getInvoices()) {
            i.setIsPaid(false);
            for (var p: dto.getPaid()) {
               if (i.getId()== p) {
                   i.setIsPaid(true);
               }
            repo.updateInvoice(i);
            }
        }
        return new ModelAndView ("redirect:/invoices");
    }
}
