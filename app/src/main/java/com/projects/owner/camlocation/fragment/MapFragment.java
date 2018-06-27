package com.projects.owner.camlocation.fragment;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projects.owner.camlocation.Activities.AddCampaignFirstActivity;
import com.projects.owner.camlocation.Activities.MapsActivity;
import com.projects.owner.camlocation.GalleryImageAdapter;
import com.projects.owner.camlocation.Utils.GPSTracker;
import com.projects.owner.camlocation.Listeners.AsyncTaskCompleteListener;
import com.projects.owner.camlocation.R;
import com.projects.owner.camlocation.Services.BaseService;
import com.projects.owner.camlocation.Services.HttpRequester;
import com.projects.owner.camlocation.Utils.SharedPrefs;
import com.projects.owner.camlocation.Utils.Urls;
import com.projects.owner.camlocation.model.ImagesModel;
import com.projects.owner.camlocation.model.MediaModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import gun0912.tedbottompicker.TedBottomPicker;

/**
 * Created by Sanawal Alvi on 10/21/2017.
 */

public class MapFragment extends Fragment implements AsyncTaskCompleteListener, View.OnClickListener, GoogleMap.OnMarkerClickListener{

    ImageView backBtn;
    MapView mMapView;
    private GoogleMap googleMap;
    GPSTracker gpsTracker;
    private ImageView idate, icat, itag, iup;
    private Boolean bdate, bcat, btag, bup;
    private LinearLayout linearLayout;
    BottomNavigationView bottomBar;
    SharedPreferences sharedPreferences;
    Double latitude, longitude;
    JSONArray jsonArray;
    Boolean rickshaw;
    BaseService baseService;
    Button captureBtn;
    Uri downloadUrl;
    String imgThumb;
    Bitmap bitmapImg = null;
    private static final int All_PERMISSIONS = 3;
    String detailViewId, detailType, detailTitle, detailImgURL, detailGuests, detailPrice;
    ImageView markerImg;
    TextView markerTitle, markerGuests, markerPrice, addCabTV, heatMapTV;
    RelativeLayout markerLayout, addCabRL;
    private Uri uri = null;
    List<MediaModel> images;
    LinearLayout sv;
    ImageView iv;
    private static final int CAMERA = 1;
    private static final int LOCATION_PERMISSIONS = 2;

    private static double mLatitude, mLongitude;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bdate = bcat = bup = btag = false;
        sharedPreferences = getActivity().getSharedPreferences(SharedPrefs.PREF_NAME, Context.MODE_PRIVATE);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE,}, All_PERMISSIONS);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_fragment, container, false);
        gpsTracker = new GPSTracker(getActivity());
        baseService = new BaseService(getActivity());
        imgThumb = "";

        markerTitle = (TextView) rootView.findViewById(R.id.markerTitle);
        markerGuests = (TextView) rootView.findViewById(R.id.markerGuests);
        markerPrice = (TextView) rootView.findViewById(R.id.markerPrice);
        markerImg = (ImageView) rootView.findViewById(R.id.markerImg);
        markerLayout = (RelativeLayout) rootView.findViewById(R.id.markerDetailRL);
        markerLayout.setOnClickListener(this);

        captureBtn = (Button) rootView.findViewById(R.id.capture_btn);
        captureBtn.setOnClickListener(this);

        //HttpRequest
        HashMap<String, String> map5 = new HashMap<String, String>();
        map5.put("url", Urls.GET_CAMPAIGN_MEDIA);
        map5.put("CampId", "3");
        new HttpRequester(getActivity(), map5, 4, MapFragment.this);
//        baseService.handleProgressBar(true);
        SharedPrefs.StoreStringPref(sharedPreferences, SharedPrefs.CURRENT_TAB, "all");

        bottomBar = (BottomNavigationView) rootView.findViewById(R.id.bottom_bar);
        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_all:
                        gpsTracker = new GPSTracker(getActivity());
                        SharedPrefs.StoreStringPref(sharedPreferences, SharedPrefs.CURRENT_TAB, "all");
