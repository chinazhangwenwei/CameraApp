package com.interjoy.camer2application.adpter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wiseweb.watermelon.R;
import com.wiseweb.watermelon.pay.EditClickLisenter;

public class ViewHolder {

    private final SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;

    private ViewHolder(Context context, ViewGroup parent, int layoutId,
                       int position) {
        this.mPosition = position;
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
                false);
        // setTag
        mConvertView.setTag(this);
    }


    /**
     * 拿到一个ViewHolder对象
     *
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @param position
     * @return
     */
    public static ViewHolder get(Context context, View convertView,
                                 ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position);
        }

        return (ViewHolder) convertView.getTag();
    }
    public static ViewHolder get(Context context, View convertView,
                                 ViewGroup parent, int layoutId) {
      return   get(context,convertView,parent,layoutId,0);
    }

    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param drawableId
     * @return
     */
    public ViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);

        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param
     * @return
     */
    public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    /*
    用glide加载网络图片
     */
    public ViewHolder setImageByGlide(int resId, String url, Object object) {
        ImageView view = getView(resId);
        if (object instanceof Fragment) {
            Glide.with((Fragment) object)
                    .load(url)
                    .placeholder(R.drawable.ic_app_launcher)
                    .error(R.drawable.ic_app_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .into(view);
        } else if (object instanceof AppCompatActivity) {
            Glide.with((Activity) object)
                    .load(url)
                    .placeholder(R.drawable.ic_app_launcher)
                    .error(R.drawable.ic_app_launcher)
                    .into(view);
        }
        return this;
    }

    /*
  用glide加载网络图片
   */
    public ViewHolder setImageGlide(int resId, String url, int placeHolder, Context context) {
        ImageView view = getView(resId);
        Glide.with(context).load(url).placeholder(placeHolder).into(view);
        return this;
    }

    public ViewHolder setImageLayoutParams(int resId, LinearLayout.LayoutParams layoutParams) {
        ImageView view = getView(resId);
        if (layoutParams != null) {
            view.setLayoutParams(layoutParams);
        }
        return this;
    }

    public int getPosition() {
        return mPosition;
    }

    public ViewHolder setOnclickListener(int resId,
                                         final EditClickLisenter editClickLisenter, final int position) {
        View view = getView(resId);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editClickLisenter.Onclick(v, position);
            }
        });
        return this;
    }

}
