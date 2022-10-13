package com.innovation.stockstock.account.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequestDto {

    private int deposit;
    private float targetReturnRate;
    private int expireAt;

}
