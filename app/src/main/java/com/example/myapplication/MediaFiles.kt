package com.example.myapplication

import android.os.Parcel
import android.os.Parcelable

data class MediaFiles(
    var id: String?,
    var title: String?,
    var displayName: String?,
    var size: String?,
    var duration: String?,
    var path: String?,
    var dateAdded: String?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(displayName)
        parcel.writeString(size)
        parcel.writeString(duration)
        parcel.writeString(path)
        parcel.writeString(dateAdded)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<MediaFiles> {
        override fun createFromParcel(parcel: Parcel) = MediaFiles(parcel)
        override fun newArray(size: Int): Array<MediaFiles?> = arrayOfNulls(size)
    }
}
