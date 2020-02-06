package com.fb.firebird.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * 数据状态
 */
public enum StatusEnum {
    ALL(0, "无效"),
    ENABLE(1, "已启用"),
    DISABLE(2, "已禁用"),
    DELETED(3, "已删除");

    private static Map<Integer, StatusEnum> map = new HashMap<>();

    static {
        StatusEnum[] types = StatusEnum.values();
        for (int i = 0; i < types.length; i++) {
            map.put(types[i].getCode(), types[i]);
        }
    }

    @Getter
    private final int code;
    @Getter
    private final String desc;

    StatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static StatusEnum getEnum(int code) {
        return map.get(code);
    }

    public static String getStatusDesc(int code) {
        if (map.containsKey(code)) {
            return map.get(code).getDesc();
        }
        return "";
    }
}