//                        Toast.makeText(getActivity(), "all clicked", Toast.LENGTH_SHORT).show();

                        //HttpRequest
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("url", Urls.GET_CAMPAIGN_MEDIA);
                        map.put("CampId", "3");
                        new HttpRequester(getActivity(), map, 4, MapFragment.this);
                        baseService.handleProgressBar(true);

                        break;
                    case R.id.actions_bqs:
                        gpsTracker = new GPSTracker(getActivity());
                        SharedPrefs.StoreStringPref(sharedPreferences, SharedPrefs.CURRENT_TAB, "cab");

                        //HttpRequest
                        HashMap<String, String> map2 = new HashMap<String, String>();
                        map2.put("url", Urls.GET_CAMPAIGN_MEDIA);
                        map2.put("CampId", "3");
                        new HttpRequester(getActivity(), map2, 4, MapFragment.this);
                        baseService.handleProgressBar(true);

                        break;
                    case R.id.action_boarding:
                        gpsTracker = new GPSTracker(getActivity());
                        SharedPrefs.StoreStringPref(sharedPreferences, SharedPrefs.CURRENT_TAB, "rr");

                        //HttpRequest
                        //HttpRequest
                        HashMap<String, String> map3 = new HashMap<String, String>();
                        map3.put("url", Urls.GET_CAMPAIGN_MEDIA);
                        map3.put("CampId", "3");
                        new HttpRequester(getActivity(), map3, 4, MapFragment.this);
                        baseService.handleProgressBar(true);

                        break;
                    case R.id.action_bus:
                        gpsTracker = new GPSTracker(getActivity());
                        rickshaw = false;
                        SharedPrefs.StoreStringPref(sharedPreferences, SharedPrefs.CURRENT_TAB, "rv");

                        //HttpRequest
                        HashMap<String, String> map4 = new HashMap<String, String>();
                        map4.put("url", Urls.GET_CAMPAIGN_MEDIA);
                        map4.put("CampId", "3");
                        new HttpRequester(getActivity(), map4, 4, MapFragment.this);
                        baseService.handleProgressBar(true);

                        break;
                    case R.id.action_rickshaw:
                        gpsTracker = new GPSTracker(getActivity());
                        rickshaw = true;
                        SharedPrefs.StoreStringPref(sharedPreferences, SharedPrefs.CURRENT_TAB, "rv");
                        //HttpRequest
                        HashMap<String, String> map5 = new HashMap<String, String>();
                        map5.put("url", Urls.GET_CAMPAIGN_MEDIA);
                        map5.put("CampId", "3");
                        new HttpRequester(getActivity(), map5, 4, MapFragment.this);
                        baseService.handleProgressBar(true);
                        break;
                }
                return true;
            }
        });


/*        images = new int[]{R.drawable.pepsi, R.drawable.pepsi_people, R.drawable.pepsi_people_two,
                R.drawable.pepsi_people_three, R.drawable.pepsi_people_four};*/

        sv = (LinearLayout) rootView.findViewById (R.id.id_gallery);

        backBtn = (ImageView) rootView.findViewById(R.id.back_arrow_Img);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(MapFragment.this).commit();
            }
        });

        linearLayout = (LinearLayout) rootView.findViewById(R.id.filtermenu);
        iup = (ImageView) rootView.findViewById(R.id.iup);
        idate = (ImageView) rootView.findViewById(R.id.idate);
        icat = (ImageView) rootView.findViewById(R.id.icat);
        itag = (ImageView) rootView.findViewById(R.id.itag);

        iup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bup) {
                    bup = false;
                    iup.animate().rotation(0).setDuration(0).start();
                    linearLayout.animate().translationY(0).setDuration(1000).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            linearLayout.setVisibility(View.VISIBLE);

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).start();
                } else {
                    bup = true;
                    iup.animate().rotation(180).setDuration(0).start();
                    linearLayout.animate().translationY(-200).setDuration(1000).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            linearLayout.setVisibility(View.GONE);

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).start();
                }
            }
        });

        idate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
//                cal.add(Calendar.YEAR, YEAR_MINUS);
                com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance
                        (new com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener()
                         {
                              @Override
                              public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
                              {

                              }
                         },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setVersion(com.wdullaer.materialdatetimepicker.date.DatePickerDialog.Version.VERSION_1);
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
            }
        });

        icat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        itag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        int visibility = markerLayout.getVisibility();
                        if (visibility == View.VISIBLE) {
                            markerLayout.setVisibility(View.GONE);
                        }
                    }
                });

                // For showing a move to my location button
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
                googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map

                //LatLng latLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                LatLng latLng = new LatLng(mLatitude, mLongitude);

