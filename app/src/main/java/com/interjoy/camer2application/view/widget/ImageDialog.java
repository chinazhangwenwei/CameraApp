package com.interjoy.camer2application.view.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wiseweb.watermelon.R;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Lenovo on 2016/4/15.
 */
public class ImageDialog extends Dialog {
    private String url;
    private ImageView imageView;
    private Context context;
    PhotoViewAttacher mAttacher;

    public ImageDialog(Context context, String url) {
        super(context, R.style.ImageloadingDialogStyle);
        this.url = url;
        this.context = context;
//		setOwnerActivity((CinemaDetailActivity) context);// 设置dialog全屏显示

    }

//	private ImageDialog(Context context, int theme) {
//		super(context, theme);
//	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_img);
        imageView = (ImageView) findViewById(R.id.iv_content);
        mAttacher = new PhotoViewAttacher(imageView);
//        LogUtil.d(url);
        Glide.with(context).load(url).into(imageView);
        mAttacher.update();


    }
}
