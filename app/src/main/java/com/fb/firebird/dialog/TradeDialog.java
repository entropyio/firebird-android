package com.fb.firebird.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.fb.firebird.CallbackListener;
import com.fb.firebird.R;
import com.fb.firebird.enums.TradeStatusEnum;
import com.fb.firebird.json.UserTradeVO;
import com.fb.firebird.model.HomeItemData;
import com.fb.firebird.utils.FormatUtil;

import java.util.Date;

public class TradeDialog extends Dialog {
    private static final String TAG = "TradeDialog";

    private CallbackListener listener;

    private String title;
    private HomeItemData itemData;

    private TextView titleView;
    private Spinner tradeType;
    private EditText opPrice;
    private EditText opAmount;
    private EditText opReason;
    private EditText opDate;

    public TradeDialog(@NonNull Context context, String title, HomeItemData itemData) {
        super(context);
        this.title = title;
        this.itemData = itemData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_trade);
        // 设置窗口大小
        WindowManager windowManager = getWindow().getWindowManager();
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
//      attribute
        attributes.alpha = 1f;
        attributes.width = screenWidth - 100;
        attributes.height = 1200;
        getWindow().setAttributes(attributes);
        setCancelable(false);

        titleView = findViewById(R.id.dialog_title);
        tradeType = findViewById(R.id.trade_type);
        opPrice = findViewById(R.id.op_price);
        opAmount = findViewById(R.id.op_amount);
        opReason = findViewById(R.id.op_reason);
        opDate = findViewById(R.id.op_date);

        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserTradeVO trade = new UserTradeVO();
                trade.setUserId(itemData.getUserId());
                trade.setSymbolId(itemData.getSymbolId());
                trade.setType(tradeType.getSelectedItemPosition() + 1);
                trade.setPrice(Double.parseDouble(opPrice.getText().toString()));
                trade.setAmount(Double.parseDouble(opAmount.getText().toString()));
                trade.setReason(opReason.getText().toString());
                trade.setGmtCreate(opDate.getText().toString());
                trade.setGmtModified(opDate.getText().toString());
                trade.setStatus(TradeStatusEnum.SUCCESS.getCode());

                Log.d("TAG", "do save:" + trade.toString());

                if (null != listener) {
                    listener.successCallback(trade, 0);
                }
                dismiss();
            }
        });

        Button btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        ImageView close = findViewById(R.id.img_header_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        Log.d(TAG, "show rule dialog");
        super.show();

        // update itemdata
        titleView.setText(title);
        if (null != this.itemData) {
            tradeType.setSelection(0, true);
            opPrice.setText(String.valueOf(itemData.getPrice()));
            opAmount.setText(String.valueOf(itemData.getHoldAmount()));
            opReason.setText("手动添加");
            opDate.setText(FormatUtil.getDatetimeStr(new Date()));
        }
    }

    public void setItemData(HomeItemData itemData) {
        this.itemData = itemData;
    }

    public void setCallbackListener(CallbackListener listener) {
        this.listener = listener;
    }
}
