package com.interjoy.camer2application.updatemvp.presenter;

import com.wiseweb.util.AppUtil;
import com.wiseweb.watermelon.App;
import com.wiseweb.watermelon.sing.mvp.data.local.SingCache;

/**
 * Created by wenwei on 2016/9/26.
 */
public abstract class BasePresenter<T> {
    public T mView;
    public SingCache singCache;
    public boolean isInitData = false;

    public void attach(T mView) {
        this.mView = mView;
        singCache = new SingCache();
    }

    public void detach() {
        cancelRequest();
        singCache = null;
        mView = null;
    }

    //app启动加载数据方法
    public abstract void initData();

    //取消请求
    public abstract void cancelRequest();



    public boolean isConnected() {
        return AppUtil.isConnected(App.context);
    }
}
