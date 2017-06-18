package com.interjoy.camer2application.updatemvp.view;

/**
 * Created by wenwei on 2016/9/26.
 */
public interface BaseView {
    //隐藏各种提示状态
    void hideAll();

    void showLoading();

    void showNetWorkError();

    void showNoData();

    void showServiceError();

    void showSnackMessage(String message);

    void showToastMessage(String message);

    void showProgressDialog(String msg) ;

    void cancelProgressDialog();

    void showNoFandom();

}
