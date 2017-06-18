package com.interjoy.camer2application.view.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.wiseweb.util.AppUtil;
import com.wiseweb.watermelon.R;
import com.wiseweb.watermelon.base.constant.Constant;
import com.wiseweb.watermelon.utils.LogUtil;

/**
 * Created by gaoqun on 2016/5/6.
 */
public class MyAppCompatActivity extends AppCompatActivity {
    private View viewRetry;
    private ProgressBar pbLoad;
    public TextView tvHint;
    private ImageView ivRetry;
    private RelativeLayout mLinearLayout;
    public RelativeLayout rlRetry;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        MobclickAgent.setDebugMode(Constant.ISDEBUG);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        initStatubar();
    }


    private void initStatubar() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setTranslucentStatus(true);
            window.addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (AppUtil.StatusBarLightMode(this) == 1 || AppUtil.StatusBarLightMode(this) == 2 ||
                    AppUtil.StatusBarLightMode(this) == 3
                    ) {
                LogUtil.d("设置status黑色字体 result="+AppUtil.StatusBarLightMode(this));
            } else {
                window.setStatusBarColor(getResources().getColor(R.color.gray_color));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getName());
        MobclickAgent.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName());
        MobclickAgent.onResume(this);
    }

    private ProgressDialog progressDialog;

    public void showProgressDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(true);
        }
        if (TextUtils.isEmpty(msg)) {
            msg = "正在加载数据...";
        }
        progressDialog.setMessage(msg);
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    public void cancelProgressDialog() {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
    }

    // 19版本支持
    @TargetApi(19)
    public void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void showLoading() {
        init();
        rlRetry.setVisibility(View.VISIBLE);
        viewRetry.setVisibility(View.VISIBLE);
        pbLoad.setVisibility(View.VISIBLE);
        tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(Constant.LOAD_HINT);
        mLinearLayout.setVisibility(View.VISIBLE);
    }

    public void hideLoding() {
        init();
        rlRetry.setVisibility(View.INVISIBLE);
        pbLoad.setVisibility(View.INVISIBLE);
        viewRetry.setVisibility(View.INVISIBLE);
        tvHint.setVisibility(View.INVISIBLE);
        mLinearLayout.setVisibility(View.INVISIBLE);
    }

    public void showNetWorkError() {
        init();
        rlRetry.setVisibility(View.VISIBLE);
        viewRetry.setVisibility(View.VISIBLE);
        ivRetry.setVisibility(View.VISIBLE);
        ivRetry.setImageResource(R.drawable.iv_no_wifi);
        tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(Constant.NET_CHECK);
        mLinearLayout.setVisibility(View.VISIBLE);
    }

    public void hideNetWorkError() {
        init();
        rlRetry.setVisibility(View.INVISIBLE);
        viewRetry.setVisibility(View.INVISIBLE);
        ivRetry.setVisibility(View.INVISIBLE);
        tvHint.setVisibility(View.INVISIBLE);
        mLinearLayout.setVisibility(View.INVISIBLE);
    }

    public void showServiceError() {
        init();
        rlRetry.setVisibility(View.VISIBLE);
        viewRetry.setVisibility(View.VISIBLE);
        ivRetry.setVisibility(View.VISIBLE);
        ivRetry.setImageResource(R.drawable.iv_no_data);
        tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(Constant.SERVIECE_ERR);
        mLinearLayout.setVisibility(View.VISIBLE);
    }

    public void hideServiceError() {
        init();
        rlRetry.setVisibility(View.INVISIBLE);
        viewRetry.setVisibility(View.INVISIBLE);
        ivRetry.setVisibility(View.INVISIBLE);
        tvHint.setVisibility(View.INVISIBLE);
        mLinearLayout.setVisibility(View.INVISIBLE);
    }

    public void init() {
        if (viewRetry == null) {
            viewRetry = findViewById(R.id.fm_root);
            tvHint = (TextView) findViewById(R.id.tv_hint);
            pbLoad = (ProgressBar) findViewById(R.id.pb_load);
            ivRetry = (ImageView) findViewById(R.id.iv_retry);
            mLinearLayout = (RelativeLayout) findViewById(R.id.ll_check);
            rlRetry = (RelativeLayout) findViewById(R.id.rl_retry);
        }
    }

}
