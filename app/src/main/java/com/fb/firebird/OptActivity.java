package com.fb.firebird;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
    private EditText editorPrice;
    private EditText editorAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opt);

        Intent intent = getIntent();
        item = (HomeItemData) intent.getSerializableExtra("item");
        opType = intent.getIntExtra("opType", 0);

        editorPrice = this.findViewById(R.id.editText_price);
        editorAmount = this.findViewById(R.id.editText_amount);

        editorPrice.setText(FormatUtil.formatInputNumber(item.getPrice()));

        Button btnOpt = this.findViewById(R.id.btn_save);
        if (opType == TradeTypeEnum.BUY.getCode()) {
            this.setTitle("买入" + item.getSymbolDesc());
            btnOpt.setText("买入");
        } else if (opType == TradeTypeEnum.SOLD.getCode()) {
            this.setTitle("卖出" + item.getSymbolDesc());
            btnOpt.setText("卖出");
            editorAmount.setHint(FormatUtil.formatInputNumber(item.getHoldAmount()));
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
        paramsMap.put("userId", userId);
        paramsMap.put("symbolId", symbolId);
        paramsMap.put("status", TradeStatusEnum.SUCCESS.getCode());
        paramsMap.put("price", price);
        paramsMap.put("amount", amount);
        paramsMap.put("type", opType);

        httpPost(FirebirdUtil.URL_TRADE_SAVE, paramsMap);
    }

    @Override
    public Type getDataType() {
        return new TypeToken<String>() {
        }.getType();
    }

    @Override
    public void updateData(ResultData<String> data) {
        showMessage("操作成功");
        OptActivity.this.finish();
    }
}
