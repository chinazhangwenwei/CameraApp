package com.interjoy.camer2application.view.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.wiseweb.util.AppUtil;
import com.wiseweb.watermelon.App;
import com.wiseweb.watermelon.R;
import com.wiseweb.watermelon.base.constant.Constant;
import com.wiseweb.watermelon.base.view.Fragment.PictureSlideFragment;
import com.wiseweb.watermelon.base.view.widget.ViewPagerForPhotoViews;
import com.wiseweb.watermelon.utils.MDStatusBarCompat;
import com.wiseweb.watermelon.utils.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * Created by wenwei on 2016/12/21.
 */
public class ImageScalePreviewActivity extends AppCompatActivity {
    @BindView(R.id.vp_content)
    ViewPagerForPhotoViews vpContent;
    @BindView(R.id.tv_ind)
    TextView tvInd;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.iv_scale)
    ImageView ivScale;
    @BindView(R.id.rl_content)
    RelativeLayout rlContent;
    private List<ImagePath> imagePaths;
    private int currentPosition;
    private String downloadPath;
    private ImageInfo imageInfo;
    private int animDuration = 300;
    private boolean isLoad = true;
    private boolean isExit = false;

    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 0x11:
                    cancelProgressDialog();
                    ToastUtil.showShort("保存成功");
                    break;
                case 0x10:
                    cancelProgressDialog();
                    ToastUtil.showShort("保存失败");
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_sacle_preview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.transparent));
        } else {
            MDStatusBarCompat.setKKStatusBar(this, R.color.transparent);
        }
        ButterKnife.bind(this);
        initViewPager();
        initLister();
    }


    private void initViewPager() {
        imagePaths = getIntent().getParcelableArrayListExtra("imagePath");
        currentPosition = getIntent().getIntExtra("currentPosition", -1);
        imageInfo = getIntent().getParcelableExtra("imageInfo");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            imageInfo.setY(imageInfo.getY() + App.statusBarHeight);
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageInfo.getWidth(), imageInfo.getHeight());
        ivScale.setX(imageInfo.getX());
        ivScale.setY(imageInfo.getY());
        ivScale.setLayoutParams(params);

        vpContent.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {

//            currentPictureFragment = PictureSlideFragment.newInstance(imagePaths.get(position));
                return PictureSlideFragment.newInstance(imagePaths.get(position).getLargePath());
            }

            @Override
            public int getCount() {
                return imagePaths.size();
            }
        });
        if (imagePaths.size() < 2) {
            tvInd.setVisibility(View.INVISIBLE);
        } else {
            tvInd.setText(initTextIndex(currentPosition + 1));
        }
        vpContent.setCurrentItem(currentPosition);
        downloadPath = imagePaths.get(currentPosition).getLargePath();
        Glide.with(ImageScalePreviewActivity.this).load(imagePaths.get(currentPosition).getSmallPath()).asBitmap().
                placeholder(R.drawable.ic_launcher1).into(ivScale);
        loadAnim();

    }

    private void initLister() {
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExit || isLoad) {
                    return;
                }
                if (!AppUtil.isConnected(App.context)) {
                    ToastUtil.showShort(Constant.NET_CHECK);
                    return;
                }
                showProgressDialog("正在保存图片");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    RxPermissions.getInstance(ImageScalePreviewActivity.this).request(Manifest.permission.READ_EXTERNAL_STORAGE)
                            .subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean aBoolean) {
                                    if (aBoolean) {
//                                                        ImageUtil.saveImageToGallery(ImagePreviewActivity.this, resource);
                                        saveImage();
                                    } else {
                                        cancelProgressDialog();
                                        ToastUtil.showShort("请给予图片保存到文件的权限");
                                    }

                                }
                            });
                } else {
                    saveImage();
                }


            }
        });

        vpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                tvInd.setText(initTextIndex(currentPosition + 1));
                downloadPath = imagePaths.get(currentPosition).getLargePath();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void saveImage() {
        ExecutorService executors = Executors.newCachedThreadPool();
        executors.submit(new DownloadImageThread());
    }

    /**
     * 下载图片线程
     */
    private class DownloadImageThread extends Thread {
        @Override
        public void run() {
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    // 获得存储卡的路径
                    File appDir = new File(Environment.getExternalStorageDirectory(), "wiseweb");
                    if (!appDir.exists()) {
                        appDir.mkdir();
                    }
                    String fileName = System.currentTimeMillis() + ".jpg";
//                    File file = new File(appDir, fileName);
                    // 创建连接
                    URL url = new URL(downloadPath);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.connect();
                    // 获取文件大小

                    // 创建输入流
                    InputStream is = conn.getInputStream();
                    File imageFile = new File(appDir, fileName);
                    FileOutputStream fos = new FileOutputStream(imageFile);

                    // 缓存
                    byte buf[] = new byte[1024];
                    int len = 0;
                    // 写入到文件中
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    fos.close();
                    is.close();

//            // 其次把文件插入到系统图库

                    MediaStore.Images.Media.insertImage(getContentResolver(),
                            imageFile.getAbsolutePath(), fileName, null);

//            // 最后通知图库更新
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(imageFile);
                    intent.setData(uri);
                    sendBroadcast(intent);//
                    handler.sendEmptyMessage(0x11);
                }
            } catch (MalformedURLException e) {
                handler.sendEmptyMessage(0x10);
                e.printStackTrace();
            } catch (IOException e) {
                handler.sendEmptyMessage(0x10);
                e.printStackTrace();
            }
        }

    }

    private String initTextIndex(int position) {
        StringBuilder stringBuilders = new StringBuilder("");
        stringBuilders.append(position).append("/").append(imagePaths.size());
        return stringBuilders.toString();
    }

    private ProgressDialog progressDialog;

    public void showProgressDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(true);
        }
        if (TextUtils.isEmpty(msg)) {
            msg = "正在加载数据...";
        }
        progressDialog.setMessage(msg);
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    public void cancelProgressDialog() {
        if (progressDialog != null) {
            progressDialog.cancel();
        }
    }


    private void loadAnim() {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator transAnimator = ObjectAnimator.ofFloat(ivScale, "translationX",
                getWindowManager().getDefaultDisplay().getWidth() / 2 - imageInfo.getWidth() / 2);
//        Log.d(TAG, "initAnimSet: " + ivScale.getWidth());
        ObjectAnimator transY = ObjectAnimator.ofFloat(ivScale, "translationY",
                getWindowManager().getDefaultDisplay().getHeight()
                        / 2 - imageInfo.getHeight() / 2);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivScale, "scaleX",
                AppUtil.getScreenWidth(ImageScalePreviewActivity.this) / (float) imageInfo.getWidth());
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivScale, "scaleY", 5);
        AnimatorSet animationDis = new AnimatorSet();
        animationDis.play(scaleX).with(scaleY);
        animatorSet.play(transAnimator).with(transY);
        animatorSet.setDuration(animDuration).start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                vpContent.setVisibility(View.VISIBLE);
                ivScale.setVisibility(View.INVISIBLE);
                isLoad = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
