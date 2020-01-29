package com.fb.firebird.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class RuleItemData implements Serializable {
    private long id;
    private long userId;
    private long symbolId;
    private long scheduleId;
    private int ruleType;
    private int joinType;
    private int opType;
    private String opValue;
}
