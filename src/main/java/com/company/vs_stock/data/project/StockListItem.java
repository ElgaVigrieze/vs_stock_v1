package com.company.vs_stock.data.project;

import com.company.vs_stock.data.project.Project;
import lombok.*;

import javax.persistence.*;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name ="StockListItem")
@Table(name ="stock_list")
public class StockListItem {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private long id;
    @Column(name="item_id")
    private long itemId;
    @Column(name="item_price")
    private Float itemPrice;
    @Column(name="item_quantity")
    private Integer itemQuantity;
    @Column(name="item_done")
    private boolean itemDone;
    @ManyToOne(cascade=CascadeType.ALL)
    public Project project;


}


