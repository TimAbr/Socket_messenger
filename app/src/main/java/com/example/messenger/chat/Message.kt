package com.example.messenger.chat

import android.os.Parcel
import android.os.Parcelable

data class Message(
    var chatId: Int,
    var senderId: Int,
    var senderName: String,
    var text: String,

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt() ?: -1,
        parcel.readInt() ?: -1,
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(chatId)
        parcel.writeInt(senderId)
        parcel.writeString(senderName)
        parcel.writeString(text)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel) = Message(parcel)
        override fun newArray(size: Int): Array<Message?> = arrayOfNulls(size)
    }
}
