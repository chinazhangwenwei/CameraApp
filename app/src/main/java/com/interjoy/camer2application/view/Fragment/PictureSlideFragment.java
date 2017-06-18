package com.interjoy.camer2application.view.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.wiseweb.watermelon.R;
import com.wiseweb.watermelon.base.view.activity.ImageScalePreviewActivity;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Administrator on 2016/1/3.
 */
public class PictureSlideFragment extends Fragment {
    private String url;
    private PhotoViewAttacher mAttacher;
    private ImageView imageView;
    //    private TextView tvSave;
    private RelativeLayout rlLoad;

    public static PictureSlideFragment newInstance(String url) {
        PictureSlideFragment f = new PictureSlideFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments() != null ? getArguments().getString("url") :
                "http://www.zhagame.com/wp-content/uploads/2016/01/JarvanIV_6.jpg";

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_imageview_item, container, false);
        imageView = (ImageView) v.findViewById(R.id.iv_content);
        mAttacher = new PhotoViewAttacher(imageView);
        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                try {
                    ((ImageScalePreviewActivity) getActivity()).exitActivity();
                } catch (ClassCastException e) {
                    getActivity().finish();
                }
            }

            @Override
            public void onOutsidePhotoTap() {

            }
        });

        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.pb_load);
        progressBar.setVisibility(View.VISIBLE);
        rlLoad = (RelativeLayout) v.findViewById(R.id.rl_load);
        rlLoad.setVisibility(View.VISIBLE);
        Glide.with(getActivity()).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().into(new GlideDrawableImageViewTarget(imageView) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                super.onResourceReady(resource, animation);
                rlLoad.setVisibility(View.INVISIBLE);
                mAttacher.update();
            }

        });
        return v;
    }

}
