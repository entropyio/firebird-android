package com.fb.firebird.http;

public interface HttpCallback<T> {
    void onSuccess(T response);

    void onError(T error);
}
