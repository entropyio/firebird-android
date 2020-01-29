package com.fb.firebird.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * 数据状态
 */
public enum RuleTypeEnum {
    INVALID(0, "无效"),
    BENEFIT(1, "收益率"),
    PRICE(2, "价格");

    @Getter
    private final int code;
    @Getter
    private final String desc;

    RuleTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private static Map<Integer, RuleTypeEnum> map = new HashMap<>();

    static {
        RuleTypeEnum[] types = RuleTypeEnum.values();
        for (int i = 0; i < types.length; i++) {
            map.put(types[i].getCode(), types[i]);
        }
    }

    public static RuleTypeEnum getEnum(int code) {
        return map.get(code);
    }

    public static String getStatusDesc(int code) {
        if (map.containsKey(code)) {
            return map.get(code).getDesc();
        }
        return "";
    }
}