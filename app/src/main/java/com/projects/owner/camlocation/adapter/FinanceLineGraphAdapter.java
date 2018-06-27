package com.projects.owner.camlocation.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.projects.owner.camlocation.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class FinanceLineGraphAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Object> contents;
    final String[] quarters = new String[]{"Q1", "Q2", "Q3", "Q4"};
    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;

    public FinanceLineGraphAdapter(List<Object> contents) {
        this.contents = contents;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_HEADER;
            default:
                return TYPE_HEADER;
        }
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case TYPE_HEADER: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_big, parent, false);
                LineChart chart = (LineChart) view.findViewById(R.id.chart);
                LineDataSet dataSet = new LineDataSet(getYAxisValues(), "oye");
                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                dataSet.setColor(Color.parseColor("#FF4081"));
//                dataSet.setDrawFilled(true);
//                dataSet.setFillColor(Color.parseColor("#FF30FFB0"));
//                dataSet.setFillAlpha(255);
                dataSet.setLineWidth(3.5f);
                dataSet.setCircleColorHole(Color.parseColor("#FF4081"));
                dataSet.setCircleColor(Color.parseColor("#FF4081"));
                dataSet.setValueFormatter(new LargeValueFormatter());

                LineDataSet dataSet1 = new LineDataSet(getYAxisValues(), "oye");
                dataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
                dataSet1.setColor(Color.parseColor("#00BCD4"));
//                dataSet1.setDrawFilled(true);
//                dataSet1.setFillColor(Color.parseColor("#FF30FFB0"));
//                dataSet1.setFillAlpha(255);
                dataSet1.setLineWidth(3.5f);
                dataSet1.setCircleColorHole(Color.parseColor("#00BCD4"));
                dataSet1.setCircleColor(Color.parseColor("#00BCD4"));
                dataSet1.setValueFormatter(new LargeValueFormatter());

                List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                dataSets.add(dataSet);
                dataSets.add(dataSet1);

                LineData data = new LineData(dataSets);
                chart.setData(data);

                chart.animateY(2000, Easing.EasingOption.EaseInSine);
                chart.setDrawBorders(false);
                chart.setDrawGridBackground(false);
                chart.setDescription("");
                chart.setTouchEnabled(false);
                chart.setScaleEnabled(false);
                chart.setPinchZoom(false);
                chart.getAxisLeft().setDrawGridLines(false);

                chart.getAxisRight().setDrawGridLines(false);
                chart.getXAxis().setDrawGridLines(false);
                YAxis left = chart.getAxisLeft();
                left.setDrawLabels(false); // no axis labels
                left.setDrawAxisLine(false); // no axis line
                left.setDrawGridLines(false); // no grid lines
                left.setDrawZeroLine(false); // draw a zero line
                left.setTextColor(R.color.accent_color);
                chart.getAxisRight().setEnabled(false);
                XAxis xAxis = chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                chart.setAutoScaleMinMaxEnabled(true);
//                chart.getXAxis().setEnabled(false);
                chart.getAxisLeft().setDrawAxisLine(false);
                chart.getAxisLeft().setDrawLabels(false);
                chart.getAxisRight().setDrawLabels(false);
//                chart.getXAxis().setDrawLabels(false);
                chart.getLegend().setEnabled(false);
                xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
                xAxis.setValueFormatter(formatter);
                chart.invalidate();
                return new RecyclerView.ViewHolder(view) {
                };
            }
            case TYPE_CELL: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_small, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }
        }
        return null;
    }

    AxisValueFormatter formatter = new AxisValueFormatter() {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return quarters[(int) value];
        }

        // we don't draw numbers, so no decimal digits needed
        @Override
        public int getDecimalDigits() {
            return 0;
        }
    };

    //
//    private ArrayList<String> getXAxisValues() {
//        ArrayList<String> xAxis = new ArrayList<>();
//        xAxis.add("JAN");
//        xAxis.add("FEB");
//        xAxis.add("MAR");
//        xAxis.add("APR");
//        xAxis.add("MAY");
//        xAxis.add("JUN");
//        return xAxis;
//    }
    private ArrayList<Entry> getYAxisValues() {
        ArrayList<Entry> xAxis = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Random r = new Random();
            int i1 = r.nextInt(30 - 10) + 10;
            xAxis.add(new Entry(i, i1));
        }
        return xAxis;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                break;
            case TYPE_CELL:
                break;
        }
    }
}