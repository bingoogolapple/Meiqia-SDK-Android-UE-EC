package com.meiqia.ue.ec.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.meiqia.ue.ec.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/15 下午10:47
 * 描述:
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {

    }

    public void preSale(View v) {
        forward(DetailActivity.class);
    }

    public void afterSale(View v) {
        forward(DetailActivity.class);
    }
}
