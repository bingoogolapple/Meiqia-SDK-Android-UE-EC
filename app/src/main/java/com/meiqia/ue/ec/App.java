package com.meiqia.ue.ec;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Process;
import android.support.v4.content.LocalBroadcastManager;

import com.meiqia.core.MQManager;
import com.meiqia.core.MQMessageManager;
import com.meiqia.core.callback.OnClientInfoCallback;
import com.meiqia.core.callback.OnInitCallback;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.meiqia.ue.ec.engine.Engine;
import com.meiqia.ue.ec.event.UnreadChatMessageEvent;
import com.meiqia.ue.ec.ui.activity.ChatActivity;
import com.meiqia.ue.ec.util.AppManager;
import com.meiqia.ue.ec.util.Constants;
import com.meiqia.ue.ec.util.Logger;
import com.meiqia.ue.ec.util.RxBus;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.HashMap;
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
    private RefWatcher mRefWatcher;
    private Engine mEngine;

    private int mUnreadChatMessageCount;

    private AppManager mAppManager;


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mRefWatcher = LeakCanary.install(this);


        initMeiqiaSDK();
        initMiPush();
        initAppManager();

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

        MQManager.getInstance(this).setDebugMode(true);
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

    private void initAppManager() {
        mAppManager = new AppManager(new AppManager.Delegate() {
            @Override
            public void onEnterFrontStage() {
                Logger.i(TAG, "进入前台状态，开启美洽服务(关闭推送)");
                openMeiqiaService();
            }

            @Override
            public void onEnterBackStage() {
                Logger.i(TAG, "进入后台状态，关闭美洽服务(开启推送)");
                closeMeiqiaService();
            }
        }) {
            @Override
            public void onActivityStarted(Activity activity) {
                super.onActivityStarted(activity);

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
        };
        registerActivityLifecycleCallbacks(mAppManager);
    }

    public static App getInstance() {
        return sInstance;
    }

    public RefWatcher getRefWatcher() {
        return mRefWatcher;
    }

    public AppManager getAppManager() {
        return mAppManager;
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

//                MQManager.getInstance(context).getUnreadMessages(new OnGetMessageListCallback() {
//                    @Override
//                    public void onSuccess(List<MQMessage> messageList) {
//                        mUnreadChatMessageCount = messageList.size();
//
//                        Logger.i(TAG, "收到新消息 " + mUnreadChatMessageCount);
//
//                        RxBus.send(new UnreadChatMessageEvent());
//                    }
//
//                    @Override
//                    public void onFailure(int code, String message) {
//                        Logger.d(TAG, "获取未读消息失败 " + message);
//                    }
//                });

                mUnreadChatMessageCount++;

                Logger.i(TAG, "收到新消息 " + mUnreadChatMessageCount);

                RxBus.send(new UnreadChatMessageEvent());
            }
        }
    };
}
