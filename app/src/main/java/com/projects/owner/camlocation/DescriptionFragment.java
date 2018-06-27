package com.projects.owner.camlocation;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by owner on 28/09/2016.
 */
public class DescriptionFragment extends Fragment {

    private IDescriptionFragment iDescriptionFragment;
    private byte[] byteArray;

    private PullToZoomScrollViewEx scrollView;
    private View mainView, contentView;
    private boolean taskRunning = false;
    private RatingBar ratingBar;
    private SwipeSelector brand;
    private SwipeSelector vendor;
    private SwipeSelector cat;
    private SwipeSelector agency;
    private SwipeSelector size;


    public DescriptionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null && b.containsKey("img")) {
            this.byteArray = b.getByteArray("img");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.descfragment, container, false);

        scrollView = (PullToZoomScrollViewEx) mainView.findViewById(R.id.scroll_view);
        scrollView.scrollBy(-1, 0);

        loadViewForCode();
        final FABProgressCircle fabProgressCircle = (FABProgressCircle) mainView.findViewById(R.id.fabProgressCircle);
        fabProgressCircle.attachListener(new FABProgressListener() {
            @Override
            public void onFABProgressAnimationEnd() {
                Snackbar.make(fabProgressCircle, "Image is uploaded successfully !", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null)
                        .show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        taskRunning = false;
                        iDescriptionFragment.confirmClick(brand.getSelectedItem().description, vendor.getSelectedItem().description, String.valueOf(ratingBar.getRating()), size.getSelectedItem().description, cat.getSelectedItem().description, agency.getSelectedItem().description);
                    }

                }, 1500);

            }
        });

        FloatingActionButton confirm = (FloatingActionButton) mainView.findViewById(R.id.fab);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!taskRunning) {
                    taskRunning = true;
                    fabProgressCircle.show();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fabProgressCircle.beginFinalAnimation();
                        }

                    }, 2000);
                }
            }
        });
        return mainView;
    }

    private void loadViewForCode() {
        PullToZoomScrollViewEx scrollView = (PullToZoomScrollViewEx) mainView.findViewById(R.id.scroll_view);
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.profile_head_view, null, false);
        View zoomView = LayoutInflater.from(getActivity()).inflate(R.layout.profile_zoom_view, null, false);
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.profile_content_view, null, false);
        ImageView imageView = (ImageView) zoomView.findViewById(R.id.iv_zoom);
        byte[] outImage = byteArray;
        ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
        Bitmap theImage = decodeSampledBitmapFromResource(imageStream, 500, 500);
        imageView.setImageBitmap(theImage);

        brand = (SwipeSelector) contentView.findViewById(R.id.brand);
        vendor = (SwipeSelector) contentView.findViewById(R.id.vendor);
        cat = (SwipeSelector) contentView.findViewById(R.id.cat);
        ratingBar = (RatingBar) contentView.findViewById(R.id.rating);
        agency = (SwipeSelector) contentView.findViewById(R.id.agency);
        size = (SwipeSelector) contentView.findViewById(R.id.size);
        brand.setItems(
                new SwipeItem(0, "Slide one", "Pepsi"),
                new SwipeItem(1, "Slide two", "Coke"),
                new SwipeItem(2, "Slide three", "Nestle")
        );
        vendor.setItems(
                new SwipeItem(0, "Slide one", "Ravi"),
                new SwipeItem(1, "Slide two", "Moto"),
                new SwipeItem(2, "Slide three", "Central")
        );
        cat.setItems(
                new SwipeItem(0, "Slide one", "BQS"),
                new SwipeItem(1, "Slide two", "Plains"),
                new SwipeItem(2, "Slide three", "Go")
        );

        agency.setItems(
                new SwipeItem(0, "Slide one", "Eu docendi"),
                new SwipeItem(1, "Slide two", "Lorem Ipsum"),
                new SwipeItem(2, "Slide three", "Cibo habeo")
        );
        size.setItems(
                new SwipeItem(0, "Slide one", "Large"),
                new SwipeItem(1, "Slide two", "Medium"),
                new SwipeItem(2, "Slide three", "Small")
        );
//        SwipeItem selectedItem = brand.getSelectedItem().description;
//        int value = (Integer) selectedItem.description;
//
//        if (value == 0) {
//            // The user selected slide number one.
//        }
        scrollView.setHeaderView(headView);
        scrollView.setZoomView(zoomView);
        scrollView.setScrollContentView(contentView);
    }

    public static Bitmap decodeSampledBitmapFromResource(InputStream res, int reqWidth, int reqHeight) {
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

    public static int calculateInSampleSize(
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

    interface IDescriptionFragment {
        void confirmClick(String brand, String vendor, String size, String cat, String rating, String agency);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.iDescriptionFragment = (IDescriptionFragment) activity;
    }
}
