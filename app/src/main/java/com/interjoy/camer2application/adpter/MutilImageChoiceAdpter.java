package com.interjoy.camer2application.adpter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wiseweb.watermelon.R;
import com.wiseweb.watermelon.base.view.activity.MutilImageChoiceActivity;
import com.wiseweb.watermelon.base.view.widget.ImageChoiceListActivity;
import com.wiseweb.watermelon.share.view.activity.PublishActivity;
import com.wiseweb.watermelon.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2016/4/15.
 */
public class MutilImageChoiceAdpter extends BaseListViewAdapter<String> {
    public List<String> getmSelectedImage() {
        return mSelectedImage;
    }

    private List<String> firstSelectImage = new ArrayList<>();

    public List<String> getFirstSelectImage() {
        return firstSelectImage;
    }

    public void setFirstSelectImage(List<String> firstSelectImage) {
        this.firstSelectImage = firstSelectImage;
    }

    public int count = 0;

    /**
     * 用户选择的图片，存储为图片的完整路径
     */
    private List<String> mSelectedImage = new ArrayList<>();


    private Context context;

//    /**
//     * 文件夹路径
//     */
//    private String mDirPath;

    public MutilImageChoiceAdpter(Context context, List<String> mDatas, int itemLayoutId
    ) {
        super(context, mDatas, itemLayoutId);
//        this.mDirPath = dirPath;
        this.context = context;

    }

    @Override
    public void convert(ViewHolder helper, final String item) {
        final ImageView mImageView = helper.getView(R.id.id_item_image);
        final ImageView mSelect = helper.getView(R.id.id_item_select);
//设置no_pic
        mImageView.setImageResource(R.mipmap.ic_launcher);
        Glide.with(context).load(item).into(mImageView);
        //设置no_selected
        if (ImageChoiceListActivity.savePath.contains(item)) {
            mSelect.setImageResource(R.mipmap.pictures_selected);
            mImageView.setColorFilter(Color.parseColor("#77000000"));

        } else {
            mSelect.setImageResource(
                    R.mipmap.picture_unselected);
        }
        //设置图片
//        helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);


        mImageView.setColorFilter(null);
        //设置ImageView的点击事件
        mImageView.setOnClickListener(new View.OnClickListener() {
            //选择，则将图片变暗，反之则反之
            @Override
            public void onClick(View v) {


                // 已经选择过该图片
                if (ImageChoiceListActivity.savePath.contains(item)) {
                    ImageChoiceListActivity.savePath.remove(item);
                    mSelectedImage.add(item);
                    if (firstSelectImage.contains(item)) {
                        firstSelectImage.remove(item);
                    }
                    mSelect.setImageResource(R.mipmap.picture_unselected);
                    mImageView.setColorFilter(null);
                    count--;
                } else
                // 未选择该图片
                {
                    if (PublishActivity.savePath.size() +
                            ImageChoiceListActivity.savePath.size() == MutilImageChoiceActivity.IMAGE_MAX) {
                        ToastUtil.showShort("选取图片总数最多为9张");
                        return;
                    }
                    firstSelectImage.add(item);
                    if (mSelectedImage.contains(item)) {
                        mSelectedImage.remove(item);
                    }
                    ImageChoiceListActivity.savePath.add(item);
                    count++;
                    mSelect.setImageResource(R.mipmap.pictures_selected);
                    mImageView.setColorFilter(Color.parseColor("#77000000"));
                }

            }
        });

//        /**
//         * 已经选择过的图片，显示出选择过的效果
//         */
//        if (mSelectedImage.contains(item)) {
//            mSelect.setImageResource(R.mipmap.pictures_selected);
//            mImageView.setColorFilter(Color.parseColor("#77000000"));
//        }


    }


}
