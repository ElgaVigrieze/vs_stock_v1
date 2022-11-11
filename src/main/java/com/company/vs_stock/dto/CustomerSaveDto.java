package com.company.vs_stock.dto;

import lombok.*;

@Data
@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerSaveDto {
    private String name;
    private String vatNumber;
    private String billingAddress;
//    private String deliveryAddress;
    private String bank;
    private String swift;
    private String accountNumber;
    private String eMail;
    private String phoneNumber;
}
