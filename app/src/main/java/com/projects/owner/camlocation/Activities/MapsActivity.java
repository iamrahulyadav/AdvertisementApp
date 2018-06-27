package com.projects.owner.camlocation.Activities;

import android.animation.Animator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.projects.owner.camlocation.DBHelper;
import com.projects.owner.camlocation.FragmentRecylerView;
import com.projects.owner.camlocation.GalleryImageAdapter;
import com.projects.owner.camlocation.IWebServiceHandler;
import com.projects.owner.camlocation.R;
import com.projects.owner.camlocation.model.ImagesModel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, IWebServiceHandler {

    private GoogleMap mMap;
    private Date date;
    private Gallery gallery;
    private List<ImagesModel> imagesModels;
    private ImageView idate, icat, itag, iup;
    private Boolean bdate, bcat, btag, bup;
    private LinearLayout linearLayout;
    private int year, month, day;
    private GalleryImageAdapter galleryImageAdapter;
    private DBHelper db;
    private int pos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        pos = getIntent().getIntExtra("pos", 0);
        bdate = bcat = bup = btag = false;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        db = new DBHelper(this);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        linearLayout = (LinearLayout) findViewById(R.id.filtermenu);
        gallery = (Gallery) findViewById(R.id.gallery);
        idate = (ImageView) findViewById(R.id.idate);
        icat = (ImageView) findViewById(R.id.icat);
        itag = (ImageView) findViewById(R.id.itag);
        iup = (ImageView) findViewById(R.id.iup);
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
                if (bdate) {
                    bdate = false;
                    idate.setImageResource(R.drawable.calender);
                    imagesModels = db.getAllImages();
                    galleryImageAdapter = new GalleryImageAdapter(MapsActivity.this, imagesModels);
                    gallery.setAdapter(galleryImageAdapter);
                } else {
                    bdate = true;
                    idate.setImageResource(R.drawable.calendersel);
                    icat.setImageResource(R.drawable.cat);
                    itag.setImageResource(R.drawable.tag);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(MapsActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Date d = new Date(year, monthOfYear + 1, dayOfMonth);
                            imagesModels = db.getAllImagesDatewise(d.getTime());
                            galleryImageAdapter = new GalleryImageAdapter(MapsActivity.this, imagesModels);
                            gallery.setAdapter(galleryImageAdapter);
                        }
                    }, year, month, day);
                    datePickerDialog.setTitle("Filter Date");
                    datePickerDialog.show();
                }
            }
        });
        icat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bcat) {
                    bcat = false;
                    icat.setImageResource(R.drawable.cat);
                    imagesModels = db.getAllImages();
                    galleryImageAdapter = new GalleryImageAdapter(MapsActivity.this, imagesModels);
                    gallery.setAdapter(galleryImageAdapter);
                } else {
                    bcat = true;
                    idate.setImageResource(R.drawable.calender);
                    itag.setImageResource(R.drawable.tag);
                    icat.setImageResource(R.drawable.catsel);
                    ///
                }
            }
        });
        itag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btag) {
                    btag = false;
                    itag.setImageResource(R.drawable.tag);
                    imagesModels = db.getAllImages();
                    galleryImageAdapter = new GalleryImageAdapter(MapsActivity.this, imagesModels);
                    gallery.setAdapter(galleryImageAdapter);
                } else {
                    btag = true;
                    idate.setImageResource(R.drawable.calender);
                    icat.setImageResource(R.drawable.cat);
                    itag.setImageResource(R.drawable.tagsel);
                }
            }
        });
        gallery.setOnItemSelectedListener(new SelectListener(this));
        gallery.setSpacing(20);
        imagesModels = db.getAllImages();
        galleryImageAdapter = new GalleryImageAdapter(this, imagesModels);
        gallery.setAdapter(galleryImageAdapter);
        gallery.setSelection(pos, true);
