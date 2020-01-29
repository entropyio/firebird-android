package com.fb.firebird.utils;

public class FirebirdUtil {
    public static final boolean isDebug = true;

    public static final String HOST_URL = "http://192.168.1.103:8080";

    public static final int DEFAULT_PAGE_NUMBER = 1;

    public static final int OPT_ADD = 1;
    public static final int OPT_UPDATE = 2;
    public static final int OPT_DELETE = 3;

    // APIs
    public static final String URL_USER_LOGIN = HOST_URL + "/user/login";
    public static final String URL_USER_LOGOUT = HOST_URL + "/user/logout";

    public static final String URL_ACCOUNT_LIST = HOST_URL + "/current/account/list";
    public static final String URL_ACCOUNT_DETAIL = HOST_URL + "/current/account/get";

    public static final String URL_TRADE_LIST = HOST_URL + "/user/trade/list";
    public static final String URL_TRADE_SAVE = HOST_URL + "/user/trade/save";

    public static final String URL_SCHEDULE_LIST = HOST_URL + "/user/schedule/list";
    public static final String URL_SCHEDULE_SAVE = HOST_URL + "/user/schedule/save";

    public static final String URL_DATA_LIST = HOST_URL + "/user/data/list";
}
