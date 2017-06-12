package com.interjoy.camer2application;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.interjoy.camer2application.bean.ImageInfo;
import com.interjoy.camer2application.dao.DbIControl;
import com.interjoy.camer2application.dao.DbManager;
import com.interjoy.camer2application.manager.CameraControl;
import com.interjoy.camer2application.manager.SensorControl;
import com.interjoy.camer2application.util.FileUtil;
import com.interjoy.camer2application.widget.CameraPreview;
import com.interjoy.camer2application.widget.FocusImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CameraPreview mPreview;
    private CameraControl cameraControl;
    private SensorControl sensorControl;
    private FrameLayout fmContainer;
    private FocusImageView focusImageView;
    private Camera.AutoFocusCallback callback;
    private ImageView ivData;
    private ScaleAnimation scaleAnimation;
    private TranslateAnimation transLateAnimation;
    private AnimationSet animationSet;
    private DbIControl dbIControl;
    private List<ImageInfo> imageInfos;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitleBar();
        setContentView(R.layout.activity_main);
        initViewAndControl();
        initListener();
        initAnimation();
        initDbAndData();


    }

    private void initDbAndData() {
        imageInfos = new ArrayList<>(30);
        dbIControl = DbManager.getDbControl(MainActivity.this);
    }

    private void initTitleBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    localLayoutParams.flags);
        }
    }


    private void initAnimation() {
        scaleAnimation = new ScaleAnimation(1.0f, 0.2f, 1.0f, 0.1f, 0.5f, 0.5f);
        transLateAnimation = new TranslateAnimation(1.0f, 1.0f, 0f, 100f);
        animationSet = new AnimationSet(true);
        animationSet.setDuration(400);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(transLateAnimation);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ivData.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivData.setVisibility(View.INVISIBLE);
                mPreview.startPreView();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void releaseAnimation() {
        if (animationSet != null) {
            animationSet.cancel();
            animationSet = null;
            scaleAnimation = null;
            transLateAnimation = null;
        }

    }


    private void initViewAndControl() {
        cameraControl = CameraControl.getCameraControl(MainActivity.this);
        fmContainer = (FrameLayout) findViewById(R.id.camera_preview);
        ivData = (ImageView) findViewById(R.id.iv_data);
        mPreview = new CameraPreview(this, cameraControl.getCamera(MainActivity.this));
        cameraControl = CameraControl.getCameraControl(MainActivity.this);
        sensorControl = SensorControl.getInstance(MainActivity.this);
        focusImageView = (FocusImageView) fmContainer.findViewById(R.id.iv_focus);
        fmContainer.addView(mPreview, 0);

    }

    private void initListener() {
        callback = new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                while (sensorControl.isFocusLocked()) {
                    sensorControl.unlockFocus();
                }
                if (success) {
                    focusImageView.successFocus();
                } else {
                    focusImageView.failFocus();
                }

            }
        };
        sensorControl.setCameraFocusListener(new SensorControl.CameraFocusListener() {
            @Override
            public void onFocus() {
                Point point = new Point(fmContainer.getWidth() / 2,
                        fmContainer.getHeight() / 2);
                sensorControl.lockFocus();
                focusImageView.setX(point.x - focusImageView.getWidth() / 2);
                focusImageView.setY(point.y - focusImageView.getHeight() / 2);
                focusImageView.startFocus();
                cameraControl.cameraFocus(point, callback);

            }
        });
        fmContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        Point point = new Point();
                        point.set((int) event.getX(), (int) event.getY());
                        Log.d(TAG, "onTouch: " + point.x + "_______point.y" + point.y);
                        focusImageView.setX(point.x - focusImageView.getWidth() / 2);
                        focusImageView.setY(point.y - focusImageView.getHeight() / 2);
                        sensorControl.lockFocus();
                        focusImageView.startFocus();
                        cameraControl.cameraFocus(point, callback);
                        break;
                }
                return true;
            }
        });
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        cameraControl.getCamera(MainActivity.this).takePicture(null, null, mPicture);
                    }
                }
        );
    }


    private static final String TAG = "MainActivity";
    private File dirFile = null;
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
            newOpts.inJustDecodeBounds = false;
            newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
            // Get bitmap info, but notice that bitmap is null now
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, newOpts);
            Matrix matrix = new Matrix();
            matrix.setRotate(90);
            Bitmap bNew = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
            ivData.setImageBitmap(bNew);
            ivData.setVisibility(View.VISIBLE);
            ivData.startAnimation(animationSet);
            //安全性更高 每次都去获取
            dirFile = FileUtil.getOutputMediaFile(MainActivity.this);
            File file = null;
            try {
                file = FileUtil.getOutPicPath(dirFile);
                FileOutputStream fos = new FileOutputStream(file);
                bNew.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                file = null;
            } catch (IOException e) {
                file = null;
            } catch (NullPointerException e) {
                file = null;

            }
            if (file != null) {
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setImageLabel("苹果");
                imageInfo.setTakeTime(System.currentTimeMillis());
                imageInfo.setImagePath(file.getAbsolutePath());
                dbIControl.add(imageInfo);
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        sensorControl.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorControl.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPreview.setCamera(cameraControl.getCamera(MainActivity.this));
    }

    private void releaseCamera() {
        if (cameraControl != null) {
            cameraControl.releaseCamera();        // release the camera for other applications
//            mCamera = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbIControl.closeDb();
        releaseAnimation();
        sensorControl = null;
        cameraControl = null;
    }
}
