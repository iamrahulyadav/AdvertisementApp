package com.projects.owner.camlocation.adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.projects.owner.camlocation.R;
import com.projects.owner.camlocation.fragment.CampaignListFragment;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Random;


public class CampaignListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> contents;
    private final int[] intres = new int[]{R.drawable.dairy_milk_bus, R.drawable.papsi_add_one, R.drawable.coke, R.drawable.lux_billboard};
    private final String[] quarters = new String[]{"Cadbury", "Pepsi", "Coca-Cola", "Unilever"};
    private final String[] names = new String[]{"Dairy Milk", "PepsiCo.", "Coca-Cola", "Lux"};
    private final String[] catagory = new String[]{"Bus", "Hoarding", "BQS", "Hoarding"};

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CELL = 1;
    private int i = 0;
    private TextView brand, vendor, date, cat;

    public CampaignListAdapter(List<Object> contents) {
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
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card_big2, parent, false);
                view.setOnClickListener(new CampaignListFragment.MyOnClickListener());
                ImageView image = (ImageView) view.findViewById(R.id.image);
                brand = (TextView) view.findViewById(R.id.brand);
                vendor = (TextView) view.findViewById(R.id.vendor);
                date = (TextView) view.findViewById(R.id.date);
                cat = (TextView) view.findViewById(R.id.cat);
                image.setImageResource(intres[i]);
                cat.setText(catagory[i]);
                brand.setText(names[i]);
                vendor.setText(quarters[i]);
                Random r = new Random();
                int i1 = r.nextInt(30 - 1) + 1;
                date.setText(i1 + "/10/2016");



                if (i == 3) {
                    i = 0;
                }
                else {
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

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
/*
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
*/
                break;
            case TYPE_CELL:
                break;
        }
    }
}