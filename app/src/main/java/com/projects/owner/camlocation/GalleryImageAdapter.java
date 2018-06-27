package com.projects.owner.camlocation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.projects.owner.camlocation.model.ImagesModel;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by owner on 30/09/2016.
 */
public class GalleryImageAdapter extends BaseAdapter {
    private final List<ImagesModel> imagesModelArrayList;
    private Context mContext;


    public GalleryImageAdapter(Context context, List<ImagesModel> imagesModelArrayList) {
        mContext = context;
        this.imagesModelArrayList = imagesModelArrayList;

    }

    public int getCount() {
        return imagesModelArrayList.size();
    }

    public ImagesModel getItem(int position) {
        return imagesModelArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    // Override this method according to your need
    public View getView(int index, View view, ViewGroup viewGroup) {
        // TODO Auto-generated method stub
        ImageView i = new ImageView(mContext);
        byte[] outImage = imagesModelArrayList.get(index).getBitmap();
        ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
        Bitmap theImage = decodeSampledBitmapFromResource(imageStream, 200, 200);
        i.setImageBitmap(theImage);
        i.setLayoutParams(new Gallery.LayoutParams(300, 300));
//        theImage.recycle();
        i.setScaleType(ImageView.ScaleType.FIT_XY);
        return i;
    }

    private static Bitmap decodeSampledBitmapFromResource(InputStream res, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();

        InputStream is = new BufferedInputStream(res);
        try {
            is.mark(is.available());
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            is.reset();
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(is, null, options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private static int calculateInSampleSize(
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


}
