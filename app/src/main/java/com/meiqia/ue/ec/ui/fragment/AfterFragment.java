package com.meiqia.ue.ec.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.meiqia.core.MQManager;
import com.meiqia.core.MQScheduleRule;
import com.meiqia.ue.ec.R;
import com.meiqia.ue.ec.ui.activity.DetailActivity;
import com.meiqia.ue.ec.ui.widget.Divider;

import java.util.ArrayList;

import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/16 下午6:37
 * 描述:
 */
public class AfterFragment extends BaseFragment implements BGAOnRVItemClickListener {
    private RecyclerView mGoodsRv;
    private GoodsAdapter mGoodsAdapter;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_after);
        mGoodsRv = getViewById(R.id.rv_after_goods);
    }

    @Override
    protected void setListener() {
        mGoodsAdapter = new GoodsAdapter(mGoodsRv);
        mGoodsAdapter.setOnRVItemClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mGoodsRv.setLayoutManager(new LinearLayoutManager(mApp));
        mGoodsRv.addItemDecoration(new Divider(mApp));
        mGoodsRv.setAdapter(mGoodsAdapter);

        ArrayList<String> datas = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            datas.add(String.valueOf(i + 1));
        }
        mGoodsAdapter.setDatas(datas);
    }

    @Override
    public void onRVItemClick(ViewGroup viewGroup, View view, int i) {
        MQManager.getInstance(mApp).setScheduledAgentOrGroupWithId("f12b03466611d678797c35fbfe27b7b2", "", MQScheduleRule.REDIRECT_GROUP);
        mActivity.forward(DetailActivity.class);
    }

    private static class GoodsAdapter extends BGARecyclerViewAdapter<String> {

        public GoodsAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_after_goods);
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, String model) {
            helper.setText(R.id.tv_after_goods_title, "已购商品" + model);
        }
    }
}
