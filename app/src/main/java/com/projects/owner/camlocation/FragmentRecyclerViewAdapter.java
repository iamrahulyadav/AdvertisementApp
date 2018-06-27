package com.projects.owner.camlocation;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.projects.owner.camlocation.model.ImagesModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by owner on 22/09/2016.
 */
public class FragmentRecyclerViewAdapter extends RecyclerView.Adapter<FragmentRecyclerViewAdapter.View_Holder> implements ItemTouchHelperAdapter {
    List<ImagesModel> list = Collections.emptyList();
    Context context;

    public FragmentRecyclerViewAdapter(List<ImagesModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list_view_row, parent, false);
        return new View_Holder(v);
    }

    @Override
    public void onBindViewHolder(View_Holder holder, int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.brandName.setText(list.get(position).getBrandName());
        holder.catagory.setText(list.get(position).getCatagory());
        holder.agency.setText(list.get(position).getAgency());
        holder.size.setText(list.get(position).getSize());
        holder.vendor.setText(list.get(position).getVendor());
        holder.rating.setText(list.get(position).getRating());
        holder.date.setText(list.get(position).getDate());

        setAnimation(holder.cardView, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
        viewToAnimate.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onItemDismiss(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(list, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(list, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public class View_Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView brandName;
        private TextView catagory;
        private TextView agency;
        private TextView size;
        private TextView vendor;
        private TextView rating;
        private TextView date;
        private CardView cardView;
        private List<ImagesModel> imagesModels = new ArrayList<>();

        public View_Holder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            brandName = (TextView) itemView.findViewById(R.id.brandname);
            catagory = (TextView) itemView.findViewById(R.id.catagory);
            agency = (TextView) itemView.findViewById(R.id.agency);
            size = (TextView) itemView.findViewById(R.id.size);
            vendor = (TextView) itemView.findViewById(R.id.vendor);
            rating = (TextView) itemView.findViewById(R.id.rating);
            date = (TextView) itemView.findViewById(R.id.date);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                // We can access the data within the views
//                Toast.makeText(context, imagesModels.getText(), Toast.LENGTH_SHORT).show();

            }
        }
    }
}
