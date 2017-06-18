package com.interjoy.camer2application.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import library.PullToRefreshListView;

/**
 * Created by wenwei on 2016/4/26.
 */
public class ListViewForScrollView extends PullToRefreshListView {
    public ListViewForScrollView(Context context) {
        super(context);
    }

    public ListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewForScrollView(Context context, Mode mode) {
        super(context, mode);
    }

    public ListViewForScrollView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    @Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
