package com.fb.firebird;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fb.firebird.enums.ScheduleStatusEnum;
import com.fb.firebird.enums.StatusEnum;
import com.fb.firebird.enums.TradeTypeEnum;
import com.fb.firebird.json.ResultData;
import com.fb.firebird.json.UserScheduleVO;
import com.fb.firebird.model.HomeItemData;
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

public class ScheduleActivity extends BaseActivity<UserScheduleVO> {
    private ListViewAdapter scheduleAdapter;

    private HomeItemData item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Intent intent = getIntent();
        item = (HomeItemData) intent.getSerializableExtra("item");

        ListView listView = this.findViewById(R.id.listveiw_schedule);

        // set header
        TextView titleView = this.findViewById(R.id.text_header_title);
        ImageView imgView = this.findViewById(R.id.img_header_icon);
        String title = "我的定投";
        int icon = R.drawable.schedule;
        if (null != item) {
            title += " - " + item.getSymbolDesc();
            icon = IconUtil.getIcon(item.getSymbolGroup().toLowerCase());
            userId = item.getUserId();
            symbolId = item.getSymbolId();
        } else {
            userId = 1;
            symbolId = 0;
        }
        titleView.setText(title);
        imgView.setImageResource(icon);

        // init add button
        ImageView addView = this.findViewById(R.id.img_header_add);
        if (null != item) {
            addView.setVisibility(View.VISIBLE);
            addView.setClickable(true);
            addView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ScheduleItemData scheItem = new ScheduleItemData();
                    scheItem.setScheduleId(0);
                    if (null != item) {
                        scheItem.setUserId(item.getUserId());
                        scheItem.setSymbolId(item.getSymbolId());
                    }

                    Intent intent = new Intent(ScheduleActivity.this, ScheEditActivity.class);
                    intent.putExtra("item", scheItem);
                    startActivity(intent);
                }
            });
        } else {
            addView.setVisibility(View.INVISIBLE);
        }

        // adapter
        List<ScheduleItemData> data = new ArrayList<>();
        scheduleAdapter = new ListViewAdapter(this, data);
        listView.setAdapter(scheduleAdapter);

        // click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScheduleItemData item = (ScheduleItemData) scheduleAdapter.getItem(position);

                Intent intent = new Intent(ScheduleActivity.this, ScheDetailActivity.class);
                intent.putExtra("item", item);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (FirebirdUtil.isDebug) {
            ResultData<UserScheduleVO> jsonData = JsonUtil.JsonFileToObject(this, "schedule.json",
                    this.getDataType());
            this.updateData(jsonData);
        } else {
            this.queryScheduleList(userId, symbolId, StatusEnum.ALL.getCode(),
                    FirebirdUtil.DEFAULT_PAGE_NUMBER, TradeTypeEnum.ALL.getCode());
        }
    }

    private void queryScheduleList(long userId, long symbolId, int status, int pageNumber, int type) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userId", userId);
        paramsMap.put("symbolId", symbolId);
        paramsMap.put("status", status);
        paramsMap.put("pageNumber", pageNumber);

        httpPost(FirebirdUtil.URL_SCHEDULE_LIST, paramsMap);
    }

    @Override
    public Type getDataType() {
        return new TypeToken<ResultData<UserScheduleVO>>() {
        }.getType();
    }

    @Override
    public void updateData(ResultData<UserScheduleVO> jsonData) {
        List<ScheduleItemData> data = new ArrayList<>();
        for (UserScheduleVO ut : jsonData.getDataList()) {
            ScheduleItemData item = new ScheduleItemData();
            item.setUserId(ut.getUserId());
            item.setSymbolId(ut.getSymbolId());
            item.setScheduleId(ut.getId());

            item.setSymbolIcon(IconUtil.getIcon(ut.getSymbolIcon()));
            item.setSymbolGroup(ut.getSymbolGroup());
            item.setSymbolDesc(ut.getSymbolDesc());

            item.setName(ut.getName());
            item.setType(ut.getType());
            item.setAmount(ut.getAmount());
            item.setTotal(ut.getTotal());
            item.setSuccess(ut.getSuccess());
            item.setFailed(ut.getFailed());
            item.setStatus(ut.getStatus());
            data.add(item);
        }
        scheduleAdapter.setData(data);

        scheduleAdapter.notifyDataSetChanged();
    }

    /*ListView适配器**/
    public class ListViewAdapter extends BaseAdapter {
        private List<ScheduleItemData> data;
        private LayoutInflater layoutInflater;
        private Context context;

        ListViewAdapter(Context context, List<ScheduleItemData> data) {
            this.context = context;
            this.layoutInflater = LayoutInflater.from(this.context);
            this.data = data;
        }

        public List<ScheduleItemData> getData() {
            return this.data;
        }

        public void setData(List<ScheduleItemData> data) {
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
                convertView = layoutInflater.inflate(R.layout.activity_schedule_item, null);
                holder = new ViewHolder();

                holder.symbolIcon = convertView.findViewById(R.id.sche_icon);
                holder.symbolGroup = convertView.findViewById(R.id.sche_group);
                holder.symbolDesc = convertView.findViewById(R.id.sche_symbol);

                holder.nameText = convertView.findViewById(R.id.sche_name);
                holder.totalText = convertView.findViewById(R.id.sche_total);
                holder.amountText = convertView.findViewById(R.id.sche_amount);
                holder.typeText = convertView.findViewById(R.id.sche_type);
                holder.countText = convertView.findViewById(R.id.sche_count);
                holder.statusText = convertView.findViewById(R.id.sche_status);

                convertView.setTag(holder);
            }

            holder = (ViewHolder) convertView.getTag();
            // 给holder中的控件进行赋值
            ScheduleItemData item = data.get(position);
            holder.symbolIcon.setImageResource(item.getSymbolIcon());
            holder.symbolGroup.setText(item.getSymbolGroup());
            holder.symbolDesc.setText(item.getSymbolDesc());

            holder.nameText.setText(item.getName());
            holder.typeText.setText(TradeTypeEnum.getStatusDesc(item.getType()));
            holder.amountText.setText(FormatUtil.formatNumber(item.getAmount()));
            holder.totalText.setText(FormatUtil.formatNumber(item.getTotal()));
            holder.countText.setText(String.valueOf(item.getSuccess() + item.getFailed()));
            holder.statusText.setText(ScheduleStatusEnum.getStatusDesc(item.getStatus()));
            return convertView;
        }
    }

    class ViewHolder {
        ImageView symbolIcon;
        TextView symbolGroup;
        TextView symbolDesc;

        TextView nameText;
        TextView statusText;
        TextView typeText;
        TextView amountText;
        TextView totalText;
        TextView countText;
    }
}
