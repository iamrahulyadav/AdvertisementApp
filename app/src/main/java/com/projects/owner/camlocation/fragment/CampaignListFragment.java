package com.projects.owner.camlocation.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
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
import com.projects.owner.camlocation.R;
import com.projects.owner.camlocation.adapter.AdapterForHome;
import com.projects.owner.camlocation.adapter.CampaignListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.projects.owner.camlocation.R.id.recyclerView;
import static com.projects.owner.camlocation.R.id.textinput_counter;
import static com.projects.owner.camlocation.R.id.v;
import static com.projects.owner.camlocation.R.id.vendor;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class CampaignListFragment extends Fragment {

    static final boolean GRID_LAYOUT = false;
    private static final int ITEM_COUNT = 4;
    private static final int LOCATION_PERMISSIONS = 1;
    private static RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Object> mContentItems = new ArrayList<>();
    public static Context context;
    public static FragmentManager fragmentManager;

    FirebaseStorage storage;
    StorageReference storageReference;
    ProgressBar prograbar;
    ArrayList<HashMap<String, String>> dataList;


    public static CampaignListFragment newInstance() {
        return new CampaignListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prograbar = (ProgressBar) view.findViewById(R.id.prograbar);
        prograbar.setVisibility(View.VISIBLE);
        dataList = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(recyclerView);
        RecyclerView.LayoutManager layoutManager;
        context = getActivity();
        fragmentManager = getFragmentManager();

        if (GRID_LAYOUT) {
            layoutManager = new GridLayoutManager(getActivity(), 2);
        } else {
            layoutManager = new LinearLayoutManager(getActivity());
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        //Use this now
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());

       /* mAdapter = new CampaignListAdapter(mContentItems);

        //mAdapter = new RecyclerViewMaterialAdapter();
        mRecyclerView.setAdapter(mAdapter);

        for (int i = 0; i < ITEM_COUNT; ++i) {
            mContentItems.add(new Object());
        }
        mAdapter.notifyDataSetChanged();*/

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED )
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                        , Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA}, LOCATION_PERMISSIONS);
                return;
            }
        }


        loadDataFromFirbase();

    }

    public static class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int itemPosition = mRecyclerView.indexOfChild(v);
            TextView date = (TextView) v.findViewById(R.id.date);
            TextView lat = (TextView) v.findViewById(R.id.lat);
            TextView lng = (TextView) v.findViewById(R.id.lng);
            TextView image_url = (TextView) v.findViewById(R.id.image_url);
            TextView userId = (TextView) v.findViewById(R.id.firebase_image_campaign_id);
            TextView images_key = (TextView) v.findViewById(R.id.images_key);


            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_container, CampaignDetailFragment.newInstance(userId.getText().toString(), lat.getText().toString(), lng.getText().toString(), image_url.getText().toString(), images_key.getText().toString()));
            fragmentTransaction.addToBackStack("");
            fragmentTransaction.commit();
        }
    }

    private void loadDataFromFirbase(){

        readintAllDataChile();
    }
    private void readintAllDataChile(){
        DatabaseReference mFirebaseDatabase =  FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = mFirebaseDatabase.child("AdvertismentDb/");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for(DataSnapshot data : dataSnapshot.getChildren()){
                    String keys = data.getKey();
                    Log.e("TAG", "available keys are: " + keys);
                    innerDatais(keys);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
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
                    i++;
                    if (i==1){
                    gettingMetaDataFromImage(keys, counts, key);
                    }
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

                HashMap<String, String>  data = new HashMap<>();
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

             /*   Log.e("TAg", "the size of data is: "  + dataList.size());
                prograbar.setVisibility(View.GONE);
                mAdapter = new AdapterForHome(getActivity(), dataList);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
*/
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
