package com.fb.firebird.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ScheduleItemData implements Serializable {
    private long userId;
    private long symbolId;
    private int symbolIcon;
    private String symbolGroup;
    private String symbolDesc;

    private long scheduleId;
    private String name;
    private int type;
    private double amount;
    private double total;
    private int success;
    private int failed;
    private int status;

    private List<RuleItemData> ruleList;
}
