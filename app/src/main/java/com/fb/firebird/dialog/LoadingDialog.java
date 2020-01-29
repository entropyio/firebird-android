package com.fb.firebird.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.fb.firebird.R;

public class LoadingDialog extends Dialog {
    private static final String TAG = "LoadingDialog";

    private String message;
    private int mImageId;
    private boolean mCancelable;
    private RotateAnimation mRotateAnimation;
    private ImageView loadingImg;
    private TextView loadingText;

    public LoadingDialog(@NonNull Context context, String message, int imageId) {
        this(context, R.style.LoadingDialog, message, imageId, false);
    }

    public LoadingDialog(@NonNull Context context, int themeResId, String message, int imageId, boolean cancelable) {
        super(context, themeResId);
        this.message = message;
        this.mImageId = imageId;
        this.mCancelable = cancelable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_loading);
        // 设置窗口大小
        WindowManager windowManager = getWindow().getWindowManager();
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha = 0.3f;
        attributes.width = screenWidth / 3;
        attributes.height = attributes.width;
        getWindow().setAttributes(attributes);
        setCancelable(mCancelable);

        loadingText = findViewById(R.id.tv_loading);
        loadingText.setText(this.message);

        loadingImg = findViewById(R.id.iv_loading);
        loadingImg.setImageResource(mImageId);
        loadingImg.measure(0, 0);
        mRotateAnimation = new RotateAnimation(0, 360, loadingImg.getMeasuredWidth() / 2, loadingImg.getMeasuredHeight() / 2);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(1000);
        mRotateAnimation.setRepeatCount(-1);
    }

    @Override
    public void show() {
        this.show(this.message);
    }

    public void show(String message) {
        super.show();
        loadingText.setText(message);
        loadingImg.startAnimation(mRotateAnimation);
    }

    @Override
    public void dismiss() {
        mRotateAnimation.cancel();
        super.dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 屏蔽返回键
            return mCancelable;
        }
        return super.onKeyDown(keyCode, event);
    }
}
