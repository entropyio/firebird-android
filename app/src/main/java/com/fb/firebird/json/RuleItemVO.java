package com.fb.firebird.json;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RuleItemVO extends BaseVO implements Serializable {
    private long scheduleId;
    private int ruleType;
    private int joinType;
    private int opType;
    private String value;
}