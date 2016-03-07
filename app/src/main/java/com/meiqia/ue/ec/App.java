package com.meiqia.ue.ec;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.meiqia.core.MQManager;
import com.meiqia.core.callback.OnClientInfoCallback;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.meiqia.ue.ec.util.Constants;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/7 下午3:21
 * 描述:
 */
public class App extends Application {
    private static final String TAG = App.class.getSimpleName();
    private static App sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        initMeiqiaSDK();
        initMiPush();
    }

    private void initMeiqiaSDK() {
        MQManager.init(this, "a71c257c80dfe883d92a64dca323ec20", null);
        MQConfig.backArrowIconResId = R.drawable.mq_ic_back_white;
        MQConfig.bgColorTitle = R.color.colorPrimary;
        MQConfig.textColorTitle = android.R.color.white;
        MQConfig.titleGravity = MQConfig.MQTitleGravity.LEFT;
    }

    private void initMiPush() {
        if (shouldInitMiPush()) {
            MiPushClient.registerPush(this, "2882303761517446019", "5731744630019");
        }
        //打开Log
        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
                // ignore
                Log.d(TAG, "setTag " + tag);
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);
    }

    private boolean shouldInitMiPush() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public void uploadPushId(String regId) {
        // 上传别名到服务器
        Map<String, String> values = new HashMap<>();
        String value = Constants.KEY_SDK_PUSH_PREF + regId;
        values.put(Constants.KEY_SDK_PUSH_ID, value);

        MQManager.getInstance(App.getInstance()).setClientInfo(values, new OnClientInfoCallback() {
            @Override
            public void onFailure(int i, String s) {
                Log.d(TAG, "upload push id failed...");
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "upload push id success...");
            }
        });
    }

    public static App getInstance() {
        return sInstance;
    }
}
