package com.company.vs_stock.dto;

import lombok.Data;

import java.util.List;

@Data
public class InvoiceSaveDto {
    private String type;
    private String date;
    private String dueDate;
    private long customerId;
    private String comment;
    private List<Integer> paid;

}