//                googleMap.addMarker(new MarkerOptions().position(latLng).title("Marker Title").snippet("Marker Description"));
                // For zooming automatically to the location of the marker

                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                /*MarkerOptions marker = new MarkerOptions();
                marker.title("Campaign Location");
                marker.position(latLng);
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.bqs_pin));*/
                googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.bqs_pin)).title("Hello Hello"));

            }
        });

        View locationButton = ((View) rootView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
// position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 100, 10, 0);

        View compassButton = ((View) rootView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("5"));
        RelativeLayout.LayoutParams crlp = (RelativeLayout.LayoutParams) compassButton.getLayoutParams();
// position on right bottom
        crlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        crlp.setMargins(0, 100, 10, 0);



        return rootView;
    } // onCreateView finished

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void populateImagesOnHorizontalBar(List<MediaModel> imageList)
    {
        sv.removeAllViews();
        Log.d("xxxx", String.valueOf(imageList.size()));
        for (int i=0 ; i<imageList.size(); i++){
            iv = new ImageView (getActivity());
            new DownloadImageTask(iv).execute(imageList.get(i).getUrl());
//            iv.setBackgroundResource ();
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
            int minHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, minHeight);
            iv.setLayoutParams(layoutParams);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            /*iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });*/
            sv.addView(iv);
        }
        sv.setOnClickListener(this);
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        if (serviceCode == 4)
        {
            try
            {
                baseService.dismissProgressDialog();
                Log.d("xxx", response);
                googleMap.clear();
//                BaseService.dismissProgressDialog();
                JSONObject obj = new JSONObject(response);
                jsonArray = new JSONArray();
                jsonArray = obj.getJSONArray("Table");
                images = new ArrayList<MediaModel>();
                int len = jsonArray.length();
                for (int i=0; i<jsonArray.length(); i++)
                {
                    if (i!=10 && i<11)
                    {
                        JSONObject mediaObj = jsonArray.getJSONObject(len-1);
                        images.add(new MediaModel(mediaObj.get("CM_Id").toString(), mediaObj.get("URL").toString(),
                                mediaObj.get("CM_Type").toString(), mediaObj.get("CM_Lat").toString(),
                                mediaObj.get("CM_Long").toString()));
                        len--;
                    }

                        String viewId = jsonArray.getJSONObject(i).get("CM_Id").toString();
                        String lat = jsonArray.getJSONObject(i).get("CM_Lat").toString();
                        String lng = jsonArray.getJSONObject(i).get("CM_Long").toString();
                        String objType = jsonArray.getJSONObject(i).get("CM_Type").toString();
                        //set marker
                        LatLng latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                        if (SharedPrefs.getStringPref(sharedPreferences, SharedPrefs.CURRENT_TAB).equals("all"))
                        {
                            if (objType.equals("image"))
                                googleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.bqs_pin)).title(jsonArray.getJSONObject(i).get("CM_Type").toString())).setTag(viewId);
                            if (objType.equals("image"))
                                googleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.board_pin)).title(jsonArray.getJSONObject(i).get("CM_Type").toString())).setTag(viewId);
                            if (objType.equals("image"))
                                googleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_pin)).title(jsonArray.getJSONObject(i).get("CM_Type").toString())).setTag(viewId);

                            googleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.rickshaw_pin)).title(jsonArray.getJSONObject(i).get("CM_Type").toString())).setTag(viewId);
                        }
                        if (SharedPrefs.getStringPref(sharedPreferences, SharedPrefs.CURRENT_TAB).equals("cab"))
                        {
                            googleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.bqs_pin)).title(jsonArray.getJSONObject(i).get("CM_Type").toString())).setTag(viewId);
                        }
                        if (SharedPrefs.getStringPref(sharedPreferences, SharedPrefs.CURRENT_TAB).equals("rr"))
                        {
                            googleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.board_pin)).title(jsonArray.getJSONObject(i).get("CM_Type").toString())).setTag(viewId);
                        }
                        if (SharedPrefs.getStringPref(sharedPreferences, SharedPrefs.CURRENT_TAB).equals("rv"))
                        {
                            if (rickshaw)
                            {
                                googleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.rickshaw_pin)).title(jsonArray.getJSONObject(i).get("CM_Type").toString())).setTag(viewId);
                            }
                            else
                            {
                                googleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_pin)).title(jsonArray.getJSONObject(i).get("CM_Type").toString())).setTag(viewId);
                            }
                        }

                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            int position = Integer.parseInt(marker.getTag().toString());
