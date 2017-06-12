package com.interjoy.camer2application.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhangwenwei on 2017/6/12.
 */

public class ImageInfo implements Parcelable {
    private String imagePath = "";
    private String imageLabel = "";
    private long takeTime;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageLabel() {
        return imageLabel;
    }

    public void setImageLabel(String imageLabel) {
        this.imageLabel = imageLabel;
    }

    public long getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(long takeTime) {
        this.takeTime = takeTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imagePath);
        dest.writeString(this.imageLabel);
        dest.writeLong(this.takeTime);
    }

    public ImageInfo() {
    }


    protected ImageInfo(Parcel in) {
        this.imagePath = in.readString();
        this.imageLabel = in.readString();
        this.takeTime = in.readLong();
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
