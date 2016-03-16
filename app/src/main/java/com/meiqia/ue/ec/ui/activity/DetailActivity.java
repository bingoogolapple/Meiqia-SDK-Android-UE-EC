package com.meiqia.ue.ec.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;

import com.meiqia.core.MQMessageManager;
import com.meiqia.meiqiasdk.util.MQUtils;
import com.meiqia.ue.ec.R;
import com.meiqia.ue.ec.ui.widget.BadgeFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.badgeview.BGABadgeable;
import cn.bingoogolapple.badgeview.BGADragDismissDelegate;
import cn.bingoogolapple.bgabanner.BGABanner;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/15 下午10:55
 * 描述:
 */
public class DetailActivity extends ToolbarActivity implements EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_CODE_CONVERSATION_PERMISSIONS = 1;
    private BadgeFloatingActionButton mChatBfab;
    private int mMessageCount;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_detail);
        mChatBfab = getViewById(R.id.bfab_detail_chat);
        initBanner();
    }

    private void initBanner() {
        BGABanner banner = getViewById(R.id.banner);
        List<View> views = new ArrayList<>();
        views.add(getPageView(R.mipmap.one));
        views.add(getPageView(R.mipmap.two));
        views.add(getPageView(R.mipmap.three));
        views.add(getPageView(R.mipmap.four));
        views.add(getPageView(R.mipmap.five));
        banner.setViews(views);
    }

    private View getPageView(@DrawableRes int resid) {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(resid);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    @Override
    protected void setListener() {
        mChatBfab.setOnClickListener(this);
        mChatBfab.setDragDismissDelegage(new BGADragDismissDelegate() {
            @Override
            public void onDismiss(BGABadgeable badgeable) {
                clearMessage();
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("商品详情");
    }

    private void clearMessage() {
        mMessageCount = 0;
        mChatBfab.hiddenBadge();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bfab_detail_chat) {
            conversationWrapper();
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_CONVERSATION_PERMISSIONS)
    private void conversationWrapper() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 跳转到聊天界面前，先清空当前界面统计的未读消息数，取消监听新消息的广播
            clearMessage();
            unRegisterMessageReceiver();

            forward(ChatActivity.class);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.mq_runtime_permission_tip), REQUEST_CODE_CONVERSATION_PERMISSIONS, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        MQUtils.show(this, R.string.mq_permission_denied_tip);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerMessageReceiver();
    }

    @Override
    protected void onDestroy() {
        unRegisterMessageReceiver();
        super.onDestroy();
    }

    private void registerMessageReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(MQMessageManager.ACTION_NEW_MESSAGE_RECEIVED));
    }

    private void unRegisterMessageReceiver() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MQMessageManager.ACTION_NEW_MESSAGE_RECEIVED.equals(intent.getAction())) {
                mMessageCount++;
                mChatBfab.showTextBadge(mMessageCount > 99 ? "99+" : String.valueOf(mMessageCount));
            }
        }
    };
}