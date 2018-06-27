package com.projects.owner.camlocation.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.projects.owner.camlocation.R;
import com.projects.owner.camlocation.fragment.MapFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CampaignDetailImagesVideosAdapter extends RecyclerView.Adapter<CampaignDetailImagesVideosAdapter.MyViewHolder> {

    SimpleExoPlayer player;
    private List<Object> contents;
    Context context;
    FragmentManager fragmentManager;
    private final int[] intres = new int[]{R.drawable.pepsi_add_one, R.drawable.pepsi_add_two, R.drawable.pepsi_people_two, R.drawable.pepsi_add_three};
    private final String[] addresses = new String[]{"ARFA karim software technology park, Ferozpur road, Lahore","Model Town, Lahore",
            "Wapda Town, Lahore","star chowk, BadamiBagh, Lahore"};

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CELL = 1;
    private int i = 0;
    private TextView brand, vendor, date, cat;

    ArrayList<HashMap<String, String>> mDataList;

    public CampaignDetailImagesVideosAdapter(Context con, FragmentManager frgmntManager, ArrayList<HashMap<String, String>> dataList) {
//        this.contents = contents;
        fragmentManager = frgmntManager;
        mDataList = dataList;
        context = con;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image, likeIcon, shareIcon, mapIcon;
        TextView address;
        SimpleExoPlayerView exoPlayer;

        public MyViewHolder(View view) {
            super(view);

            //view.setOnClickListener(new CampaignListFragment.MyOnClickListener());
            image = (ImageView) view.findViewById(R.id.content);
            likeIcon = (ImageView) view.findViewById(R.id.likeLL);
            shareIcon = (ImageView) view.findViewById(R.id.shareLL);
            mapIcon = (ImageView) view.findViewById(R.id.map_icon);
            address = (TextView) view.findViewById(R.id.address);
            exoPlayer = (SimpleExoPlayerView) view.findViewById(R.id.video_view);
        }
    }

    @Override
    public int getItemCount() {
//        return contents.size();
        return 4;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_view_recycler_view_layout, parent, false);

        Handler handler = new Handler();
        player = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector(handler), new DefaultLoadControl());

        initializePlayer();

        return new MyViewHolder(view) {};
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.image.setBackgroundResource(intres[position]);
        if (position == 3)
        {
            holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.play_icon));
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.image.setVisibility(View.GONE);
                    holder.exoPlayer.setVisibility(View.VISIBLE);
                    holder.exoPlayer.setPlayer(player);
                    player.setPlayWhenReady(true);
                    holder.address.setVisibility(View.GONE);
                }
            });
        }

        holder.address.setText(addresses[position]);

            holder.likeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.likeIcon.setImageResource(R.drawable.like_icon_colored);
                }
            });
        holder.shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do share
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String url = "http://www.nzcf.org.nz/wp-content/uploads/advertising.jpg";
                intent.putExtra(Intent.EXTRA_TEXT, url);
                context.startActivity(Intent.createChooser(intent, "Select"));
            }
        });
        holder.mapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   String lat = mDataList.get(2);
                String lng = mDataList.get(3);
                double latitude = Double.parseDouble(lat);
                double longitude = Double.parseDouble(lng);
                Log.e("TAG", "wow lat is: " + latitude);
                Log.e("TAG", "wow lat is: " + longitude);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_container, MapFragment.newInstance(latitude, longitude));
                fragmentTransaction.addToBackStack("");
                fragmentTransaction.commit();*/
            }
        });
    }

    private void initializePlayer() {
        Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/gotukxisupply.appspot.com/o/%2B923074903898%2FVideoRecordings%2Fvideoplayback.mp4?alt=media&token=c6a8dc6a-c7be-4891-8460-dfd96a419e2d"); // put your url here
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri, new DefaultHttpDataSourceFactory("ua"),
                new DefaultExtractorsFactory(), null, null);
    }

    private void releasePlayer() {
        if (player != null) {
/*            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();*/
            player.release();
            player = null;
        }
    }
}