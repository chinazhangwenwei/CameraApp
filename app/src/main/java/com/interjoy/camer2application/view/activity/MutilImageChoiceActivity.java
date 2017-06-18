package com.interjoy.camer2application.view.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wiseweb.watermelon.R;
import com.wiseweb.watermelon.base.adpter.MutilImageChoiceAdpter;
import com.wiseweb.watermelon.base.beans.ImageFloder;
import com.wiseweb.watermelon.base.view.widget.ImageChoiceListActivity;
import com.wiseweb.watermelon.utils.ToastUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lenovo on 2016/4/15.
 */
public class MutilImageChoiceActivity extends Activity implements View.OnClickListener {
    @BindView(R.id.id_gridView)
    GridView idGridView;
    ProgressDialog mProgressDialog;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_photo)
    TextView tvPhoto;
    @BindView(R.id.id_bottom_ly)
    RelativeLayout idBottomLy;
    private int mPicsSize;
    private File mImgDir;
    private int totalCount;
    private HashSet<String> mDirPaths = new HashSet<>();
    private List<ImageFloder> mImageFloders = new ArrayList<>();
    private List<String> imagePath = new ArrayList<>();
    private MutilImageChoiceAdpter mutilImageChoiceAdpter;
    private ImageFloder imageFloder;
    private int position;
    public static final int IMAGE_MAX = 10;


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x110:
                    mProgressDialog.dismiss();
                    initGridView();
                    break;
                case 0x111:
                    mProgressDialog.dismiss();
