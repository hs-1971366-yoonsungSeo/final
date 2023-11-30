package com.example.weproject

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class ArticleModel(
    val sellerId: String,
    val title: String,
    val content : String,
    val createdAt: Long,
    val price: String,
    val imageURL: String,
    val onSale: String,
    val visible: Boolean
) : Parcelable {
    // 기존 코드...
    constructor() : this( "", "", "0", 0, "", "", "판매중", false)

    constructor(imageURL: String, sellerId: String) : this(
        sellerId = sellerId,
        title = "",
        content ="",
        createdAt = System.currentTimeMillis(),
        price = "",
        imageURL = imageURL,
        onSale = "판매중",
        visible = false,
    )
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt() == 1


    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(sellerId)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeLong(createdAt)
        parcel.writeString(price)
        parcel.writeString(imageURL)
        parcel.writeString(onSale)
        parcel.writeInt(if (visible) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ArticleModel> {
        override fun createFromParcel(parcel: Parcel): ArticleModel {
            return ArticleModel(parcel)
        }

        override fun newArray(size: Int): Array<ArticleModel?> {
            return arrayOfNulls(size)
        }
    }
}