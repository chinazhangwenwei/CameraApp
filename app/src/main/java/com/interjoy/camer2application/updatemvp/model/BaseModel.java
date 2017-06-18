package com.interjoy.camer2application.updatemvp.model;

import com.ta.utdid2.android.utils.StringUtils;
import com.wiseweb.watermelon.base.beans.ResponseData;
import com.wiseweb.watermelon.base.constant.Constant;
import com.wiseweb.watermelon.base.updatemvp.CallBack;
import com.wiseweb.watermelon.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Response;

/**
 * Created by wenwei on 2016/12/12.
 */
public class BaseModel implements CallBack.ResponseResult {

    @Override
    public void handleFailed(Throwable throwable, CallBack callBack) {
        if (throwable != null && throwable.getMessage() != null && !throwable.getMessage().equals("")) {
            callBack.Failed(throwable.getMessage());
        } else {
            callBack.Failed("throwable==null||'---'");
        }

    }

    @Override
    public <T> void handleSucceed(Response<ResponseData<T>> response, CallBack callBack) {

        if (response == null) {
            callBack.Failed("没有获取到response!");
        } else if (response.body() == null) {
            callBack.ServiceError("error_" + response.code());
        } else {
            LogUtil.d(response.body().getCode() + "code代码");

            if (response.body().getCode() != null && response.body().getCode().equals("530")) {
                callBack.Failed("530");
            } else if (response.code() == 200 && response.body().isSuccess()) {
                callBack.Succeed(response.body().getData());
            } else if (response.code() == 800) {
                callBack.Failed("800");
            }
            else {
                if (!StringUtils.isEmpty(response.body().getCode()) && response.body().getCode().equals(Constant.AUTH_ERR)) {
                    EventBus.getDefault().post(Constant.AUTH_ERR);
                }
                callBack.ServiceError(response.body().getMsg());
//                callBack.Failed(response.body().getMsg() + "");
            }


        }
    }

}
