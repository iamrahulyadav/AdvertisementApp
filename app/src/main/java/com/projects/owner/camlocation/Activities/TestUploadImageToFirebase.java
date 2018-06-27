package com.projects.owner.camlocation.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projects.owner.camlocation.R;
import com.projects.owner.camlocation.Utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import static com.androidquery.util.AQUtility.getContext;
import static com.projects.owner.camlocation.fragment.CampaignListFragment.context;

public class TestUploadImageToFirebase extends AppCompatActivity {

    //eidit by shoaib anwar
    private DatabaseReference mFirebaseDatabaseLatitude, mFirebaseDatabaseLongititude;
    private FirebaseDatabase mFirebaseInstance;
    FirebaseStorage storage;
    StorageReference storageReference;
    static Uri mFileUri;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String  userChoosenTask;
    static Uri imageUri = null;
    Bitmap bitmap1;
    String timestamp1;
    ImageView iv_preview;
    Button bt_upload, bt_select_photo;
    public static String profileImagePath;
    public static boolean isImageLoadingFromDevice = false;

    private ExifInterface exifObject;

    RelativeLayout pictureplaceholder;
    android.support.design.widget.FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_upload_image_to_firebase);

        init();
        btSelectPhotClickHandler();
        //gettingMetaDataFromImage("123456");
        readintAllDataChile();

    }

    private void init(){
        iv_preview = (ImageView) findViewById(R.id.iv_preview);
        bt_select_photo = (Button) findViewById(R.id.bt_select_photo);
        bt_upload = (Button) findViewById(R.id.bt_upload);
        pictureplaceholder = (RelativeLayout) findViewById(R.id.pictureplaceholder);
        floatingActionButton = (android.support.design.widget.FloatingActionButton) findViewById(R.id.fab);


    }

    private void insertNewDataToFirebase(final String userID, final String request){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        final StorageReference ref = storageReference.child("Campaigns/"+ request);
        Calendar date = Calendar.getInstance();

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .setCustomMetadata("lat", "32.14522224")
                .setCustomMetadata("lng", "74.12352225")
                .setCustomMetadata("detail", "Image of cocacola wall bannar")
                .setCustomMetadata("address", "Lahore, Pakistan")
                .setCustomMetadata("brand", "Pepsi")
                .setCustomMetadata("vendor", "Pepsi")
                .setCustomMetadata("category", "Bus")
                .setCustomMetadata("city", "Lahore")
                .setCustomMetadata("start_date", "22-jun-2018")
                .setCustomMetadata("end_date", "25-jun-2018")

                .build();
        ref.putFile(imageUri, metadata)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(TestUploadImageToFirebase.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                        Log.e("TAG", "the download uri of images are: " + downloadUri);

                        //uploading to database
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("AdvertismentDb/"+userID);
                        Log.e("TAg", "the key for reference " + mDatabase.getKey());
                        ImageUrls latilngi = new ImageUrls(request);
                        //Adding values
                        mDatabase.child(request).setValue(latilngi);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(TestUploadImageToFirebase.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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



    public static class LatiLng{
        double ImageLat;
        Uri filePath;

        public LatiLng(double lat, double lng) {
            this.ImageLat = lat;

            this.filePath = filePath;
        }
    }

    public static class ImageUrls{
        String userID;
        String imageId;


        public ImageUrls(String imageID) {

            this.imageId = imageID;

        }
    }

   /* private void uploadImage() {

        if(mFileUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();


            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
    }*/

   private void btSelectPhotClickHandler(){

       bt_select_photo.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               selectImage();

           }
       });
   }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Select Photo From Gallery",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(TestUploadImageToFirebase.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //boolean result= Utility.checkPermission(getContext());
                boolean result = true;

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Select Photo From Gallery")) {
                    userChoosenTask ="Select Photo From Gallery";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//


        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("TAg", "The Request code is: data " + data);
        Log.e("TAg", "The Request code is: " + requestCode);
        Log.e("tag" , "capture image uri in onActivityResult : "+imageUri);

        pictureplaceholder.setVisibility(View.VISIBLE);
        floatingActionButton.setVisibility(View.VISIBLE);

        //  if (resultCode == Activity.RESULT_OK) {
        if (requestCode == SELECT_FILE) {
            onSelectFromGalleryResult(data);
        }
        else if (requestCode == REQUEST_CAMERA) {
            onCaptureImageResult(data);
        }


    }//end of onActivity result

    private void onCaptureImageResult(Intent data) {



        if(data!= null) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");

            imageUri = getImageUri(getApplicationContext(), photo);
            // bitmap1 = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            Log.e("tag" , "capture image uri above : "+ imageUri);
            profileImagePath = getRealPathFromURI(imageUri);
            isImageLoadingFromDevice = true;
            iv_preview.setImageBitmap(photo);

            uploadImage();

        }

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (data!=null) {
            Uri image = data.getData();
            Log.e("TAG", "onSelectFromGalleryResult: license file");

            try {

                profileImagePath = getRealPathFromURI(image);
                isImageLoadingFromDevice = true;

                bitmap1 = MediaStore.Images.Media.getBitmap(getContentResolver(), image);
                iv_preview.setImageBitmap(bitmap1);
                // profileImg.setImageBitmap(bitmap1);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



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

    private void uploadImage(){

       bt_upload.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               String uniqueId = UUID.randomUUID().toString();
               Log.e("TAG", "the uniq id is: " + uniqueId);
               insertNewDataToFirebase("1234", uniqueId);

           }
       });
    }

    private void gettingMetaDataFromImage(String imageId){

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
                // Metadata now contains the metadata for 'images/forest.jpg'
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }

   /* private void updateFirebaseDb(String userId, String imageID){
        Log.e("TAg", "lat lat lat: " + latitude);
        Log.e("TAg", "lat lat lat: " + longitude);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabaseLatitude = mFirebaseInstance.getReference("AdvertismentDb/"+requestID+"/Latitude");
        mFirebaseDatabaseLongititude = mFirebaseInstance.getReference("AdvertismentDb/"+requestID+"/Longitude");
        mFirebaseDatabaseLatitude.setValue(latitude);
        mFirebaseDatabaseLongititude.setValue(longitude);
    }*/

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
   private void innerDatais(String key){
       DatabaseReference mFirebaseDatabase =  FirebaseDatabase.getInstance().getReference();
       DatabaseReference ref = mFirebaseDatabase.child("AdvertismentDb/"+key);
       ref.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {

               for(DataSnapshot data : dataSnapshot.getChildren()){
                   String keys = data.getKey();
                   Log.e("TAG", "available keys are: " + keys);
                   gettingMetaDataFromImage(keys);
               }

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });

   }
}

