package com.fb.firebird;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fb.firebird.enums.TradeStatusEnum;
import com.fb.firebird.enums.TradeTypeEnum;
import com.fb.firebird.json.ResultData;
import com.fb.firebird.json.UserTradeVO;
import com.fb.firebird.model.SDItemData;
import com.fb.firebird.model.ScheduleItemData;
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

public class ScheDetailActivity extends BaseActivity<UserTradeVO> {
    private static final String TAG = ScheDetailActivity.class.getSimpleName();

    private ListViewAdapter adapter;
    private ScheduleItemData item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sd);

        Intent intent = getIntent();
        item = (ScheduleItemData) intent.getSerializableExtra("item");

        ListView listView = this.findViewById(R.id.listveiw_schedule_detail);

        // header
        View headView = View.inflate(this, R.layout.activity_sd_header, null);
        headView.setBackground(this.getDrawable(R.drawable.fade_blue));
        listView.addHeaderView(headView);

        // set header
        TextView titleView = this.findViewById(R.id.text_header_title);
        ImageView imgView = this.findViewById(R.id.img_header_icon);
        String title = "定投详情";
        int icon = R.drawable.schedule;
        if (null != item) {
            title += " - " + item.getSymbolDesc();
            icon = IconUtil.getIcon(item.getSymbolGroup().toLowerCase());
        }
        titleView.setText(title);
        imgView.setImageResource(icon);

        // edit button
        ImageView editView = this.findViewById(R.id.img_header_edit);
        editView.setClickable(true);
        editView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "--- edit in detail ---");
                Intent intent = new Intent(ScheDetailActivity.this, ScheEditActivity.class);
                intent.putExtra("item", item);
                startActivity(intent);
            }
        });

        // set header data
        if (null != item) {
            TextView totalView = this.findViewById(R.id.sche_total);
            totalView.setText(FormatUtil.formatNumber(item.getTotal()));

            TextView countView = this.findViewById(R.id.sche_count);
            countView.setText(String.valueOf(item.getFailed() + item.getSuccess()));

            TextView typeView = this.findViewById(R.id.sche_type);
            typeView.setText(TradeTypeEnum.getStatusDesc(item.getType()));

            TextView amountView = this.findViewById(R.id.sche_amount);
            amountView.setText(FormatUtil.formatNumber(item.getAmount()));
        }

        // adapter
        List<SDItemData> data = new ArrayList<>();
        adapter = new ListViewAdapter(this, data);
        listView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (FirebirdUtil.isDebug) {
            ResultData<UserTradeVO> jsonData = JsonUtil.JsonFileToObject(this, "trade.json",
                    this.getDataType());
            this.updateData(jsonData);
        } else {
            this.queryTradeList(item.getUserId(), item.getSymbolId(), TradeStatusEnum.ALL.getCode(), 1, item.getScheduleId());
        }
    }

    private void queryTradeList(long userId, long symbolId, int status, int pageNumber, long scheduleId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userId", userId);
        paramsMap.put("symbolId", symbolId);
        paramsMap.put("status", status);
        paramsMap.put("pageNumber", pageNumber);
        paramsMap.put("scheduleId", scheduleId);

        httpPost(FirebirdUtil.HTTP_SERVER+FirebirdUtil.URL_TRADE_LIST, paramsMap);
    }

    @Override
    public Type getDataType() {
        return new TypeToken<ResultData<UserTradeVO>>() {
        }.getType();
    }

    @Override
    public void updateData(ResultData<UserTradeVO> jsonData) {
        List<SDItemData> data = new ArrayList<>();
        for (UserTradeVO ut : jsonData.getDataList()) {
            SDItemData item = new SDItemData();
            item.setTime(ut.getGmtCreate());
            item.setPrice(ut.getPrice());
            item.setAmount(ut.getAmount());
            item.setReason(ut.getReason());
            item.setStatus(ut.getStatus());
            data.add(item);
        }
        adapter.setData(data);

        adapter.notifyDataSetChanged();
    }

    private void setViewStatus(TextView view, int status) {
        view.setText(TradeStatusEnum.getStatusDesc(status));

        if (status == TradeStatusEnum.SUCCESS.getCode()) {
            view.setTextColor(this.getResources().getColor(R.color.positive));
        } else if (status == TradeStatusEnum.FAILED.getCode()) {
            view.setTextColor(this.getResources().getColor(R.color.negative));
        }
    }

    /*ListView适配器**/
    public class ListViewAdapter extends BaseAdapter {
        private List<SDItemData> data;
        private LayoutInflater layoutInflater;
        private Context context;

        ListViewAdapter(Context context, List<SDItemData> data) {
            this.context = context;
            this.layoutInflater = LayoutInflater.from(this.context);
            this.data = data;
        }

        public List<SDItemData> getData() {
            return this.data;
        }

        public void setData(List<SDItemData> data) {
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

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            /*View复用*/
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.activity_sd_item, null);
                holder = new ViewHolder();
                // 通过findViewById()方法实例R.layout.item_list内各组件
                holder.timeText = convertView.findViewById(R.id.sd_time);
                holder.amountText = convertView.findViewById(R.id.sd_amount);
                holder.priceText = convertView.findViewById(R.id.sd_price);
                holder.totalText = convertView.findViewById(R.id.sd_total);
                holder.statusText = convertView.findViewById(R.id.sd_status);
                holder.reasonText = convertView.findViewById(R.id.sd_reason);

                convertView.setTag(holder);
            }

            holder = (ViewHolder) convertView.getTag();
            // 给holder中的控件进行赋值
            SDItemData item = data.get(position);
            holder.timeText.setText(item.getTime());
            holder.amountText.setText(FormatUtil.formatNumber(item.getAmount()));
            holder.priceText.setText(FormatUtil.formatNumber(item.getPrice()));
            holder.totalText.setText(FormatUtil.formatNumber(item.getPrice() * item.getPrice()));
            holder.reasonText.setText(item.getReason());
            setViewStatus(holder.statusText, item.getStatus());
            return convertView;
        }
    }

    class ViewHolder {
        TextView timeText;
        TextView amountText;
        TextView priceText;
        TextView totalText;
        TextView statusText;
        TextView reasonText;
    }
}
