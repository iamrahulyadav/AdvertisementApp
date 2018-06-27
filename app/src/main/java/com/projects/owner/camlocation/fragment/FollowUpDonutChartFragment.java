package com.projects.owner.camlocation.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.projects.owner.camlocation.R;

import java.util.ArrayList;
import java.util.List;


public class FollowUpDonutChartFragment extends Fragment {
    private ObservableScrollView mScrollView;

    private PieChart pieChart;
    final String[] names = new String[]{"Pepsi", "Nesle", "Coke", "Unilever"};


    public static FollowUpDonutChartFragment newInstance() {
        return new FollowUpDonutChartFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scroll1, container, false);
        pieChart = (PieChart) view.findViewById(R.id.chart);
        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(19, names[0]));
        entries.add(new PieEntry(26, names[1]));
        entries.add(new PieEntry(24, names[2]));
        entries.add(new PieEntry(30, names[3]));

        PieDataSet set = new PieDataSet(entries, "Campaign Results");
        set.setValueLineColor(Color.parseColor("#ffffff"));
        set.setColors(new int[]{R.color.accent_color, R.color.cyan, R.color.red, R.color.green}, getActivity());

        PieData data = new PieData(set);
        pieChart.setDescription("");
        pieChart.setData(data);
        pieChart.animateXY(1500, 1500);
        pieChart.invalidate(); // refresh
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScrollView = (ObservableScrollView) view.findViewById(R.id.scrollView);

        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);

    }
}
