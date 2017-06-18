package com.interjoy.camer2application.view.widget;

import android.view.View;

import library.PullToRefreshListView;

/**
 * Created by wenwei on 2016/6/15.
 */
public class PullListViewForScroll extends PullToRefreshListView {
    public PullListViewForScroll(android.content.Context context,
                                 android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Integer.MAX_VALUE >> 2,如果不设置，系统默认设置是显示两条
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }
}
