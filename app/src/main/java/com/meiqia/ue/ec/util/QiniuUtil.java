package com.meiqia.ue.ec.util;

import com.google.gson.Gson;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/24 上午12:05
 * 描述:七牛云存储工具类
 */
public class QiniuUtil {
    public static final String QN_PATH = "http://7xs7nf.com1.z0.glb.clouddn.com/";
    private static final String ACCESS_KEY = "khnCiCkgfTcVrROG3yjEoSM_NtHeUzrTglCukord";
    private static final String SECRET_KEY = "LYLDVa__DfA02By8Jl36wBZpQ7tQpjtCUcC51tjI";
    private static final String BUCKET_NAME = "mq-ue";

    private static final UploadManager sUploadManager = new UploadManager();
    private static final Auth sAuth = Auth.create(ACCESS_KEY, SECRET_KEY);

    private static String getUpToken() {
        return sAuth.uploadToken(BUCKET_NAME);
    }

    public static FileInfo uploadFile(String filePath, String key) throws Exception {
        Response res = sUploadManager.put(filePath, null, getUpToken());
        return new Gson().fromJson(res.bodyString(), FileInfo.class);
    }
}
