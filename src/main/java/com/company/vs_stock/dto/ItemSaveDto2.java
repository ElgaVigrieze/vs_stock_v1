package com.company.vs_stock.dto;

import lombok.Data;

import java.util.List;

@Data
public class ItemSaveDto2 {
    private List<Integer> id;
//    private List<Integer> check;
    private List<Integer> quantity;
    private List<Float> price;
    private List<Integer> done;

}
