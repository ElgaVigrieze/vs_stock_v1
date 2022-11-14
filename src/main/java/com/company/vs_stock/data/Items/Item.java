package com.company.vs_stock.data.Items;

import com.company.vs_stock.data.enums.Category;
import com.company.vs_stock.data.utilities.ImageDisplay;
import com.company.vs_stock.data.enums.Location;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.IOException;
import java.util.Objects;

import static com.company.vs_stock.data.utilities.ImageDisplay.defaultPic;
import static com.company.vs_stock.data.utilities.ImageDisplay.uploadPathItem;

@Data
@NoArgsConstructor

@Entity
@Table(name="items")
@DiscriminatorColumn(name="category", discriminatorType=DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Item {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private String name;
    @Column(name="price")
    private Float price;
    @Column(name="pic")
    private String pic;
    @Column(name="active")   //piemēram, remonts
    boolean isActive;
    @Enumerated(EnumType.STRING)
    private Location location;
    @Column(name="total_count")
    private int totalCount;
    @Column (name="category", insertable = false, updatable = false)
    protected String category;
    @Transient
    private int quantity;
    @Transient
    private boolean done;
    @Transient
    private Float originalPrice;

    public Item(long id, String name, Float price, String  pic, boolean isActive, Location location, int totalCount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.pic = pic;
        this.isActive = isActive;
        this.location = location;
        this.totalCount = totalCount;

    }

    public Item(long id, String name, Float price, String  pic, boolean isActive, Category category, Location location, int totalCount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.pic = pic;
        this.isActive = isActive;
        this.category = category.getLabel();
        this.location = location;
        this.totalCount = totalCount;

    }

    public Item(long id, String name, Float price, boolean isActive, Location location, int totalCount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.isActive = isActive;
        this.location = location;
        this.totalCount = totalCount;

    }

    public Item(long id, String name, Float price, boolean isActive, Location location, Category category, int totalCount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.isActive = isActive;
        this.location = location;
        this.category = category.getLabel();
        this.totalCount = totalCount;

    }


    public Item(long id, int quantity) {
        this.id = id;
        this.quantity = quantity;

    }

    public Item(long id, String name, int quantity, String category, Float price) {
        this.id = id;
        this.name = name;

        this.quantity = quantity;
        this.category = category;
        this.price = price;

    }

    public Item(long id, String name, String  pic, Float originalPrice, Integer totalCount, int quantity, Float price, String category, boolean done) {
        this.id = id;
        this.name = name;
        this.pic = pic;
        this.originalPrice=originalPrice;
        this.totalCount = totalCount;
        this.quantity = quantity;
        this.price=price;
        this.category = category;
        this.done = done;
    }

    public String getPicEncoded() throws IOException {
        if(Objects.equals(this.pic, defaultPic)){
            return defaultPic;
        }
        var image = new ImageDisplay();
        return image.displayImageFromPath(uploadPathItem+ this.pic);
    }

}
