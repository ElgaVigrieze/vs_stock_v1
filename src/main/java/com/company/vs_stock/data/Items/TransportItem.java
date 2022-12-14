package com.company.vs_stock.data.Items;

import com.company.vs_stock.data.enums.Category;
import com.company.vs_stock.data.enums.Location;
import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Data
@Entity
@DiscriminatorValue(value= Category.CatValues.TRANSPORT)
public class TransportItem extends Other {

    public TransportItem(long id, String name, Float price, String pic, boolean isActive, Location location, int totalCount) {
        super(id, name, price, pic, isActive, location, totalCount);
    }

    public TransportItem(long id, int quantity) {
        super(id, quantity);
    }

    public TransportItem() {
    }

    @Override
    public String getCategory() {
        return super.getCategory();
    }
}

