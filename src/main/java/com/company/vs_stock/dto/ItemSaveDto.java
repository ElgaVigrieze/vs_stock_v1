package com.company.vs_stock.dto;

import com.company.vs_stock.data.enums.Location;
import lombok.Data;

@Data
public class ItemSaveDto {
    private String category;
    private int id;
    private String name;
    private Float price;
    private String pic;
    private Location location;
    private int totalCount;
    private Float power;
    private Float length;
    private boolean isActive;

}
