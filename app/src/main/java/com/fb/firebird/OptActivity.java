package com.fb.firebird;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fb.firebird.enums.TradeStatusEnum;
import com.fb.firebird.enums.TradeTypeEnum;
import com.fb.firebird.json.ResultData;
import com.fb.firebird.model.HomeItemData;
import com.fb.firebird.utils.FirebirdUtil;
import com.fb.firebird.utils.FormatUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class OptActivity extends BaseActivity<String> {
    private static final String TAG = OptActivity.class.getSimpleName();

    private HomeItemData item;
    private int opType;
    private double rate;

    private EditText editorPrice;
    private EditText editorAmount;
    private EditText editorRate;
    private TextView textAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opt);

        Intent intent = getIntent();
        item = (HomeItemData) intent.getSerializableExtra("item");
        opType = intent.getIntExtra("opType", 0);
        rate = 0.0;

        editorPrice = this.findViewById(R.id.editText_price);
        editorAmount = this.findViewById(R.id.editText_amount);
        editorRate = this.findViewById(R.id.editText_rate);
        textAmount = this.findViewById(R.id.text_amount);

        editorPrice.setText(FormatUtil.formatInputNumber(item.getPrice(), 4));

        Button btnRateSub = this.findViewById(R.id.btn_rate_sub);
        btnRateSub.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                doUpdateRate(-1.0);
            }
        });
        Button btnRateAdd = this.findViewById(R.id.btn_rate_add);
        btnRateAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                doUpdateRate(1.0);
            }
        });

        Button btnOpt = this.findViewById(R.id.btn_save);
        if (opType == TradeTypeEnum.BUY.getCode()) {
            this.setTitle("买入" + item.getSymbolDesc());
            btnOpt.setText("买入");
        } else if (opType == TradeTypeEnum.SOLD.getCode()) {
            this.setTitle("卖出" + item.getSymbolDesc());
            btnOpt.setText("卖出");
            editorAmount.setHint(FormatUtil.formatInputNumber(item.getHoldAmount(), 4));
        }
        btnOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doOperation();
            }
        });

        Button btnCancel = this.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OptActivity.this.finish();
            }
        });

        Log.d(TAG, item.getSymbolName());
    }

    private void doUpdateRate(double addRate){
        try {
            rate = Double.parseDouble(editorRate.getText().toString());
        } catch (Exception e) {
            rate = 0.0;
        }
        rate += addRate;
        editorRate.setText(FormatUtil.formatNumber(rate));

        double price = item.getPrice() * (1 + rate/100);
        editorPrice.setText(FormatUtil.formatInputNumber(price, 4));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isHideInput(v, ev)) { //需要隐藏软键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                // 计算总金额
                double price = 0.0;
                double amount = 0.0;
                try {
                    price = Double.parseDouble(editorPrice.getText().toString());
                    amount = Double.parseDouble(editorAmount.getText().toString());
                } catch (Exception e) {
                    // do nothing
                }

                textAmount.setText(FormatUtil.formatNumber(price * amount));
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    private boolean isHideInput(View v, MotionEvent event) {
        if (v instanceof EditText) { //如果点击的view是EditText
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            // 点击的是输入框区域，保留点击EditText的事件
            return !(event.getX() > left) || !(event.getX() < right)
                    || !(event.getY() > top) || !(event.getY() < bottom);
        }
        return false;
    }


    public void doOperation() {
        if (opType != TradeTypeEnum.BUY.getCode()
                && opType != TradeTypeEnum.SOLD.getCode()) {
            return;
        }

        double price = 0.0;
        double amount = 0.0;
        try {
            price = Double.parseDouble(editorPrice.getText().toString());
            amount = Double.parseDouble(editorAmount.getText().toString());
        } catch (Exception e) {
            // do nothing
        }

        Log.i(TAG, "price=" + price + ", amount=" + amount);
        if (amount * price < 0.000001) {
            showMessage("请输入价格和数量");
            return;
        }

        // send request
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userId", item.getUserId());
        paramsMap.put("symbolId", item.getSymbolId());
        paramsMap.put("status", TradeStatusEnum.SUCCESS.getCode());
        paramsMap.put("price", price);
        paramsMap.put("amount", amount);
        paramsMap.put("type", opType);

        httpPost(FirebirdUtil.HTTP_SERVER+FirebirdUtil.URL_TRADE_SAVE, paramsMap);
    }

    @Override
    public Type getDataType() {
        return new TypeToken<ResultData<String>>() {
        }.getType();
    }

    @Override
    public void updateData(ResultData<String> data) {
        showMessage("操作成功");
        OptActivity.this.finish();
    }
}
