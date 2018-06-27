package com.projects.owner.camlocation.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.projects.owner.camlocation.adapter.PlaceArrayAdapter;
import com.projects.owner.camlocation.Listeners.AsyncTaskCompleteListener;
import com.projects.owner.camlocation.R;
import com.projects.owner.camlocation.Utils.GPSTracker;
import com.projects.owner.camlocation.Utils.SharedPrefs;
import com.projects.owner.camlocation.Utils.Utils;

import org.json.JSONArray;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

/**
 * Created by Sanawal Alvi on 9/22/2017.
 */

public class AddCampaignLocation extends FragmentActivity implements OnMapReadyCallback, AsyncTaskCompleteListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    SupportMapFragment mapFragment;
    GoogleMap mMap;
    ImageView backBtn, addIcon;
    GPSTracker gpsTracker;
    SharedPreferences sharedPreferences;
    RelativeLayout nextBtn;
    LinearLayout startBotomBar, endBottomBar;
    RelativeLayout startBtn, alertBtn, endRL;
    JSONArray jsonArray;
    String latitude, longitude;
    AutoCompleteTextView searchAutoCompleteTextView;
    private Geocoder geocoder;
    private List<Address> addresses;

    private static final int GOOGLE_API_CLIENT_ID = 0;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(31.398160, 74.180831), new LatLng(31.430610, 74.972090));

    public static AddCampaignLocation getInstance()
    {
        return new AddCampaignLocation();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_campaign_location);
        sharedPreferences = getSharedPreferences(SharedPrefs.PREF_NAME, Context.MODE_PRIVATE);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.trip_map);
        mapFragment.getMapAsync(this);

        backBtn = (ImageView) findViewById(R.id.back_arrow_Img);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        gpsTracker = new GPSTracker(this);

        addIcon = (ImageView) (ImageView) findViewById(R.id.add_icon);
        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAutoCompleteTextView.setText("");
            }
        });

        nextBtn = (RelativeLayout) findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SharedPrefs.StoreStringPref(sharedPreferences, SharedPrefs.ADD_R_ADDRESS, searchAutoCompleteTextView.getText().toString());
                Toast.makeText(AddCampaignLocation.this, "Campaign added successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddCampaignLocation.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(AddCampaignLocation.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        searchAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.auto_complete_search_bar);
        searchAutoCompleteTextView.setThreshold(2);
        searchAutoCompleteTextView.setDropDownBackgroundResource(R.color.dark_grey);
        searchAutoCompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
//        AutocompleteFilter filter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES).build();
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1, BOUNDS_MOUNTAIN_VIEW, null);
        searchAutoCompleteTextView.setAdapter(mPlaceArrayAdapter);


        searchAutoCompleteTextView.clearFocus();
        searchAutoCompleteTextView.setCursorVisible(false);
        searchAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAutoCompleteTextView.setCursorVisible(true);
            }
        });
    }

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e("Location", "Place query did not complete. Error: " + places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);

            Log.i("name", place.getName().toString());
            Log.i("coordinates", place.getLatLng().toString());

            mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));

            geocoder = new Geocoder(AddCampaignLocation.this, Locale.getDefault());
            double latitude = place.getLatLng().latitude;
            double longitude = place.getLatLng().longitude;
            try
            {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            String address = addresses.get(0).getAddressLine(0);

            LatLng latLng = new LatLng(latitude, longitude);
            // Zoom in the Google Map
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

            Utils.hideSoftKeyboard(AddCampaignLocation.this);
//            searchAutoCompleteTextView.setText("");
//            searchAutoCompleteTextView.setHint("Search");
//            searchAutoCompleteTextView.clearFocus();
        }
    };

    AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i("Location", "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i("Location", "Fetching details for ID: " + item.placeId);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
// position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0,0,10,180);
        latitude = String.valueOf(gpsTracker.getLatitude());
        longitude = String.valueOf(gpsTracker.getLongitude());

        LatLng latlng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        Log.d("xxx", "lat&Lng: " + latlng.toString()    );
        mMap.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        //move camera to current location point
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()),15));
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Places API connection failed with error code:" + connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
