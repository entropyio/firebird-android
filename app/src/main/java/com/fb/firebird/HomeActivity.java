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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fb.firebird.json.ResultData;
import com.fb.firebird.json.UserAccountVO;
import com.fb.firebird.model.HomeItemData;
import com.fb.firebird.utils.FirebirdUtil;
import com.fb.firebird.utils.IconUtil;
import com.fb.firebird.utils.JsonUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends BaseActivity<UserAccountVO> {
    private static final String TAG = HomeActivity.class.getSimpleName();

    private ListView listView;
    private HomeListAdapter adapter;


    private TextView totalText;
    private TextView rateText;
    private TextView nowText;
    private TextView holdText;
    private TextView allText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        listView = this.findViewById(R.id.listview_home);
        // header
        View headView = View.inflate(this, R.layout.section_header, null);
        listView.addHeaderView(headView);

        // adapter
        List<HomeItemData> data = new ArrayList<>();
        adapter = new HomeListAdapter(this, data);
        listView.setAdapter(adapter);

        // click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // skip header row
                if (position < 1) {
                    return;
                }

                HomeItemData item = (HomeItemData) adapter.getItem(position - 1);
                Log.d(TAG, "click on " + item.getSymbolDesc());

                Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
                intent.putExtra("item", item);
                startActivity(intent);
            }
        });

        // text views
        totalText = this.findViewById(R.id.text_amount_total);
        rateText = this.findViewById(R.id.text_benefit_rate);
        nowText = this.findViewById(R.id.text_amount_now);
        holdText = this.findViewById(R.id.text_amount_hold);
        allText = this.findViewById(R.id.text_amount_benefit);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (FirebirdUtil.isDebug) {
            Type type = this.getDataType();

            ResultData<UserAccountVO> jsonData = JsonUtil.JsonFileToObject(this, "home.json",
                    type);
            updateData(jsonData);
        } else {
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("userId", userId);
            paramsMap.put("t", new Date().getTime());
            httpPost(FirebirdUtil.URL_ACCOUNT_LIST, paramsMap);
        }
        Log.d(TAG, "in onResume");
    }

    @Override
    public Type getDataType() {
        return new TypeToken<ResultData<UserAccountVO>>() {
        }.getType();
    }

    @Override
    public void updateData(ResultData<UserAccountVO> jsonData) {
        if (jsonData.getRetCode() != 0) {
            showMessage(jsonData.getMessage());
            return;
        }

        UserAccountVO totalAcc = jsonData.getData();
        this.setViewText(totalText, totalAcc.getTotal(), null, 0.0);
        this.setViewText(rateText, totalAcc.getRate(), "%", totalAcc.getRate());
        this.setViewText(nowText, totalAcc.getBenefit(), null, totalAcc.getBenefit());
        this.setViewText(holdText, totalAcc.getYestBenefit(), null, totalAcc.getYestBenefit());
        this.setViewText(allText, totalAcc.getTotalBenefit(), null, totalAcc.getTotalBenefit());

        // list data
        List<HomeItemData> data = new ArrayList<>();
        for (UserAccountVO account : jsonData.getDataList()) {
            HomeItemData item = new HomeItemData();
            item.setUserId(account.getUserId());
            item.setSymbolId(account.getSymbolId());

            item.setSymbolIcon(IconUtil.getIcon(account.getSymbolIcon()));
            item.setSymbolGroup(account.getSymbolGroup());
            item.setSymbolDesc(account.getSymbolDesc());
            item.setSymbolName(account.getSymbolName());

            item.setHoldAmount(account.getHoldAmount());
            item.setHoldPrice(account.getHoldPrice());
            item.setPrice(account.getPrice());
            item.setRate(account.getRate());
            item.setYestBenefit(account.getYestBenefit());
            item.setBenefit(account.getBenefit());

            data.add(item);
        }
        adapter.setData(data);

        adapter.notifyDataSetChanged();
    }

    public void showBenefits(View view) {
        Log.d(TAG, "showBenefits");
        Intent intent = new Intent(HomeActivity.this, BenefitActivity.class);
        startActivity(intent);
    }

    public void showTrades(View view) {
        Log.d(TAG, "showTrades");
        Intent intent = new Intent(HomeActivity.this, TradeActivity.class);
        startActivity(intent);
    }

    public void showSchedule(View view) {
        Log.d(TAG, "showSchedule");
        Intent intent = new Intent(HomeActivity.this, ScheduleActivity.class);
        startActivity(intent);
    }

    /*ListView适配器**/
    public class HomeListAdapter extends BaseAdapter {
        private List<HomeItemData> data;
        private LayoutInflater layoutInflater;
        private Context context;

        public HomeListAdapter(Context context, List<HomeItemData> data) {
            this.context = context;
            this.layoutInflater = LayoutInflater.from(this.context);
            this.data = data;
        }

        public List<HomeItemData> getData() {
            return this.data;
        }

        public void setData(List<HomeItemData> data) {
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
            ViewHolder holder = null;
            /*View复用*/
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.activity_home_item, null);
                holder = new ViewHolder();
                // 通过findViewById()方法实例R.layout.item_list内各组件
                holder.symbolIcon = convertView.findViewById(R.id.symbol_icon);
                holder.symbolGroup = convertView.findViewById(R.id.symbol_group);
                holder.symbolDesc = convertView.findViewById(R.id.symbol_desc);
                holder.price = convertView.findViewById(R.id.text_price);
                holder.rate = convertView.findViewById(R.id.text_rate);
                holder.hold = convertView.findViewById(R.id.sche_amount);
                holder.yestBenefit = convertView.findViewById(R.id.sche_type);
                holder.benefit = convertView.findViewById(R.id.sche_count);

                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();

            // 给holder中的控件进行赋值
            HomeItemData item = data.get(position);
            holder.symbolIcon.setImageResource(item.getSymbolIcon());
            holder.symbolGroup.setText(item.getSymbolGroup());
            holder.symbolDesc.setText(item.getSymbolDesc());

            setViewText(holder.price, item.getPrice(), null, (item.getPrice() - item.getHoldPrice()));
            setViewText(holder.hold, item.getPrice() * item.getHoldAmount(), null, 0.0);
            setViewText(holder.rate, item.getRate(), "%", item.getRate());
            setViewText(holder.yestBenefit, item.getYestBenefit(), null, item.getYestBenefit());
            setViewText(holder.benefit, item.getBenefit(), null, item.getBenefit());
            return convertView;
        }
    }

    class ViewHolder {
        ImageView symbolIcon;
        TextView symbolGroup;
        TextView symbolDesc;

        TextView hold;          //持有金额
        TextView price;         //当前价格
        TextView rate;          //当前收益率
        TextView yestBenefit;   //昨日收益
        TextView benefit;       //当前收益
    }
}
