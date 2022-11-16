package com.company.vs_stock;
import com.company.vs_stock.data.*;
import com.company.vs_stock.data.Items.Item;
import com.company.vs_stock.data.enums.Category;
import com.company.vs_stock.data.project.Project;
import com.company.vs_stock.data.utilities.PdfFileExporter;
import com.company.vs_stock.dto.CustomerSaveDto;
import com.company.vs_stock.dto.ItemSaveDto2;
import com.company.vs_stock.dto.ProjectSaveDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static com.company.vs_stock.data.utilities.DateConverter.convertStringToDate;
import static com.company.vs_stock.data.utilities.ImageDisplay.displayImageFromPath;
import static com.company.vs_stock.data.utilities.ImageDisplay.uploadPathProject;
import static com.company.vs_stock.data.utilities.PreparePdfData.setPdfDataStocklist;


@Controller
public class CustomerController {
    private CustomerRepository repo;
    private InvoiceRepository repo1;

    public CustomerController() {
        repo = new CustomerRepository();
        repo1 = new InvoiceRepository();
    }

    @ExceptionHandler(value = SQLIntegrityConstraintViolationException.class)
    public String exception(Model model) {
        model.addAttribute("ex", "Darbība ar datiem nav iespējama");
        return "error";
    }

    @GetMapping("/customers")
    public String viewProjects(Model model) {
        var customers = repo.getCustomers();
        var totalTurnover=repo1.getTotalTurnoverPerYear(LocalDate.now().getYear());
        model.addAttribute("title", "Klienti");
        model.addAttribute("totalTurnover", totalTurnover);
        model.addAttribute("customers", customers);
        return "customers/customers";
    }

    @PostMapping("/customers")
    public String viewProjects() {
        return "customers/customer_detail";
    }

    @GetMapping("/customers/{customerId}")
    public String viewCustomer(@PathVariable long customerId, Model model) {
        var customer = (Customer)repo.getCustomer(customerId);
        model.addAttribute("title", customer.getName());
        model.addAttribute("customer", customer);
        return "customers/customers_detail";
    }



   @GetMapping("/customers/{customerId}/confirm")
    public String deleteCustomerConfirm(@PathVariable long customerId,Model model) {
        var customer = (Customer)repo.getCustomer(customerId);
        model.addAttribute("title", customer.getName());
        model.addAttribute("customer", customer);
        model.addAttribute("confirmDelete", customer);

        return "customers/customers_detail";
    }

    @GetMapping("/customers/{customerId}/delete")
    public ModelAndView deleteCustomer (@PathVariable long customerId) {
        repo.deleteCustomer(customerId);
        return new ModelAndView("redirect:/customers");
    }

    @GetMapping("/customers/{customerId}/update")
    public String updateProject (@PathVariable long customerId, Model model) {
        var customer = (Customer)repo.getCustomer(customerId);
        model.addAttribute("customerId",customerId);
        model.addAttribute("customer", customer);
        model.addAttribute("title", customer.getName());
        return "customers/customers_id_update";
    }
    @PostMapping("/customers/{customerId}/update")
    public ModelAndView updateProjectSave (@PathVariable long customerId, CustomerSaveDto dto, Model model) {
        var customer = (Customer)repo.getCustomer(customerId);
        if(dto.getName()!=null){
            customer.setName(dto.getName());
        }
        if(dto.getVatNumber()!=null){
            customer.setVatNumber(dto.getVatNumber());
        }
        if(dto.getBillingAddress()!=null){
            customer.setBillingAddress(dto.getBillingAddress());
        }
        if(dto.getBank()!=null){
            customer.setBank(dto.getBank());
        }
        if(dto.getAccountNumber()!=null){
            customer.setAccountNumber(dto.getAccountNumber());
        }

        if(dto.getEMail()!=null){
            customer.setEMail(dto.getEMail());
        }
        if(dto.getPhoneNumber()!=null){
            customer.setPhoneNumber(dto.getPhoneNumber());
        }
        if(dto.getSwift()!=null){
            customer.setSwift(dto.getSwift());
        }
        if(dto.getContact()!=null){
            customer.setContact(dto.getContact());
        }
        repo.updateCustomer(customer);
        model.addAttribute("customerId", customerId);
        model.addAttribute("customer", customer);
        return new ModelAndView("redirect:/customers/{customerId}");
    }

    @GetMapping("/new_customer")
    public String addProject(Model model) {
        model.addAttribute("title","Jauns klients");
        return "customers/new_customer";
    }

    @PostMapping("/new_customer")
    public ModelAndView saveProject(@ModelAttribute CustomerSaveDto dto) {
        repo.addCustomer(new Customer(0,dto.getName(),dto.getVatNumber(), dto.getBillingAddress(), dto.getBank(),dto.getSwift(),dto.getAccountNumber(), dto.getEMail(),dto.getPhoneNumber(),dto.getContact()));
        return new ModelAndView("redirect:/customers");
    }

    @GetMapping("/customers/finances")
    public String viewCustomerFinancialInfo(Model model) {
        var years = repo1.getInvoiceYears();
        model.addAttribute("title", "Apgrozījums pa gadiem");
        model.addAttribute("years", years);
        return "customers/customers_finances";
    }

    @GetMapping("/customers/finances/{year}")
    public String viewCustomerFinancialInfo(Model model, @PathVariable int year) {
        var totalTurnover = repo1.getTotalTurnoverPerYear(year);
        var customers = repo.getCustomers();
        model.addAttribute("customers", customers);
        model.addAttribute("title", "Apgrozījums " + year + ". gads");
        model.addAttribute("year", year);
        model.addAttribute("totalTurnover", totalTurnover);
        return "customers/customers_finances_year";
    }

}

