package com.meiqia.ue.ec.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.meiqia.ue.ec.App;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/18 上午11:37
 * 描述:
 */
public class SPUtil {
    private static SharedPreferences mSharedPreferences;

    private SPUtil() {
    }

    private static SharedPreferences getPreferneces() {
        if (mSharedPreferences == null) {
            synchronized (SPUtil.class) {
                if (mSharedPreferences == null) {
                    mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
                }
            }
        }
        return mSharedPreferences;
    }

    public static void clear() {
        getPreferneces().edit().clear().apply();
    }

    public static void putString(String key, String value) {
        getPreferneces().edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        return getPreferneces().getString(key, "");
    }

    public static void putInt(String key, int value) {
        getPreferneces().edit().putInt(key, value).apply();
    }

    public static int getInt(String key) {
        return getPreferneces().getInt(key, 0);
    }

    public static void putBoolean(String key, Boolean value) {
        getPreferneces().edit().putBoolean(key, value).apply();
    }

    public static void putLong(String key, long value) {
        getPreferneces().edit().putLong(key, value).apply();
    }

    public static long getLong(String key) {
        return getPreferneces().getLong(key, 0);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return getPreferneces().getBoolean(key, defValue);
    }

    public static void remove(String key) {
        getPreferneces().edit().remove(key).apply();
    }

    public static boolean hasKey(String key) {
        return getPreferneces().contains(key);
    }


    public static String getCustomId() {
        return getString("custom_id");
    }

    public static void setCustomId(String customId) {
        putString("custom_id", customId);
    }

    public static String getNickname() {
        return getString("nickname");
    }

    public static void setNickname(String nickname) {
        putString("nickname", nickname);
    }

    public static String getTel() {
        return getString("tel");
    }

    public static void setTel(String tel) {
        putString("tel", tel);
    }
}