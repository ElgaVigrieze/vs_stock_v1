package com.company.vs_stock.data.project;

import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

import static com.company.vs_stock.data.utilities.DateConverter.formatDate;

@Data
@Getter
@Setter
@Entity(name="Project")
@Table(name="project")
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", unique = true, nullable = false)
    private long id;
    @Column(name="title")
    private String title;
    @Column(name="location")
    private String location;
    @Column(name="date")
    private LocalDate date;
    @Column(name="description")
    private String description;
    @Column(name="gallery")
    private String pics;
    @OneToMany(cascade=CascadeType.REMOVE)
//    @OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name="stock_list", joinColumns=@JoinColumn(name="project_id"), inverseJoinColumns=@JoinColumn(name="item_id"))
    private List<StockListItem> items;
//    @Column(name="invoiced")
//    private boolean isInvoiced;
@Column(name="offer_valid")
private LocalDate offerValid;


    public Project(long id, String title, String location, LocalDate date, String description) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.description = description;
    }

    public Project(long id, String title, String location, LocalDate date, String description, List<StockListItem>items, String pics) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.description = description;
        this.items=items;
        this.pics=pics;
    }

    public Project(long id, String title, String location, LocalDate date, String description, List<StockListItem> items) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.description = description;
        this.items=items;
    }

    public void setItem(StockListItem item){
        items.add(item);
    }

    public List<StockListItem> getItems() {
        return items;
    }

    public String getValidDate(){
        return formatDate(getOfferValid());
    }
}