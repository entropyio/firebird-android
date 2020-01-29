package com.fb.firebird.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * 数据状态
 */
public enum TradeStatusEnum {
    ALL(0, "无效"),
    PROGRESSING(1, "进行中"),
    SUCCESS(2, "已完成"),
    FAILED(3, "已失败");

    @Getter
    private final int code;
    @Getter
    private final String desc;

    TradeStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private static Map<Integer, TradeStatusEnum> map = new HashMap<>();

    static {
        TradeStatusEnum[] types = TradeStatusEnum.values();
        for (int i = 0; i < types.length; i++) {
            map.put(types[i].getCode(), types[i]);
        }
    }

    public static TradeStatusEnum getEnum(int code) {
        return map.get(code);
    }

    public static String getStatusDesc(int code) {
        if (map.containsKey(code)) {
            return map.get(code).getDesc();
        }
        return "";
    }
}