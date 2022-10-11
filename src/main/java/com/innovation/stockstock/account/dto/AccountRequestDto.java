package com.innovation.stockstock.account.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequestDto {

    private String deposit;
    private float targetReturnRate;
    private Long expireAt;

}
