package com.projects.owner.camlocation.Activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.DimType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;
import com.projects.owner.camlocation.R;
import com.projects.owner.camlocation.fragment.FinanceLineGraphsFragment;
import com.projects.owner.camlocation.fragment.ProgressArcProgressBarFragment;
import com.projects.owner.camlocation.fragment.CampaignListFragment;
import com.projects.owner.camlocation.fragment.FollowUpDonutChartFragment;
import com.projects.owner.camlocation.fragment.ContributerBarGraphFragment;

public class HomeActivity extends DrawerActivity {

    private MaterialViewPager mViewPager;
    private Toolbar toolbar;
    private BoomMenuButton boomMenuButton;
    private boolean init = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        boomMenuButton = (BoomMenuButton) findViewById(R.id.boom);

        setTitle("");

        mViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);

        toolbar = mViewPager.getToolbar();

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                switch (position % 5) {
                    case 0:
                        return CampaignListFragment.newInstance();
                    case 1:
                        return ProgressArcProgressBarFragment.newInstance();
                    case 2:
                        return FollowUpDonutChartFragment.newInstance();
                    case 3:
                        return ContributerBarGraphFragment.newInstance();
                    default:
                        return FinanceLineGraphsFragment.newInstance();
                }
            }

            @Override
            public int getCount() {
                return 5;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position % 5) {
                    case 0:
                        return "Campaigns";
                    case 1:
                        return "Progress";
                    case 2:
                        return "Follow Up";
                    case 3:
                        return "Contributions";
                    case 4:
                        return "Fiance";
                }
                return "";
            }
        });

        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.green,
                                "http://m.img.brothersoft.com/android/88/88ee4281e902ccf0eefe3ce84af4ab29_screeshots_2.jpeg");
                    case 1:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.blue,
                                "http://cdn1.tnwcdn.com/wp-content/blogs.dir/1/files/2014/06/wallpaper_51.jpg");
                    case 2:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.cyan,
                                "http://www.droid-life.com/wp-content/uploads/2014/10/lollipop-wallpapers10.jpg");
                    case 3:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.red,
                                "http://www.tothemobile.com/wp-content/uploads/2014/07/original.jpg");
                    case 4:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.colorAccent,
                                "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcT_upr8H9ldBQSUfAxI89nSYxc-nZLPOsLcuRBIcoiRvaZZ81R2GA");
                }

                //execute others actions if needed (ex : modify your header logo)

                return null;
            }
        });

        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

        View logo = findViewById(R.id.logo_white);
        if (logo != null) {
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.notifyHeaderChanged();
                    Toast.makeText(getApplicationContext(), "Yes, the title is clickable", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Use a param to record whether the boom button has been initialized
        // Because we don't need to init it again when onResume()
        if (init) return;
        init = true;

        Drawable[] subButtonDrawables = new Drawable[3];
        int[] drawablesResource = new int[]{
                R.drawable.submit,
                R.drawable.logo_white,
                R.drawable.logobig
        };
        for (int i = 0; i < 3; i++)
            subButtonDrawables[i] = ContextCompat.getDrawable(this, drawablesResource[i]);

        String[] subButtonTexts = new String[]{"Camera", "Camera", "Follow me"};

//        int[][] subButtonColors = new int[3][2];
        int[][] subButtonColors = new int[3][2];
        for (int i = 0; i < 3; i++) {
            subButtonColors[i][1] = ContextCompat.getColor(this, R.color.colorAccent);
            subButtonColors[i][0] = Util.getInstance().getPressedColor(subButtonColors[i][1]);
        }

        // Now with Builder, you can init BMB more convenient
        new BoomMenuButton.Builder()
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.cam), subButtonColors[0], "Camera")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.add), subButtonColors[0], "Campaign")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.pin_map), subButtonColors[0], "Map")
                .button(ButtonType.CIRCLE)
                .boom(BoomType.PARABOLA)
                .place(PlaceType.CIRCLE_3_3)
                .subButtonTextColor(ContextCompat.getColor(this, R.color.white))
                .subButtonsShadow(Util.getInstance().dp2px(2), Util.getInstance().dp2px(2))
                .init(boomMenuButton);
        boomMenuButton.setDimType(DimType.DIM_6);
        boomMenuButton.setOnSubButtonClickListener(new BoomMenuButton.OnSubButtonClickListener() {
            @Override
            public void onClick(int buttonIndex) {
                switch (buttonIndex) {
                    case 0:
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(HomeActivity.this, CamActivity.class);
                                //Intent i = new Intent(HomeActivity.this, TestUploadImageToFirebase.class);
                                startActivity(i);
                            }

                        }, 700);


                        break;
                    case 1:
                        final Handler handler1 = new Handler();
                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(HomeActivity.this, AddCampaignFirstActivity.class);
                                startActivity(i);
                            }

                        }, 700);
                        break;
                    case 2:
                        final Handler handler2 = new Handler();
                        handler2.postDelayed(new Runnable() {
                            @Override
                            public void run() {
/*                                Intent i = new Intent(HomeActivity.this, GalleryActivity.class);
                                startActivity(i);*/
                                /*FragmentManager fragmentManager = getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.main_container, com.projects.owner.camlocation.fragment.MapFragment.newInstance());
                                fragmentTransaction.addToBackStack("");
                                fragmentTransaction.commit();*/
                            }

                        }, 700);
                        break;
                }
            }
        });
//        boomMenuButton.init(
//                subButtonDrawables, // The drawables of images of sub buttons. Can not be null.
//                subButtonTexts,     // The texts of sub buttons, ok to be null.
//                subButtonColors,    // The colors of sub buttons, including pressed-state and normal-state.
//                ButtonType.HAM,     // The button type.
//                BoomType.PARABOLA,  // The boom type.
//                PlaceType.HAM_3_1,  // The place type.
//                null,               // Ease type to move the sub buttons when showing.
//                null,               // Ease type to scale the sub buttons when showing.
//                null,               // Ease type to rotate the sub buttons when showing.
//                null,               // Ease type to move the sub buttons when dismissing.
//                null,               // Ease type to scale the sub buttons when dismissing.
//                null,               // Ease type to rotate the sub buttons when dismissing.
//                null                // Rotation degree.
//        );
//
//        boomMenuButton.setTextViewColor(ContextCompat.getColor(this, R.color.black));
//        boomMenuButton.setSubButtonShadowOffset(Util.getInstance().dp2px(2), Util.getInstance().dp2px(2));
    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }

    }
}
