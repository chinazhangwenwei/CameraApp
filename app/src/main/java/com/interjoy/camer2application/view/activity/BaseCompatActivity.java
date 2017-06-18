package com.interjoy.camer2application.view.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umeng.message.PushAgent;
import com.wiseweb.util.AppUtil;
import com.wiseweb.watermelon.App;
import com.wiseweb.watermelon.R;
import com.wiseweb.watermelon.base.constant.Constant;
import com.wiseweb.watermelon.manager.LoginManager;
import com.wiseweb.watermelon.manager.SystemBarTintManager;
import com.wiseweb.watermelon.utils.LogUtil;

import butterknife.ButterKnife;


public abstract class BaseCompatActivity extends AppCompatActivity {

    private View viewRetry;
    private ProgressBar pbLoad;
    private TextView tvHint;
    private ImageView ivRetry;
    private boolean isDestory = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushAgent.getInstance(App.context).onAppStart();
        initStatubar();
        this.setContentView(getLayoutId());
        ButterKnife.bind(this);
        this.initToolbar(savedInstanceState);
        this.initData();
        this.initListeners();
    }

    private void initStatubar() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // 激活状态栏设置
            tintManager.setStatusBarTintEnabled(true);
            // 使用颜色资源
            tintManager.setStatusBarTintResource(R.color.status_bar_transparent);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int type = AppUtil.StatusBarLightMode(this);
            if (!(type == 1 || type == 2 || type == 3)) {
                window.setStatusBarColor(getResources().getColor(R.color.status_bar_transparent));
            }
        }
    }

    protected abstract int getLayoutId();


    protected abstract void initToolbar(Bundle savedInstanceState);

    protected abstract void initListeners();

    protected abstract void initData();


    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }


    @Override
    public void startActivity(Intent intent, Bundle options) {
        super.startActivity(intent, options);
    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestory = true;

    }

    public void loadImage(String url, ImageView imageView, int placeHolder) {
        if (!isDestory) {
            Glide.with(this).load(url).placeholder(placeHolder).into(imageView);
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

    public void showNoData(String content) {
        initHintView();
        viewRetry.setVisibility(View.VISIBLE);
        ivRetry.setVisibility(View.VISIBLE);
        ivRetry.setImageResource(R.drawable.iv_no_data);
        tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(content);

    }

    public void hideAll() {
        initHintView();
        viewRetry.setVisibility(View.INVISIBLE);
        pbLoad.setVisibility(View.INVISIBLE);
        ivRetry.setVisibility(View.INVISIBLE);
        viewRetry.setVisibility(View.INVISIBLE);
    }

    public void showLoading() {
        initHintView();
        viewRetry.setVisibility(View.VISIBLE);
        pbLoad.setVisibility(View.VISIBLE);
        tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(Constant.LOAD_HINT);
        LogUtil.d("showLoading");
    }

    public void hideLoding() {
        initHintView();
        pbLoad.setVisibility(View.INVISIBLE);
        viewRetry.setVisibility(View.INVISIBLE);
        tvHint.setVisibility(View.INVISIBLE);
        tvHint.setText(Constant.LOAD_HINT);
        LogUtil.d("hindLoading");
    }

    public void showNetWorkError() {
        initHintView();
        viewRetry.setVisibility(View.VISIBLE);
        ivRetry.setVisibility(View.VISIBLE);
        ivRetry.setImageResource(R.drawable.iv_no_wifi);
        tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(Constant.NET_CHECK);
        LogUtil.d("showNetWorkError");
    }

    public void hideNetWorkError() {
        initHintView();
        viewRetry.setVisibility(View.INVISIBLE);
        ivRetry.setVisibility(View.INVISIBLE);
        tvHint.setVisibility(View.INVISIBLE);
    }

    public void showServiceError() {
        initHintView();
        viewRetry.setVisibility(View.VISIBLE);
        ivRetry.setVisibility(View.VISIBLE);
        ivRetry.setImageResource(R.drawable.iv_no_data);
        tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(Constant.SERVIECE_ERR);
    }

    public void hideServiceError() {
        initHintView();
        viewRetry.setVisibility(View.INVISIBLE);
        ivRetry.setVisibility(View.INVISIBLE);
        tvHint.setVisibility(View.INVISIBLE);
        tvHint.setText(Constant.SERVIECE_ERR);
    }

    public void initHintView() {
        if (null == viewRetry) {
            viewRetry = findViewById(R.id.fm_root);
            tvHint = (TextView) findViewById(R.id.tv_hint);
            pbLoad = (ProgressBar) findViewById(R.id.pb_load);
            ivRetry = (ImageView) findViewById(R.id.iv_retry);
        }
    }

    public boolean isConnected() {
        return AppUtil.isConnected(App.context);
    }

    public boolean isLogin() {
        return LoginManager.getLoginManager(App.context).getUser() != null;
    }

    private ProgressDialog progressDialog;

    public void showProgressDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);

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


}