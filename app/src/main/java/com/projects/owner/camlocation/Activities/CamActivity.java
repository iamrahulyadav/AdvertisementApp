package com.projects.owner.camlocation.Activities;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projects.owner.camlocation.IWebServiceHandler;
import com.projects.owner.camlocation.R;
import com.projects.owner.camlocation.Utils.SharedPrefs;
import com.projects.owner.camlocation.WebServiceHandler;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * Created by owner on 28/09/2016.
 */
public class CamActivity extends Activity implements IWebServiceHandler, Camera.PictureCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    final CountDownLatch latch = new CountDownLatch(1);
    private Bitmap picture;

    //    public static String encodedImage;
    private Camera mCamera;
    private CameraPreview mPreview;
    private PowerManager.WakeLock mWakeLock;
    private ImageView camButton, mapsButton, selfieButton;
    private double lng = 0.0, lat = 0.0;
    private byte[] byteArray;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private RelativeLayout preview;
    private GoogleApiClient mGoogleApiClientplace;
    private ImageView imagePhoto;
    private EditText textPlace;
    private RelativeLayout imagePlaceHolder;
    private Bitmap theImage;
    private static final int CAMPAIGN_ID = 1000;
    private static final int CAMPAGIN_CITY_ID = 9001;
    private boolean imageUpload, imageTaken;
    private int orientationScreen;
    private int pictureOrentation;
    private CoordinatorLayout coordinator;
    private FloatingActionButton confirm;
    private byte[] orginalByte;
    private byte[] byteArray1;
    private byte[] byteArray2;
    private byte[] byteArray3;
    private int i = 0;
    private ProgressDialog progress;

    //eidit by shoaib anwar
    private DatabaseReference mFirebaseDatabaseLatitude, mFirebaseDatabaseLongititude;
    private FirebaseDatabase mFirebaseInstance;
    FirebaseStorage storage;
    StorageReference storageReference;

    static Uri imageUri = null;
    String profileImagePath;

    int yearTBU, monthTBU, dayTBU;
    private static final int YEAR_MINUS = -18;



    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");

        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }


    private final Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
        }
    };


    private Bitmap decodeSampledBitmapFromResource(byte[] res, int reqWidth,
                                                   int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();

        byte[] is = res;
        try {
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(is, 0, is.length, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeByteArray(is, 0, is.length, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (mCamera == null) {
                mCamera = getCameraInstance();
                // create a basic camera preview class that can be included in a View layout.
                mPreview = new CameraPreview(this, mCamera);

                //add your preview class to the FrameLayout element.
                preview.addView(mPreview);
            }
//            mPreview = new CameraPreview(CamActivity.this, mCamera);//set preview
//            preview.addView(mPreview);
        } catch (Exception e) {
            Log.d("", "Error starting camera preview: " + e.getMessage());
        }


    }

    protected void onStart() {
        mGoogleApiClient.connect();
        mGoogleApiClientplace.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        mGoogleApiClientplace.disconnect();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cam);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClientplace = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        imageUpload = imageTaken = true;
        imagePhoto = (ImageView) findViewById(R.id.photo);
        textPlace = (EditText) findViewById(R.id.placetextholder);
        imagePlaceHolder = (RelativeLayout) findViewById(R.id.pictureplaceholder);
        coordinator = (CoordinatorLayout) findViewById(R.id.coordinator);
        confirm = (FloatingActionButton) findViewById(R.id.fab);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUpload) {
                /*    progress = new ProgressDialog(CamActivity.this);
                    progress.setTitle("Uploading");
                    progress.setMessage("Please Wait....");
                    progress.show();
                    progress.setCancelable(false);
                    progress.setCanceledOnTouchOutside(false);
                    //convert();*/


                    if (ActivityCompat.checkSelfPermission(CamActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CamActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (mLastLocation != null) {
                        lat = mLastLocation.getLatitude();
                        lng = mLastLocation.getLongitude();
                    }

                    customeDialog();


                }
            }

            private void convert() {
                imageUpload = false;
                if (ActivityCompat.checkSelfPermission(CamActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CamActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    lat = mLastLocation.getLatitude();
                    lng = mLastLocation.getLongitude();
                }

                new BitmapWorkerTask(600, 600).execute(orginalByte);
                new BitmapWorkerTask(250, 250).execute(orginalByte);
                new BitmapWorkerTask(100, 100).execute(orginalByte);

            }
        });

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();

        preview = (RelativeLayout) findViewById(R.id.camera_preview);
        camButton = (ImageView) findViewById(R.id.shutter);
        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageTaken) {
                    mCamera.takePicture(shutterCallback, null, CamActivity.this);
                }
            }
        });
        mapsButton = (ImageView) findViewById(R.id.maps);
        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CamActivity.this, GalleryActivity.class);
                startActivity(i);
            }
        });
        selfieButton = (ImageView) findViewById(R.id.selfie);
        selfieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void upload() {
        String SOAP_ACTION = "http://tempuri.org/ICameraWCFService/Camera_Post_Image";
//        String SOAP_ACTION = "http://tempuri.org/Camera_Post_Image";
        String OPERATION_NAME = "Camera_Post_Image";
        PropertyInfo propertyInfo10 = new PropertyInfo();
        PropertyInfo propertyInfo2 = new PropertyInfo();
        PropertyInfo propertyInfo3 = new PropertyInfo();
        PropertyInfo propertyInfo4 = new PropertyInfo();
        PropertyInfo propertyInfo5 = new PropertyInfo();
        PropertyInfo propertyInfo6 = new PropertyInfo();
        PropertyInfo propertyInfo7 = new PropertyInfo();
        PropertyInfo propertyInfo8 = new PropertyInfo();
        PropertyInfo propertyInfo9 = new PropertyInfo();

        propertyInfo2.type = PropertyInfo.STRING_CLASS;
        propertyInfo2.name = "lat";
        propertyInfo3.type = PropertyInfo.STRING_CLASS;
        propertyInfo3.name = "lng";
        propertyInfo4.type = PropertyInfo.INTEGER_CLASS;
        propertyInfo4.name = "Campaign_ID";
        propertyInfo5.type = PropertyInfo.INTEGER_CLASS;
        propertyInfo5.name = "City_ID";
        propertyInfo6.type = PropertyInfo.STRING_CLASS;
        propertyInfo6.name = "location";
        propertyInfo7.name = "image_large";
        propertyInfo8.name = "image_medium";
        propertyInfo9.name = "image_small";
        propertyInfo10.type = PropertyInfo.INTEGER_CLASS;
        propertyInfo10.name = "rotation";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = df.format(c.getTime());
        /////////////////////////////////////////////////   change according to class and procedure name
        WebServiceHandler webServiceHandler = new WebServiceHandler(CamActivity.this);
        webServiceHandler.setValues(SOAP_ACTION, OPERATION_NAME, propertyInfo2, propertyInfo3, propertyInfo4, propertyInfo5, propertyInfo6, propertyInfo7, propertyInfo8, propertyInfo9, propertyInfo10,
                Double.toString(lat), Double.toString(lng), CAMPAIGN_ID, CAMPAGIN_CITY_ID, textPlace.getText().toString(),
                byteArray1,
                byteArray2,
                byteArray3,
                pictureOrentation);
        this.byteArray1 = byteArray2 = byteArray3 = null;

        //test image upload to fireabase

       // insertNewDataToFirebase("12345", 32.1234566789, 74.123456789, mFileUri);

        try {
            webServiceHandler.executeProcedure();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        this.mWakeLock.release();
        super.onDestroy();
    }

    protected boolean checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();
            mCamera = null;
        }

    }


    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            Camera.Parameters parameters = c.getParameters();
            //set focous mode
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            //set flash mode
            parameters.setFlashMode("auto");
            //set camera parameters
            c.setParameters(parameters);
        } catch (Exception e) {
            Log.d("", "getCameraInstance: " + e.getMessage());
        }//
        return c; // returns null if camera is unavailable
    }

    @Override
    public void StartedRequest() {
    }

    @Override
    public void CodeFinished(String methodName, Object Data) {
         if (((SoapObject)Data).getProperty(0).toString().equals("1")) {
            mCamera.startPreview();
            progress.dismiss();
            Snackbar.make(coordinator, "Image is uploaded successfully !", Snackbar.LENGTH_SHORT).setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    super.onDismissed(snackbar, event);
                    imagePlaceHolder.setVisibility(View.INVISIBLE);
                    confirm.setVisibility(View.INVISIBLE);
                    imageUpload = true;
                    imageTaken = true;
                }
            }).setAction("Action", null).show();
        } else {
            mCamera.startPreview();
            progress.dismiss();
            Snackbar.make(coordinator, "Upload failed !", Snackbar.LENGTH_SHORT).setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    super.onDismissed(snackbar, event);
                    imagePlaceHolder.setVisibility(View.INVISIBLE);
                    confirm.setVisibility(View.INVISIBLE);
                    imageUpload = true;
                    imageTaken = true;
                }
            }).setAction("Action", null).show();
        }
    }

    @Override
    public void CodeFinishedWithException(Exception ex, String exp) {
        if (exp.equals("wrong")) {
            try {
                Toast.makeText(CamActivity.this, "Can't complete your request", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e(e.getMessage(), e.getMessage());
            }
        } else {
            try {
                mCamera.startPreview();
                progress.dismiss();
                Snackbar.make(coordinator, "Upload failed !", Snackbar.LENGTH_SHORT).setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        imagePlaceHolder.setVisibility(View.INVISIBLE);
                        confirm.setVisibility(View.INVISIBLE);
                        imageUpload = true;
                        imageTaken = true;
                    }
                }).setAction("Action", null).show();
            } catch (Exception e) {
                Log.e(e.getMessage(), e.getMessage());

            }
        }
    }

    @Override
    public void CodeEndedRequest() {

    }


    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext()
                .getContentResolver();
        return Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
    }

    protected void alertbox(String title, String mymessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Device's GPS is Disable")
                .setCancelable(false)
                .setTitle("Turn on GPS")
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
                                Intent myIntent = new Intent(
                                        Settings.ACTION_SECURITY_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lng = mLastLocation.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("", "onConnectionFailed: " + connectionResult.getErrorMessage());
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        mCamera.stopPreview();
        imageTaken = false;
        orginalByte = data;
        pictureOrentation = orientationScreen;
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            return;
        }

        try {
            byteArray = null;
            //On release uncomment these
//            FileOutputStream fos = new FileOutputStream(pictureFile);
//            fos.write(data);
//            fos.close();
            theImage = BitmapFactory.decodeByteArray(data, 0, data.length);
            imageUri = getImageUri(getApplicationContext(), theImage);
            // bitmap1 = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            Log.e("tag" , "capture image uri above : "+ imageUri);
            profileImagePath = getRealPathFromURI(imageUri);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            theImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byteArray = stream.toByteArray();
            displayImage();
        } catch (Exception e) {
            Log.d("error", "Error accessing file: " + e.getMessage());
        }
    }

    private void displayImage() throws IOException {
        imagePlaceHolder.setVisibility(View.VISIBLE);
        confirm.setVisibility(View.VISIBLE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            InputStream is = new ByteArrayInputStream(byteArray);
            ExifInterface ei = new ExifInterface(is);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    imagePhoto.setImageBitmap(rotateImage(theImage, 90));
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    imagePhoto.setImageBitmap(rotateImage(theImage, 180));
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    imagePhoto.setImageBitmap(rotateImage(theImage, 270));
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                    imagePhoto.setImageBitmap(rotateImage(theImage, 0));
                default:
                    break;
            }
        } else {
            switch (pictureOrentation) {
                case Surface.ROTATION_0:
                    imagePhoto.setImageBitmap(rotateImage(theImage, 90));
                    break;
                case Surface.ROTATION_90:
                    imagePhoto.setImageBitmap(rotateImage(theImage, 0));
                    break;
                case Surface.ROTATION_180:
                    imagePhoto.setImageBitmap(rotateImage(theImage, 270));
                    break;
                case Surface.ROTATION_270:
                    imagePhoto.setImageBitmap(rotateImage(theImage, 180));
                default:
                    imagePhoto.setImageBitmap(theImage);
                    break;
            }
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClientplace, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
//                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
//                    Log.e("Places ", String.format("Place '%s' has likelihood: %g",
//                            placeLikelihood.getPlace().getName(),
//                            placeLikelihood.getLikelihood()));
//                    String s = (String) placeLikelihood.getPlace().getName();
//                    float a = placeLikelihood.getLikelihood();
//                }
                try {
                    textPlace.setText(likelyPlaces.get(0).getPlace().getName());
                    Log.e("TAG", "the city address is: "+likelyPlaces.get(0).getPlace().getAddress());
                } catch (Exception e) {
                    Log.e("places error", "onResult: ");
                }
                likelyPlaces.release();
            }
        });
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    class BitmapWorkerTask extends AsyncTask<byte[], Void, byte[]> {
        private final int width, height;
        private byte[] data;


        BitmapWorkerTask(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        protected void onPreExecute() {

        }

        // Decode image in background.
        @Override
        protected byte[] doInBackground(byte[]... params) {
            data = params[0];
            Bitmap bitmap = decodeSampledBitmapFromResource(data, width, height);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            assert bitmap != null;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        }

        @Override
        protected void onPostExecute(byte[] output) {
            if (output != null) {
                switch (width) {
                    case 600:
                        byteArray1 = output;
                        break;
                    case 250:
                        byteArray2 = output;
                        break;
                    case 100:
                        byteArray3 = output;
                        break;
                }
            }
            i++;
            if (i == 3) {
              //  upload();
                /*progress.dismiss();
                insertNewDataToFirebase("12345", 32.1234566789, 74.123456789);*/

                i=0;
            }
        }
    }


    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private static final String TAG = "Camera class error";
        private final Context context;
        private SurfaceHolder mHolder;
        private Camera mCamera;
        private boolean isPreviewRunning;
        private Animator animator;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            this.context = context;
            mCamera = camera;

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
                isPreviewRunning = true;
            } catch (Exception e) {
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            this.getHolder().removeCallback(this);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            isPreviewRunning = false;
        }


        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.


            try {
                if (isPreviewRunning) {
                    mCamera.stopPreview();
                }
                if (mHolder.getSurface() == null) {
                    // preview surface does not exist
                    return;
                }
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                //set flash mode
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                orientationScreen = display.getRotation();
                if (display.getRotation() == Surface.ROTATION_0) {
                    parameters.setPreviewSize(height, width);
                    mCamera.setDisplayOrientation(90);
//                    animator = ObjectAnimator.ofFloat(camButton, "rotation", camButton.getRotation(), 90);
//                    animator.setDuration(1000);//25000L / speed
//                    animator.start();

                }

                if (display.getRotation() == Surface.ROTATION_90) {
                    parameters.setPreviewSize(width, height);
                    mCamera.setDisplayOrientation(0);
//                    animator = ObjectAnimator.ofFloat(camButton, "rotation", camButton.getRotation(), 180);
//                    animator.setDuration(1000);//25000L / speed
//                    animator.start();
                }

                if (display.getRotation() == Surface.ROTATION_180) {
                    parameters.setPreviewSize(height, width);
                    mCamera.setDisplayOrientation(270);
//                    animator = ObjectAnimator.ofFloat(camButton, "rotation", camButton.getRotation(), 270);
//                    animator.setDuration(1000);//25000L / speed
//                    animator.start();
                }

                if (display.getRotation() == Surface.ROTATION_270) {
                    parameters.setPreviewSize(width, height);
                    mCamera.setDisplayOrientation(180);
//                    animator = ObjectAnimator.ofFloat(camButton, "rotation", camButton.getRotation(), 360);
//                    animator.setDuration(1000);//25000L / speed
//                    animator.start();
                }

                mCamera.setParameters(parameters);
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
                isPreviewRunning = true;

            } catch (Exception e) {
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }

    }


    //*************************Shoaib Anwar*************************//
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String getRealPathFromURI(Uri contentURI)
    {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void insertNewDataToFirebase(final String userID, final String request, final String campaignDetail, final String brandName, final String subBraind, String vendorName, String cateogry, String city){

        long time= System.currentTimeMillis();
        String mTime = String.valueOf(time);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        final StorageReference ref = storageReference.child("Campaigns/"+ request);

        String lati = String.valueOf(lat);
        String lngi = String.valueOf(lng);
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .setCustomMetadata("userId", userID)
                .setCustomMetadata("lat", lati)
                .setCustomMetadata("lng", lngi)
                .setCustomMetadata("detail", campaignDetail)
                .setCustomMetadata("address", textPlace.getText().toString())
                .setCustomMetadata("brand", brandName)
                .setCustomMetadata("subBrand", subBraind)
                .setCustomMetadata("vendor", vendorName)
                .setCustomMetadata("category", cateogry)
                .setCustomMetadata("city", city)
                .setCustomMetadata("createdTime", mTime)

                .build();
        ref.putFile(imageUri, metadata)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(CamActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                        Log.e("TAG", "the download uri of images are: " + downloadUri);

                        //uploading to database
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("AdvertismentDb/"+userID);
                        Log.e("TAg", "the key for reference " + mDatabase.getKey());
                        TestUploadImageToFirebase.ImageUrls latilngi = new TestUploadImageToFirebase.ImageUrls(request);
                        //Adding values
                        mDatabase.child(request).setValue(latilngi);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(CamActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    }
                });
    }

    private void customeDialog(){

       final  Dialog detaiDialog = new Dialog(CamActivity.this);
        detaiDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        detaiDialog.setContentView(R.layout.advertising_detail_dialog);
        final EditText campaign_name = (EditText) detaiDialog.findViewById(R.id.campaign_name);
        final EditText brand_name = (EditText) detaiDialog.findViewById(R.id.brand_name);
        final EditText sub_brand_name = (EditText) detaiDialog.findViewById(R.id.sub_brand_name);
        final EditText vedor_name = (EditText) detaiDialog.findViewById(R.id.vedor_name);
        final EditText catory_name = (EditText) detaiDialog.findViewById(R.id.catory_name);
        final EditText et_compaign_detail = (EditText) detaiDialog.findViewById(R.id.et_compaign_detail);
        FloatingActionButton dialog_fab = (FloatingActionButton) detaiDialog.findViewById(R.id.dialog_fab);


        dialog_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String compaignName = campaign_name.getText().toString();
                String brandName = brand_name.getText().toString();
                String subBrand = sub_brand_name.getText().toString();
                String vendorName =  vedor_name.getText().toString();
                String categoryName = catory_name.getText().toString();
                String campignDetail = et_compaign_detail.getText().toString();

                if (compaignName.length()==0){
                    campaign_name.setError("Should not empty");
                }
                else if (brandName.length()==0){
                    brand_name.setError("Should not empty");
                }
                else if (subBrand.length()==0){
                    sub_brand_name.setError("Should not empty");
                }
                else if (vendorName.length()==0) {
                    vedor_name.setError("Should not empty");
                }

                else {

                    if(campignDetail.length()==0){
                        campignDetail = "not provided";
                    }
                    String uniqueId = UUID.randomUUID().toString();
                    Log.e("TAG", "the uniq id is: " + uniqueId);
                    detaiDialog.dismiss();
                    SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefs.PREF_NAME, Context.MODE_PRIVATE);
                    String cid = SharedPrefs.getStringPref(sharedPreferences, SharedPrefs.C_ID);
                    insertNewDataToFirebase(cid, uniqueId, compaignName, brandName, subBrand, vendorName, categoryName, "Lahore");

                }
            }
        });

        detaiDialog.setCancelable(false);
        detaiDialog.show();
    }

    private void showDatePickerDialogForStartDate(final EditText startDate){
        Calendar cal = Calendar.getInstance();
//                cal.add(Calendar.YEAR, YEAR_MINUS);
        DatePickerDialog dpd = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                                                                @Override
                                                                public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
                                                                {
                                                                    yearTBU = year;
                                                                    monthTBU = monthOfYear;
                                                                    dayTBU = dayOfMonth;
                                                                    showDate(startDate, year, monthOfYear, dayOfMonth);
                                                                }
                                                            },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_1);
        dpd.show(CamActivity.this.getFragmentManager(), "Datepickerdialog");
    }

    private void showDatePickerDialogForEndDate(final EditText endDate){
        Calendar cal = Calendar.getInstance();
//                cal.add(Calendar.YEAR, YEAR_MINUS);
        DatePickerDialog dpd = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                                                                @Override
                                                                public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
                                                                {
                                                                    yearTBU = year;
                                                                    monthTBU = monthOfYear;
                                                                    dayTBU = dayOfMonth;
                                                                    showDate2(endDate, year, monthOfYear, dayOfMonth);
                                                                }
                                                            },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_1);
        dpd.show(CamActivity.this.getFragmentManager(), "Datepickerdialog");
    }

    public void showDate(EditText startDate, int year, int month, int dayOfMonth)
    {
        startDate.setText(month + "/" + dayOfMonth + "/" + year);
    }

    public void showDate2(EditText endDate, int year, int month, int dayOfMonth)
    {
        endDate.setText(month + "/" + dayOfMonth + "/" + year);
    }
}
