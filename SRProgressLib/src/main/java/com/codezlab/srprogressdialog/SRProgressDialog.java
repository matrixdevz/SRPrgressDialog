package com.codezlab.srprogressdialog;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by coderzlab on 25/6/17.
 */

public class SRProgressDialog extends AlertDialog {

    // Default background for the progress spinner
    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    private static final int MAX_ALPHA = 255;
    private Context mContext;
    private CircleImageView mCircleView;
    private MaterialProgressDrawable mProgress;
    private int mMediumAnimationDuration = 500;
    private boolean mHasStarted;
    private Animation mScaleAnimation;
    private Animation.AnimationListener mListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @SuppressLint("NewApi")
        @Override
        public void onAnimationEnd(Animation animation) {
            mProgress.setAlpha(MAX_ALPHA);
            mProgress.start();
        }
    };


    public SRProgressDialog(Context context) {
        super(context);
        init(context);
    }


    public SRProgressDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    public static SRProgressDialog show(Context context, boolean cancelable, OnCancelListener cancelListener) {
        SRProgressDialog dialog = new SRProgressDialog(context);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.show();
        return dialog;
    }

    private void init(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.sr_alert_dialog_layout, null);
        ViewGroup parent = (ViewGroup) view.findViewById(R.id.sr_container);
        createProgressView(parent);
    }

    private void createProgressView(ViewGroup parent) {
        mCircleView = new CircleImageView(getContext(), CIRCLE_BG_LIGHT);
        mProgress = new MaterialProgressDrawable(getContext(), parent);
        mProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        mCircleView.setImageDrawable(mProgress);
        parent.addView(mCircleView);
        setContentView(parent);
    }

    @Override
    public void onStart() {
        super.onStart();
        startScaleUpAnimation(mListener);
        mHasStarted = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mProgress.stop();
        mHasStarted = false;
    }

    @SuppressLint("NewApi")
    private void startScaleUpAnimation(Animation.AnimationListener listener) {
        mCircleView.setVisibility(View.VISIBLE);
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            // Pre API 11, alpha is used in place of scale up to show the
            // progress circle appearing.
            // Don't adjust the alpha during appearance otherwise.
            mProgress.setAlpha(MAX_ALPHA);
        }
        mScaleAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(interpolatedTime);
            }
        };
        mScaleAnimation.setDuration(mMediumAnimationDuration);
        if (listener != null) {
            mCircleView.setAnimationListener(listener);
        }
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mScaleAnimation);
    }

    void setAnimationProgress(float progress) {
        if (isAlphaUsedForScale()) {
            setColorViewAlpha((int) (progress * MAX_ALPHA));
        } else {
            ViewCompat.setScaleX(mCircleView, progress);
            ViewCompat.setScaleY(mCircleView, progress);
        }
    }

    @SuppressLint("NewApi")
    private void setColorViewAlpha(int targetAlpha) {
        mCircleView.getBackground().setAlpha(targetAlpha);
        mProgress.setAlpha(targetAlpha);
    }

    private boolean isAlphaUsedForScale() {
        return android.os.Build.VERSION.SDK_INT < 11;
    }


}
