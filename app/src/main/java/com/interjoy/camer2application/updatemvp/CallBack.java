package com.interjoy.camer2application.updatemvp;

import com.wiseweb.watermelon.base.beans.ResponseData;

import retrofit2.Response;

/**
 * Created by wenwei on 2016/9/26.
 */
public interface CallBack<T> {
    void Succeed(T t);

    void Failed(String errorMsg);

    void ServiceError(String message);

    interface ResponseResult {
        <T> void handleSucceed(Response<ResponseData<T>> response, CallBack callBack);

        void handleFailed(Throwable throwable, CallBack callBack);


    }
}