//                    Intent intent = new Intent();
//                    intent.putStringArrayListExtra("imagePath", mutilImageChoiceAdpter.getmSelectedImage());
                    setResult(1);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutil_image_choice);
        ButterKnife.bind(this);
        tvConfirm.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvPhoto.setOnClickListener(this);
        getImages();
        initGridView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initGridView() {
        position = getIntent().getIntExtra("position", -1);
        imageFloder = ImageChoiceListActivity.mImageFloders.get(position);
        getAllImagePath();
        mutilImageChoiceAdpter = new MutilImageChoiceAdpter(MutilImageChoiceActivity.this,
                imagePath, R.layout.activity_mutil_image_item);
        mutilImageChoiceAdpter.count = imageFloder.getSelectCount();
        idGridView.setAdapter(mutilImageChoiceAdpter);
        tvCount.setText(imageFloder.getCount() + "张");
    }

    private void getAllImagePath() {
//        for (ImageFloder imageFloder : mImageFloders) {
//            List<String> tempList = new ArrayList<>();
//            mImgDir = new File(imageFloder.getDir());
//            for (File file : mImgDir.listFiles()) {
//                String filename = file.getAbsolutePath();
//                if (filename.endsWith(".jpg") || filename.endsWith(".png")
//                        || filename.endsWith(".jpeg")) {
//                    tempList.add(filename);
//                }
//            }
//            tempList = Arrays.asList(mImgDir.list(new FilenameFilter() {
//                @Override
//                public boolean accept(File dir, String filename) {
//                    if (filename.endsWith(".jpg") || filename.endsWith(".png")
//                            || filename.endsWith(".jpeg"))
//                        return true;
//                    return false;
//                }
//            }));
//        List<String> tempList = new ArrayList<>();
        imagePath.clear();
        mImgDir = new File(imageFloder.getDir());
        List<File> list = new ArrayList<>();
        for (File tempFile : mImgDir.listFiles()) {
            list.add(tempFile);
        }
        Collections.sort(list, new FileComparator());
        for (File file : list) {
            String filename = file.getAbsolutePath();
            if (filename.endsWith(".jpg") || filename.endsWith(".png")
                    || filename.endsWith(".jpeg")) {
                imagePath.add(filename);
            }
        }
//        imagePath.addAll(tempList);
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            ToastUtil.showShort("暂无图片");
            return;
        }
        // 显示进度条
        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
        ExecutorService executors = Executors.newCachedThreadPool();
        executors.submit(new ScanImageTask());


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
//                ImageChoiceListActivity.savePath.addAll(mutilImageChoiceAdpter.getmSelectedImage());
//                final List<String> temp = mutilImageChoiceAdpter.getmSelectedImage();
                if (mutilImageChoiceAdpter.count == 0) {
                    ToastUtil.showShort("请您选择照片");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("count", mutilImageChoiceAdpter.count);
                intent.putExtra("position", position);
                setResult(1, intent);
                finish();
                break;
            case R.id.tv_cancel:
                setResult(-1);
            case R.id.tv_photo:
                ImageChoiceListActivity.savePath.addAll(mutilImageChoiceAdpter.getmSelectedImage());
                ImageChoiceListActivity.savePath.removeAll(mutilImageChoiceAdpter.getFirstSelectImage());
                setResult(-1);
                finish();
                break;

        }


    }

    @Override
    public void onBackPressed() {
        ImageChoiceListActivity.savePath.addAll(mutilImageChoiceAdpter.getmSelectedImage());
        ImageChoiceListActivity.savePath.removeAll(mutilImageChoiceAdpter.getFirstSelectImage());
        setResult(-1);
        finish();
//        mProgressDialog.dismiss();
//        super.onBackPressed();
    }

    class ScanImageTask implements Runnable {
        @Override
        public void run() {
            String firstImage = null;
            Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver mContentResolver = MutilImageChoiceActivity.this
                    .getContentResolver();
            // 只查询jpeg和png的图片
            Cursor mCursor = mContentResolver.query(mImageUri, null,
                    MediaStore.Images.Media.MIME_TYPE + "=? or "
                            + MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg", "image/png"},
                    MediaStore.Images.Media.DATE_MODIFIED);
//
            while (mCursor.moveToNext()) {
                // 获取图片的路径
                String path = mCursor.getString(mCursor
                        .getColumnIndex(MediaStore.Images.Media.DATA));
                // 拿到第一张图片的路径
                if (firstImage == null)
                    firstImage = path;
                // 获取该图片的父路径名
                File parentFile = new File(path).getParentFile();
                if (parentFile == null)
                    continue;
                String dirPath = parentFile.getAbsolutePath();
                ImageFloder imageFloder = null;
                // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                if (mDirPaths.contains(dirPath)) {
                    continue;
                } else {
                    mDirPaths.add(dirPath);
                    // 初始化imageFloder
                    imageFloder = new ImageFloder();
                    imageFloder.setDir(dirPath);
                    imageFloder.setFirstImagePath(path);
                }
                String[] file = parentFile.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
//                        LogUtil.d(filename + "dir路径");
                        return filename.endsWith(".jpg")
                                || filename.endsWith(".png")
                                || filename.endsWith(".jpeg");
                    }
                });
                if (file == null) {
                    mDirPaths.remove(dirPath);
                    continue;
                }
                int picSize = file.length;
//                int picSize = parentFile.list(new FilenameFilter() {
//                    @Override
//                    public boolean accept(File dir, String filename) {
//                        if (filename.endsWith(".jpg")
//                                || filename.endsWith(".png")
//                                || filename.endsWith(".jpeg"))
//                            return true;
//                        return false;
//                    }
//                }).length;
                totalCount += picSize;

                imageFloder.setCount(picSize);
                mImageFloders.add(imageFloder);

                if (picSize > mPicsSize) {
                    mPicsSize = picSize;
                    mImgDir = parentFile;
                }
            }
            mCursor.close();
            // 扫描完成，辅助的HashSet也就可以释放内存了
            mDirPaths = null;
            // 通知Handler扫描图片完成
//            getAllImagePath();
            mHandler.sendEmptyMessage(0x110);
        }
    }

    private class FileComparator implements Comparator<File> {

        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.lastModified() < rhs.lastModified()) {
                return 1;//最后修改的照片在前
            } else {
                return -1;
            }
        }

    }

}





