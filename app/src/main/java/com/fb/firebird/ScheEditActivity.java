package com.fb.firebird;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.fb.firebird.dialog.RuleDialog;
import com.fb.firebird.enums.JoinTypeEnum;
import com.fb.firebird.enums.OpTypeEnum;
import com.fb.firebird.enums.RuleTypeEnum;
import com.fb.firebird.enums.TradeTypeEnum;
import com.fb.firebird.http.HttpCallback;
import com.fb.firebird.http.HttpUtil;
import com.fb.firebird.json.ResultData;
import com.fb.firebird.model.RuleItemData;
import com.fb.firebird.model.ScheduleItemData;
import com.fb.firebird.utils.AesUtil;
import com.fb.firebird.utils.FirebirdUtil;
import com.fb.firebird.utils.FormatUtil;
import com.fb.firebird.utils.IconUtil;
import com.fb.firebird.utils.JsonUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheEditActivity extends BaseActivity<String> implements CallbackListener {
    private static final String TAG = ScheEditActivity.class.getSimpleName();

    private ScheduleItemData item;

    private RuleListAdapter adapter;

    private RuleDialog ruleDialog;

    // edit item
    private EditText scheName;
    private EditText scheAmount;
    private RadioButton radioBuy;
    private RadioButton radioSold;
    private Spinner scheStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sd_edit);

        Intent intent = getIntent();
        item = (ScheduleItemData) intent.getSerializableExtra("item");
        if (null == item) {
            item = new ScheduleItemData();
        }
        if (null == item.getRuleList()) {
            item.setRuleList(new ArrayList<RuleItemData>());
        }

        scheName = this.findViewById(R.id.sche_name);
        scheAmount = this.findViewById(R.id.sche_amount);
        scheStatus = this.findViewById(R.id.sche_status);
        radioBuy = this.findViewById(R.id.radio_buy);
        radioSold = this.findViewById(R.id.radio_sold);

        // set title
        ImageView iconView = this.findViewById(R.id.img_header_icon);
        iconView.setImageResource(IconUtil.getIcon(item.getSymbolGroup().toLowerCase()));
        TextView titleView = this.findViewById(R.id.text_header_title);
        titleView.setText(item.getSymbolDesc());

        if (item.getScheduleId() > 0) {
            // set data
            scheName.setText(item.getName());
            scheAmount.setText(String.valueOf(item.getAmount()));
            scheStatus.setSelection(item.getStatus() - 1, true);
            if (item.getType() == TradeTypeEnum.BUY.getCode()) {
                radioBuy.setChecked(true);
                radioSold.setChecked(false);
            } else if (item.getType() == TradeTypeEnum.SOLD.getCode()) {
                radioBuy.setChecked(false);
                radioSold.setChecked(true);
            }
        } else {
            radioBuy.setChecked(true);
            radioSold.setChecked(false);
        }

        // dialog
        ruleDialog = new RuleDialog(this, "规则");
        ruleDialog.setCallbackListener(this);

        // list adapter
        ListView ruleListView = this.findViewById(R.id.listveiw_rule);
        List<RuleItemData> data = new ArrayList<>();
        ;
        if (null != item && null != item.getRuleList()) {
            data = item.getRuleList();
        }
        adapter = new RuleListAdapter(this, data);
        ruleListView.setAdapter(adapter);
        ruleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ruleDialog.setItemData((RuleItemData) adapter.getItem(position));
                ruleDialog.show();
            }
        });

        if (null != item && item.getScheduleId() > 0) {
            this.showRuleList();
        }

        // button
        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSave();
            }
        });

        Button btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScheEditActivity.this.finish();
            }
        });
    }

    private void showRuleList() {
        //获取数据
        if (FirebirdUtil.isDebug) {
            ResultData<RuleItemData> jsonData = JsonUtil.JsonFileToObject(this, "rulelist.json",
                    new TypeToken<ResultData<RuleItemData>>() {
                    }.getType());
            adapter.setData(jsonData.getDataList());
            adapter.notifyDataSetChanged();
        } else {
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("userId", item.getUserId());
            paramsMap.put("ts", 1579760667789l);
            paramsMap.put("token", "6c1e5239c801e8f004ac5c68ad23bea4");
            paramsMap.put("symbolId", item.getSymbolId());
            paramsMap.put("scheduleId", item.getScheduleId());
            paramsMap.put("pageNumber", 1);
            paramsMap.put("pageSize", 10);

            String params = "q=" + AesUtil.encrypt(FormatUtil.getHttpParams(paramsMap));
            byte[] postData = params.getBytes();
            showLoading();
            HttpUtil.getUtil().httpPost(FirebirdUtil.HTTP_SERVER+FirebirdUtil.URL_SCHEDULE_RULES, postData,
                    new HttpCallback<String>() {
                        @Override
                        public void onSuccess(final String response) {
                            final ResultData<RuleItemData> jsonData = JsonUtil.JsonToObject(response,
                                    new TypeToken<ResultData<RuleItemData>>() {
                                    }.getType());
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    List<RuleItemData> ruleList = jsonData.getDataList();
                                    if (null != ruleList && ruleList.size() > 0) {
                                        adapter.setData(ruleList);
                                        adapter.notifyDataSetChanged();
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
    }

    public void addRuleItem(View view) {
        RuleItemData item = new RuleItemData();
        item.setId(0);
        item.setJoinType(1);
        item.setOpType(1);
        item.setRuleType(1);
        item.setOpValue("0");
        ruleDialog.setItemData(item);
        ruleDialog.show();
    }

    public void doSave() {
        item.setName(scheName.getText().toString());
        item.setAmount(Double.parseDouble(scheAmount.getText().toString()));
        item.setStatus(scheStatus.getSelectedItemPosition() + 1);
        if (radioBuy.isChecked()) {
            item.setType(TradeTypeEnum.BUY.getCode());
        } else if (radioSold.isChecked()) {
            item.setType(TradeTypeEnum.SOLD.getCode());
        }
        Log.d(TAG, JsonUtil.toJSONString(item));

        Map<String, Object> paramsMap = JsonUtil.objToMap(item);
        paramsMap.put("ruleList", JsonUtil.toJSONString(item.getRuleList()));
        httpPost(FirebirdUtil.HTTP_SERVER+FirebirdUtil.URL_SCHEDULE_SAVE, paramsMap);
    }

    @Override
    public void updateData(ResultData<String> data) {
        ScheEditActivity.this.finish();
    }

    @Override
    public Type getDataType() {
        return new TypeToken<ResultData<String>>() {
        }.getType();
    }

    @Override
    public void successCallback(Object data, int type) {
        RuleItemData ruleItem = (RuleItemData) data;
        List<RuleItemData> ruleList = item.getRuleList();
        if (null == ruleList) {
            ruleList = new ArrayList<>();
            item.setRuleList(ruleList);
        }
        switch (type) {
            case FirebirdUtil.OPT_ADD: {
                ruleItem.setId(Math.round(Math.random() * 1000000000));
                ruleList.add(ruleItem);
                break;
            }
            case FirebirdUtil.OPT_UPDATE: {
                int index = this.getRuleItemIndex(ruleItem, ruleList);
                if (index >= 0) {
                    RuleItemData r = ruleList.get(index);
                    r.setOpValue(ruleItem.getOpValue());
                    r.setOpType(ruleItem.getOpType());
                    r.setJoinType(ruleItem.getJoinType());
                    r.setRuleType(ruleItem.getRuleType());
                }
                break;
            }
            case FirebirdUtil.OPT_DELETE: {
                int index = this.getRuleItemIndex(ruleItem, ruleList);
                if (index >= 0) {
                    ruleList.remove(index);
                }
                break;
            }
            default:
                break;
        }
        adapter.setData(ruleList);
        adapter.notifyDataSetChanged();
    }

    private int getRuleItemIndex(RuleItemData ruleItem, List<RuleItemData> ruleList) {
        for (int i = 0; i < ruleList.size(); i++) {
            if (ruleItem.getId() == ruleList.get(i).getId()) {
                return i;
            }
        }

        return -1;
    }


    /*ListView适配器**/
    public class RuleListAdapter extends BaseAdapter {
        private List<RuleItemData> data;
        private LayoutInflater layoutInflater;
        private Context context;

        RuleListAdapter(Context context, List<RuleItemData> data) {
            this.context = context;
            this.layoutInflater = LayoutInflater.from(this.context);
            this.data = data;
        }

        public List<RuleItemData> getData() {
            return this.data;
        }

        public void setData(List<RuleItemData> data) {
            this.data = data;
        }

        public int getCount() {
            return data.size();
        }

        public Object getItem(int position) {
            return data.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ScheEditActivity.ViewHolder holder;
            /*View复用*/
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.activity_sd_rule_item, null);
                holder = new ScheEditActivity.ViewHolder();
                holder.joinType = convertView.findViewById(R.id.trade_type);
                holder.ruleType = convertView.findViewById(R.id.rule_type);
                holder.opType = convertView.findViewById(R.id.op_type);
                holder.opValue = convertView.findViewById(R.id.op_amount);

                convertView.setTag(holder);
            }
            holder = (ScheEditActivity.ViewHolder) convertView.getTag();

            // 给holder中的控件进行赋值
            RuleItemData item = data.get(position);

            holder.joinType.setText(JoinTypeEnum.getStatusDesc(item.getJoinType()));
            holder.ruleType.setText(RuleTypeEnum.getStatusDesc(item.getRuleType()));
            holder.opType.setText(OpTypeEnum.getStatusDesc(item.getOpType()));
            holder.opValue.setText(item.getOpValue());

            return convertView;
        }
    }

    class ViewHolder {
        TextView joinType;
        TextView ruleType;
        TextView opType;
        TextView opValue;
    }
}
