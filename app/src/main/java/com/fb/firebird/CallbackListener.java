package com.fb.firebird;

public interface CallbackListener {
    /**
     * dialog操作回调方法
     *
     * @param data   dialog返回数据
     * @param opType dialog操作类型
     */
    void successCallback(Object data, int opType);
}
