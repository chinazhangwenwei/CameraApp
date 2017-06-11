package com.interjoy.camer2application.manager;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;

import com.interjoy.camer2application.icamera.ICameraControl;

import java.util.List;

/**
 * Created by zhangwenwei on 2017/6/9.
 */

public class CameraControl implements ICameraControl {
    private Camera mCamera;
    private static volatile CameraControl cameraControl;
    private static final String TAG = "CameraControl";
    private Context context;

    private CameraControl() {
    }

    @Override
    public Camera getCamera() {
        return mCamera;
    }

    public static CameraControl getCameraControl(Context context) {
        if (cameraControl == null) {
            if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                // this device has a camera
                return null;
            }
            synchronized (CameraControl.class) {
                if (cameraControl == null) {
                    cameraControl = new CameraControl();
                }
            }
        }
        cameraControl.initCamera();
        return cameraControl;
    }

    /**
     * 初始化相机
     */
    private void initCamera() {
        if (mCamera == null) {
            try {
                mCamera = Camera.open(); // attempt to get a Camera instance
            } catch (Exception e) {
                // Camera is not available (in use or does not exist)
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initDefaultCameraPara() {
        if (mCamera != null) {
            determineDisplayOrientation(Camera.CameraInfo.CAMERA_FACING_BACK);
            setPhotoSize(1280, 720);
        }
    }

    /**
     * 设置预览相机的旋转角度
     */
    private void determineDisplayOrientation(int cameraId) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);

        int rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0: {
                degrees = 0;
                break;
            }
            case Surface.ROTATION_90: {
                degrees = 90;
                break;
            }
            case Surface.ROTATION_180: {
                degrees = 180;
                break;
            }
            case Surface.ROTATION_270: {
                degrees = 270;
                break;
            }
        }
        int displayOrientation;
        // Camera direction
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // Orientation is angle of rotation when facing the camera for
            // the camera image to match the natural orientation of the device
            displayOrientation = (cameraInfo.orientation + degrees) % 360;
            displayOrientation = (360 - displayOrientation) % 360;
        } else {
            displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
        }
        Log.d(TAG, "相机: " + cameraInfo.orientation + "___________屏幕" + degrees);

        mCamera.setDisplayOrientation(displayOrientation);

        Log.i(TAG, "displayOrientation:" + displayOrientation);
    }

    @Override
    public void setPhotoSize(int width, int height) {
        Camera.Parameters paramters = mCamera.getParameters();
        List<Camera.Size> pictureSize = paramters.getSupportedPictureSizes();
        List<Camera.Size> previewSize = paramters.getSupportedPreviewSizes();
        Camera.Size sizes = null;
        for (Camera.Size size : pictureSize) {
            Log.d(TAG, "setPhotoSize: pictureW" + size.width + "pictureH" + height);
            if (width > size.width) {
                if (sizes == null) {
                    sizes = size;
                }
                break;
            }
            sizes = size;
        }
        paramters.setPictureSize(sizes.width, sizes.height);
        for (Camera.Size preSize : previewSize) {
            Log.d(TAG, "setPhotoSize: pictureW" + preSize.width + "pictureH" + height);
            if (width > preSize.width) {
                if (sizes == null) {
                    sizes = preSize;
                }
                break;
            }
            sizes = preSize;
        }
        paramters.setPreviewSize(sizes.width, sizes.height);
        // get Camera parameters
//        Camera.Parameters params = mCamera.getParameters();
        List<String> focusModes = paramters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            // Autofocus mode is supported
            paramters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        mCamera.setParameters(paramters);

    }


    @Override
    public boolean canSwitchCamera() {
        int number = 0;
        if(mCamera!=null){
           number =  mCamera.getNumberOfCameras();
        }
        return number>0;
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
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
}
