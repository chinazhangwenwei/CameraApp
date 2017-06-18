package com.interjoy.camer2application.updatemvp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wiseweb.watermelon.R;

/**
 * Created by wenwei on 2016/9/26.
 */
public class BaseTitleActivity extends BaseCompatActivity {
    private Toolbar toolbar;
    private ImageView ivLeft;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ivLeft = (ImageView) toolbar.findViewById(R.id.iv_left);
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public ImageView getIvLeft() {
        return ivLeft;
    }

    public void setTitleText(int Title) {
        TextView textView = (TextView) toolbar.findViewById(R.id.tv_title);
        textView.setText(Title);
    }

    public void setTitleText(String titleText) {
        TextView textView = (TextView) toolbar.findViewById(R.id.tv_title);
        textView.setText(titleText);
    }

    public void setTitlesColor(int color) {
        TextView textView = (TextView) toolbar.findViewById(R.id.tv_title);
        textView.setTextColor(color);
    }

    public void setRightImage(int id) {
        ImageView ivRight = (ImageView) toolbar.findViewById(R.id.iv_right);
        ivRight.setVisibility(View.VISIBLE);

        ivRight.setImageResource(id);
    }

    public void hideLeftImage() {
        ivLeft.setVisibility(View.INVISIBLE);
    }

    public void setLeftImage(int id) {
        ivLeft.setImageResource(id);
    }

    public TextView getRightText() {
        TextView tvRight = (TextView) toolbar.findViewById(R.id.tv_right);
        return tvRight;
    }

    public void setRightText(int text) {
        TextView tvRight = (TextView) toolbar.findViewById(R.id.tv_right);
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText(text);
    }

    public void setRightText(int text, int color) {
        TextView tvRight = (TextView) toolbar.findViewById(R.id.tv_right);
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setTextColor(color);
        tvRight.setText(text);
    }
}
