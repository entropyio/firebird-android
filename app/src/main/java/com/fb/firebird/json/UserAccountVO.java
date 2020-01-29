package com.fb.firebird.json;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserAccountVO extends BaseVO implements Serializable {
    private double holdPrice;       // 持有价格
    private double holdAmount;      // 持有数量
    private double yestBenefit;     // 昨日收益，通过close价格计算
    private double totalBenefit;    // 累计收益
    private double price;           // 当前价格
    private double amount;          // 当前数量
    private double total;           // 当前总量 = price * amount
    private double benefit;         // 当前收益 = (price - holdPrice) * holdAmount
    private double rate;            // 当前收益率 = (price - holdPrice) * 100 / holdPrice
}