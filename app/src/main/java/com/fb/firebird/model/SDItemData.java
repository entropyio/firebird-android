package com.fb.firebird.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class SDItemData implements Serializable {
    private String time;
    private Double amount;
    private Double price;
    private int status;
    private String reason;
}
