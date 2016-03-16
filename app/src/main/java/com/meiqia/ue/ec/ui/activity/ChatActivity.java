package com.meiqia.ue.ec.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.meiqia.meiqiasdk.activity.MQConversationActivity;
import com.meiqia.meiqiasdk.util.MQUtils;
import com.meiqia.ue.ec.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/16 下午3:42
 * 描述:
 */
public class ChatActivity extends MQConversationActivity {
    private static final String EXTRA_IS_FROM_NOTIFICATION = "EXTRA_IS_FROM_NOTIFICATION";

    public static final Intent newIntent(Context context, boolean isFromNotification) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_IS_FROM_NOTIFICATION, isFromNotification);
        return intent;
    }

    @Override
    public void onBackPressed() {
        MQUtils.closeKeyboard(this);

        if (getIntent().getBooleanExtra(EXTRA_IS_FROM_NOTIFICATION, false)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        finish();
        overridePendingTransition(R.anim.activity_backward_enter, R.anim.activity_backward_exit);
    }
}