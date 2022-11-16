package com.company.vs_stock.data;

import lombok.*;

import javax.persistence.*;
@Data
@Getter
@Setter
@Entity(name="Customer")
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", unique = true, nullable = false)
    private long id;
    @Column(name="name")
    private String name;
    @Column(name="vat_number")
    private String vatNumber;
    @Column(name="billing_address")
    private String billingAddress;
//    @Column(name="delivery_address")
//    private String deliveryAddress;
    @Column(name="bank")
    private String bank;
    @Column(name="swift")
    private String swift;
    @Column(name="account_number")
    private String accountNumber;
    @Column(name="e_mail")
    private String eMail;
    @Column(name="phone_number")
    private String phoneNumber;
    @Column(name="contact")
    private String contact;

    public double getPercentageOfTurnover(long customerId){
        InvoiceRepository repo = new InvoiceRepository();
        return Math.round((repo.getSumOfAllInvoicesPerCustomer(customerId)/repo.getTotalTurnover()*100.0)* 100.0)/100.0;
    }

    public double getSumOfAllInvoicesPerCustomer(long customerId){
        InvoiceRepository repo = new InvoiceRepository();
        return Math.round(repo.getSumOfAllInvoicesPerCustomer(customerId)* 100.0)/100.0;
    }

    public double getPercentageOfTurnoverPerYear(long customerId, int year){
        InvoiceRepository repo = new InvoiceRepository();
        return Math.round((repo.getSumOfAllInvoicesPerCustomerPerYear(customerId, year)/repo.getTotalTurnoverPerYear(year)*100.0)* 100.0)/100.0;
    }

    public double getSumOfAllInvoicesPerCustomerPerYear(long customerId, int year){
        InvoiceRepository repo = new InvoiceRepository();
        return Math.round(repo.getSumOfAllInvoicesPerCustomerPerYear(customerId, year)* 100.0)/100.0;
    }


}