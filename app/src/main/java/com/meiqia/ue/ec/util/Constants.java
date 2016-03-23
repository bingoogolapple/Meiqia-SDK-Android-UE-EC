package com.meiqia.ue.ec.util;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/7 下午3:47
 * 描述:
 */
public class Constants {
    // -------------------------- 推送 START --------------------------
    /**
     * 上传推送的push id
     */
    public static final String KEY_SDK_PUSH_ID = "sdk_push_id";
    /**
     * sdk pushid 前缀。目前app使用小米，其对应的前缀为「mi_」
     */
    public static final String KEY_SDK_PUSH_PREF = "mi_";
    public static final String KEY_APP_NAME = "appName";
    // -------------------------- 推送 END --------------------------

    // 售前客服 ID
    public static final String MQ_AGENT_ID_BEFORE = "990a7cbe603fe029e269b4c32f4fed09";
    // 售后客服 ID
    public static final String MQ_AGENT_ID_AFTER = "f12b03466611d678797c35fbfe27b7b2";

    // -------------------------- 七牛云存储相关 START --------------------------
    public static final String QN_ACCESS_KEY = "";
    public static final String QN_SECRET_KEY = "";
    public static final String QN_BUCKET_NAME = "";
    // -------------------------- 七牛云存储相关 END --------------------------
}
