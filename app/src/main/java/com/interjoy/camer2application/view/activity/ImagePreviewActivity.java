package com.interjoy.camer2application.view.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.wiseweb.util.AppUtil;
import com.wiseweb.watermelon.App;
import com.wiseweb.watermelon.R;
import com.wiseweb.watermelon.base.constant.Constant;
import com.wiseweb.watermelon.base.view.Fragment.PictureSlideFragment;
import com.wiseweb.watermelon.base.view.widget.ViewPagerForPhotoViews;
import com.wiseweb.watermelon.utils.LogUtil;
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
 * Created by Lenovo on 2016/4/15.
 */
public class ImagePreviewActivity extends FragmentActivity {
    @BindView(R.id.vp_content)
    ViewPagerForPhotoViews vpContent;
    @BindView(R.id.tv_ind)
    TextView tvInd;
    @BindView(R.id.tv_save)
    TextView tvSave;
    private List<String> imagePaths;
    private int currentPosition;
    private String imagePath;


    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
//            super.dispatchMessage(msg);
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
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview_preview);

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
        imagePaths = getIntent().getStringArrayListExtra("imagePath");
        currentPosition = getIntent().getIntExtra("currentPosition", 1);
//        vpContent.setAdapter(new MyPagerAdapter());
        vpContent.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                LogUtil.d("fragment" + position);
//                currentPictureFragment = PictureSlideFragment.newInstance(imagePaths.get(position));
                return PictureSlideFragment.newInstance(imagePaths.get(position));
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
        imagePath = imagePaths.get(currentPosition);


    }

    private void initLister() {

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppUtil.isConnected(App.context)) {
                    ToastUtil.showShort(Constant.NET_CHECK);
                    return;
                }
                showProgressDialog("正在保存图片");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    RxPermissions.getInstance(ImagePreviewActivity.this).request(Manifest.permission.READ_EXTERNAL_STORAGE)
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
                tvInd.setText(initTextIndex(position + 1));
                imagePath = imagePaths.get(position);
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
                    URL url = new URL(imagePath);
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
}
