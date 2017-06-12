package com.interjoy.camer2application.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

import com.interjoy.camer2application.R;

/**
 * Created by zhangwenwei on 2017/6/12.
 */

public class FocusImageView extends AppCompatImageView {
    private static final String TAG = "FocusImageView";
    private int imageStartId = -1;
    private int imageSuccessId = -1;
    private int imageErrorId = -1;
    private AnimationSet animationSet;
    private ScaleAnimation scaleAnimation;
    private AlphaAnimation alfaAnimation;
    private int duration = 500;
    private Handler mHandelr = new Handler(Looper.getMainLooper());


    public FocusImageView(Context context) {
        this(context, null);
    }

    public FocusImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            initParams(attrs, context);
        }
    }

    private void initParams(AttributeSet attrs, Context context) {
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.FocusImageView);
        duration = typeArray.getInt(R.styleable.FocusImageView_animation_duration, duration);
        imageStartId = typeArray.getResourceId(R.styleable.FocusImageView_start_imageId, imageStartId);
        imageSuccessId = typeArray.getResourceId(R.styleable.FocusImageView_success_imageId, imageSuccessId);
        imageErrorId = typeArray.getResourceId(R.styleable.FocusImageView_fail_imageId, imageErrorId);
        setVisibility(INVISIBLE);
        typeArray.recycle();
    }

    public void setImageStartId(int imageStartId) {
        this.imageStartId = imageStartId;
    }

    public void setImageSuccessId(int imageSuccessId) {
        this.imageSuccessId = imageSuccessId;
    }

    @Override
    protected void onAttachedToWindow() {
        Log.d(TAG, "onAttachedToWindow: ");
        if (animationSet == null) {
            scaleAnimation = new ScaleAnimation(1.2f, 1.0f, 1.2f, 1.0f, 0.5f, 0.5f);

            alfaAnimation = new AlphaAnimation(1.0f, 0.5f);
            animationSet = new AnimationSet(true);
            animationSet.setFillAfter(false);
            animationSet.setDuration(duration);
            animationSet.addAnimation(alfaAnimation);
            animationSet.addAnimation(scaleAnimation);
            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d(TAG, "onDetachedFromWindow: ");
        animationSet.cancel();
        animationSet = null;
        scaleAnimation = null;
        alfaAnimation = null;
        mHandelr.removeCallbacks(null);
        super.onDetachedFromWindow();
    }

    public void setImageErrorId(int imageErrorId) {
        this.imageErrorId = imageErrorId;
    }

    public void startFocus() {
        if (getVisibility() == VISIBLE) {
            stopFocus();
        }
        setVisibility(VISIBLE);
        startAnimation(animationSet);
        setImageResource(imageStartId);
    }

    public void successFocus() {
        setImageResource(imageSuccessId);
        mHandelr.postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisibility(INVISIBLE);
            }
        }, 500);

    }

    public void stopFocus() {
        setVisibility(INVISIBLE);
        animationSet.cancel();
    }

    public void failFocus() {
        setImageErrorId(imageErrorId);
        mHandelr.postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisibility(INVISIBLE);
            }
        }, 500);
    }
}
