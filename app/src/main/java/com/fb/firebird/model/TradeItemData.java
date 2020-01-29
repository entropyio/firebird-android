package com.fb.firebird.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class TradeItemData implements Serializable {
    private int type;
    private String name;
    private int icon;
    private double price;
    private double amount;
    private String time;
    private int status;
}
