package com.meiqia.ue.ec;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.meiqia.core.MQManager;
import com.meiqia.core.callback.OnClientInfoCallback;
import com.meiqia.core.callback.OnInitCallback;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.meiqia.ue.ec.util.Constants;
import com.meiqia.ue.ec.util.SimpleActivityLifecycleCallbacks;
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
    private static final int BACKGROUND = 0;
    private static App sInstance;
    private int mActivityState = BACKGROUND;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        initMeiqiaSDK();
        initMiPush();
        listenActivityLifecycle();
    }

    private void initMeiqiaSDK() {
        initMQManager(null);
        MQConfig.backArrowIconResId = R.drawable.mq_ic_back_white;
        MQConfig.bgColorTitle = R.color.colorPrimary;
        MQConfig.textColorTitle = android.R.color.white;
        MQConfig.titleGravity = MQConfig.MQTitleGravity.LEFT;
    }

    private void initMQManager(OnInitCallback onInitCallback) {
        MQManager.init(this, "55b1b546b06656d9b930deeef07cdc1a", onInitCallback);
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

    private void listenActivityLifecycle() {
        registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {
            @Override
            public void onActivityStarted(Activity activity) {
                if (isAppBackground()) {
                    foregroundState();
                }
                mActivityState++;
            }

            @Override
            public void onActivityStopped(Activity activity) {
                mActivityState--;
                // 进入后台状态
                if (isAppBackground()) {
                    backgroundState();
                }
            }
        });
    }

    public static App getInstance() {
        return sInstance;
    }

    /**
     * App 是否进入了后台
     *
     * @return
     */
    private boolean isAppBackground() {
        return mActivityState == BACKGROUND;
    }

    /**
     * App 进入后台
     */
    private void backgroundState() {
        MQManager.getInstance(this).closeMeiqiaService();
    }

    /**
     * App 进入前台
     */
    private void foregroundState() {
        // 保证初始化成功才打开 socket
        initMQManager(new OnInitCallback() {
            @Override
            public void onSuccess(String s) {
                MQManager.getInstance(App.getInstance()).openMeiqiaService();
            }

            @Override
            public void onFailure(int i, String s) {
                Log.i(TAG, "初始化美洽SDK失败 " + s);
            }
        });
    }

    public void uploadPushId(String regId) {
        // 上传别名到服务器
        Map<String, String> values = new HashMap<>();
        String value = Constants.KEY_SDK_PUSH_PREF + regId;
        values.put(Constants.KEY_SDK_PUSH_ID, value);
        values.put(Constants.KEY_APP_NAME, "Meiqia_Android_ECommerce");

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
}
