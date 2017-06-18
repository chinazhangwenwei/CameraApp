package com.interjoy.camer2application.view.widget;

import android.Manifest;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wiseweb.watermelon.R;
import com.wiseweb.watermelon.utils.ToastUtil;

import rx.functions.Action1;

/**
 * Created by wenwei on 2016/8/9.
 */
public abstract class SharePopupWindow extends PopupWindow implements View.OnClickListener {
    private Context context;

    public SharePopupWindow(int layoutID, Context context) {
        this.context = context;
        LinearLayout shareQQ, shareSina, shareWX, shareWXFriendsCircle;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View popupWindow = layoutInflater.inflate(layoutID, null);
        shareQQ = ((LinearLayout) popupWindow.findViewById(R.id.ll_p_qq));
        shareSina = (LinearLayout) popupWindow.findViewById(R.id.ll_p_sina);
        shareWX = (LinearLayout) popupWindow.findViewById(R.id.ll_p_wx);
        shareWXFriendsCircle = (LinearLayout) popupWindow.findViewById(R.id.ll_p_wx_friends_circle);
        shareQQ.setOnClickListener(this);
        shareSina.setOnClickListener(this);
        shareWX.setOnClickListener(this);
        shareWXFriendsCircle.setOnClickListener(this);
        TextView cancel = (TextView) popupWindow.findViewById(R.id.cancel_share);
        cancel.setOnClickListener(this);
        setContentView(popupWindow);
        setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);
        setFocusable(true);
        setAnimationStyle(R.style.activity_page_share_popuwindow);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_p_qq:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    RxPermissions.getInstance(context).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean aBoolean) {
                                    if (aBoolean) {
                                        shareOnClick(SHARE_MEDIA.QQ);
                                        MobclickAgent.onEvent(context, context.getResources().getString(R.string.share_qq));
                                    } else {
                                        ToastUtil.showShort("请允许获取手机系统类型权限");
                                    }
                                }
                            });
                } else {
                    shareOnClick(SHARE_MEDIA.QQ);
                    MobclickAgent.onEvent(context, context.getResources().getString(R.string.share_qq));
                }

                break;
            case R.id.ll_p_sina:
                shareOnClick(SHARE_MEDIA.SINA);
                MobclickAgent.onEvent(context, context.getString(R.string.share_sina));
                break;
            case R.id.ll_p_wx:
                shareOnClick(SHARE_MEDIA.WEIXIN);
                MobclickAgent.onEvent(context, context.getResources().getString(R.string.share_wx_friends));
                break;
            case R.id.ll_p_wx_friends_circle:
                shareOnClick(SHARE_MEDIA.WEIXIN_CIRCLE);
                MobclickAgent.onEvent(context, context.getResources().getString(R.string.share_wx_circle));
                break;
        }
        if (isShowing()) {
            dismiss();//点击后关闭popWindow;
        }
    }

    //点击分享的回调
    public abstract void shareOnClick(SHARE_MEDIA shareMedia);
}
