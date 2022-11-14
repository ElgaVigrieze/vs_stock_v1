package com.company.vs_stock.dto;

import lombok.*;

import java.time.LocalDate;
@Data
@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectSaveDto {
    private String title;
    private String location;
    private String date;
    private String description;
    private String validDate;
}
