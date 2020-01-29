package com.fb.firebird.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * 数据状态
 */
public enum ScheduleStatusEnum {
    INVALID(0, "无效"),
    PROGRESSING(1, "进行中"),
    PAUSED(2, "已暂停"),
    FINISHED(3, "已终止");

    @Getter
    private final int code;
    @Getter
    private final String desc;

    ScheduleStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private static Map<Integer, ScheduleStatusEnum> map = new HashMap<>();

    static {
        ScheduleStatusEnum[] types = ScheduleStatusEnum.values();
        for (int i = 0; i < types.length; i++) {
            map.put(types[i].getCode(), types[i]);
        }
    }

    public static ScheduleStatusEnum getEnum(int code) {
        return map.get(code);
    }

    public static String getStatusDesc(int code) {
        if (map.containsKey(code)) {
            return map.get(code).getDesc();
        }
        return "";
    }
}