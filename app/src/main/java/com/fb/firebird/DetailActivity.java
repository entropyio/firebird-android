package com.fb.firebird;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fb.firebird.chart.LineChartManager;
import com.fb.firebird.enums.TradeTypeEnum;
import com.fb.firebird.http.HttpCallback;
import com.fb.firebird.http.HttpUtil;
import com.fb.firebird.json.BenefitVO;
import com.fb.firebird.json.ResultData;
import com.fb.firebird.json.UserAccountVO;
import com.fb.firebird.model.HomeItemData;
import com.fb.firebird.model.LineDataBean;
import com.fb.firebird.utils.FirebirdUtil;
import com.fb.firebird.utils.JsonUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class DetailActivity extends BaseActivity<UserAccountVO> {
    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final int CHART_TYPE_BENEFIT = 0;
    private static final int CHART_TYPE_PRICE = 1;

    private HomeItemData item;

    private List<LineDataBean> holdRateList;

    private List<LineDataBean> holdPriceList;
    private List<LineDataBean> highPriceList;
    private List<LineDataBean> lowPriceList;

    private LineChart lineChart;
    private LineChartManager lineChartManager;
    private double minValue, maxValue;

    private TextView totalText;
    private TextView rateText;
    private TextView nowText;
    private TextView holdText;
    private TextView allText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        item = (HomeItemData) intent.getSerializableExtra("item");
        this.setTitle(item.getSymbolDesc());

        // text views
        totalText = this.findViewById(R.id.text_amount_total);
        rateText = this.findViewById(R.id.text_benefit_rate);
        nowText = this.findViewById(R.id.text_amount_now);
        holdText = this.findViewById(R.id.text_amount_hold);
        allText = this.findViewById(R.id.text_amount_benefit);

        // tab
        TabLayout tabs = findViewById(R.id.tab_chart);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showChart(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        showChart(CHART_TYPE_BENEFIT);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (FirebirdUtil.isDebug) {
            ResultData<UserAccountVO> jsonData = JsonUtil.JsonFileToObject(this, "detail.json",
                    this.getDataType());
            updateData(jsonData);
        } else {
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("userId", item.getUserId());
            paramsMap.put("symbolId", item.getSymbolId());
            httpPost(FirebirdUtil.URL_ACCOUNT_DETAIL, paramsMap);
        }
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

        UserAccountVO account = jsonData.getData();
        this.setViewText(totalText, account.getTotal(), null, 0.0);
        this.setViewText(rateText, account.getRate(), "%", account.getRate());
        this.setViewText(nowText, account.getBenefit(), null, account.getBenefit());
        this.setViewText(holdText, account.getYestBenefit(), null, account.getYestBenefit());
        this.setViewText(allText, account.getTotalBenefit(), null, account.getTotalBenefit());
    }

    public void showBenefits(View view) {
        Intent intent = new Intent(DetailActivity.this, BenefitActivity.class);
        intent.putExtra("item", item);
        startActivity(intent);
    }

    public void showTrades(View view) {
        Intent intent = new Intent(DetailActivity.this, TradeActivity.class);
        intent.putExtra("item", item);
        startActivity(intent);
    }

    public void showSchedule(View view) {
        Intent intent = new Intent(DetailActivity.this, ScheduleActivity.class);
        intent.putExtra("item", item);
        startActivity(intent);
    }

    public void doSold(View view) {
        Intent intent = new Intent(DetailActivity.this, OptActivity.class);
        intent.putExtra("item", item);
        intent.putExtra("opType", TradeTypeEnum.SOLD.getCode());
        startActivity(intent);
    }

    public void doBuy(View view) {
        Intent intent = new Intent(DetailActivity.this, OptActivity.class);
        intent.putExtra("item", item);
        intent.putExtra("opType", TradeTypeEnum.BUY.getCode());
        startActivity(intent);
    }

    private void showChart(final int chartType) {
        //获取数据
        if (FirebirdUtil.isDebug) {
            ResultData<BenefitVO> jsonData = JsonUtil.JsonFileToObject(this, "benefit.json",
                    new TypeToken<ResultData<BenefitVO>>() {
                    }.getType());
            List<BenefitVO> benefitVOList = jsonData.getDataList();
            setChartData(chartType, benefitVOList);
            showChartView(chartType);
        } else {
            String params = String.format(Locale.US, "userId=%d&symbolId=%d", item.getUserId(), item.getSymbolId());
            byte[] postData = params.getBytes();
            showLoading();
            HttpUtil.getUtil().httpPost(FirebirdUtil.URL_DATA_LIST, postData,
                    new HttpCallback<String>() {
                        @Override
                        public void onSuccess(final String response) {
                            final ResultData<BenefitVO> jsonData = JsonUtil.JsonToObject(response,
                                    new TypeToken<ResultData<BenefitVO>>() {
                                    }.getType());
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    List<BenefitVO> benefitVOList = jsonData.getDataList();
                                    if (null != benefitVOList && benefitVOList.size() > 0) {
                                        setChartData(chartType, benefitVOList);
                                        showChartView(chartType);
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

    private void setChartData(int chartType, List<BenefitVO> dataList) {
        LineDataBean bean;
        BenefitVO vo;

        minValue = 0.0f;
        maxValue = 0.0f;
        if (chartType == CHART_TYPE_BENEFIT) {
            holdRateList = new ArrayList<>();
            for (int i = dataList.size() - 1; i > 0; i--) {
                vo = dataList.get(i);
                bean = new LineDataBean(vo.getGmtCreate(), vo.getHoldRate());
                holdRateList.add(bean);
                updateMinMaxValue(vo.getHoldRate());
            }
        } else if (chartType == CHART_TYPE_PRICE) {
            holdPriceList = new ArrayList<>();
            highPriceList = new ArrayList<>();
            lowPriceList = new ArrayList<>();

            for (int i = dataList.size() - 1; i > 0; i--) {
                vo = dataList.get(i);
                bean = new LineDataBean(vo.getGmtCreate(), vo.getHoldPrice());
                holdPriceList.add(bean);
                updateMinMaxValue(vo.getHoldPrice());

                bean = new LineDataBean(vo.getGmtCreate(), vo.getHighPrice());
                highPriceList.add(bean);
                updateMinMaxValue(vo.getHighPrice());

                bean = new LineDataBean(vo.getGmtCreate(), vo.getLowPrice());
                lowPriceList.add(bean);
                updateMinMaxValue(vo.getLowPrice());
            }
        }
    }

    private void updateMinMaxValue(double value) {
        if (value < minValue) {
            minValue = value;
        } else if (value > maxValue) {
            maxValue = value;
        }
    }

    private void showChartView(int chartType) {
        lineChart = this.findViewById(R.id.lineChart);
        lineChart.clear();

        lineChartManager = new LineChartManager(lineChart, (float) minValue, (float) maxValue);

        if (chartType == CHART_TYPE_BENEFIT) {
            lineChartManager.showLineChart(holdRateList, "收益率", getResources().getColor(R.color.blue));
        } else if (chartType == CHART_TYPE_PRICE) {
            //展示图表
            lineChartManager.showLineChart(holdPriceList, "我的收益", getResources().getColor(R.color.blue));
            lineChartManager.addLine(highPriceList, "最大收益", getResources().getColor(R.color.positive));
            lineChartManager.addLine(lowPriceList, "最小收益", getResources().getColor(R.color.negative));
            //设置曲线填充色 以及 MarkerView
//        Drawable drawable = getResources().getDrawable(R.drawable.fade_blue);
//        lineChartManager.setChartFillDrawable(drawable);
        }
        lineChartManager.setMarkerView(this);
    }
}
