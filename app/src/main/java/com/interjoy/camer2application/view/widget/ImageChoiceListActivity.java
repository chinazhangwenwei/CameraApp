package com.interjoy.camer2application.view.widget;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wiseweb.watermelon.R;
import com.wiseweb.watermelon.base.adpter.BaseListViewAdapter;
import com.wiseweb.watermelon.base.adpter.ViewHolder;
import com.wiseweb.watermelon.base.beans.ImageFloder;
import com.wiseweb.watermelon.base.view.activity.MutilImageChoiceActivity;
import com.wiseweb.watermelon.share.view.activity.PublishActivity;
import com.wiseweb.watermelon.utils.ImageUtil;
import com.wiseweb.watermelon.utils.ToastUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Lenovo on 2016/4/18.
 */
public class ImageChoiceListActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.lv_content)
    ListView lvContent;
    public static List<ImageFloder> mImageFloders = new ArrayList<>();
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    private HashSet<String> mDirPaths = new HashSet<>();
    private BaseListViewAdapter<ImageFloder> listViewAdapter;
    private static final int REQUEST_CODE = 1;
    private int mPicsSize;
    private File mImgDir;
    private File mCacheFile;

    public static List<String> savePath = new ArrayList<>(9);

    ProgressDialog mProgressDialog;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x110:
                    mProgressDialog.dismiss();
                    initListView();
                    break;
                case 0x111:
                    mProgressDialog.dismiss();
                    setResult(1);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_image_dir);
        mCacheFile = new File(getExternalCacheDir() + "/imageCache");
        if (!mCacheFile.exists()) {
            mCacheFile.mkdirs();
        }
        ButterKnife.bind(this);
        getImages();
        initListener();
    }

    @Override
    protected void onDestroy() {
//        ButterKnife.unbind(this);
        savePath.clear();
        mImageFloders.clear();
//        bitmapList.clear();
        super.onDestroy();
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
        mProgressDialog.setCanceledOnTouchOutside(true);
        ExecutorService executors = Executors.newCachedThreadPool();
        executors.submit(new ScanImageTask());


    }

    private void initListView() {
        if (null == listViewAdapter) {
            listViewAdapter = new BaseListViewAdapter<ImageFloder>(ImageChoiceListActivity.this, mImageFloders,
                    R.layout.item_list_dir_image) {
                @Override
                public void convert(ViewHolder helper, ImageFloder item) {
                    ImageView imageView = helper.getView(R.id.iv_content);
                    TextView tvSelect = helper.getView(R.id.tv_select);
                    tvSelect.setVisibility(View.INVISIBLE);
                    if (item.getSelectCount() > 0) {
                        tvSelect.setVisibility(View.VISIBLE);
                        tvSelect.setText("已选取" + item.getSelectCount() + "张");
                    }
                    Glide.with(ImageChoiceListActivity.this).load(item.getFirstImagePath()).into(imageView);
//                    helper.setImageByGlide(R.id.iv_content, item.getFirstImagePath(), ImageChoiceListActivity.this);
                    helper.setText(R.id.tv_name, item.getName());
                    helper.setText(R.id.tv_count, item.getCount() + "个");
                }
            };
            lvContent.setAdapter(listViewAdapter);
        } else {
            listViewAdapter.notifyDataSetChanged();
        }

    }

    private void initListener() {
        lvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ImageChoiceListActivity.this, MutilImageChoiceActivity.class);
                intent.putExtra("position", position);
                startActivityForResult(intent, REQUEST_CODE);
//                setResult(REQUEST_CODE);

            }
        });
        tvCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
    }

    class ScanImageTask implements Runnable {
        @Override
        public void run() {
            String firstImage = null;
            Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver mContentResolver = ImageChoiceListActivity.this
                    .getContentResolver();
            // 只查询jpeg和png的图片
            Cursor mCursor = mContentResolver.query(mImageUri, null,
                    MediaStore.Images.Media.MIME_TYPE + "=? or "
                            + MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg", "image/png"},
                    MediaStore.Images.Media.DATE_MODIFIED);
            while (mCursor.moveToNext()) {
                // 获取图片的路径
                String path = mCursor.getString(mCursor
                        .getColumnIndex(MediaStore.Images.Media.DATA));
//                LogUtil.d(path + "路径");
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
//                    LogUtil.d(path + "路径");
                    imageFloder = new ImageFloder();
                    imageFloder.setDir(dirPath);
                    //获取第一张图片
                    File[] files = parentFile.listFiles();
                    if (files != null && files.length > 0) {
                        for (int i = (files.length - 1); i >= 0; i--) {
                            File tempFile = files[i];
                            firstImage = tempFile.getAbsolutePath();

                            if (firstImage.contains(".jpg") ||
                                    firstImage.contains(".png") ||
                                    firstImage.contains(".jpeg")) {

                                break;
                            }
                        }
                    }
                    imageFloder.setFirstImagePath(firstImage);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE:
                if (data != null) {
                    int position = data.getIntExtra("position", -1);
                    if (position != -1) {
                        int count = data.getIntExtra("count", -1);
                        if (count > 0) {
                            if (count != mImageFloders.get(position).getSelectCount()) {
                                mImageFloders.get(position).setSelectCount(count);
                                listViewAdapter.notifyDataSetChanged();
                            }


                        }

                    }
                }
                break;
        }
    }

    private Bitmap tempImage;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                closeSelf();
                break;
            case R.id.tv_confirm:
                if (savePath.size() == 0) {
                    ToastUtil.showShort("请您选择相册");
                    return;
                }
                ExecutorService executors = Executors.newCachedThreadPool();
                mProgressDialog = ProgressDialog.show(this, null, "正在处理图片");
                executors.submit(new Runnable() {
                    @Override
                    public void run() {
                        for (String path : savePath) {
                            tempImage = ImageUtil.getSmallBitmap(path);
                            File fileImage = new File(mCacheFile, System.currentTimeMillis() + "_temp.jpg");
                            if (tempImage != null) {
                                ImageUtil.saveImage(fileImage, tempImage);
                                PublishActivity.savePath.add(PublishActivity.savePath.size() - 1, fileImage.getAbsolutePath());
//                                PublishActivity.bitmapList.add(PublishActivity.bitmapList.size() - 1, image);
                            }
                            tempImage.recycle();
                            tempImage = null;
                        }

                        mHandler.sendEmptyMessage(0x111);
                    }
                });

                break;
        }
    }

    private void closeSelf() {
        setResult(-1);
        finish();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        mProgressDialog.dismiss();
        closeSelf();
    }
}