//                Toast.makeText(MapsActivity.this, ""+position, Toast.LENGTH_SHORT).show();
                            for (int i=0; i<jsonArray.length() ; i++) {
                                try {
                                    detailViewId = jsonArray.getJSONObject(i).get("CM_Id").toString();
                                    if(detailViewId.equals(String.valueOf(position)))
                                    {
                                        detailTitle = jsonArray.getJSONObject(i).get("CM_Type").toString();
                                        detailImgURL = jsonArray.getJSONObject(i).get("URL").toString();

                                        //setmarker image through downloading URL
                                        new DownloadImageTask(markerImg).execute(detailImgURL);
                                        markerTitle.setText(detailTitle);
//                                    markerGuests.setText(detailGuests);
//                                    markerPrice.setText(detailPrice);
                                        markerLayout.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            return false;
                        }
                    });

                }
                populateImagesOnHorizontalBar(images);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        if (serviceCode == 3)
        {
            try
            {
                baseService.dismissProgressDialog();
                Log.d("xxx", response);
//                googleMap.clear();
//                BaseService.dismissProgressDialog();
                JSONObject obj = new JSONObject(response);
                jsonArray = new JSONArray();
                jsonArray = obj.getJSONArray("Table");
                if (jsonArray.length()>0)
                {
                    if (jsonArray.getJSONObject(0).get("IsSuccess").equals("True"))
                    {
                        Toast.makeText(getActivity(), "Media added successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
                //HttpRequest
                HashMap<String, String> map5 = new HashMap<String, String>();
                map5.put("url", Urls.GET_CAMPAIGN_MEDIA);
                map5.put("CampId", "3");
                new HttpRequester(getActivity(), map5, 4, MapFragment.this);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        if(serviceCode == 1)
        {
            try {
                baseService.dismissProgressDialog();
                Log.d("xxx", response);
                googleMap.clear();
//                BaseService.dismissProgressDialog();
                JSONObject obj = new JSONObject(response);
                jsonArray = new JSONArray();
                jsonArray = obj.getJSONArray("Table");
                for (int i=0; i<jsonArray.length() ; i++)
                {
                    String viewId = jsonArray.getJSONObject(i).get("CM_Id").toString();
                    String lat = jsonArray.getJSONObject(i).get("CM_Lat").toString();
                    String lng = jsonArray.getJSONObject(i).get("CM_Long").toString();
                    String objType = jsonArray.getJSONObject(i).get("CM_Type").toString();
                    //set marker
                    LatLng latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                    if (SharedPrefs.getStringPref(sharedPreferences, SharedPrefs.CURRENT_TAB).equals("all"))
                    {
                        if (objType.equals("Cab"))
                            googleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.bqs_pin)).title(jsonArray.getJSONObject(i).get("V_Title").toString())).setTag(viewId);
                        if (objType.equals("rr"))
                            googleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.board_pin)).title(jsonArray.getJSONObject(i).get("V_Title").toString())).setTag(viewId);
                        if (objType.equals("rv"))
                            googleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_pin)).title(jsonArray.getJSONObject(i).get("V_Title").toString())).setTag(viewId);

                        googleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.rickshaw_pin)).title(jsonArray.getJSONObject(i).get("V_Title").toString())).setTag(viewId);
                    }
                    if (SharedPrefs.getStringPref(sharedPreferences, SharedPrefs.CURRENT_TAB).equals("cab"))
                    {
                        googleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.bqs_pin)).title(jsonArray.getJSONObject(i).get("V_Title").toString())).setTag(viewId);
                    }
                    if (SharedPrefs.getStringPref(sharedPreferences, SharedPrefs.CURRENT_TAB).equals("rr"))
                    {
                        googleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.board_pin)).title(jsonArray.getJSONObject(i).get("V_Title").toString())).setTag(viewId);
                    }
                    if (SharedPrefs.getStringPref(sharedPreferences, SharedPrefs.CURRENT_TAB).equals("rv"))
                    {
                        if (rickshaw)
                        {
                            googleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.rickshaw_pin)).title(jsonArray.getJSONObject(i).get("V_Title").toString())).setTag(viewId);
                        }
                        else
                        {
                            googleMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_pin)).title(jsonArray.getJSONObject(i).get("V_Title").toString())).setTag(viewId);
                        }
                    }
                }

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        int position = Integer.parseInt(marker.getTag().toString());
//                Toast.makeText(MapsActivity.this, ""+position, Toast.LENGTH_SHORT).show();
                        for (int i=0; i<jsonArray.length() ; i++) {
                            try {
                                detailViewId = jsonArray.getJSONObject(i).get("CM_Id").toString();
                                if(detailViewId.equals(String.valueOf(position)))
                                {
                                    detailTitle = jsonArray.getJSONObject(i).get("CM_Type").toString();
                                    detailImgURL = jsonArray.getJSONObject(i).get("URL").toString();

                                    //setmarker image through downloading URL
                                    new DownloadImageTask(markerImg).execute(detailImgURL);
                                    markerTitle.setText(detailTitle);
//                                    markerGuests.setText(detailGuests);
//                                    markerPrice.setText(detailPrice);
                                    markerLayout.setVisibility(View.VISIBLE);
                                }
                                else
                                {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        return false;
                    }
                });

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.id_gallery:
            /*    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_container, CampaignDetailFragment.newInstance());
                fragmentTransaction.addToBackStack("");
                fragmentTransaction.commit();*/
                break;
            case R.id.capture_btn:
