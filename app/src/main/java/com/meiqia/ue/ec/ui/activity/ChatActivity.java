package com.meiqia.ue.ec.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.meiqia.meiqiasdk.activity.MQConversationActivity;
import com.meiqia.meiqiasdk.util.MQUtils;
import com.meiqia.ue.ec.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/16 下午3:42
 * 描述:
 */
public class ChatActivity extends MQConversationActivity {
    public static boolean sIsCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sIsCreated = true;
    }

    @Override
    public void onBackPressed() {
        MQUtils.closeKeyboard(this);

        if (!MainActivity.sIsCreated) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        finish();
        overridePendingTransition(R.anim.activity_backward_enter, R.anim.activity_backward_exit);
    }

    @Override
    protected void onDestroy() {
        sIsCreated = false;
        super.onDestroy();
    }
}