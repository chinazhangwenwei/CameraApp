package com.interjoy.camer2application.dao;

import com.interjoy.camer2application.bean.ImageInfo;

import java.util.List;

/**
 * Created by zhangwenwei on 2017/6/12.
 */

public interface DbIControl {
    void add(ImageInfo imageInfo);

    void delete(ImageInfo imageInfo);

    void update(ImageInfo imageInfo);

    List<ImageInfo> getLastNumbers(int number);

    List<ImageInfo> getAllNumbers();

    void closeDb();
}
