package com.meiqia.ue.ec.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.meiqia.meiqiasdk.util.MQUtils;
import com.meiqia.ue.ec.R;
import com.meiqia.ue.ec.util.SPUtil;

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

        mTelTiet.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submitCustomInfo();
                    return true;
                }
                return false;
            }
        });
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
        } else {
            mNicknameTil.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(tel)) {
            mTelTil.setError("手机号不能为空!");
            flag = false;
        } else {
            mTelTil.setErrorEnabled(false);
        }


        if (flag) {
            SPUtil.setCustomId(id);
            SPUtil.setNickname(nickname);
            SPUtil.setTel(tel);

            MQUtils.show(mApp, "保存成功，将在下一次打开对话时生效");
        }
    }
}
