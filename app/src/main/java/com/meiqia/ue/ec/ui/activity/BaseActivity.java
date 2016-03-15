package com.meiqia.ue.ec.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.view.MenuItem;
import android.view.View;

import com.meiqia.meiqiasdk.util.MQUtils;
import com.meiqia.ue.ec.App;
import com.meiqia.ue.ec.R;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/15 下午10:15
 * 描述:
 */
public abstract class BaseActivity extends RxAppCompatActivity implements View.OnClickListener {
    protected String TAG;
    protected App mApp;
    protected SweetAlertDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        mApp = App.getInstance();

        initView(savedInstanceState);
        setListener();
        processLogic(savedInstanceState);
    }

    /**
     * 设置点击事件
     *
     * @param id 控件的id
     */
    protected void setOnClickListener(@IdRes int id) {
        getViewById(id).setOnClickListener(this);
    }

    /**
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected <VT extends View> VT getViewById(@IdRes int id) {
        return (VT) findViewById(id);
    }

    /**
     * 初始化布局以及View控件
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 给View控件添加事件监听器
     */
    protected abstract void setListener();

    /**
     * 处理业务逻辑，状态恢复等操作
     *
     * @param savedInstanceState
     */
    protected abstract void processLogic(Bundle savedInstanceState);

    /**
     * 需要处理点击事件时，重写该方法
     *
     * @param v
     */
    public void onClick(View v) {
    }

    @Override
    public void onBackPressed() {
        backward();
    }

    /**
     * 跳转到下一个Activity，并且销毁当前Activity
     *
     * @param cls 下一个Activity的Class
     */
    public void forwardAndFinish(Class<?> cls) {
        forward(cls);
        finish();
    }

    /**
     * 跳转到下一个Activity，不销毁当前Activity
     *
     * @param cls 下一个Activity的Class
     */
    public void forward(Class<?> cls) {
        MQUtils.closeKeyboard(this);
        startActivity(new Intent(this, cls));
        executeForwardAnim();
    }

    public void forward(Class<?> cls, int requestCode) {
        forward(new Intent(this, cls), requestCode);
    }

    public void forwardAndFinish(Intent intent) {
        forward(intent);
        finish();
    }

    public void forward(Intent intent) {
        MQUtils.closeKeyboard(this);
        startActivity(intent);
        executeForwardAnim();
    }

    public void forward(Intent intent, int requestCode) {
        MQUtils.closeKeyboard(this);
        startActivityForResult(intent, requestCode);
        executeForwardAnim();
    }

    /**
     * 执行跳转到下一个Activity的动画
     */
    public void executeForwardAnim() {
        overridePendingTransition(R.anim.activity_forward_enter, R.anim.activity_forward_exit);
    }

    /**
     * 回到上一个Activity，并销毁当前Activity
     */
    public void backward() {
        MQUtils.closeKeyboard(this);
        finish();
        executeBackwardAnim();
    }

    /**
     * 回到上一个Activity，并销毁当前Activity（应用场景：欢迎、登录、注册这三个界面）
     *
     * @param cls 上一个Activity的Class
     */
    public void backwardAndFinish(Class<?> cls) {
        MQUtils.closeKeyboard(this);
        startActivity(new Intent(this, cls));
        executeBackwardAnim();
        finish();
    }

    /**
     * 执行回到到上一个Activity的动画
     */
    public void executeBackwardAnim() {
        overridePendingTransition(R.anim.activity_backward_enter, R.anim.activity_backward_exit);
    }

    /**
     * 显示加载对话框
     *
     * @param resId
     */
    public void showLoadingDialog(@StringRes int resId) {
        showLoadingDialog(getString(resId));
    }

    public void showLoadingDialog(String msg) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            mLoadingDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
            mLoadingDialog.setCancelable(false);
        }
        mLoadingDialog.setTitleText(msg);
        mLoadingDialog.show();
    }

    /**
     * 隐藏加载对话框
     */
    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}