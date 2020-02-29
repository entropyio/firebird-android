package com.fb.firebird;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fb.firebird.enums.StatusEnum;
import com.fb.firebird.json.BenefitVO;
import com.fb.firebird.json.ResultData;
import com.fb.firebird.model.HomeItemData;
import com.fb.firebird.utils.FirebirdUtil;
import com.fb.firebird.utils.IconUtil;
import com.fb.firebird.utils.JsonUtil;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class BenefitActivity extends BaseActivity<BenefitVO> {
    private static final String TAG = BenefitActivity.class.getSimpleName();
    protected Typeface tfLight;
    private HomeItemData item;
    private HorizontalBarChart barChart;
    private List<BenefitVO> chartData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benefit);

        Intent intent = getIntent();
        item = (HomeItemData) intent.getSerializableExtra("item");

        // set header
        TextView titleView = this.findViewById(R.id.text_header_title);
        ImageView imgView = this.findViewById(R.id.img_header_icon);
        String title = "我的收益";
        int icon = R.drawable.benefit;
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

        // hide add button
        ImageView addView = this.findViewById(R.id.img_header_add);
        addView.setVisibility(View.INVISIBLE);

        // barChart init
        barChart = findViewById(R.id.bar_chart);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        // if more than 60 entries are displayed in the barChart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);
        // draw shadows for each bar that show the maximum value
        // barChart.setDrawBarShadow(true);
        barChart.setDrawGridBackground(false);

        XAxis xl = barChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setTypeface(tfLight);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setGranularity(1f);
        xl.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int position = (int) (value / 10); //data设置时*10
                BenefitVO vo = chartData.get(chartData.size() - position + 1);
                if (null != vo) {
                    return vo.getGmtCreate().substring(5, 10); // mm-dd
                } else {
                    return super.getFormattedValue(value);
                }
            }
        });

        YAxis yl = barChart.getAxisLeft();
        yl.setTypeface(tfLight);
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis yr = barChart.getAxisRight();
        yr.setTypeface(tfLight);
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        barChart.setFitBars(true);
        barChart.animateY(500);

        // 不显示图例
        Legend legend = barChart.getLegend();
        legend.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (FirebirdUtil.isDebug) {
            ResultData<BenefitVO> jsonData = JsonUtil.JsonFileToObject(this, "benefit.json",
                    this.getDataType());
            this.updateData(jsonData);
        } else {
            this.queryList(userId, symbolId, StatusEnum.ENABLE.getCode(), 30);
        }
    }

    @Override
    public Type getDataType() {
        return new TypeToken<ResultData<BenefitVO>>() {
        }.getType();
    }

    private void queryList(long userId, long symbolId, int status, int pageSize) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userId", userId);
        paramsMap.put("symbolId", symbolId);
        paramsMap.put("status", status);
        paramsMap.put("pageSize", pageSize);
        httpPost(FirebirdUtil.HTTP_SERVER+FirebirdUtil.URL_DATA_LIST, paramsMap);
    }

    @Override
    public void updateData(ResultData<BenefitVO> jsonData) {
        if (jsonData.getRetCode() != 0 || null == jsonData.getDataList()) {
            showMessage("无收益数据");
            return;
        }

        float barWidth = 9f;
        float spaceForBar = 10f;
        ArrayList<BarEntry> oriValues = new ArrayList<>(); // 原始数据集(包含负数，负数在坐标轴上不显示)
        ArrayList<BarEntry> values = new ArrayList<>(); // 转换为正数的数据集
        chartData = jsonData.getDataList();

        int i = jsonData.getDataList().size();
        for (BenefitVO vo : jsonData.getDataList()) {
            float val = (float) vo.getHoldBenefit();
            oriValues.add(new BarEntry(i * spaceForBar, val));
            values.add(new BarEntry(i * spaceForBar, Math.abs(val)));
            i--;
        }

        BenefitBarDataSet barDataSet;
        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            barDataSet = (BenefitBarDataSet) barChart.getData().getDataSetByIndex(0);
            barDataSet.setValues(values);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            barDataSet = new BenefitBarDataSet(oriValues, values, "每日收益");
            barDataSet.setColors(Color.parseColor("#4CAF50"), Color.parseColor("#F44336"));

            barDataSet.setDrawIcons(false);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(barDataSet);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(tfLight);
            data.setBarWidth(barWidth);
            barChart.setData(data);
        }
    }

    class BenefitBarDataSet extends BarDataSet {
        private List<BarEntry> oriValues;

        public BenefitBarDataSet(List<BarEntry> oriValues, List<BarEntry> values, String label) {
            super(values, label);
            this.oriValues = oriValues;
        }

        @Override
        public int getColor(int index) {
//            BarEntry entry = getEntryForIndex(index);
            BarEntry entry = oriValues.get(index);
            if (entry.getY() > 0) {
                return mColors.get(0);
            } else {
                return mColors.get(1);
            }
        }

        @Override
        public ValueFormatter getValueFormatter() {
            return new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.format(Locale.US, "%.2f", value);
                }
            };
        }
    }
}
