package com.fb.firebird.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fb.firebird.BaseActivity;
import com.fb.firebird.R;
import com.fb.firebird.enums.RetCodeEnum;
import com.fb.firebird.http.HttpCallback;
import com.fb.firebird.http.HttpUtil;
import com.fb.firebird.json.ResultData;
import com.fb.firebird.model.LoginData;
import com.fb.firebird.utils.AesUtil;
import com.fb.firebird.utils.FirebirdUtil;
import com.fb.firebird.utils.JsonUtil;
import com.fb.firebird.utils.Md5Util;
import com.google.gson.reflect.TypeToken;

public class LoginDialog extends Dialog {
    private static final String TAG = "LoginDialog";
    private Context context;

    private TextView username;
    private TextView password;
    private TextView message;

    public LoginDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login);
        // 设置窗口大小
        // 设置窗口大小
        WindowManager windowManager = getWindow().getWindowManager();
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
//      attribute
        attributes.alpha = 1f;
        attributes.width = screenWidth - 100;
        attributes.height = 900;
        getWindow().setAttributes(attributes);
        setCancelable(false);

        username = findViewById(R.id.txt_username);
        password = findViewById(R.id.txt_password);
        message = findViewById(R.id.txt_message);
        message.setVisibility(View.INVISIBLE);

        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });

        Button btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 屏蔽返回键
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void doLogin() {
        String name = username.getText().toString();
        String pwd = Md5Util.md5(password.getText().toString());
        Log.d(TAG, "login with: " + name + ", " + pwd);

        String params = String.format("username=%s&pwd=%s", name, pwd);
        params = "q=" + AesUtil.encrypt(params);
        byte[] postData = params.getBytes();
        HttpUtil.getUtil().httpPost(FirebirdUtil.URL_USER_LOGIN, postData,
                new HttpCallback<String>() {
                    @Override
                    public void onSuccess(final String response) {
                        final ResultData<LoginData> jsonData = JsonUtil.JsonToObject(response,
                                new TypeToken<ResultData<LoginData>>() {
                                }.getType());
                        final BaseActivity base = (BaseActivity) context;
                        base.mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (jsonData.getRetCode() == RetCodeEnum.SUCCESS.getCode()) {
                                    base.doRefresh();
                                } else {
                                    Toast.makeText(context, jsonData.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(final String error) {
                        showMessage(error);
                    }
                });
    }

    private void showMessage(final String msg) {
        BaseActivity base = (BaseActivity) this.context;
        base.mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });

    }
}
