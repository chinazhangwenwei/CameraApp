package com.interjoy.camer2application.util;

import android.content.Context;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhangwenwei on 2017/6/9.
 */

public class FileUtil {
      public static final String PICTURE_PATH = "INTERJOY_PICS";

    /**
     * Create a File for saving an image or video
     */
    public static File getOutputMediaFile(Context context) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File[] files = ContextCompat.getExternalFilesDirs(context,
                Environment.DIRECTORY_PICTURES);
        String MyPictureDir = null;
        if (files.length > 0) {
            MyPictureDir = files[0].getAbsolutePath();
        } else {
            return null;
        }
        File mediaStorageDir = new File(
                MyPictureDir, PICTURE_PATH);
        return mediaStorageDir;
    }

    public static File getOutPicPath(File mediaStorageDir) {
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }
}
