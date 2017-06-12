package com.interjoy.camer2application.manager;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import com.interjoy.camer2application.icamera.ICameraControl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwenwei on 2017/6/9.
 */

public class CameraControl implements ICameraControl {
    private Camera mCamera;
    private static volatile CameraControl cameraControl;
    private static final String TAG = "CameraControl";

    private CameraControl() {

    }

    @Override
    public Camera getCamera(Context context) {
        if (mCamera == null) {
            initCamera(context);

        }
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
        cameraControl.initCamera(context);
        return cameraControl;
    }

    /**
     * 初始化相机
     */
    private void initCamera(Context context) {
        if (mCamera == null) {
            try {
                mCamera = Camera.open(); // attempt to get a Camera instance
                initDefaultCameraPara(context);
            } catch (Exception e) {
                // Camera is not available (in use or does not exist)
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initDefaultCameraPara(Context context) {
        determineDisplayOrientation(Camera.CameraInfo.CAMERA_FACING_BACK, context);
        setPhotoSize(1280, 720);
    }

    /**
     * 设置预览相机的旋转角度
     */
    private void determineDisplayOrientation(int cameraId, Context context) {
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
            Log.d(TAG, "setPhotoSize: pictureW" + size.width + "pictureH" + size.height);
            if (size.width == width) {
                sizes = size;
                break;
            }
            if (size.width > width) {
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

            if (preSize.width == width) {
                sizes = preSize;
                break;
            }
            if (preSize.width > width) {
                if (sizes == null) {
                    sizes = preSize;
                }
                break;
            }

            sizes = preSize;
        }

        paramters.setPreviewSize(sizes.width, sizes.height);

//        Camera.Parameters params = mCamera.getParameters();
        List<String> focusModes = paramters.getSupportedFocusModes();
        if (focusModes != null && focusModes.
                contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            // Autofocus mode is supported
            paramters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        mCamera.setParameters(paramters);

    }

    public void setPreViewSize(int w, int h) {
        Camera.Parameters paramters = mCamera.getParameters();
        Camera.Size size = getBestPreviewSize(w, h);
        paramters.setPreviewSize(size.width, size.height);
        Log.d(TAG, "setPreViewSize: " + size.width + "size.height" + size.height);
        mCamera.setParameters(paramters);
    }

    public void setPicSize(int w, int h) {
        Camera.Parameters paramters = mCamera.getParameters();
        Camera.Size size = getBestPictureSize(w, h);
        paramters.setPictureSize(size.width, size.height);
        Log.d(TAG, "setPreViewSize: " + size.width + "size.height" + size.height);
        mCamera.setParameters(paramters);

    }


    public void setScalePhotoSize(int w, int h) {
        setPreViewSize(w, h);
        setPicSize(w, h);

    }

    @Override
    public void cameraFocus(Point point, Camera.AutoFocusCallback callback) {

        if (mCamera == null) {
            return;
        }

        Camera.Parameters parameters = null;
        try {
            parameters = mCamera.getParameters();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        //不支持设置自定义聚焦，则使用自动聚焦，返回

        if (Build.VERSION.SDK_INT >= 14) {

            if (parameters.getMaxNumFocusAreas() <= 0) {
                focus(callback);
            }
            Log.i(TAG, "onCameraFocus:" + point.x + "," + point.y);

            List<Camera.Area> areas = new ArrayList<Camera.Area>();
            int left = point.x - 300;
            int top = point.y - 300;
            int right = point.x + 300;
            int bottom = point.y + 300;
            left = left < -1000 ? -1000 : left;
            top = top < -1000 ? -1000 : top;
            right = right > 1000 ? 1000 : right;
            bottom = bottom > 1000 ? 1000 : bottom;
            areas.add(new Camera.Area(new Rect(left, top, right, bottom), 100));
            parameters.setFocusAreas(areas);
            try {
                //本人使用的小米手机在设置聚焦区域的时候经常会出异常，看日志发现是框架层的字符串转int的时候出错了，
                //目测是小米修改了框架层代码导致，在此try掉，对实际聚焦效果没影响
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return;
            }
        }
        focus(callback);
    }

    private boolean focus(Camera.AutoFocusCallback callback) {
        try {
            mCamera.autoFocus(callback);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    @Override
    public boolean canSwitchCamera() {
        int number = 0;
        if (mCamera != null) {
            number = mCamera.getNumberOfCameras();
        }
        return number > 0;
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
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    private Camera.Size getBestPreviewSize(int width, int height) {
        Camera.Size result = null;
        final Camera.Parameters p = mCamera.getParameters();
        //特别注意此处需要规定rate的比是大的比小的，不然有可能出现rate = height/width，但是后面遍历的时候，current_rate = width/height,所以我们限定都为大的比小的。
        float rate = (float) Math.max(width, height) / (float) Math.min(width, height);
        float tmp_diff;
        float min_diff = -1f;
        for (Camera.Size size : p.getSupportedPreviewSizes()) {
            float current_rate = (float) Math.max(size.width, size.height) / (float) Math.min(size.width, size.height);
            tmp_diff = Math.abs(current_rate - rate);
            if (min_diff < 0) {
                min_diff = tmp_diff;
                result = size;
            }
            if (tmp_diff < min_diff) {
                min_diff = tmp_diff;
                result = size;
            }
        }
        return result;
    }

    private Camera.Size getBestPictureSize(int width, int height) {
        Camera.Size result = null;
        final Camera.Parameters p = mCamera.getParameters();
        //特别注意此处需要规定rate的比是大的比小的，不然有可能出现rate = height/width，但是后面遍历的时候，current_rate = width/height,所以我们限定都为大的比小的。
        float rate = (float) Math.max(width, height) / (float) Math.min(width, height);
        float tmp_diff;
        float min_diff = -1f;
        for (Camera.Size size : p.getSupportedPictureSizes()) {
            float current_rate = (float) Math.max(size.width, size.height) / (float) Math.min(size.width, size.height);
            tmp_diff = Math.abs(current_rate - rate);
            if (min_diff < 0) {
                min_diff = tmp_diff;
                result = size;
            }
            if (tmp_diff < min_diff) {
                min_diff = tmp_diff;
                result = size;
            }
        }
        return result;

    }

}
