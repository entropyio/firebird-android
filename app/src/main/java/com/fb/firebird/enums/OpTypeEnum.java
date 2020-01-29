package com.fb.firebird.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * 数据状态
 */
public enum OpTypeEnum {
    INVALID(0, "无效"),
    EQUAL(1, "等于"),
    NOT_EQUAL(2, "不等于"),
    MORE_THAN(3, "大于"),
    MORE_EQUAL(4, "大于等于"),
    LESS_THAN(5, "小于"),
    LESS_EQUAL(6, "小于等于"),
    CONTAIN(7, "包含"),
    NOT_CONTAIN(8, "不包含");

    @Getter
    private final int code;
    @Getter
    private final String desc;

    OpTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private static Map<Integer, OpTypeEnum> map = new HashMap<>();

    static {
        OpTypeEnum[] types = OpTypeEnum.values();
        for (int i = 0; i < types.length; i++) {
            map.put(types[i].getCode(), types[i]);
        }
    }

    public static OpTypeEnum getEnum(int code) {
        return map.get(code);
    }

    public static String getStatusDesc(int code) {
        if (map.containsKey(code)) {
            return map.get(code).getDesc();
        }
        return "";
    }
}