package com.meiqia.ue.ec.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.meiqia.ue.ec.R;
import com.meiqia.ue.ec.model.GoodsModel;
import com.meiqia.ue.ec.ui.activity.DetailActivity;
import com.meiqia.ue.ec.ui.adapter.GoodsAdapter;
import com.meiqia.ue.ec.ui.widget.Divider;
import com.meiqia.ue.ec.util.Constants;
import com.meiqia.ue.ec.util.SweetAlertDialogUtil;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/16 下午6:37
 * 描述:
 */
public class GoodsFragment extends BaseFragment implements BGAOnRVItemClickListener {
    private static final String EXTRA_IS_AFTER_SALE = "EXTRA_IS_AFTER_SALE";

    private RecyclerView mGoodsRv;
    private GoodsAdapter mGoodsAdapter;

    public static GoodsFragment newInstance(boolean isAfterSale) {
        GoodsFragment goodsFragment = new GoodsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_IS_AFTER_SALE, isAfterSale);
        goodsFragment.setArguments(bundle);
        return goodsFragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_recyclerview);
        mGoodsRv = getViewById(R.id.rv_recyclerview_content);
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
    }

    @Override
    public void onUserVisible() {
        if (mGoodsAdapter == null || mGoodsAdapter.getDatas().size() == 0) {
            loadDatas();
        }
    }

    private void loadDatas() {
        mApp.getEngine().loadBeforeGoods()
                .compose(this.<List<GoodsModel>>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mActivity.showLoadingDialog(R.string.mq_data_is_loading);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<GoodsModel>>() {
                    @Override
                    public void call(List<GoodsModel> goodsModels) {
                        mActivity.dismissLoadingDialog();

                        mGoodsAdapter.setDatas(goodsModels);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mActivity.dismissLoadingDialog();
                        SweetAlertDialogUtil.showWarning(mActivity, "数据加载失败", throwable.getMessage());
                    }
                });
    }

    @Override
    public void onRVItemClick(ViewGroup viewGroup, View view, int position) {
        String mqAgentId = Constants.MQ_AGENT_ID_BEFORE;
        if (getArguments().getBoolean(EXTRA_IS_AFTER_SALE)) {
            mqAgentId = Constants.MQ_AGENT_ID_AFTER;
        }

        mActivity.forward(DetailActivity.newIntent(mActivity, mqAgentId, mGoodsAdapter.getItem(position)));
    }
}
