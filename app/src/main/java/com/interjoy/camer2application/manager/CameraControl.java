package com.interjoy.camer2application.manager;

import android.content.Context;
import android.hardware.Camera;

import com.interjoy.camer2application.icamera.ICamerControl;

/**
 * Created by zhangwenwei on 2017/6/9.
 */

public class CameraControl implements ICamerControl {
    private Camera mCamera;

    @Override
    public Camera getCamera() {
        return mCamera;
    }

    @Override
    public void initCamera(Context context) {
        if(mCamera!=null){

        }

    }

    @Override
    public boolean canSwitchCamera() {
        return false;
    }

    @Override
    public void switchCamera() {

    }

    @Override
    public void lightCamera() {

    }

    @Override
    public String getCameraPath() {
        return null;
    }

    @Override
    public void relaseCamera() {

    }
}
