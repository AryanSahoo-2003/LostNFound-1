package com.example.lostnfound.models

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class Users(
    val name:String="",
    val roll:String="" ,
    val email:String="",
    val whatsapp:String="",
    val mobile:String="",
    val image:String="", // stores image link
    val admin:Boolean=false,
    val valid:Boolean=true
    ): Parcelable {
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
        parcel.writeString(name)
        parcel.writeString(roll)
        parcel.writeString(email)
        parcel.writeString(whatsapp)
        parcel.writeString(mobile)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Users> {
        override fun createFromParcel(parcel: Parcel): Users {
            return Users(parcel)
        }

        override fun newArray(size: Int): Array<Users?> {
            return arrayOfNulls(size)
        }
    }
}

