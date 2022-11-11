package com.company.vs_stock.data.Items;

import com.company.vs_stock.data.enums.Category;
import com.company.vs_stock.data.enums.Location;
import lombok.Data;

import javax.persistence.*;


@Data
@Entity
@DiscriminatorValue(value= Category.CatValues.CABLE)
public class CableItem extends Item {
    public CableItem() {
    }

    public CableItem(long id, String name, Float price, String pic, boolean isActive, Location location, int totalCount) {
        super(id, name, price, pic, isActive, location, totalCount);
    }

    public CableItem(long id, int quantity) {
        super(id, quantity);
    }

    @Override
    public String getCategory() {
        return super.getCategory();
    }
}

