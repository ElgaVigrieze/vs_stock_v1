package com.company.vs_stock.data.Items;

import com.company.vs_stock.data.enums.Location;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity

public class Other extends Item {

    public Other(long id, String name, Float price, String pic, boolean isActive, Location location, int totalCount) {
        super(id, name, price, pic, isActive, location, totalCount);
    }

    public Other(long id, String name, Float price, boolean isActive, Location location, int totalCount) {
        super(id, name, price,  isActive, location, totalCount);
    }


    public Other(long id, int quantity) {
        super(id, quantity);
    }

    public Other() {
    }



    @Override
    public String getCategory() {
        return super.getCategory();
    }
}

