package com.projects.owner.camlocation.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.projects.owner.camlocation.IWebServiceHandler;
import com.projects.owner.camlocation.R;
import com.projects.owner.camlocation.WebServiceHandler;
import com.projects.owner.camlocation.model.PostImageModel;
import com.yalantis.flipviewpager.adapter.BaseFlipAdapter;
import com.yalantis.flipviewpager.utils.FlipSettings;

import org.ksoap2.serialization.SoapObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GalleryActivity extends AppCompatActivity implements IWebServiceHandler {

    private List<PostImageModel> imagesModels;
    private List<Integer> backColors = new ArrayList<>();
    private int count = 0;
    private PostImageModel postImageModelModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_list);
        backColors.add(R.color.sienna);
        backColors.add(R.color.saffron);
        backColors.add(R.color.green);
        backColors.add(R.color.pink);
        backColors.add(R.color.orange);
        backColors.add(R.color.saffron);
        backColors.add(R.color.green);
        backColors.add(R.color.purple);

        String SOAP_ACTION = "http://tempuri.org/ICameraWCFService/CameraApp_Get_10_MediumImage";
//        String SOAP_ACTION = "http://tempuri.org/CameraApp_Get_10_MediumImage";
        String OPERATION_NAME = "CameraApp_Get_10_MediumImage";
        imagesModels = new ArrayList<>();
        WebServiceHandler webServiceHandler = new WebServiceHandler(GalleryActivity.this);
        webServiceHandler.setValues(SOAP_ACTION, OPERATION_NAME);
        try {
            webServiceHandler.executeProcedure();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void StartedRequest() {

    }

    @Override
    public void CodeFinished(String methodName, Object Data) {
        if (Data != null) {
            SoapObject soapObject = (SoapObject) Data;
            SoapObject tableObjects = (SoapObject) soapObject.getProperty(0);
            for (int i = 0; i < tableObjects.getPropertyCount(); i++) {
                if (tableObjects.getPropertyCount() != 0) {
                    SoapObject rowObject = (SoapObject) tableObjects.getProperty(i);
                    postImageModelModel = new PostImageModel();
                    postImageModelModel.setCampaignName(rowObject.getProperty(0).toString());
                    postImageModelModel.setCityName(rowObject.getProperty(1).toString());
                    postImageModelModel.setCurrentdate(rowObject.getProperty(2).toString());
                    byte[] decodeByte = Base64.decode(rowObject.getProperty(3).toString(), Base64.DEFAULT);
                    postImageModelModel.setImageByte(decodeByte);
                    postImageModelModel.setLat(rowObject.getProperty(4).toString());
                    postImageModelModel.setLng(rowObject.getProperty(5).toString());
                    postImageModelModel.setLocation(rowObject.getProperty(6).toString());
                    postImageModelModel.setRotation(Integer.parseInt(rowObject.getProperty(7).toString()));
                    imagesModels.add(postImageModelModel);
                }
            }

            final ListView images = (ListView) findViewById(R.id.friends);

            FlipSettings settings = new FlipSettings.Builder().defaultPage(1).build();
            images.setAdapter(new ImagesAdapter(this, imagesModels, settings));

            images.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    PostImageModel ima = (PostImageModel) images.getAdapter().getItem(position);
                    Toast.makeText(GalleryActivity.this, "click " + ima.getCurrentdate(), Toast.LENGTH_SHORT).show();
                    PostImageModel f = (PostImageModel) images.getAdapter().getItem(position);
                    Intent i = new Intent(GalleryActivity.this, MapsActivity.class);
                    i.putExtra("pos", position);
                    startActivity(i);
                }
            });
        }
    }

    @Override
    public void CodeFinishedWithException(Exception ex, String exp) {

    }

    @Override
    public void CodeEndedRequest() {

    }

    class ImagesAdapter extends BaseFlipAdapter {

        private final int PAGES = 3;
        private final List<PostImageModel> items;
        private int[] IDS_INTEREST = {R.id.interest_1, R.id.interest_2, R.id.interest_3, R.id.interest_4, R.id.interest_5};

        ImagesAdapter(Context context, List<PostImageModel> items, FlipSettings settings) {
            super(context, items, settings);
            this.items = items;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Override
        public View getPage(final int position, View convertView, ViewGroup parent, Object item1, Object item2) {
            final ImagesHolder holder;

            if (convertView == null) {
                holder = new ImagesHolder();
                convertView = getLayoutInflater().inflate(R.layout.gallery_merge_page, parent, false);

                holder.leftAvatar = (ImageView) convertView.findViewById(R.id.first);
                holder.rightAvatar = (ImageView) convertView.findViewById(R.id.second);
                holder.leftAvatar.setFocusable(false);
                holder.rightAvatar.setFocusable(false);
                holder.rightAvatar.setFocusableInTouchMode(false);
                holder.leftAvatar.setFocusableInTouchMode(false);

                holder.infoPage = getLayoutInflater().inflate(R.layout.gallery_info, parent, false);
                holder.infoPage.setFocusable(false);
                holder.infoPage.setFocusableInTouchMode(false);
                holder.nickName = (TextView) holder.infoPage.findViewById(R.id.nickname);

                for (int id : IDS_INTEREST)
                    holder.interests.add((TextView) holder.infoPage.findViewById(id));

                convertView.setTag(holder);
            } else {
                holder = (ImagesHolder) convertView.getTag();
            }

            switch (position) {
                // Merged page with 2 friends
                case 1:
                    byte[] outImage = ((PostImageModel) item1).getImageByte();
//                    Bitmap theImage = decodeSampledBitmapFromResource(outImage, 400, 400);
                    Bitmap theImage = BitmapFactory.decodeByteArray(outImage, 0, outImage.length);
                    try {
                        displayImage(holder.leftAvatar, theImage, (PostImageModel) item1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    holder.leftAvatar.setScaleType(ImageView.ScaleType.FIT_XY);
                    if (item2 != null) {
                        byte[] outImage2 = ((PostImageModel) item2).getImageByte();
//                        Bitmap theImage2 = decodeSampledBitmapFromResource(outImage2, 400, 400);
                        Bitmap theImage2 = BitmapFactory.decodeByteArray(outImage2, 0, outImage2.length);
                        try {
                            displayImage(holder.rightAvatar, theImage2, (PostImageModel) item2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                        holder.rightAvatar.setScaleType(ImageView.ScaleType.FIT_XY);
                    }
                    break;
                default:
                    fillHolder(holder, position == 0 ? (PostImageModel) item1 : (PostImageModel) item2);
                    holder.infoPage.setTag(holder);
                    return holder.infoPage;
            }
            convertView.setOnClickListener(new OnItemClickListener(position));
            return convertView;
        }

        private void displayImage(ImageView imagePhoto, Bitmap theImage, PostImageModel item) throws IOException {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                theImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
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
                switch (item.getRotation()) {
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

        }

        private Bitmap rotateImage(Bitmap source, float angle) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                    true);
        }

        private Bitmap decodeSampledBitmapFromResource(byte[] res, int reqWidth, int reqHeight) {
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
                Bitmap a = BitmapFactory.decodeByteArray(is, 0, is.length, options);
                return a;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

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
        public int getPagesCount() {
            return PAGES;
        }

        private void fillHolder(ImagesHolder holder, PostImageModel imagesModel) {
            if (imagesModel == null)
                return;
            List<String> list = new ArrayList<>();
            list.add(imagesModel.getCampaignName());
            list.add(imagesModel.getCityName());
            list.add(imagesModel.getLocation());
            Iterator<TextView> iViews = holder.interests.iterator();
            Iterator<String> iInterests = list.iterator();
            while (iViews.hasNext() && iInterests.hasNext())
                iViews.next().setText(iInterests.next());
            if (count == 7) {
                count = 0;
            }
            holder.infoPage.setBackgroundColor(getResources().getColor(backColors.get(count)));
            count++;
            holder.nickName.setText(imagesModel.getCurrentdate());
        }

        private class OnItemClickListener implements View.OnClickListener {
            private int mPosition;

            OnItemClickListener(int position) {
                mPosition = position;
            }

            @Override
            public void onClick(View arg0) {
                Toast.makeText(GalleryActivity.this, "class click " + mPosition, Toast.LENGTH_SHORT).show();

                Log.d("", "onItemClick at position" + mPosition);
            }
        }

        class ImagesHolder {
            ImageView leftAvatar;
            ImageView rightAvatar;
            View infoPage;

            List<TextView> interests = new ArrayList<>();
            TextView nickName;
        }
    }
}
