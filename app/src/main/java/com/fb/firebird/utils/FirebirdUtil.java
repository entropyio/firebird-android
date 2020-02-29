package com.fb.firebird.utils;

public class FirebirdUtil {
    public static final boolean isDebug = false;

    public static String HTTP_SERVER = "http://192.168.1.101:8080";

    public static final int DEFAULT_PAGE_NUMBER = 1;

    public static final int OPT_ADD = 1;
    public static final int OPT_UPDATE = 2;
    public static final int OPT_DELETE = 3;

    // APIs
    public static final String URL_USER_LOGIN = "/user/login";
    public static final String URL_USER_LOGOUT = "/user/logout";

    public static final String URL_ACCOUNT_LIST = "/current/account/list";
    public static final String URL_ACCOUNT_DETAIL = "/current/account/get";

    public static final String URL_TRADE_LIST = "/user/trade/list";
    public static final String URL_TRADE_SAVE = "/user/trade/save";

    public static final String URL_SCHEDULE_LIST = "/user/schedule/list";
    public static final String URL_SCHEDULE_SAVE = "/user/schedule/save";
    public static final String URL_SCHEDULE_RULES = "/user/rule/list";

    public static final String URL_DATA_LIST = "/user/data/list";
}
