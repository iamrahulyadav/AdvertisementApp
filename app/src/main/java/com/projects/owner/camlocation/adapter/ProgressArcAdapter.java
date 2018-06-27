package com.projects.owner.camlocation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.projects.owner.camlocation.R;
import com.shinelw.library.ColorArcProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class ProgressArcAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Object> contents;
    final String[] quarters = new String[]{"Q1", "Q2", "Q3", "Q4"};
    final String[] names = new String[]{"Pepsi", "Nesle", "Coke", "Unilever"};
    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;
    private ColorArcProgressBar progress;
    private int i = 0;

    public ProgressArcAdapter(List<Object> contents) {
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
                        .inflate(R.layout.list_item_card_big1, parent, false);
                progress = (ColorArcProgressBar) view.findViewById(R.id.bar1);
                Random r = new Random();
                int i1 = r.nextInt(100 - 40) + 40;
                progress.setCurrentValues(i1);
                progress.setUnit(names[i]);
                if (i == 3) {
                    i = 0;
                } else {
                    i++;
                }
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