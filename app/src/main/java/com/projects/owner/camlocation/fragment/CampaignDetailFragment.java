package com.projects.owner.camlocation.fragment;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.projects.owner.camlocation.Listeners.RecyclerItemClickListener;
import com.projects.owner.camlocation.R;
import com.projects.owner.camlocation.adapter.AdapterForHome;
import com.projects.owner.camlocation.adapter.CampaignDetailImagesVideosAdapter;
import com.projects.owner.camlocation.adapter.CampaignListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import static com.projects.owner.camlocation.R.id.play;
import static com.projects.owner.camlocation.R.id.recyclerView;
import static com.projects.owner.camlocation.R.id.relativeLayout;

/**
 * Created by Sanawal Alvi on 10/23/2017.
 */

public class CampaignDetailFragment extends Fragment implements View.OnClickListener{

    private TextView city, brandName, subBrandName, totalImages, totalVideos, startingDate, endDate;
    private ImageView backgroundImage;
    public static FragmentManager fragmentManager;
    private static RecyclerView mRecyclerView;
    private static RecyclerView.Adapter mAdapter;
    ImageView backBtn, images, videos;
    SimpleExoPlayerView exoPlayer;
    SimpleExoPlayer player;
    RelativeLayout headerRL;
    ScrollView scrollView;
    String urid, lati, longi;

    ArrayList<HashMap<String, String>> dataList = new ArrayList<>();

    private static FirebaseStorage storage;
   public static String imageId;

    public static CampaignDetailFragment newInstance(String userid, String lat, String lng, String imageUrl, String imagesKey)
    {
        Log.e("TAg", "here is image or user id: " + userid);
        Log.e("TAg", "here is image or user id: " + imageUrl);
        Log.e("TAg", "here is image or user id: " + lat);
        Log.e("TAg", "here is image or user id: " + lng);
        Log.e("TAg", "here is image or user id: " + imagesKey);
        imageId = imagesKey;

    /*    dataList.add(userid);
        dataList.add(imageUrl);
        dataList.add(lat);
        dataList.add(lng);
        dataList.add(imagesKey);*/


        return new CampaignDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.detail_view_layout, container, false);
        backBtn = (ImageView) view.findViewById(R.id.back_arrow_Img);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(CampaignDetailFragment.this).commit();
            }
        });

        headerRL = (RelativeLayout) view.findViewById(R.id.header_RL);
        exoPlayer = (SimpleExoPlayerView) view.findViewById(R.id.video_view);
        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.image_view_recycler_view);
        fragmentManager = getFragmentManager();
        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());

        innerDatais(CampaignDetailFragment.imageId);
      //  mAdapter = new CampaignDetailImagesVideosAdapter(getActivity(), fragmentManager, dataList);

/*        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        if(position==3)
                        {
                            scrollView.smoothScrollTo(0,0);
                            exoPlayer.setVisibility(View.VISIBLE);
                            exoPlayer.setPlayer(player);
                            player.setPlayWhenReady(true);
                            headerRL.setVisibility(View.GONE);
                        }
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );*/

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        images = (ImageView) view.findViewById(R.id.c);
        images.setOnClickListener(this);
        videos = (ImageView) view.findViewById(R.id.v);
        videos.setOnClickListener(this);

        city = (TextView) view.findViewById(R.id.city);
        subBrandName = (TextView) view.findViewById(R.id.sub_brand_name);
        totalImages = (TextView) view.findViewById(R.id.total_images);
        totalVideos = (TextView) view.findViewById(R.id.total_videos);
/*        startingDate = (TextView) view.findViewById(R.id.start_date);
        endDate = (TextView) view.findViewById(R.id.end_date);*/
        backgroundImage = (ImageView) view.findViewById(R.id.background_img);

        Handler handler = new Handler();
        player = ExoPlayerFactory.newSimpleInstance(getActivity(), new DefaultTrackSelector(handler), new DefaultLoadControl());

        initializePlayer();

