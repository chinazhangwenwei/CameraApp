package com.interjoy.camer2application.view.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wiseweb.watermelon.R;


/**
 * Created by Lenovo on 2016/4/21.
 */
public abstract class BaseTitleActivity extends BaseCompatActivity implements View.OnClickListener {
    public Handler handler = new Handler();
    private ImageView ivLeft;
    private Toolbar toolbar;


    @Override
    protected void initToolbar(Bundle savedInstanceState) {
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(false);
//        actionBar.setDisplayShowHomeEnabled(true);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        ivLeft.setOnClickListener(this);


    }


    public void setTitleText(int Title) {
        TextView textView = (TextView) toolbar.findViewById(R.id.tv_title);
        textView.setText(Title);
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

    public void setLeftImage(int id) {

        ivLeft.setVisibility(View.VISIBLE);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                finish();
                break;
            default:
                onClicks(v);
        }
    }

    public void onClicks(View v) {

    }


}
