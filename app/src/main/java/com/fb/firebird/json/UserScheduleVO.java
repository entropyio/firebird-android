package com.fb.firebird.json;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserScheduleVO extends BaseVO implements Serializable {
    private String name;
    private int type;
    private double amount;
    private double total;
    private int failed;
    private int success;
    private List<RuleItemVO> ruleList;
}