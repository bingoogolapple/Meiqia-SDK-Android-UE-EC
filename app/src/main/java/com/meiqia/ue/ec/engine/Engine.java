package com.meiqia.ue.ec.engine;

import com.meiqia.ue.ec.model.GoodsModel;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/17 上午1:27
 * 描述:
 */
public interface Engine {

    @GET("test_api.json")
    Observable<List<GoodsModel>> loadBeforeGoods();
}
