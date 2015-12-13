package com.hideactive.config;

import com.hideactive.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * Created by Senierr on 2015/12/13.
 */
public class ImageLoaderOptions {
    public static DisplayImageOptions getOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.image_default) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.image_default) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.image_fail) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .build(); // 构建完成
        return options;
    }
}
