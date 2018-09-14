package com.gvm.demoffmpeg.slideitem

import android.os.Parcel
import android.os.Parcelable

/**
 * Brought to you by rickykurniawan on 04/09/18.
 */
data class SlideItemModel(var punchLine1: String = "",
                          var punchLine2: String = "",
                          var slideText: String = "",
                          var imagePath: String = "",
                          var selected: Boolean = false) : Parcelable {
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            1 == source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(punchLine1)
        writeString(punchLine2)
        writeString(slideText)
        writeString(imagePath)
        writeInt((if (selected) 1 else 0))
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SlideItemModel> = object : Parcelable.Creator<SlideItemModel> {
            override fun createFromParcel(source: Parcel): SlideItemModel = SlideItemModel(source)
            override fun newArray(size: Int): Array<SlideItemModel?> = arrayOfNulls(size)
        }
    }
}