package com.meiqia.ue.ec.ui.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.meiqia.core.MQManager;
import com.meiqia.core.bean.MQAgent;
import com.meiqia.meiqiasdk.util.MQUtils;
import com.meiqia.ue.ec.R;
import com.meiqia.ue.ec.event.UnreadChatMessageEvent;
import com.meiqia.ue.ec.ui.fragment.GoodsFragment;
import com.meiqia.ue.ec.ui.fragment.ProfileFragment;
import com.meiqia.ue.ec.ui.widget.BadgeFloatingActionButton;
import com.meiqia.ue.ec.util.Constants;
import com.meiqia.ue.ec.util.RxBus;
import com.trello.rxlifecycle.ActivityEvent;

import java.util.List;

import cn.bingoogolapple.badgeview.BGABadgeable;
import cn.bingoogolapple.badgeview.BGADragDismissDelegate;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/15 下午10:47
 * 描述:
 */
public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_CODE_CONVERSATION_PERMISSIONS = 1;

    public static boolean sIsCreated = false;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private BadgeFloatingActionButton mChatBfab;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mChatBfab = getViewById(R.id.bfab_main_chat);
        mToolbar = getViewById(R.id.toolbar);
        mTabLayout = getViewById(R.id.tabLayout);
        mViewPager = getViewById(R.id.viewPager);
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
        initToolbar();
        setUpTabLayoutAndViewPager();

        sIsCreated = true;

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

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        setTitle(R.string.app_name);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
    }

    private void setUpTabLayoutAndViewPager() {
        ContentPagerAdapter contentPagerAdapter = new ContentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(contentPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setTabsFromPagerAdapter(contentPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bfab_main_chat) {
            conversationWrapper();
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_CONVERSATION_PERMISSIONS)
    private void conversationWrapper() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 如果当前未读消息条数为0，则请求分配售前客服。如果当前未读消息条数不为0，则分配默认的客服
            String agentId = Constants.MQ_AGENT_ID_BEFORE;

            if (mApp.getUnreadChatMessageCount() != 0) {
                MQAgent agent = MQManager.getInstance(mApp).getCurrentAgent();
                if (agent != null) {
                    agentId = agent.getId();
                } else {
                    agentId = "";
                }
            }

            forward(ChatActivity.newIntent(mApp, agentId));
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
    public void onBackPressed() {
        mApp.getAppManager().exitWithDoubleClick();
    }

    @Override
    protected void onDestroy() {
        sIsCreated = false;
        super.onDestroy();
    }

    private class ContentPagerAdapter extends FragmentStatePagerAdapter {

        public ContentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return GoodsFragment.newInstance(false);
            } else if (position == 1) {
                return GoodsFragment.newInstance(true);
            }
            return new ProfileFragment();
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getString(R.string.goods_list);
            } else if (position == 1) {
                return getString(R.string.after_sale_goods);
            } else {
                return getString(R.string.profile);
            }
        }
    }
}