/*        exoPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoPlayer.setUseController(false);
            }
        });
        exoPlayer.setControllerVisibilityListener(new PlaybackControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {

            }
        });*/

        //player.seekTo(currentWindow, playbackPosition);

    }

    private void initializePlayer() {
        Uri uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/gotukxisupply.appspot.com/o/%2B923074903898%2FVideoRecordings%2Fvideoplayback.mp4?alt=media&token=c6a8dc6a-c7be-4891-8460-dfd96a419e2d"); // put your url here
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("ua"),
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.c:
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.listview_container, ImagesNotificationDetailListFragment.newInstance());
                fragmentTransaction.addToBackStack("");
                fragmentTransaction.commit();
                break;
            case R.id.v:
                FragmentTransaction fragmentTransaction2 = getChildFragmentManager().beginTransaction();
                fragmentTransaction2.replace(R.id.listview_container, ImagesNotificationDetailListFragment.newInstance());
                fragmentTransaction2.addToBackStack("");
                fragmentTransaction2.commit();
                break;
        }
    }

/*    public static class MyLikeClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
//            int itemPosition = mRecyclerView.indexOfChild(v);
        }
    }*/

    private void innerDatais(final String key){
        DatabaseReference mFirebaseDatabase =  FirebaseDatabase.getInstance().getReference();

        DatabaseReference ref = mFirebaseDatabase.child("AdvertismentDb/"+key);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long coutner = dataSnapshot.getChildrenCount();
                String counts = String.valueOf(coutner);
                Log.e("TAG", "the counter is: " + dataSnapshot.getChildrenCount());


                // gettingMetaDataFromImage(data1);
                int i = 0;
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    String keys = data.getKey();
                        gettingMetaDataFromImage(keys, counts, key);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void gettingMetaDataFromImage(String imageId, final String counter, final String key){

        // Create a storage reference from our app
        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
// Get reference to the file

        StorageReference forestRef = storageRef.child("Campaigns/"+imageId);

        forestRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {

                Log.e("TAg", "the metadata is: " + storageMetadata.getDownloadUrl());
                Log.e("TAg", "the metadata is: " + storageMetadata.getCustomMetadata("lng"));
                Log.e("TAg", "the metadata is: " + storageMetadata.getCustomMetadata("lat"));
                Log.e("TAg", "the metadata is: " + storageMetadata.getCustomMetadata("detail"));
                Log.e("TAg", "the metadata is: " + storageMetadata.getCustomMetadata("city"));
                Log.e("TAg", "the metadata is: " + storageMetadata.getCustomMetadata("address"));
                Log.e("TAg", "the metadata is: " + storageMetadata.getCustomMetadata("brand"));
                Log.e("TAg", "the metadata is: " + storageMetadata.getCustomMetadata("Vendar"));
                Log.e("TAg", "the metadata is: " + storageMetadata.getCustomMetadata("category"));
                Log.e("TAg", "the metadata is: " + storageMetadata.getCustomMetadata("start_date"));
                Log.e("TAg", "the metadata is: " + storageMetadata.getCustomMetadata("end_date"));

                String lat = storageMetadata.getCustomMetadata("lat").toString();
                String lng = storageMetadata.getCustomMetadata("lng").toString();
                String detail = storageMetadata.getCustomMetadata("detail").toString();
                String city = storageMetadata.getCustomMetadata("city").toString();
                String address = storageMetadata.getCustomMetadata("address").toString();
                String brand = storageMetadata.getCustomMetadata("brand").toString();
                String subBrand = storageMetadata.getCustomMetadata("subBrand").toString();
                String Vendar = storageMetadata.getCustomMetadata("vendor").toString();
                String category = storageMetadata.getCustomMetadata("category").toString();
                String time = storageMetadata.getCustomMetadata("createdTime").toString();
                String userID = storageMetadata.getCustomMetadata("userId").toString();
                String imageUrl = storageMetadata.getDownloadUrl().toString();

                HashMap<String, String> data = new HashMap<>();
                data.put("lat", lat);
                data.put("lng", lng);
                data.put("detail", detail);
                data.put("city", city);
                data.put("address", address);
                data.put("brand", brand);
                data.put("subBraind", subBrand);
                data.put("Vendar", Vendar);
                data.put("category", category);
                data.put("time", time);
                data.put("userID", userID);
                data.put("imageUrl", imageUrl);
                data.put("count", counter);
                data.put("key", key);
                //adding into dataList
                dataList.add(data);

                Log.e("TAg", "the size of data is: "  + dataList.size());
             /*   prograbar.setVisibility(View.GONE);
                mAdapter = new AdapterForHome(getActivity(), dataList);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();*/

                // Metadata now contains the metadata for 'images/forest.jpg'
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }

}
