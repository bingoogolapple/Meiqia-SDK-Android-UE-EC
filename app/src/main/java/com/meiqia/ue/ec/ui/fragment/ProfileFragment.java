package com.meiqia.ue.ec.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;

import com.meiqia.core.MQManager;
import com.meiqia.core.callback.OnClientInfoCallback;
import com.meiqia.meiqiasdk.util.MQUtils;
import com.meiqia.ue.ec.R;
import com.meiqia.ue.ec.util.SPUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/18 上午12:39
 * 描述:
 */
public class ProfileFragment extends BaseFragment {
    private TextInputLayout mIdTil;
    private TextInputEditText mIdTiet;

    private TextInputLayout mNicknameTil;
    private TextInputEditText mNicknameTiet;

    private TextInputLayout mTelTil;
    private TextInputEditText mTelTiet;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_profile);

        mIdTil = getViewById(R.id.til_profile_id);
        mIdTiet = getViewById(R.id.tiet_profile_id);

        mNicknameTil = getViewById(R.id.til_profile_nickname);
        mNicknameTiet = getViewById(R.id.tiet_profile_nickname);

        mTelTil = getViewById(R.id.til_profile_tel);
        mTelTiet = getViewById(R.id.tiet_profile_tel);
    }

    @Override
    protected void setListener() {
        setOnClickListener(R.id.btn_profile_done);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mIdTiet.setText(SPUtil.getCustomId());
        mNicknameTiet.setText(SPUtil.getNickname());
        mTelTiet.setText(SPUtil.getTel());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_profile_done) {
            submitCustomInfo();
        }
    }

    private void submitCustomInfo() {
        final String id = mIdTiet.getText().toString().trim();
        final String nickname = mNicknameTiet.getText().toString().trim();
        final String tel = mTelTiet.getText().toString().trim();

        boolean flag = true;
        if (TextUtils.isEmpty(nickname)) {
            mNicknameTil.setError("昵称不能为空!");
            flag = false;
        }

        if (TextUtils.isEmpty(tel)) {
            mTelTil.setError("手机号不能为空!");
            flag = false;
        }

        if (flag) {
            final Map<String, String> info = new HashMap<>();
            info.put("name", nickname);
            info.put("avatar", "https://avatars2.githubusercontent.com/u/8949716?v=3&s=460");
            info.put("tel", tel);
            MQManager.getInstance(mApp).setClientInfo(info, new OnClientInfoCallback() {
                @Override
                public void onSuccess() {
                    MQUtils.show(mApp, "设置自定义信息成功");
                    SPUtil.setCustomId(id);
                    SPUtil.setNickname(nickname);
                    SPUtil.setTel(tel);
                }

                @Override
                public void onFailure(int code, String msg) {
                    MQUtils.show(mApp, "设置自定义信息失败!");
                }
            });
        }
    }
}
