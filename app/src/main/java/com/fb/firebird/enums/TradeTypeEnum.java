package com.fb.firebird.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * 数据状态
 */
public enum TradeTypeEnum {
    ALL(0, "无效"),
    BUY(1, "买进"),
    SOLD(2, "卖出");

    private static Map<Integer, TradeTypeEnum> map = new HashMap<>();

    static {
        TradeTypeEnum[] types = TradeTypeEnum.values();
        for (int i = 0; i < types.length; i++) {
            map.put(types[i].getCode(), types[i]);
        }
    }

    @Getter
    private final int code;
    @Getter
    private final String desc;

    TradeTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TradeTypeEnum getEnum(int code) {
        return map.get(code);
    }

    public static String getStatusDesc(int code) {
        if (map.containsKey(code)) {
            return map.get(code).getDesc();
        }
        return "";
    }
}