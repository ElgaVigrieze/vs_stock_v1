package com.company.vs_stock.data.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public enum DocType {
    SERVICE_INVOICE("pakalpojumu rēķins"),
    GOODS_INVOICE("preču rēķins"),
//    INVOICE_DELIVERY_INVOICE("Rēķins - pavadzīme"),
    DELIVERY_INVOICE("pavadzīme");

    public final String value;
    static final List<DocType> types = Arrays.asList(DocType.values());
    private DocType(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }



    public static DocType getType(String value){
        for (var t: types) {
            if(Objects.equals(t.getValue(), value)){
                return t;

            }
        }
        return null;
    }




}


