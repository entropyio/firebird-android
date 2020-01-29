package com.fb.firebird.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public enum RetCodeEnum {
    FAILED(-1, "失败"),
    SUCCESS(0, "成功"),

    NEED_LOGIN(10, "待认证");

    @Getter
    private final int code;
    @Getter
    private final String desc;

    RetCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private static Map<Integer, RetCodeEnum> map = new HashMap<>();

    static {
        RetCodeEnum[] types = RetCodeEnum.values();
        for (int i = 0; i < types.length; i++) {
            map.put(types[i].getCode(), types[i]);
        }
    }

    public static RetCodeEnum getEnum(int code) {
        return map.get(code);
    }

    public static String getStatusDesc(int code) {
        if (map.containsKey(code)) {
            return map.get(code).getDesc();
        }
        return "";
    }
}
