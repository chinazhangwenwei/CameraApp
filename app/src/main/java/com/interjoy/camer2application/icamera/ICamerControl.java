package com.interjoy.camer2application.icamera;

import android.content.Context;
import android.hardware.Camera;

/**
 * Created by zhangwenwei on 2017/6/9.
 */

public interface ICamerControl {
    Camera getCamera();

    void initCamera(Context context);

    boolean canSwitchCamera();

    void switchCamera();

    void lightCamera();

    String getCameraPath();

    void relaseCamera();

}
