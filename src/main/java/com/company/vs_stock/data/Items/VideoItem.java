package com.company.vs_stock.data.Items;

import com.company.vs_stock.data.enums.Category;
import com.company.vs_stock.data.enums.Location;
import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Data
@Entity
@DiscriminatorValue(value= Category.CatValues.VIDEO)
public class VideoItem extends Item {

    public VideoItem(long id, String name, Float price, String pic, boolean isActive, Location location, int totalCount) {
        super(id, name, price, pic, isActive, location, totalCount);
    }

    public VideoItem(long id, int quantity) {
        super(id, quantity);
    }

    public VideoItem() {
    }

    @Override
    public String getCategory() {
        return super.getCategory();
    }
}
