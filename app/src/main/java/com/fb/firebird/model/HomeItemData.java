package com.fb.firebird.model;


import java.io.Serializable;

import lombok.Data;

@Data
public class HomeItemData implements Serializable {
    private long userId;
    private long symbolId;

    private int symbolIcon;
    private String symbolName;
    private String symbolDesc;
    private String symbolGroup;

    private double holdPrice;
    private double holdAmount;
    private double price;
    private double rate;
    private double benefit;
    private double yestBenefit;
}
