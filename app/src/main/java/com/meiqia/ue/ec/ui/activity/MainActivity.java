package com.meiqia.ue.ec.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.meiqia.core.MQManager;
import com.meiqia.meiqiasdk.activity.MQConversationActivity;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.meiqia.meiqiasdk.util.MQUtils;
import com.meiqia.ue.ec.R;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_CODE_CONVERSATION_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMeiqiaSDK();

        initCube();
    }

    private void initMeiqiaSDK() {
        MQManager.init(this, "a71c257c80dfe883d92a64dca323ec20", null);
        MQConfig.backArrowIconResId = R.drawable.mq_ic_back_white;
        MQConfig.bgColorTitle = R.color.colorPrimary;
        MQConfig.textColorTitle = android.R.color.white;
        MQConfig.titleGravity = MQConfig.MQTitleGravity.LEFT;
    }

    public void startChat(View v) {
        conversationWrapper();
    }

    @AfterPermissionGranted(REQUEST_CODE_CONVERSATION_PERMISSIONS)
    private void conversationWrapper() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {
//            startActivity(new Intent(MainActivity.this, HelpActivity.class));
            startActivity(new Intent(MainActivity.this, MQConversationActivity.class));
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.mq_runtime_permission_tip), REQUEST_CODE_CONVERSATION_PERMISSIONS, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(List<String> perms) {
        MQUtils.show(this, R.string.mq_permission_denied_tip);
    }

    private void initCube() {
        BGABanner mCubeBanner = (BGABanner) findViewById(R.id.banner);
        List<View> views = new ArrayList<>();
        views.add(getPageView(R.mipmap.one));
        views.add(getPageView(R.mipmap.two));
        views.add(getPageView(R.mipmap.three));
        views.add(getPageView(R.mipmap.four));
        views.add(getPageView(R.mipmap.five));
        mCubeBanner.setViews(views);
    }

    private View getPageView(@DrawableRes int resid) {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(resid);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }
}