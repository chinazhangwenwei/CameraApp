package com.interjoy.camer2application.adpter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wiseweb.util.AppUtil;
import com.wiseweb.watermelon.R;
import com.wiseweb.watermelon.base.beans.PicBean;

import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private int mHeight = 0;// 代表items的高度
    private List<PicBean> picBeen;// 图片下面的文字
    private int width;
    private float spaceDip;


    public GridViewAdapter(Context context,
                           List<PicBean> picBeen, int width, float spaceDip) {
        this.context = context;
        this.picBeen = picBeen;
        this.width = width;
        this.spaceDip = spaceDip;
    }

    @Override
    public int getCount() {
        return picBeen.size();
    }

    @Override
    public Object getItem(int position) {
        return picBeen.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder = getViewHolder(position, convertView,
                parent);
        if (mHeight == 0) {
            mHeight = (width / 3) - AppUtil.dip2px(context, spaceDip);
        }
        View itemView = viewHolder.getConvertView();
        AbsListView.LayoutParams mLayoutParams = (AbsListView.LayoutParams) itemView.getLayoutParams();
        mLayoutParams.height = mHeight;
        mLayoutParams.width = mHeight;
        itemView.setLayoutParams(mLayoutParams);
        Glide.with(context).load(picBeen.get(position).getThumbail_img()).
                placeholder(R.drawable.ic_launcher1).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into((ImageView) viewHolder.getView(R.id.iv_content));
        return itemView;

    }

    private ViewHolder getViewHolder(int position, View convertView,
                                     ViewGroup parent) {
        return ViewHolder.get(context, convertView, parent,
                R.layout.fragment_share_gridview_item, position);
    }

}
