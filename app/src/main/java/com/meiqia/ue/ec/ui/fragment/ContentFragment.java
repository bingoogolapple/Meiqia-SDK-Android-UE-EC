package com.meiqia.ue.ec.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/16 下午6:30
 * 描述:
 */
public class ContentFragment  extends Fragment {
    private static final java.lang.String PAGE_NUMBER = "PAGE_NUMBER";

    public ContentFragment() {
    }

    public static ContentFragment newInstance(int pageNumber) {
        ContentFragment contentFragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putInt(PAGE_NUMBER, pageNumber);
        contentFragment.setArguments(args);
        return contentFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        int pageNumber = arguments.getInt(PAGE_NUMBER);
        TextView testTv = new TextView(getActivity());
        testTv.setText("第" + pageNumber + "页");
        testTv.setGravity(Gravity.CENTER);
        return testTv;
    }
}