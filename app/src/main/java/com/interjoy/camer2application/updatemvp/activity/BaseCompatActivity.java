package com.interjoy.camer2application.updatemvp.activity;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wiseweb.util.AppUtil;
import com.wiseweb.watermelon.App;
import com.wiseweb.watermelon.R;
import com.wiseweb.watermelon.base.constant.Constant;
import com.wiseweb.watermelon.manager.LoginManager;
import com.wiseweb.watermelon.manager.SystemBarTintManager;
import com.wiseweb.watermelon.sing.mvp.view.BaseView;
import com.wiseweb.watermelon.utils.LogUtil;
import com.wiseweb.watermelon.utils.ToastUtil;

/**
 * Created by wenwei on 2016/9/26.
 */
public class BaseCompatActivity extends AppCompatActivity implements BaseView {
    private View viewRetry;
    private ProgressBar pbLoad;
    private TextView tvHint;
    private ImageView ivRetry;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
    }


    public <T> void startTargetActivity(Class<T> c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    @Override
    public void showNoFandom() {
        initHintView();
        viewRetry.setVisibility(View.VISIBLE);
        ivRetry.setVisibility(View.VISIBLE);
        ivRetry.setImageResource(R.drawable.icon_cry);
        tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(R.string.no_fandom);
    }

    @Override
    public void showNetWorkError() {
        initHintView();
        viewRetry.setVisibility(View.VISIBLE);
        ivRetry.setVisibility(View.VISIBLE);
        ivRetry.setImageResource(R.drawable.iv_no_wifi);
        tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(Constant.NET_CHECK);
        LogUtil.d("showNetWorkError");
    }

    @Override
    public void showNoData() {
        initHintView();
        viewRetry.setVisibility(View.VISIBLE);
        ivRetry.setVisibility(View.VISIBLE);
        ivRetry.setImageResource(R.drawable.iv_no_data);
        tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(Constant.SERVICE_NO_DATA);
    }

    @Override
    public void showServiceError() {
        initHintView();
        viewRetry.setVisibility(View.VISIBLE);
        ivRetry.setVisibility(View.VISIBLE);
        ivRetry.setImageResource(R.drawable.iv_no_data);
        tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(Constant.SERVIECE_ERR);
    }

    @Override
    public void showSnackMessage(String message) {
        Snackbar snackbar = Snackbar.make(viewRetry, message, Snackbar.LENGTH_SHORT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
        snackbarLayout.setAlpha(1f);
        String action = "";
        if (message.startsWith("数")) {
            action = "重试！";
        } else if (message.startsWith("网")) {
            action = "打开网络！";
        } else if (message.startsWith("加")) {
            action = "";
        }
        ((TextView) snackbarLayout.findViewById(R.id.snackbar_text)).setTextColor(ContextCompat.getColor(App.context, R.color.dark_blue_color));
        final String finalAction = action;
        snackbar.setAction(action, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalAction.startsWith("打")) {
                    startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                }
            }
        });
        snackbar.show();
    }


    @Override
    public void showToastMessage(String message) {
        ToastUtil.showShort(message);
    }

    @Override
    public void showLoading() {
        initHintView();
        viewRetry.setVisibility(View.VISIBLE);
        pbLoad.setVisibility(View.VISIBLE);
        tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(Constant.LOAD_HINT);

        LogUtil.d("showLoading");
    }

    @Override
    public void hideAll() {
        initHintView();
        viewRetry.setVisibility(View.INVISIBLE);
        pbLoad.setVisibility(View.INVISIBLE);
        ivRetry.setVisibility(View.INVISIBLE);
        viewRetry.setVisibility(View.INVISIBLE);
    }

    private void initStatusBar() {
        //统一状态栏颜色
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


    public void hideServiceErrors() {

    }

    public void initHintView() {
        if (null == viewRetry) {
            viewRetry = findViewById(R.id.fm_root);
            tvHint = (TextView) findViewById(R.id.tv_hint);
            pbLoad = (ProgressBar) findViewById(R.id.pb_load);
            ivRetry = (ImageView) findViewById(R.id.iv_retry);
        }
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
