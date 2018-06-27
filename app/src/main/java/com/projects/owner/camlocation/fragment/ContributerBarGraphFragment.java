package com.projects.owner.camlocation.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.projects.owner.camlocation.R;

import java.util.ArrayList;
import java.util.List;


public class ContributerBarGraphFragment extends Fragment {
    private ObservableScrollView mScrollView;

    private HorizontalBarChart barChart;
    final String[] names = new String[]{"Pepsi", "Nesle", "Coke", "Unilever"};

    public static ContributerBarGraphFragment newInstance() {
        return new ContributerBarGraphFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scroll, container, false);
        barChart = (HorizontalBarChart) view.findViewById(R.id.chartbar);
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 30f));
        entries.add(new BarEntry(1f, 80f));
        entries.add(new BarEntry(2f, 60f));
        entries.add(new BarEntry(3f, 50f));
        // gap of 2f
        entries.add(new BarEntry(5f, 70f));
        entries.add(new BarEntry(6f, 60f));

        BarDataSet set = new BarDataSet(entries, "BarDataSet");
        set.setColors(new int[]{R.color.accent_color, R.color.cyan, R.color.red, R.color.green}, getActivity());
        BarData data = new BarData(set);

        data.setBarWidth(0.9f); // set custom bar width
        barChart.setData(data);
        barChart.animateXY(1500, 1500);
        barChart.setDrawBorders(false);
        barChart.setDrawGridBackground(false);
        barChart.setDescription("");
        barChart.setTouchEnabled(false);
        barChart.setScaleEnabled(false);
        barChart.setPinchZoom(false);
        barChart.getAxisLeft().setDrawGridLines(false);

        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        YAxis left = barChart.getAxisLeft();
        left.setDrawLabels(false); // no axis labels
        left.setDrawAxisLine(false); // no axis line
        left.setDrawGridLines(false); // no grid lines
        left.setDrawZeroLine(false); // draw a zero line
        left.setTextColor(R.color.accent_color);
        barChart.getAxisRight().setEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.setAutoScaleMinMaxEnabled(true);
//                chart.getXAxis().setEnabled(false);
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.getAxisLeft().setDrawLabels(false);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.setFitBars(true); // make the x-axis fit exactly all bars
        barChart.invalidate(); //
//        set.setColors();

//        PieData data = new PieData(set);
//        barChart.setDescription("");
//        barChart.setData(data);
//        barChart.animateXY(1500, 1500);
//        barChart.invalidate(); // refresh
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScrollView = (ObservableScrollView) view.findViewById(R.id.scrollView);

        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);

    }
}
