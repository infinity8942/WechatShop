package com.qiushi.wechatshop.model

import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.previewlibrary.enitity.IThumbViewInfo

/**
 * Created by Rylynn on 2018-05-18.
 *
 * 素材宫格图片
 */
data class NineImage(var img_url: String, var oss_url: String, var mBounds: Rect?, var oss_id: Long, var size: Int, var is_del: Int, var type: String) : IThumbViewInfo, Parcelable, MultiItemEntity {

    override fun getItemType(): Int {
        return if (size == 0) {
            -1  //有加号的情况
        } else {
            1   //满九张的情况
        }
    }

    override fun getUrl(): String {
        return if (oss_url != null && oss_url.isNotEmpty()) {
            oss_url
        } else {
            img_url
        }
    }

    override fun getBounds(): Rect? = mBounds

    constructor() : this("", "", null, 0, 0, 0, "0")

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(Rect::class.java.classLoader),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(img_url)
        parcel.writeString(oss_url)
        parcel.writeParcelable(this.mBounds, 0)
        parcel.writeLong(oss_id)
        parcel.writeInt(size)
        parcel.writeInt(is_del)
        parcel.writeString(type)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<NineImage> {
        override fun createFromParcel(parcel: Parcel): NineImage {
            return NineImage(parcel)
        }

        override fun newArray(size: Int): Array<NineImage?> {
            return arrayOfNulls(size)
        }
    }
}