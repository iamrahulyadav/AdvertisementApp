package com.projects.owner.camlocation.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.morphingbutton.MorphingButton;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.projects.owner.camlocation.Listeners.AsyncTaskCompleteListener;
import com.projects.owner.camlocation.R;
import com.projects.owner.camlocation.Services.HttpRequester;
import com.projects.owner.camlocation.Utils.SharedPrefs;
import com.projects.owner.camlocation.Utils.Urls;
import com.projects.owner.camlocation.fragment.MapFragment;
import com.projects.owner.camlocation.model.BaseActivity;
import com.projects.owner.camlocation.model.MediaModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends BaseActivity implements AsyncTaskCompleteListener {
    private static final int CONTACTS = 1;
    private MorphingButton btnMorph;
    TextView tv_register_now;
    EditText etun, etpas;
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

      /*  Intent i = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(i);
        finish();*/
        init();
        loginButtonClickHanlder();


        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS);
        }
    }

    private void init(){

        btnMorph = (MorphingButton) findViewById(R.id.btnMorph1);
        etun = (EditText) findViewById(R.id.etun);
        etpas = (EditText) findViewById(R.id.etpas);
        tv_register_now  = (TextView) findViewById(R.id.tv_register_now);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);
    }

    private void loginButtonClickHanlder(){

        // inside on click event
        btnMorph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etun.getText().toString();
                String password = etpas.getText().toString();
                if (email.length()==0){
                    etun.setError("Should not be empty");
                }
                else if (password.length()==0){
                    etpas.setError("Should not be empty");
                } else if (!emailValidator(email)){
                    etun.setError("Invalid Email");
                }
                else {

                    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                    Log.e("TAg", "the firelbase token is: " + refreshedToken);
                    progressbar.setVisibility(View.VISIBLE);

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("url", Urls.SIGN_IN);
                    map.put("Email", email);
                    map.put("Password", password);
                    map.put("Device", refreshedToken);
                    new HttpRequester(LoginActivity.this, map, 1, LoginActivity.this);
                }

            }
        });


    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        progressbar.setVisibility(View.GONE);

        if (serviceCode == 1)
        {
            try {
                Log.d("xxxxxxxxx", response.toString());
                JSONObject obj = new JSONObject(response);
                JSONArray jsonArray = new JSONArray();
                jsonArray = obj.getJSONArray("Table");
                if (jsonArray != null && jsonArray.length() > 0)
                {
                    String CId = jsonArray.getJSONObject(0).get("Cid").toString();
                    String CName = jsonArray.getJSONObject(0).get("CustomerName").toString();
                    String CMobile = jsonArray.getJSONObject(0).get("CMobileNo").toString();
                    String CEmail = jsonArray.getJSONObject(0).get("EmailId").toString();
                    String CNIC = jsonArray.getJSONObject(0).get("CNIC").toString();


                    Log.e("TAg", "the user detail from server is cid: " + CId);
                    Log.e("TAg", "the user detail from server is name: " + CName);
                    Log.e("TAg", "the user detail from server is mobile: " + CMobile);
                    SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefs.PREF_NAME, Context.MODE_PRIVATE);

                    SharedPrefs.StoreStringPref(sharedPreferences, SharedPrefs.C_ID, CId);
                    SharedPrefs.StoreStringPref(sharedPreferences, SharedPrefs.C_NAME, CName);
                    SharedPrefs.StoreStringPref(sharedPreferences, SharedPrefs.C_EMAIL, CEmail);
                    SharedPrefs.StoreStringPref(sharedPreferences, SharedPrefs.C_MOBILE, CMobile);
                    SharedPrefs.StoreBooleanPref(sharedPreferences, SharedPrefs.ISLOGGEDIN, true);
                    SharedPrefs.StoreStringPref(sharedPreferences, SharedPrefs.CNIC, CNIC);

                    Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                    startActivity(intent);
                    finish();

                    buttonAnimation();
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static boolean emailValidator(final String mailAddress) {

        Pattern pattern;
        Matcher matcher;

        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(mailAddress);
        return matcher.matches();
    }

    private void buttonAnimation(){
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(500)
                .cornerRadius(dimen(R.dimen.mb_height_56)) // 56 dp
                .width(dimen(R.dimen.mb_height_56)) // 56 dp
                .height(dimen(R.dimen.mb_height_56)) // 56 dp
                .color(color(R.color.main_color)) // normal state color
                .colorPressed(color(R.color.main_color)) // pressed state color
                .icon(R.drawable.submit); // icon
        btnMorph.morph(circle);
    }

}