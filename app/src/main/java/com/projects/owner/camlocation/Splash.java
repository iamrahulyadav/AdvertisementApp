package com.projects.owner.camlocation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.projects.owner.camlocation.Activities.HomeActivity;
import com.projects.owner.camlocation.Activities.LoginActivity;
import com.projects.owner.camlocation.Utils.SharedPrefs;

public class Splash extends AppCompatActivity {
    private AnimatedCircleLoadingView animatedCircleLoadingView;
    private Animation anima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        animatedCircleLoadingView = (AnimatedCircleLoadingView) findViewById(R.id.circle_loading_view);
        animatedCircleLoadingView.startDeterminate();
        ValueAnimator va = ValueAnimator.ofInt(0, 100);
        va.setDuration(3000);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                animatedCircleLoadingView.setPercent(value.intValue());
            }
        });
        va.setInterpolator(new DecelerateInterpolator());
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        anima = AnimationUtils.loadAnimation(Splash.this, R.anim.outright);
                        anima.setFillAfter(true);
                        animatedCircleLoadingView.startAnimation(anima);
                        anima.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefs.PREF_NAME, Context.MODE_PRIVATE);
                                String cid = SharedPrefs.getStringPref(sharedPreferences, SharedPrefs.C_ID);
                                if (!cid.equals("C_ID")){
                                    //Intent intent = new Intent(Splash.this, LoginActivity.class);
                                    Intent intent = new Intent(Splash.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    Intent intent = new Intent(Splash.this, LoginActivity.class);
                                    //Intent intent = new Intent(Splash.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });


                    }
                }, 2000);

//
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        va.start();
    }
}
