package com.qiushi.wechatshop.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Rylynn on 2018-05-18.
 *
 * 朋友圈素材
 */
data class Moment(var id: Long, var content: String, var type: Int, var images: ArrayList<NineImage>?, var shop_id: Long) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readInt(),
            parcel.createTypedArrayList(NineImage.CREATOR),
            parcel.readLong()
    )

    //test
    constructor() : this(0, "", 0, null, 0)

    constructor(content: String, images: ArrayList<NineImage>, shop_id: Long) : this(1, content, 1, images, shop_id)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(content)
        parcel.writeInt(type)
        parcel.writeTypedList(this.images)
        parcel.writeLong(shop_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Moment> {
        override fun createFromParcel(parcel: Parcel): Moment {
            return Moment(parcel)
        }

        override fun newArray(size: Int): Array<Moment?> {
            return arrayOfNulls(size)
        }
    }
}