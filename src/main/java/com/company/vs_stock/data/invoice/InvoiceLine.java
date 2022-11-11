package com.company.vs_stock.data.invoice;

import com.company.vs_stock.data.ItemRepository;
import com.company.vs_stock.data.ProjectRepository;
import com.company.vs_stock.data.project.Project;
import lombok.*;

import javax.persistence.*;
@Data
@Getter
@Setter
@Entity(name="InvoiceLine")
@Table(name="invoice_line")
@NoArgsConstructor
public class InvoiceLine {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", unique = true, nullable = false)
    private long id;
    @ManyToOne(targetEntity=Invoice.class)
    @JoinColumn(name="invoice_id")
    private Invoice invoice;
    @Column(name="description")
    private String description;
    @Column(name="price")
    private float price;
    @Column(name="quantity")
    private int quantity;
    @Column(name="sum")
    private float sum;
    @OneToOne
    @JoinColumn(name = "project_id")
    private Project project;
    @OneToOne
    @JoinColumn(name = "base_invoice_id")
    Invoice baseInvoice;

    public InvoiceLine(long id, Invoice invoice, String description, float price, int quantity, float sum, Project project) {
        this.id = id;
        this.invoice = invoice;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.sum = sum;
        this.project=project;
    }

    public InvoiceLine(long id, Invoice invoice, String description, int quantity, Project project) {
        this.id = id;
        this.invoice = invoice;
        this.description = description;
        this.quantity = quantity;
        this.project=project;
    }

    public InvoiceLine(int i, Invoice invoice, String description, float price, int quantity, float sum, Project project, Invoice baseInvoice) {
        this.id = id;
        this.invoice = invoice;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.sum = sum;
        this.project=project;
        this.baseInvoice = baseInvoice;
       }

    public float fetchSum() {
        var repo = new ItemRepository();
        var repo1 = new ProjectRepository();
        var items = repo1.getStockListItemsSorted(project);
        return repo.getProjectSum(items);
    }


}
