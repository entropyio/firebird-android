package com.fb.firebird.json;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserTradeVO extends BaseVO implements Serializable {
    private double price;
    private double amount;
    private int type;
    private double holdPrice;
    private double holdAmount;
    private String reason;
}