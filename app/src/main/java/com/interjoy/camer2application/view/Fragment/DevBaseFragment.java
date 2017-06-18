package com.interjoy.camer2application.view.Fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.wiseweb.watermelon.App;
import com.wiseweb.watermelon.R;
import com.wiseweb.watermelon.base.constant.Constant;
import com.wiseweb.watermelon.manager.LoginManager;
import com.wiseweb.watermelon.user.bean.User;
import com.wiseweb.watermelon.utils.ToastUtil;


public class DevBaseFragment extends Fragment implements OnClickListener {
    private View rootView;
    private View viewRetry;
    private ProgressBar pbLoad;
    public TextView tvHint;
    private ImageView ivRetry;
    public RelativeLayout rlRetry;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        rootView = view;
        if (null == viewRetry) {
            viewRetry = findView(R.id.fm_root);
            tvHint = findView(R.id.tv_hint);
            pbLoad = findView(R.id.pb_load);
            ivRetry = findView(R.id.iv_retry);
            rlRetry = findView(R.id.rl_retry);
        }
    }

    protected <V extends View> V findView(int id) {
        return (V) rootView.findViewById(id);
    }

    public int getLayoutId() {
        return 0;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public String getTitle() {
        return getTag();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName());
    }

    @Override
    public void onClick(View v) {

    }

    public View getRootView() {
        return rootView;
    }

    private ProgressDialog progressDialog;

    public void showProgressDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
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

    public <T> void startActivitys(Class<T> c) {
        Intent intent = new Intent(getActivity(), c);
        startActivity(intent);
    }

    public void hideAll() {

        viewRetry.setVisibility(View.INVISIBLE);
        pbLoad.setVisibility(View.INVISIBLE);
//        ivRetry.setVisibility(View.GONE);
        tvHint.setVisibility(View.INVISIBLE);

    }

    public void showLoading() {

        rlRetry.setVisibility(View.VISIBLE);
        ivRetry.setVisibility(View.INVISIBLE);
        viewRetry.setVisibility(View.VISIBLE);
        pbLoad.setVisibility(View.VISIBLE);
        tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(Constant.LOAD_HINT);
    }

    public void hideLoding() {
        rlRetry.setVisibility(View.INVISIBLE);
        pbLoad.setVisibility(View.INVISIBLE);
        viewRetry.setVisibility(View.INVISIBLE);
        tvHint.setVisibility(View.INVISIBLE);
    }

    public void showNetWorkError() {
        rlRetry.setVisibility(View.VISIBLE);
        viewRetry.setVisibility(View.VISIBLE);
        ivRetry.setVisibility(View.VISIBLE);
        ivRetry.setImageResource(R.drawable.iv_no_wifi);
        tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(Constant.NET_CHECK);
    }

    public void hideNetWorkError() {
        rlRetry.setVisibility(View.INVISIBLE);
        viewRetry.setVisibility(View.INVISIBLE);
        ivRetry.setVisibility(View.INVISIBLE);
        tvHint.setVisibility(View.INVISIBLE);
    }

    public void showServiceError() {
        rlRetry.setVisibility(View.VISIBLE);
        viewRetry.setVisibility(View.VISIBLE);
        ivRetry.setVisibility(View.VISIBLE);
        ivRetry.setImageResource(R.drawable.iv_no_data);
        tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(Constant.SERVIECE_ERR);
    }

    public void hideServiceError() {
        rlRetry.setVisibility(View.INVISIBLE);
        viewRetry.setVisibility(View.INVISIBLE);
        ivRetry.setVisibility(View.INVISIBLE);
        tvHint.setVisibility(View.INVISIBLE);
    }

    public void showNoData(String info) {
        rlRetry.setVisibility(View.VISIBLE);
        viewRetry.setVisibility(View.VISIBLE);
        ivRetry.setVisibility(View.VISIBLE);
        ivRetry.setImageResource(R.drawable.iv_no_data);
        tvHint.setVisibility(View.VISIBLE);
        tvHint.setText(info);
    }

    public void showNoData() {
        showNoData(Constant.SERVICE_NO_DATA);
    }


    public void showSnackMessage(String message) {
    }

    public void showToastMessage(String message) {
        ToastUtil.showShort(message);
    }

    public void showNoFandom() {
    }

    public boolean isLogin() {
        return LoginManager.getLoginManager(getActivity()).getUser() != null;
    }

    public User getUser() {
        return LoginManager.getLoginManager(App.context).getUser();
    }

}
