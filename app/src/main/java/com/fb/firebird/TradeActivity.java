package com.fb.firebird;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fb.firebird.dialog.TradeDialog;
import com.fb.firebird.enums.TradeStatusEnum;
import com.fb.firebird.enums.TradeTypeEnum;
import com.fb.firebird.json.ResultData;
import com.fb.firebird.json.UserTradeVO;
import com.fb.firebird.model.HomeItemData;
import com.fb.firebird.model.TradeItemData;
import com.fb.firebird.utils.FirebirdUtil;
import com.fb.firebird.utils.FormatUtil;
import com.fb.firebird.utils.IconUtil;
import com.fb.firebird.utils.JsonUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeActivity extends BaseActivity<UserTradeVO> implements CallbackListener {
    private static final String TAG = TradeActivity.class.getSimpleName();

    private ListView listView;
    private ListViewAdapter tradeAdapter;

    private TradeDialog tradeDialog;

    private HomeItemData item;

    private boolean isLast;     //是否滑动到底部的标志位
    private int pageNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);

        Intent intent = getIntent();
        item = (HomeItemData) intent.getSerializableExtra("item");

        tradeDialog = new TradeDialog(this, "交易", item);
        tradeDialog.setCallbackListener(this);

        listView = this.findViewById(R.id.listveiw_trade);

        // set header
        TextView titleView = this.findViewById(R.id.text_header_title);
        ImageView imgView = this.findViewById(R.id.img_header_icon);
        String title = "交易记录";
        int icon = R.drawable.trade;
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
                    Log.i(TAG, "--- 添加交易 ---");
                    tradeDialog.setItemData(item);
                    tradeDialog.show();
                }
            });
        } else {
            addView.setVisibility(View.INVISIBLE);
        }

        // adapter
        List<TradeItemData> data = new ArrayList<>();
        tradeAdapter = new TradeActivity.ListViewAdapter(this, data);
        listView.setAdapter(tradeAdapter);

        // 上拉翻页
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //判断是否滑倒底部
                isLast = (totalItemCount == firstVisibleItem + visibleItemCount);
                pageNumber = (totalItemCount / 10) + 1;
                Log.i(TAG, "isLast = " + isLast + ", pageNumber = " + pageNumber);
            }

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                //当滑动到底部 && 手指离开屏幕时,确定为需要加载分页
                if (isLast && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//                    queryTradeList(item.getUserId(), item.getSymbolId(), 2, 1, 0);
                }
            }
        });

        // tab
        TabLayout tabs = findViewById(R.id.tab_trade);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "trade tab position: " + tab.getPosition());
                refreshData(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (FirebirdUtil.isDebug) {
            ResultData<UserTradeVO> jsonData = JsonUtil.JsonFileToObject(this, "trade.json",
                    this.getDataType());
            this.updateData(jsonData);
        } else {
            this.queryTradeList(userId, symbolId, TradeStatusEnum.SUCCESS.getCode(),
                    FirebirdUtil.DEFAULT_PAGE_NUMBER, TradeTypeEnum.ALL.getCode());
        }
    }

    @Override
    public Type getDataType() {
        return new TypeToken<ResultData<UserTradeVO>>() {
        }.getType();
    }

    private void refreshData(int tabPosition) {
        int status = TradeStatusEnum.SUCCESS.getCode();
        int type = TradeTypeEnum.ALL.getCode();
        switch (tabPosition) {
            case 1:
                status = TradeStatusEnum.PROGRESSING.getCode();
                break;
            case 2:
                type = TradeTypeEnum.BUY.getCode();
                break;
            case 3:
                type = TradeTypeEnum.SOLD.getCode();
                break;
            default:
                break;
        }

        this.queryTradeList(userId, symbolId, status, 1, type);
    }

    private void queryTradeList(long userId, long symbolId, int status, int pageNumber, int type) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userId", userId);
        paramsMap.put("symbolId", symbolId);
        paramsMap.put("status", status);
        paramsMap.put("pageNumber", pageNumber);
        paramsMap.put("type", type);

        httpPost(FirebirdUtil.URL_TRADE_LIST, paramsMap);
    }

    @Override
    public void successCallback(Object data, int opType) {
        UserTradeVO item = (UserTradeVO) data;

        this.saveTrade(item.getUserId(), item.getSymbolId(), item.getStatus(),
                item.getPrice(), item.getAmount(), item.getType());
    }

    private void saveTrade(long userId, long symbolId, int status, double price, double amount, int type) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userId", userId);
        paramsMap.put("symbolId", symbolId);
        paramsMap.put("status", status);
        paramsMap.put("price", price);
        paramsMap.put("amount", amount);
        paramsMap.put("type", type);

        httpPost(FirebirdUtil.URL_TRADE_SAVE, paramsMap, "添加成功", true);
    }

    @Override
    public void updateData(ResultData<UserTradeVO> jsonData) {
        List<TradeItemData> data = new ArrayList<>();
        for (UserTradeVO ut : jsonData.getDataList()) {
            TradeItemData item = new TradeItemData();
            item.setType(ut.getType());
            item.setName(ut.getSymbolDesc());
            item.setIcon(IconUtil.getIcon(ut.getSymbolIcon()));
            item.setStatus(ut.getStatus());
            item.setPrice(ut.getPrice());
            item.setAmount(ut.getAmount());
            item.setTime(ut.getGmtCreate());
            data.add(item);
        }
        tradeAdapter.setData(data);

        tradeAdapter.notifyDataSetChanged();
    }

    /*ListView适配器**/
    public class ListViewAdapter extends BaseAdapter {
        private List<TradeItemData> data;
        private LayoutInflater layoutInflater;
        private Context context;

        public ListViewAdapter(Context context, List<TradeItemData> data) {
            this.context = context;
            this.layoutInflater = LayoutInflater.from(this.context);
            this.data = data;
        }

        public List<TradeItemData> getData() {
            return this.data;
        }

        public void setData(List<TradeItemData> data) {
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
            TradeActivity.ViewHolder holder = null;
            /*View复用*/
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.activity_trade_item, null);
                holder = new TradeActivity.ViewHolder();
                // 通过findViewById()方法实例R.layout.item_list内各组件
                holder.tradeType = convertView.findViewById(R.id.trade_type);
                holder.tradeName = convertView.findViewById(R.id.trade_name);
                holder.tradePrice = convertView.findViewById(R.id.trade_price);
                holder.tradeAmount = convertView.findViewById(R.id.trade_amount);
                holder.tradeTotal = convertView.findViewById(R.id.trade_total);
                holder.tradeTime = convertView.findViewById(R.id.trade_time);
                holder.tradeStatus = convertView.findViewById(R.id.trade_status);

                convertView.setTag(holder);
            }
            holder = (TradeActivity.ViewHolder) convertView.getTag();
            // 给holder中的控件进行赋值
            TradeItemData item = data.get(position);
            holder.tradeType.setText(TradeTypeEnum.getStatusDesc(item.getType()));
            if (item.getType() == TradeTypeEnum.BUY.getCode()) {
                holder.tradeType.setTextColor(getResources().getColor(R.color.positive));
            } else if (item.getType() == TradeTypeEnum.SOLD.getCode()) {
                holder.tradeType.setTextColor(getResources().getColor(R.color.white));
            }
            holder.tradeName.setText(item.getName());
            Drawable drawable = getResources().getDrawable(item.getIcon());
            drawable.setBounds(0, 0, 36, 36);
            holder.tradeName.setCompoundDrawables(drawable, null, null, null);

            holder.tradePrice.setText(FormatUtil.formatNumber(item.getPrice()));
            holder.tradeAmount.setText(FormatUtil.formatNumber(item.getAmount()));
            holder.tradeTotal.setText(FormatUtil.formatNumber(item.getPrice() * item.getAmount()));
            holder.tradeTime.setText(item.getTime());
            holder.tradeStatus.setText(TradeStatusEnum.getStatusDesc(item.getStatus()));
            return convertView;
        }
    }

    class ViewHolder {
        TextView tradeType;
        TextView tradeName;
        TextView tradePrice;
        TextView tradeAmount;
        TextView tradeTotal;
        TextView tradeTime;
        TextView tradeStatus;
    }
}
