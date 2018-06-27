package com.projects.owner.camlocation.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.projects.owner.camlocation.R;
import com.projects.owner.camlocation.fragment.CampaignListFragment;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by gold on 6/22/2018.
 */

public class AdapterForHome  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<HashMap<String, String>> arrayList;
    Activity mContext;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CELL = 1;
    private int i = 0;
    private TextView brand, sub_brand, date, cat;
    TextView lat, lng, image_url, detail, city, user_id;
    TextView tv_total_count, images_key;

    public AdapterForHome(Activity activity, ArrayList<HashMap<String, String>> arrayList) {
        this.mContext = activity;
        this.arrayList = arrayList;
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
        return arrayList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case TYPE_HEADER: {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card_big2, parent, false);

                ImageView image = (ImageView) view.findViewById(R.id.image);
                brand = (TextView) view.findViewById(R.id.brand);
                sub_brand = (TextView) view.findViewById(R.id.sub_brand);
                date = (TextView) view.findViewById(R.id.date);
                tv_total_count = (TextView) view.findViewById(R.id.tv_total_count);
                images_key = (TextView) view.findViewById(R.id.images_key);


                cat = (TextView) view.findViewById(R.id.cat);
                //cat.setText(arrayList.get(i).get("category"));
                lat = (TextView) view.findViewById(R.id.lat);
                lng = (TextView) view.findViewById(R.id.lng);
                image_url = (TextView) view.findViewById(R.id.image_url);
                detail = (TextView) view.findViewById(R.id.detail);
                city = (TextView) view.findViewById(R.id.city);
                user_id = (TextView) view.findViewById(R.id.firebase_image_campaign_id);

                String mLat = arrayList.get(i).get("lat").toString();
                String mLng = arrayList.get(i).get("lng").toString();
                String mDetail = arrayList.get(i).get("detail").toString();
                String mCity = arrayList.get(i).get("city").toString();
                String mAddress = arrayList.get(i).get("address").toString();
                String mBrand = arrayList.get(i).get("brand").toString();
                String mSubBrand = arrayList.get(i).get("subBraind").toString();
                String mVendar = arrayList.get(i).get("Vendar").toString();
                String mCategory = arrayList.get(i).get("category").toString();
                String mTime = arrayList.get(i).get("time").toString();
                String mUserID = arrayList.get(i).get("userID").toString();
                String mImageUrl = arrayList.get(i).get("imageUrl").toString();
                String counter = arrayList.get(i).get("count");
                String keyIs = arrayList.get(i).get("key");

                lat.setText(mLat);
                lng.setText(mLng);
                tv_total_count.setText(counter);
                tv_total_count.bringToFront();
                date.setText(mTime);
                image_url.setText(mImageUrl);
                user_id.setText(mUserID);
                cat.setText(mCategory);
                brand.setText(mBrand);
                sub_brand.setText(mSubBrand);
                images_key.setText(keyIs);
                Picasso.with(mContext)
                        .load(arrayList.get(i).get("imageUrl"))
                      //  .placeholder(R.drawable.amu_bubble_shadow)
                        //.fit()
                        .into(image);

                view.setOnClickListener(new CampaignListFragment.MyOnClickListener());

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