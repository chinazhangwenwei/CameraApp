package com.interjoy.camer2application.icamera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;

/**
 * Created by zhangwenwei on 2017/6/9.
 */

public interface ICameraControl {
    Camera getCamera(Context context);

    void initDefaultCameraPara(Context context);

    boolean canSwitchCamera();

    void switchCamera();

    void lightCamera();

    String getCameraPath();

    void cameraFocus(Point point, Camera.AutoFocusCallback callback);

    void releaseCamera();

    void setPhotoSize(int width, int height);

}
