package com.fb.firebird;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fb.firebird.dialog.LoadingDialog;
import com.fb.firebird.dialog.LoginDialog;
import com.fb.firebird.enums.RetCodeEnum;
import com.fb.firebird.http.HttpCallback;
import com.fb.firebird.http.HttpUtil;
import com.fb.firebird.json.ResultData;
import com.fb.firebird.utils.AesUtil;
import com.fb.firebird.utils.FormatUtil;
import com.fb.firebird.utils.JsonUtil;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseActivity<T> extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    public long userId;
    public long symbolId;

    private LoadingDialog loadingDialog;

    private LoginDialog loginDialog;

    public Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = 1;
        symbolId = 0;

        // loading
        loadingDialog = new LoadingDialog(this, "正在加载...", R.drawable.ic_dialog_loading);
    }

    public void showLoading() {
        loadingDialog.show();
    }

    public void hideLoading() {
        if (null != loadingDialog && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public void httpPost(String url, Map<String, Object> paramsMap) {
        httpPost(url, paramsMap, null);
    }

    public void httpPost(String url, Map<String, Object> paramsMap, final String message) {
        showLoading();
        // todo: append userId, ts and token to params
        if (null == paramsMap) {
            paramsMap = new HashMap<>();
        }
        paramsMap.put("userId", 1); // get from login file
        paramsMap.put("ts", 1579760667789l);
        paramsMap.put("token", "6c1e5239c801e8f004ac5c68ad23bea4");

        String params = "q=" + AesUtil.encrypt(FormatUtil.getHttpParams(paramsMap));
        byte[] postData = params.getBytes();
        HttpUtil.getUtil().httpPost(url, postData,
                new HttpCallback<String>() {
                    @Override
                    public void onSuccess(final String response) {
                        Log.d(TAG, response);
                        Type type = getDataType();
                        final ResultData<T> jsonData = JsonUtil.JsonToObject(response, type);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // handle error code
                                if (jsonData.getRetCode() == RetCodeEnum.SUCCESS.getCode()) {
                                    if (null != message) {
                                        showMessage(message);
                                    } else {
                                        updateData(jsonData);
                                    }
                                } else if (jsonData.getRetCode() == RetCodeEnum.NEED_LOGIN.getCode()) {
                                    // do login
                                    doLogin();
                                } else {
                                    showMessage(jsonData.getMessage());
                                }
                                hideLoading();
                            }
                        });
                    }

                    @Override
                    public void onError(final String error) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showMessage(error);
                                hideLoading();
                            }
                        });
                    }
                });
    }

    /**
     * HTTP json数据解析类型，由子类返回bean类型
     *
     * @return
     */
    public abstract Type getDataType();

    /**
     * 更新子类UI数据
     *
     * @param data
     */
    public abstract void updateData(ResultData<T> data);

    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void setViewText(TextView view, double value, String append, double colorType) {
        String text = FormatUtil.formatNumber(value);
        if (null != append) {
            text += append;
        }
        view.setText(text);

        BigDecimal color = new BigDecimal(colorType);
        BigDecimal zero = new BigDecimal(0.0);
        if (color.compareTo(zero) > 0) {
            view.setTextColor(this.getResources().getColor(R.color.positive));
        } else if (color.compareTo(zero) < 0) {
            view.setTextColor(this.getResources().getColor(R.color.negative));
        }
    }

    /**
     * 显示登录对话框
     */
    public void doLogin() {
        Log.i(TAG, "need do authentication.");

        if (null == loginDialog) {
            loginDialog = new LoginDialog(this);
        }
        loginDialog.show();
    }

    public void doRefresh() {
        onResume();
    }
}
