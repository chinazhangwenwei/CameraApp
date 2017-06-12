package com.interjoy.camer2application.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.interjoy.camer2application.bean.ImageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwenwei on 2017/6/12.
 */

public class DbManager implements DbIControl {
    private DbHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private static DbIControl dbIControl;

    private DbManager(Context context) {
        if (null == dbHelper) {
            dbHelper = new DbHelper(context);
            sqLiteDatabase = dbHelper.getWritableDatabase();
        }
    }

    public static DbIControl getDbControl(Context context) {
        if (dbIControl == null) {
            dbIControl = new DbManager(context);
        }
        return dbIControl;
    }


    @Override
    public void closeDb() {
        sqLiteDatabase.close();
    }

    @Override
    public void add(ImageInfo imageInfo) {
        ContentValues values = new ContentValues();
        values.put(DbContract.FeedEntry.COLUNMN_IMAGEABLE, imageInfo.getImageLabel());
        values.put(DbContract.FeedEntry.COLUNMN_IMAGEPATH, imageInfo.getImagePath());
        values.put(DbContract.FeedEntry.COLUNMN_TIME, imageInfo.getTakeTime());
        sqLiteDatabase.insert(DbContract.FeedEntry.TABLE_NAME, null, values);
    }

    @Override
    public void delete(ImageInfo imageInfo) {
        sqLiteDatabase.execSQL("delete from" + DbContract.FeedEntry.COLUNMN_IMAGEABLE + "where ImagePath=?",
                new Object[]{imageInfo.getImagePath()});

    }

    @Override
    public void update(ImageInfo imageInfo) {
        sqLiteDatabase.execSQL("update from person where ImagePath=?",
                new Object[]{imageInfo.getImagePath()});
    }

    @Override
    public List<ImageInfo> getLastNumbers(int number) {

        List<ImageInfo> imageInfos = new ArrayList<>();
        String sql = "select * from " + DbContract.FeedEntry.TABLE_NAME + "order by id desc limit 0," + number;
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String label = cursor.getString(cursor.getColumnIndex(DbContract.FeedEntry.COLUNMN_IMAGEABLE));
            String path = cursor.getString(cursor.getColumnIndex(DbContract.FeedEntry.COLUNMN_IMAGEPATH));
            long takeTime = cursor.getLong(cursor.getColumnIndex(DbContract.FeedEntry.COLUNMN_IMAGEPATH));
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setImageLabel(label);
            imageInfo.setImagePath(path);
            imageInfo.setTakeTime(takeTime);
            imageInfos.add(imageInfo);
        }
        cursor.close();
        return imageInfos;

    }

    @Override
    public List<ImageInfo> getAllNumbers() {
        List<ImageInfo> imageInfos = new ArrayList<>();
        String sql = "select * from " + DbContract.FeedEntry.TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String label = cursor.getString(cursor.getColumnIndex(DbContract.FeedEntry.COLUNMN_IMAGEABLE));
            String path = cursor.getString(cursor.getColumnIndex(DbContract.FeedEntry.COLUNMN_IMAGEPATH));
            long takeTime = cursor.getLong(cursor.getColumnIndex(DbContract.FeedEntry.COLUNMN_IMAGEPATH));
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setImageLabel(label);
            imageInfo.setImagePath(path);
            imageInfo.setTakeTime(takeTime);
            imageInfos.add(imageInfo);
        }
        cursor.close();
        return imageInfos;
    }
}
