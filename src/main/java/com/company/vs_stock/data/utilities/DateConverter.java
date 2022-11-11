package com.company.vs_stock.data.utilities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateConverter {
    public static LocalDate convertStringToDate(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);
    }

    public static String formatDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
        return formatter.format(date);
    }


}
