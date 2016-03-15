package com.meiqia.ue.ec.ui.activity;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.Toolbar;
import android.view.ViewStub;

import com.meiqia.ue.ec.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/15 下午11:32
 * 描述:
 */
public abstract class ToolbarActivity extends BaseActivity {
    protected Toolbar mToolbar;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.toolbar_viewstub);
        mToolbar = getViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewStub viewStub = getViewById(R.id.viewStub);
        viewStub.setLayoutResource(layoutResID);
        viewStub.inflate();
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }
}