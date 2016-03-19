package com.meiqia.ue.ec;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.content.LocalBroadcastManager;

import com.meiqia.core.MQManager;
import com.meiqia.core.MQMessageManager;
import com.meiqia.core.callback.OnClientInfoCallback;
import com.meiqia.core.callback.OnInitCallback;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.meiqia.meiqiasdk.util.MQUtils;
import com.meiqia.ue.ec.engine.Engine;
import com.meiqia.ue.ec.event.UnreadChatMessageEvent;
import com.meiqia.ue.ec.ui.activity.ChatActivity;
import com.meiqia.ue.ec.util.Constants;
import com.meiqia.ue.ec.util.Logger;
import com.meiqia.ue.ec.util.RxBus;
import com.meiqia.ue.ec.util.SimpleActivityLifecycleCallbacks;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/7 下午3:21
 * 描述:
 */
public class App extends Application {
    private static final String TAG = App.class.getSimpleName();
    private static App sInstance;
    private long mLastPressBackKeyTime;
    private LinkedList<Activity> mActivities = new LinkedList<>();
    private RefWatcher mRefWatcher;
    private Engine mEngine;

    private int mActivityStartedCount = 0;

    private int mUnreadChatMessageCount;


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mRefWatcher = LeakCanary.install(this);


        initMeiqiaSDK();
        initMiPush();
        listenActivityLifecycle();

        initEngine();
    }

    private void initEngine() {
        mEngine = new Retrofit.Builder()
                .baseUrl("http://7xk9dj.com1.z0.glb.clouddn.com/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(Engine.class);
    }

    public Engine getEngine() {
        return mEngine;
    }

    private void initMeiqiaSDK() {
        initMQManager(new OnInitCallback() {
            @Override
            public void onSuccess(String s) {
            }

            @Override
            public void onFailure(int i, String s) {
            }
        });
        MQConfig.ui.backArrowIconResId = R.drawable.mq_ic_back_white;
        MQConfig.ui.titleGravity = MQConfig.ui.MQTitleGravity.LEFT;
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
                Logger.d(TAG, "setTag " + tag);
            }

            @Override
            public void log(String content, Throwable t) {
                Logger.d(TAG, content + " " + t.getMessage());
            }

            @Override
            public void log(String content) {
                Logger.d(TAG, content);
            }
        };
        com.xiaomi.mipush.sdk.Logger.setLogger(this, newLogger);
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
                if (mActivityStartedCount == 0) {
                    Logger.i(TAG, "进入前台状态");
                    openMeiqiaService();
                }
                mActivityStartedCount++;

                // 处理离线消息广播接收者的注册与取消注册
                if (activity instanceof ChatActivity) {
                    Logger.i(TAG, "聊天界面可见，取消聊天消息广播接收者，清空未读消息");
                    LocalBroadcastManager.getInstance(sInstance).unregisterReceiver(mChatMessageReceiver);
                    clearUnreadChatMessageCount();
                } else {
                    Logger.i(TAG, "其他界面可见，注册聊天消息广播接收者");
                    LocalBroadcastManager.getInstance(sInstance).registerReceiver(mChatMessageReceiver, new IntentFilter(MQMessageManager.ACTION_NEW_MESSAGE_RECEIVED));
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {
                mActivityStartedCount--;
                if (mActivityStartedCount == 0) {
                    Logger.i(TAG, "进入后台状态");
                    closeMeiqiaService();
                }
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                mActivities.add(activity);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                mActivities.remove(activity);
            }
        });
    }

    public static App getInstance() {
        return sInstance;
    }

    public static RefWatcher getRefWatcher() {
        return getInstance().mRefWatcher;
    }

    /**
     * 双击后完全退出应用程序
     */
    public void exitWithDoubleClick() {
        if (System.currentTimeMillis() - mLastPressBackKeyTime <= 1500) {
            exit();
        } else {
            mLastPressBackKeyTime = System.currentTimeMillis();
            MQUtils.show(this, R.string.toast_exit_tip);
        }
    }

    /**
     * 退出应用程序
     */
    public void exit() {
        Activity activity;
        while (mActivities.size() != 0) {
            activity = mActivities.poll();
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        System.gc();
    }

    private void closeMeiqiaService() {
        MQManager.getInstance(this).closeMeiqiaService();
    }

    private void openMeiqiaService() {
        // 保证初始化成功才打开 socket
        initMQManager(new OnInitCallback() {
            @Override
            public void onSuccess(String s) {
                MQManager.getInstance(App.getInstance()).openMeiqiaService();
            }

            @Override
            public void onFailure(int i, String s) {
                Logger.i(TAG, "初始化美洽SDK失败 " + s);
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
                Logger.d(TAG, "upload push id failed...");
            }

            @Override
            public void onSuccess() {
                Logger.d(TAG, "upload push id success...");
            }
        });
    }

    public void clearUnreadChatMessageCount() {
        mUnreadChatMessageCount = 0;
        RxBus.send(new UnreadChatMessageEvent());
    }

    public int getUnreadChatMessageCount() {
        return mUnreadChatMessageCount;
    }

    private BroadcastReceiver mChatMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MQMessageManager.ACTION_NEW_MESSAGE_RECEIVED.equals(intent.getAction())) {
                mUnreadChatMessageCount++;

                Logger.i(TAG, "收到新消息 " + mUnreadChatMessageCount);

                RxBus.send(new UnreadChatMessageEvent());
            }
        }
    };
}
