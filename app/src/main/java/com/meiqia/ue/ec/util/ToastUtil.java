package com.meiqia.ue.ec.util;

import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Toast;

import com.meiqia.ue.ec.App;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/24 上午1:52
 * 描述:
 */
public class ToastUtil {

    private ToastUtil() {
    }

    public static void show(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            if (text.length() < 10) {
                Toast.makeText(App.getInstance(), text, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(App.getInstance(), text, Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void show(@StringRes int resId) {
        show(App.getInstance().getResources().getString(resId));
    }

    public static void showSafe(final CharSequence text) {
        ThreadUtil.runInUIThread(new Runnable() {
            @Override
            public void run() {
                show(text);
            }
        });
    }

    public static void showSafe(@StringRes int resId) {
        showSafe(App.getInstance().getResources().getString(resId));
    }
}