package com.example.contact_list_app.model

import android.os.Parcel
import android.os.Parcelable

data class ContactModel(
    val fullName: String,
    val phone: String,
    val photoUri: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(fullName)
        dest.writeString(phone)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContactModel> {
        override fun createFromParcel(source: Parcel): ContactModel? {
            return ContactModel(source)
        }

        override fun newArray(size: Int): Array<out ContactModel?>? {
            return arrayOfNulls(size)
        }
    }
}