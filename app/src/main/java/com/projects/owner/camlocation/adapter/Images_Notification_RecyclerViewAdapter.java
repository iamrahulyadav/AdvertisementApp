package com.projects.owner.camlocation.adapter;

    import android.content.Context;
    import android.support.v7.widget.RecyclerView;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.TextView;
    import com.projects.owner.camlocation.R;
    import java.util.List;

public class Images_Notification_RecyclerViewAdapter extends RecyclerView.Adapter<Images_Notification_RecyclerViewAdapter.MyViewHolder> {

    private List<Object> contents;
    Context context;

    public Images_Notification_RecyclerViewAdapter(Context con) {
//        this.contents = contents;
        context = con;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image, likeIcon, shareIcon, mapIcon;
        TextView address;

        public MyViewHolder(View view) {
            super(view);

            //view.setOnClickListener(new CampaignListFragment.MyOnClickListener());
/*            image = (ImageView) view.findViewById(R.id.content);
            likeIcon = (ImageView) view.findViewById(R.id.likeLL);
            shareIcon = (ImageView) view.findViewById(R.id.shareLL);
            mapIcon = (ImageView) view.findViewById(R.id.map_icon);
            address = (TextView) view.findViewById(R.id.address);*/
        }
    }

    @Override
    public int getItemCount() {
//        return contents.size();
        return 12;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.images_notification_detail_adapter_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

    }
}