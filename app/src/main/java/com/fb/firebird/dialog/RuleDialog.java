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
import com.fb.firebird.model.RuleItemData;
import com.fb.firebird.utils.FirebirdUtil;

public class RuleDialog extends Dialog {
    private static final String TAG = "RuleDialog";

    private CallbackListener listener;

    private String title;
    private RuleItemData itemData;

    private TextView titleView;
    private Spinner joinType;
    private Spinner ruleType;
    private Spinner opType;
    private EditText opValue;

    private Button btnSave;
    private Button btnDelete;

    public RuleDialog(@NonNull Context context, String title) {
        super(context);
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_rule);
        // 设置窗口大小
        WindowManager windowManager = getWindow().getWindowManager();
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
//      attribute
        attributes.alpha = 1f;
        attributes.width = screenWidth - 100;
        attributes.height = 1000;
        getWindow().setAttributes(attributes);
        setCancelable(false);

        titleView = findViewById(R.id.dialog_title);
        joinType = findViewById(R.id.trade_type);
        ruleType = findViewById(R.id.rule_type);
        opType = findViewById(R.id.op_type);
        opValue = findViewById(R.id.op_amount);

        ImageView close = findViewById(R.id.img_header_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "do save:" + itemData.toString());
                itemData.setJoinType(joinType.getSelectedItemPosition() + 1);
                itemData.setRuleType(ruleType.getSelectedItemPosition() + 1);
                itemData.setOpType(opType.getSelectedItemPosition() + 1);
                itemData.setOpValue(opValue.getText().toString());

                int opType = itemData.getId() > 0 ? FirebirdUtil.OPT_UPDATE : FirebirdUtil.OPT_ADD;
                if (null != listener) {
                    listener.successCallback(itemData, opType);
                }
                dismiss();
            }
        });

        btnDelete = findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "do delete:" + itemData.toString());
                if (null != listener) {
                    listener.successCallback(itemData, FirebirdUtil.OPT_DELETE);
                }
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
        joinType.setSelection(itemData.getJoinType() - 1, true);
        ruleType.setSelection(itemData.getRuleType() - 1, true);
        opType.setSelection(itemData.getOpType() - 1, true);
        opValue.setText(itemData.getOpValue());
        if (itemData.getId() > 0) {
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.INVISIBLE);
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            // 屏蔽返回键
//            return false;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    public void setItemData(RuleItemData itemData) {
        this.itemData = itemData;
    }

    public void setCallbackListener(CallbackListener listener) {
        this.listener = listener;
    }
}
