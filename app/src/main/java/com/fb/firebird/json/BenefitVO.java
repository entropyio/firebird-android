package com.fb.firebird.json;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BenefitVO extends BaseVO implements Serializable {
    private double openPrice;
    private double closePrice;
    private double highPrice;
    private double lowPrice;
    private double holdPrice;
    private double holdAmount;
    private double holdRate;
    private double holdBenefit;
}
