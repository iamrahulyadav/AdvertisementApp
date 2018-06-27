package com.projects.owner.camlocation.Activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.projects.owner.camlocation.R;
import com.projects.owner.camlocation.Utils.Utils;
import com.projects.owner.camlocation.adapter.PlaceArrayAdapter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sanawal Alvi on 10/28/2017.
 */

public class AddCampaignSecond extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener
, GoogleApiClient.ConnectionCallbacks{

    RelativeLayout nextBtn;
    AutoCompleteTextView nameAutoCompleteTextView;
    String address;
    String latitude, longitude;
    private Geocoder geocoder;
    private List<Address> addresses;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(31.398160, 74.180831), new LatLng(31.430610, 74.972090));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_campaign_second);
        nextBtn = (RelativeLayout) findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(this);
        nameAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.name_auto_complete_tv);
        nameAutoCompleteTextView.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(AddCampaignSecond.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        nameAutoCompleteTextView.setThreshold(2);
        nameAutoCompleteTextView.setDropDownBackgroundResource(R.color.dark_grey);
        nameAutoCompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
//        AutocompleteFilter filter = new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES).build();
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1, BOUNDS_MOUNTAIN_VIEW, null);
        nameAutoCompleteTextView.setAdapter(mPlaceArrayAdapter);


        nameAutoCompleteTextView.clearFocus();
        nameAutoCompleteTextView.setCursorVisible(false);
        nameAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameAutoCompleteTextView.setCursorVisible(true);
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

/*            mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));*/

            geocoder = new Geocoder(AddCampaignSecond.this, Locale.getDefault());
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

            address = addresses.get(0).getAddressLine(0);

/*            LatLng latLng = new LatLng(latitude, longitude);
            // Zoom in the Google Map
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));*/

            Utils.hideSoftKeyboard(AddCampaignSecond.this);
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.next_btn:
                if (nextBtn.equals(""))
                {
                    Toast.makeText(this, "Please fill all the credentials", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(this, AddCampaignLocation.class);
                    startActivity(intent);
//                finish();
                }
                break;
            case R.id.name_auto_complete_tv:
                
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
