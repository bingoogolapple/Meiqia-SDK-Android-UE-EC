package com.meiqia.ue.ec.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.meiqia.core.MQScheduleRule;
import com.meiqia.meiqiasdk.activity.MQConversationActivity;
import com.meiqia.meiqiasdk.util.MQIntentBuilder;
import com.meiqia.meiqiasdk.util.MQUtils;
import com.meiqia.ue.ec.R;
import com.meiqia.ue.ec.util.SPUtil;

import java.util.HashMap;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/16 下午3:42
 * 描述:
 */
public class ChatActivity extends MQConversationActivity {
    public static boolean sIsCreated = false;

    public static Intent newIntent(Context context, String agentId) {
        HashMap<String, String> clientInfo = new HashMap<>();
        clientInfo.put("name", SPUtil.getNickname());
        clientInfo.put("tel", SPUtil.getTel());

        if (!TextUtils.isEmpty(SPUtil.getAvatar())) {
            clientInfo.put("avatar", SPUtil.getAvatar());
        }

        return new MQIntentBuilder(context, ChatActivity.class)
                .setClientInfo(clientInfo)
                .setCustomizedId(SPUtil.getCustomId())
                .setScheduledAgent(agentId)
                .setScheduleRule(MQScheduleRule.REDIRECT_GROUP)
                .build();
    }

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