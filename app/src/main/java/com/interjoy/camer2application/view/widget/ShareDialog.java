package com.interjoy.camer2application.view.widget;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wiseweb.util.AppUtil;
import com.wiseweb.watermelon.R;


/**
 * Created by wenwei on 2016/11/18.
 */
public abstract class ShareDialog implements View.OnClickListener {
    private AlertDialog dialog;
    private Context context;

    public ShareDialog(Context context) {
        this.context = context;
        View dialogShare = View.inflate(context, R.layout.dialog_share, null);
        dialog = new AlertDialog.Builder(context)
                .setView(dialogShare)
                .setCancelable(false)
                .create();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = AppUtil.getScreenWidth(context) - AppUtil.dip2px(context, 60);
        dialog.getWindow().setAttributes(params);

        dialog.show();
        View viewCancel = dialogShare.findViewById(R.id.tv_cancel);
        View shareQq = dialogShare.findViewById(R.id.ll_share_qq);
        View shareWx = dialogShare.findViewById(R.id.ll_share_wx);
        View shareWxFriend = dialogShare.findViewById(R.id.ll_share_wx_friends_circle);
        View shareSina = dialogShare.findViewById(R.id.ll_share_sina);
        viewCancel.setOnClickListener(this);
        shareQq.setOnClickListener(this);
        shareWx.setOnClickListener(this);
        shareWxFriend.setOnClickListener(this);
        shareSina.setOnClickListener(this);

    }

    public void showDialog() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void destroy() {
        if (dialog != null) {
            dialog = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                if (dialog != null) {
                    dialog.dismiss();
                    shareCancel();
                }
                break;
            case R.id.ll_share_qq:
                shareOnClick(SHARE_MEDIA.QQ);
                MobclickAgent.onEvent(context, context.getResources().getString(R.string.share_qq));
                break;
            case R.id.ll_share_sina:
                shareOnClick(SHARE_MEDIA.SINA);
                MobclickAgent.onEvent(context, context.getResources().getString(R.string.share_sina));
                break;
            case R.id.ll_share_wx:
                shareOnClick(SHARE_MEDIA.WEIXIN);
                MobclickAgent.onEvent(context, context.getResources().getString(R.string.share_wx_friends));
                break;
            case R.id.ll_share_wx_friends_circle:
                shareOnClick(SHARE_MEDIA.WEIXIN_CIRCLE);
                MobclickAgent.onEvent(context, context.getResources().getString(R.string.share_wx_circle));
                break;
        }
    }

    //点击分享的回调
    public abstract void shareOnClick(SHARE_MEDIA shareMedia);

    public abstract void shareCancel();
}