/*                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 11, null);*/

/*                TedBottomPicker tbp = new TedBottomPicker.Builder(getActivity()).setOnMultiImageSelectedListener(
                        new TedBottomPicker.OnMultiImageSelectedListener() {
                            @Override
                            public void onImagesSelected(ArrayList<Uri> uriList) {
                                uploadImagestoFireBase(uriList);
                            }
                        }
                )
                        .setPeekHeight(1600)
                        .showTitle(false)
                        .setCompleteButtonText("Done")
                        .setEmptySelectionText("No Image selected yet, Click by Camera or select from Gallery")
                        .setSelectMaxCount(1)
                        .setSelectMinCount(1)
                        .create();
                tbp.show(getActivity().getSupportFragmentManager());*/

                takePhotoFromCamera();

                break;
        }
    }

    public void takePhotoFromCamera() {
        Calendar cal = Calendar.getInstance();
        File file;
        file = new File(Environment.getExternalStorageDirectory(), (cal.getTimeInMillis() + ".jpg"));

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        uri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", file);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(cameraIntent, 113);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 113) {
            if (uri != null) {
               /* Bitmap photo = (Bitmap) data.getExtras().get("data");
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(getActivity().getApplicationContext(), photo);
                Log.d("xxx", tempUri.toString());
                uploadImagestoFireBase(tempUri);*/
            }
    }
    }

    protected void uploadImagestoFireBase(Uri imageUri)
    {
        baseService.handleProgressBar(true);
        Uri contentURI = null;
        try {
            for (int i = 0; i < 1; i++) {
                contentURI = imageUri;
                Log.d("xxx", contentURI.toString());
                try
                {
                    bitmapImg = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference mountainImagesRef = storageRef.child("Campaigns" + "/" + "Images" + "/" + Calendar.getInstance().getTimeInMillis()
                        + ".jpg");
                //Log.d("xxx", mountainImagesRef.toString());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmapImg.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] byte_data = baos.toByteArray();
                UploadTask uploadTask = mountainImagesRef.putBytes(byte_data);
                final int finalI = i;
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        downloadUrl = taskSnapshot.getDownloadUrl();
                        //sendMsg("" + downloadUrl, 2);
                        Log.d("downloadUrl-->", "" + downloadUrl);
                        if (finalI == 0)
                        {
                            baseService.dismissProgressDialog();
                            imgThumb = downloadUrl.toString();
                            Toast.makeText(getActivity(), "Image uploaded successfully!", Toast.LENGTH_SHORT).show();

                            //HttpRequest
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("url", Urls.ADD_CAMPAIGN_MEDIA);
                            map.put("CampId", "3");
                            map.put("Uid", "1235");
                            map.put("URL", downloadUrl.toString());
                            map.put("Type", "image");
                            map.put("MediaLat", String.valueOf(gpsTracker.getLatitude()));
                            map.put("MediaLong", String.valueOf(gpsTracker.getLongitude()));
                            map.put("City", getAddressFromLatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()));
                            new HttpRequester(getActivity(), map, 3, MapFragment.this);
                            baseService.handleProgressBar(true);
                        }
                    }
                });
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getAddressFromLatLng(double lat, double lng)
    {
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        double latitude = lat;
        double longitude = lng;
        String address = "";
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            try {
                bmImage.setImageBitmap(result);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
