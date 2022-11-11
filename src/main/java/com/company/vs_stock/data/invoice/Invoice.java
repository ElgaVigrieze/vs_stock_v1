package com.company.vs_stock.data.invoice;

import com.company.vs_stock.data.*;
import com.company.vs_stock.data.enums.DocType;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static com.company.vs_stock.data.utilities.DateConverter.formatDate;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@Getter
@Setter
@Entity(name="Invoice")
@NoArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", unique = true, nullable = false)
    private long id;
    @Enumerated
    private DocType type;
    @Column(name="number")
    private String number;
    @Column(name="date")
    private LocalDate date;
    @Column(name="due_date")
    private LocalDate dueDate;
    @ManyToOne
//    @JoinTable(name="customer", joinColumns=@JoinColumn(name="id"), inverseJoinColumns=@JoinColumn(name="customer_id"))
    private Customer customer;
    @Column(name="total")
    private Float total;
    @Column(name="comment")
    private String comment;
//    @Transient
    @Column(name="delivery_address")
    private String deliveryAddress;
    @OneToMany(cascade=CascadeType.DETACH)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name="invoice_line", joinColumns=@JoinColumn(name="invoice_id"), inverseJoinColumns=@JoinColumn(name="id"))
    List<InvoiceLine> lines;
    @Column(name="paid")
    private Boolean isPaid;




    public Invoice(long id, DocType type, LocalDate date, LocalDate dueDate, Customer customer, String comment) { //type invoice
        this.id = id;
        this.type = type;
        this.date=date;
        this.dueDate=dueDate;
        this.customer = customer;
        this.comment=comment;
    }
    public Invoice(long id, DocType type, LocalDate date, LocalDate dueDate, Customer customer, String comment, Float total) { //type invoice
        this.id = id;
        this.type = type;
        this.date=date;
        this.dueDate=dueDate;
        this.customer = customer;
        this.comment=comment;
        this.total=total;
    }

    public Invoice(long id, DocType type, String number, LocalDate date, LocalDate dueDate, Customer customer, Float total, String comment, List<InvoiceLine> lines) {
        this.id = id;
        this.type = type;
        this.number = number;
        this.date = date;
        this.dueDate = dueDate;
        this.customer = customer;
        this.total = total;
        this.comment = comment;
        this.lines = lines;
    }

    public Invoice(long id, DocType type, String number, LocalDate date, LocalDate dueDate, Customer customer, Float total, String comment, List<InvoiceLine> lines, String deliveryAddress) {
        this.id = id;
        this.type = type;
        this.number = number;
        this.date = date;
        this.dueDate = dueDate;
        this.customer = customer;
        this.total = total;
        this.comment = comment;
        this.lines = lines;
    this.deliveryAddress = deliveryAddress;}


    public Float fetchTotal(List<InvoiceLine> lines){
        float total = 0;
        for (var item: lines) {
            total += item.getSum();
        }
        return total;
    }

//    public Float setTotal(){
//        float total = 0;
//        for (var item: lines) {
//            total += item.getSum();
//        }
//        return total;
//    }

    public String generateNumber(){
        var year = String.valueOf(LocalDate.now().getYear()).substring(2,4);
        var repo = new InvoiceRepository();
        var lastInvoice = (Invoice)repo.getLastStandardInvoice();
        var lastInvoiceNumber = lastInvoice.getNumber();
        var lastInvoiceYear = lastInvoiceNumber.substring(2,4);
//        var lastInvoiceLastDigits = lastInvoiceNumber.substring(lastInvoice.getNumber().lastIndexOf("/")+1);
//        var lastNumber = Integer.parseInt(lastInvoiceLastDigits);
        if(year.equals(lastInvoiceYear)){
            var lastInvoiceLastDigits = lastInvoiceNumber.substring(lastInvoice.getNumber().lastIndexOf("/")+1);
            var lastNumber = Integer.parseInt(lastInvoiceLastDigits);
            return "VS"+ year+"/" + (lastNumber+1);
        }
        return "VS"+ year+"/" + "01";
    }

    public String generateDeliveryInvoiceNumber(){
        var year = String.valueOf(LocalDate.now().getYear()).substring(2,4);
        var repo = new InvoiceRepository();
        var lastInvoice = (Invoice)repo.getLastDeliveryInvoice();

        var lastInvoiceNumber = lastInvoice.getNumber();
        var lastInvoiceYear = lastInvoiceNumber.substring(2,4);
        if(year.equals(lastInvoiceYear)){
            var lastInvoiceLastDigits = lastInvoiceNumber.substring(lastInvoice.getNumber().lastIndexOf("/")+1);
            var lastNumber = Integer.parseInt(lastInvoiceLastDigits);
            return "PZ"+ year+"/" + (lastNumber+1);
        }
        return "PZ"+ year+"/" + "1";
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getStringDate(){
        return formatDate(this.date);
    }

    public String getStringDueDate(){
        return formatDate(this.dueDate);
    }





}