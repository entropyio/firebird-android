package com.fb.firebird.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * 数据状态
 */
public enum JoinTypeEnum {
    AND(1, "并且"),
    OR(2, "或者");

    private static Map<Integer, JoinTypeEnum> map = new HashMap<>();

    static {
        JoinTypeEnum[] types = JoinTypeEnum.values();
        for (int i = 0; i < types.length; i++) {
            map.put(types[i].getCode(), types[i]);
        }
    }

    @Getter
    private final int code;
    @Getter
    private final String desc;

    JoinTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static JoinTypeEnum getEnum(int code) {
        return map.get(code);
    }

    public static String getStatusDesc(int code) {
        if (map.containsKey(code)) {
            return map.get(code).getDesc();
        }
        return "";
    }
}