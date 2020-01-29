package com.fb.firebird.chart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.fb.firebird.R;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class LineChartMarkView extends MarkerView {

    private TextView tvDate;
    private TextView tvValue0;
    private TextView tvValue1;
    private TextView tvValue2;

    private ValueFormatter xAxisValueFormatter;
    DecimalFormat df = new DecimalFormat("0.00");

    public LineChartMarkView(Context context, ValueFormatter xAxisValueFormatter) {
        super(context, R.layout.chart_markview);
        this.xAxisValueFormatter = xAxisValueFormatter;
        tvDate = findViewById(R.id.tv_date);
        tvValue0 = findViewById(R.id.tv_value0);
        tvValue1 = findViewById(R.id.tv_value1);
        tvValue2 = findViewById(R.id.tv_value2);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        Chart chart = getChartView();
        if (chart instanceof LineChart) {
            LineData lineData = ((LineChart) chart).getLineData();
            //获取到图表中的所有曲线
            List<ILineDataSet> dataSetList = lineData.getDataSets();
            for (int i = 0; i < dataSetList.size(); i++) {
                LineDataSet dataSet = (LineDataSet) dataSetList.get(i);
                //获取到曲线的所有在Y轴的数据集合，根据当前X轴的位置 来获取对应的Y轴值
                float y = dataSet.getValues().get((int) e.getX()).getY();
                Log.d("chart", y + "");
                String label = dataSet.getLabel() + "：" + String.format(Locale.US, "%.2f", y);
                if (i == 0) {
                    tvValue0.setText(label);
                } else if (i == 1) {
                    tvValue1.setText(label);
                } else if (i == 2) {
                    tvValue2.setText(label);
                }
            }
            tvDate.setText(xAxisValueFormatter.getFormattedValue(e.getX()));
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
