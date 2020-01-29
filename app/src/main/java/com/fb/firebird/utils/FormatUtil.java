package com.fb.firebird.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class FormatUtil {
    public static String formatDateToMD(String str) {
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sf2 = new SimpleDateFormat("MM-dd");
        String formatStr = "";
        try {
            formatStr = sf2.format(sf1.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatStr;
    }

    public static String formatDateToYMD(String str) {
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd");
        String formatStr = "";
        try {
            formatStr = sf2.format(sf1.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatStr;
    }

    public static String formatNumber(double number) {
        return new java.text.DecimalFormat("###,###,##0.00").format(number);
    }

    public static String formatInputNumber(double number) {
        return new java.text.DecimalFormat("########0.00").format(number);
    }

    private static final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static String getDatetimeStr(Date date) {
        String formatStr = "";
        try {
            formatStr = sf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formatStr;
    }

    /**
     * 将map转为http get/post字符串参数
     *
     * @param params
     * @return
     */
    public static String getHttpParams(Map<String, Object> params) {
        StringBuffer sb = new StringBuffer();
        Set<Map.Entry<String, Object>> set = params.entrySet();
        for (Map.Entry<String, Object> entry : set) {
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1); //移除最后一个&符合
        return sb.toString();
    }
}
