package com.meiqia.ue.ec.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.meiqia.ue.ec.R;
import com.meiqia.ue.ec.model.GoodsModel;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/17 上午10:35
 * 描述:
 */

public class GoodsAdapter extends BGARecyclerViewAdapter<GoodsModel> {

    public GoodsAdapter(RecyclerView recyclerView) {
        super(recyclerView, R.layout.item_goods);
    }

    @Override
    protected void fillData(BGAViewHolderHelper helper, int position, GoodsModel model) {
        ImageView iconIv = helper.getImageView(R.id.iv_item_goods_icon);
        Glide.with(mContext).load(model.small_icon).placeholder(R.mipmap.holder).into(iconIv);

        helper.setText(R.id.tv_item_goods_title, model.title);
        StringBuilder postageAndAddress = new StringBuilder();
        if (model.postage == 0) {
            postageAndAddress.append("包邮");
        } else {

            postageAndAddress.append("运费 ¥" + model.postage);
        }
        postageAndAddress.append("      " + model.address);
        helper.setText(R.id.tv_item_postage_and_address, postageAndAddress.toString());
        helper.setText(R.id.tv_item_price, "¥" + model.price);
        helper.setText(R.id.tv_item_sale_count, model.sale_count + "人付款");
    }
}