//        ObjectAnimator animator = ObjectAnimator.ofFloat(ivContent, "translationX", imageInfo.getY()).setDuration(200);

    }

    @Override
    public void onBackPressed() {
        exitActivity();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    public void exitActivity() {
        if (isLoad || isExit) {
            return;
        }
        isExit = true;
        tvSave.setVisibility(View.INVISIBLE);
        tvInd.setVisibility(View.INVISIBLE);
        if (currentPosition != imageInfo.getPosition()) {
            int position[] = new int[2];
            calculatePosition(currentPosition + 1, position);
            imageInfo.setX(imageInfo.getX() + (position[1] - imageInfo.getCurrentLine()) * imageInfo.getWidth());
            imageInfo.setY(imageInfo.getY() + (position[0] - imageInfo.getCurrentRow()) * imageInfo.getHeight());
        }
        final AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator transAnimator = ObjectAnimator.ofFloat(ivScale, "translationX",
                imageInfo.getX());
        ObjectAnimator transY = ObjectAnimator.ofFloat(ivScale, "translationY", imageInfo.getY());
        animatorSet.play(transAnimator).with(transY).with(ObjectAnimator.ofFloat(rlContent, "alpha", 0.5f).setDuration(animDuration));
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        vpContent.setVisibility(View.GONE);
        if (currentPosition != imageInfo.getPosition()) {
            Glide.with(ImageScalePreviewActivity.this).load(imagePaths.get(currentPosition).getSmallPath()).asBitmap().
                    placeholder(R.drawable.ic_launcher1).into(ivScale);
        }
        ivScale.setVisibility(View.VISIBLE);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
                overridePendingTransition(0, 0);
                vpContent.setVisibility(View.GONE);
                ivScale.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorSet.setDuration(animDuration).start();
        //延时开启动画

    }

    //计算行列
    public static void calculatePosition(int position, int[] positions) {
        positions[1] = position % 3;
        if (positions[1] == 0) {
            positions[1] = 3;
            positions[0] = position / 3;
        } else {
            positions[0] = position / 3 + 1;
        }
    }

    public static class ImageInfo implements Parcelable {
        private float x;
        private float y;
        private int width;
        private int currentLine;
        private int currentRow;
        private int position;

        public int getCurrentLine() {
            return currentLine;
        }

        public void setCurrentLine(int currentLine) {
            this.currentLine = currentLine;
        }

        public int getCurrentRow() {
            return currentRow;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public void setCurrentRow(int currentRow) {
            this.currentRow = currentRow;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }


        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        private int height;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeFloat(this.x);
            dest.writeFloat(this.y);
            dest.writeInt(this.width);
            dest.writeInt(this.height);
            dest.writeInt(this.currentLine);
            dest.writeInt(this.currentRow);
            dest.writeInt(this.position);
        }

        public ImageInfo() {
        }

        @Override
        public String toString() {
            return "ImageInfo{" +
                    "x=" + x +
                    ", y=" + y +
                    ", width=" + width +
                    ", currentLine=" + currentLine +
                    ", currentRow=" + currentRow +
                    ", height=" + height +
                    '}';
        }

        protected ImageInfo(Parcel in) {
            this.x = in.readFloat();
            this.y = in.readFloat();
            this.width = in.readInt();
            this.height = in.readInt();
            this.currentLine = in.readInt();
            this.currentRow = in.readInt();
            this.position = in.readInt();
        }

        public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
            @Override
            public ImageInfo createFromParcel(Parcel source) {
                return new ImageInfo(source);
            }

            @Override
            public ImageInfo[] newArray(int size) {
                return new ImageInfo[size];
            }
        };
    }

    public static class ImagePath implements Parcelable {
        private String smallPath;
        private String largePath;

        public String getSmallPath() {
            return smallPath;
        }

        public void setSmallPath(String smallPath) {
            this.smallPath = smallPath;
        }

        public String getLargePath() {
            return largePath;
        }

        public void setLargPath(String largePath) {
            this.largePath = largePath;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.smallPath);
            dest.writeString(this.largePath);
        }

        public ImagePath() {
        }

        protected ImagePath(Parcel in) {
            this.smallPath = in.readString();
            this.largePath = in.readString();
        }

        public static final Creator<ImagePath> CREATOR = new Creator<ImagePath>() {
            @Override
            public ImagePath createFromParcel(Parcel source) {
                return new ImagePath(source);
            }

            @Override
            public ImagePath[] newArray(int size) {
                return new ImagePath[size];
            }
        };
    }
}
