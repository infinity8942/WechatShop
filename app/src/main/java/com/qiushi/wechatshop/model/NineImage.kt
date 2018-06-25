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
data class NineImage(var img_url: String, var oss_img: String, var mBounds: Rect?, var oss_id: Long, var size: Int) : IThumbViewInfo, Parcelable, MultiItemEntity {
    override fun getItemType(): Int {
        return if (size == 0) {
            -1  //有加号的情况
        } else {
            1   //满九张的情况
        }
    }

    override fun getUrl(): String {
        return img_url
    }

    override fun getBounds(): Rect? {
        return mBounds
    }

    constructor() : this("", "", null, 0, 0)

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(Rect::class.java.classLoader),
            parcel.readLong(),
            parcel.readInt()

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(img_url)
        parcel.writeString(oss_img)
        parcel.writeParcelable(this.mBounds, 0)
        parcel.writeLong(oss_id)
        parcel.writeInt(size)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NineImage> {
        override fun createFromParcel(parcel: Parcel): NineImage {
            return NineImage(parcel)
        }

        override fun newArray(size: Int): Array<NineImage?> {
            return arrayOfNulls(size)
        }
    }
}