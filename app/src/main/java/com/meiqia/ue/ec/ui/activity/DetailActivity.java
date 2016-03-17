package com.meiqia.ue.ec.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;

import com.meiqia.core.MQManager;
import com.meiqia.core.MQScheduleRule;
import com.meiqia.meiqiasdk.util.MQUtils;
import com.meiqia.ue.ec.R;
import com.meiqia.ue.ec.event.UnreadChatMessageEvent;
import com.meiqia.ue.ec.ui.widget.BadgeFloatingActionButton;
import com.meiqia.ue.ec.util.RxBus;
import com.trello.rxlifecycle.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.badgeview.BGABadgeable;
import cn.bingoogolapple.badgeview.BGADragDismissDelegate;
import cn.bingoogolapple.bgabanner.BGABanner;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/15 下午10:55
 * 描述:
 */
public class DetailActivity extends ToolbarActivity implements EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_CODE_CONVERSATION_PERMISSIONS = 1;
    private static final String EXTRA_MQ_AGENT_ID = "EXTRA_MQ_AGENT_ID";
    private BadgeFloatingActionButton mChatBfab;

    public static Intent newIntent(Context context, String mqAgentId) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_MQ_AGENT_ID, mqAgentId);
        return intent;
    }

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
                mApp.clearUnreadChatMessageCount();
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("商品详情");

        observeUnreadChatMessage();
    }

    private void observeUnreadChatMessage() {
        renderChatBfab();
        RxBus.toObserverable()
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<Object, Boolean>() {
                    @Override
                    public Boolean call(Object object) {
                        return object instanceof UnreadChatMessageEvent;
                    }
                })
                .cast(UnreadChatMessageEvent.class)
                .subscribe(new Action1<UnreadChatMessageEvent>() {
                    @Override
                    public void call(UnreadChatMessageEvent unreadChatMessageEvent) {
                        renderChatBfab();
                    }
                });
    }

    private void renderChatBfab() {
        if (mApp.getUnreadChatMessageCount() == 0) {
            mChatBfab.hiddenBadge();
        } else {
            mChatBfab.showTextBadge(String.valueOf(mApp.getUnreadChatMessageCount()));
        }
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
            // 指定美洽客服id
            MQManager.getInstance(mApp).setScheduledAgentOrGroupWithId(getIntent().getStringExtra(EXTRA_MQ_AGENT_ID), "", MQScheduleRule.REDIRECT_GROUP);

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
}