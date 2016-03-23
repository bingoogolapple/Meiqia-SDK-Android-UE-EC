package com.meiqia.ue.ec.util;

import android.os.Environment;

import com.meiqia.ue.ec.R;

import java.io.File;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/24 上午1:49
 * 描述:
 */
public class StorageUtil {
    public static final String DIR_ROOT = "美洽x电商";
    public static final String DIR_IMAGE = DIR_ROOT + File.separator + "images";

    private StorageUtil() {
    }

    /**
     * 判断外存储是否可写
     *
     * @return
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * 获取当前app图片存储路径
     *
     * @return
     */
    public static File getImageDir() {
        File imageDir = null;
        if (isExternalStorageWritable()) {
            imageDir = new File(Environment.getExternalStorageDirectory(), DIR_IMAGE);
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }
        } else {
            ToastUtil.showSafe(R.string.mq_no_sdcard);
        }
        return imageDir;
    }
}