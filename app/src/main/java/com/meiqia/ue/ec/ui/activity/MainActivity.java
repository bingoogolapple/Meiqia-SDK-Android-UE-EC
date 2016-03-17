package com.meiqia.ue.ec.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.meiqia.meiqiasdk.util.MQUtils;
import com.meiqia.ue.ec.R;
import com.meiqia.ue.ec.ui.fragment.AfterFragment;
import com.meiqia.ue.ec.ui.fragment.BeforeFragment;
import com.meiqia.ue.ec.ui.widget.BadgeFloatingActionButton;

import cn.bingoogolapple.badgeview.BGABadgeable;
import cn.bingoogolapple.badgeview.BGADragDismissDelegate;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/15 下午10:47
 * 描述:
 */
public class MainActivity extends BaseActivity {
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
                MQUtils.show(mApp, "消失");
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        initToolbar();
        setUpTabLayoutAndViewPager();


        mChatBfab.showTextBadge("3");

        sIsCreated = true;
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
            forward(ChatActivity.class);
        }
    }

    @Override
    public void onBackPressed() {
        mApp.exitWithDoubleClick();
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
                return new BeforeFragment();
            }
            return new AfterFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return position == 0 ? "商品列表" : "已购商品";
        }
    }
}
