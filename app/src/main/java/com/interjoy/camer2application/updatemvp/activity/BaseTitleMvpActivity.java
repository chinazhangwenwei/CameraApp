package com.interjoy.camer2application.updatemvp.activity;

import android.os.Bundle;

import com.wiseweb.watermelon.base.updatemvp.presenter.BasePresenter;


/**
 * Created by wenwei on 2016/9/26.
 */
public abstract class BaseTitleMvpActivity<V,T extends BasePresenter<V>> extends BaseTitleActivity {

    public T presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = initPresenter();
        presenter.attach((V) this);

    }


    @Override
    protected void onDestroy() {
        presenter.detach();
        super.onDestroy();
    }

    public abstract T initPresenter();


}
