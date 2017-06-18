package com.interjoy.camer2application.view.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Lenovo on 2016/4/15.
 */
public class ViewPagerForPhotoViews extends ViewPager {
    public ViewPagerForPhotoViews(Context context) {
        super(context);
    }

    public ViewPagerForPhotoViews(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }
}