//        for (ImagesModel img : imagesModels) {
//            String log = "Id: "+cn.getID()+" ,Name: " + cn.getName() + " ,Phone: " + cn.getPhoneNumber();
//            // Writing Contacts to log
//            Log.d("Name: ", log);
//        String SOAP_ACTION = "http://tempuri.org/CameraApp_Get_Img";
//        String OPERATION_NAME = "CameraApp_Get_Img";
//        ///////////////////////////////////////////////////   change according to class and procedure name
//        WebServiceHandler webServiceHandler = new WebServiceHandler(MapsActivity.this);
//        webServiceHandler.setValues(SOAP_ACTION, OPERATION_NAME);
//        if (checkInternet()) {
//            try {
//                webServiceHandler.executeProcedure();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(MapsActivity.this).create();
//            alertDialog.setMessage("Please check your Internet Settings");
//            alertDialog.setCanceledOnTouchOutside(true);
//            alertDialog.setTitle("No Internet Connection");
//            alertDialog.show();
//        }

    }

    private class SelectListener implements AdapterView.OnItemSelectedListener {

        private Animation grow = null;
        private View lastView = null;

        SelectListener(Context c) {
            grow = AnimationUtils.loadAnimation(c, R.anim.grow);
        }

        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

            // Shrink the view that was zoomed
            try {
                if (null != lastView) lastView.clearAnimation();
            } catch (Exception ignored) {
            }

            // Zoom the new selected view
            try {
                v.startAnimation(grow);
                FragmentRecylerView frag = new FragmentRecylerView();
                Bundle b = imagesModels.get(position).toBundle();

                frag.setArguments(b);
                getFragmentManager().beginTransaction().replace(R.id.framecontainer, frag).commit();

                LatLng latlng = new LatLng(imagesModels.get(position).getLat(), imagesModels.get(position).getLng());

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latlng, 16.0f)));
            } catch (Exception ignored) {
            }

            // Set the last view so we can clear the animation
            lastView = v;
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }

    }

    protected boolean checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for (ImagesModel img : imagesModels) {
            LatLng latlng = new LatLng(img.getLat(), img.getLng());
            mMap.addMarker(new MarkerOptions().position(latlng).title(img.getBrandName()).draggable(true).snippet(img.getCatagory()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 6.0f));

        }
    }


    @Override
    public void StartedRequest() {

    }

    @Override
    public void CodeFinished(String methodName, Object Data) {
//        String result = (String) Data;
//        //////////////////////////////////////////////////parsing string
//        String[] parts = result.split(";");
//        String[][] col_data = new String[parts.length][23];
//        for (int i = 0; i < parts.length; i++) {
//            String strr = parts[i];
//            String[] temp = strr.split("---");
//            System.arraycopy(temp, 0, col_data[i], 0, 9);
//            byte[] decodeString = Base64.decode(col_data[i][6], Base64.DEFAULT);
//            Bitmap decodebitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
////            img.setImageBitmap(decodebitmap);
//            DateFormat format = new SimpleDateFormat("MM dd,yyyy", Locale.ENGLISH);
//            try {
//                date = format.parse(col_data[i][7]);
//            } catch (ParseException e) {
//                e.printStackTrace();
//                Log.d("", "Date error: ");
//            }
//            imagesModel.setImagesModelArrayList(new ImagesModel(col_data[i][0], col_data[i][1], col_data[i][2], col_data[i][3], col_data[i][4], col_data[i][5], decodebitmap, date, Double.parseDouble(col_data[i][8]), Double.parseDouble(col_data[i][9])));
//        }
//        final GalleryImageAdapter galleryImageAdapter = new GalleryImageAdapter(this, imagesModel.getImagesModelArrayList());
//        gallery.setAdapter(galleryImageAdapter);
    }

    @Override
    public void CodeFinishedWithException(Exception ex, String exp) {
        if (exp.equals("wrong")) {
            try {
                Toast.makeText(MapsActivity.this, "Can't complete your request", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e(e.getMessage(), e.getMessage());
            }
        } else {
            try {
                final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(MapsActivity.this).create();
                alertDialog.setMessage(ex.getMessage());
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.setTitle("Error");
                alertDialog.show();
            } catch (Exception e) {
                Log.e(e.getMessage(), e.getMessage());

            }
        }
    }

    @Override
    public void CodeEndedRequest() {

    }
}
