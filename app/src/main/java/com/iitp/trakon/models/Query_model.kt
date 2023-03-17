package com.iitp.trakon.models

import android.os.Parcel
import android.os.Parcelable

data class Query_model(
    val querySelected : String="",
    val name:String="",
    val phone:String="",
    val email:String="",
    val description:String="",
    val image:String="", // stores image link
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(querySelected)
        parcel.writeString(name)
        parcel.writeString(phone)
        parcel.writeString(email)
        parcel.writeString(description)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Query_model> {
        override fun createFromParcel(parcel: Parcel): Query_model {
            return Query_model(parcel)
        }

        override fun newArray(size: Int): Array<Query_model?> {
            return arrayOfNulls(size)
        }
    }
}