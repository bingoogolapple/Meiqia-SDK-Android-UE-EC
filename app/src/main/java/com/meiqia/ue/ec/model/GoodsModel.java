package com.meiqia.ue.ec.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/17 上午1:25
 * 描述:
 */
public class GoodsModel implements Parcelable {
    public String title;
    public int postage;
    public String address;
    public float price;
    public int sale_count;
    public String small_icon;
    public String desc;
    public List<String> icons;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeInt(this.postage);
        dest.writeString(this.address);
        dest.writeFloat(this.price);
        dest.writeInt(this.sale_count);
        dest.writeString(this.small_icon);
        dest.writeString(this.desc);
        dest.writeStringList(this.icons);
    }

    public GoodsModel() {
    }

    protected GoodsModel(Parcel in) {
        this.title = in.readString();
        this.postage = in.readInt();
        this.address = in.readString();
        this.price = in.readFloat();
        this.sale_count = in.readInt();
        this.small_icon = in.readString();
        this.desc = in.readString();
        this.icons = in.createStringArrayList();
    }

    public static final Parcelable.Creator<GoodsModel> CREATOR = new Parcelable.Creator<GoodsModel>() {
        @Override
        public GoodsModel createFromParcel(Parcel source) {
            return new GoodsModel(source);
        }

        @Override
        public GoodsModel[] newArray(int size) {
            return new GoodsModel[size];
        }
    };
}
