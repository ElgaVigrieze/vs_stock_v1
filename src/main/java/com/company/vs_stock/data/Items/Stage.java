package com.company.vs_stock.data.Items;

import com.company.vs_stock.data.enums.Location;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
//@DiscriminatorValue(value= Category.CatValues.STAGE)
public class Stage extends Item {

    public Stage(long id, String name, Float price, String pic, boolean isActive, Location location, int totalCount) {
        super(id, name, price, pic, isActive, location, totalCount);
    }

    public Stage(long id, String name, Float price, boolean isActive, Location location, int totalCount) {
        super(id, name, price,  isActive, location, totalCount);
    }


    public Stage(long id, int quantity) {
        super(id, quantity);
    }

    public Stage() {
    }



    @Override
    public String getCategory() {
        return super.getCategory();
    }
}

