package com.interjoy.camer2application.dao;

import android.provider.BaseColumns;

public final class DbContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DbContract() {
    }

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "ImageInfo";
        public static final String COLUNMN_IMAGEPATH = "ImagePath";
        public static final String COLUNMN_IMAGEABLE = "ImageLabel";
        public static final String COLUNMN_TIME = "TakeTime";
    }
}
