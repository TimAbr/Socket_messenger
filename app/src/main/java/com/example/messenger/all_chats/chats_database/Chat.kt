package com.example.messenger.all_chats.chats_database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.messenger.chat.Message

@Entity(
    tableName = "chats_table",
    indices = [
        Index(value = ["chat_id"], unique = true)
    ])
data class Chat(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    @ColumnInfo(name = "chat_id")
    var chatId: Long = 0,

    @ColumnInfo(name = "chat_name")
    var chatName: String = "",
) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readLong() ?: -1,
        parcel.readLong() ?: -1,
        parcel.readString() ?: "",
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(chatId)
        parcel.writeString(chatName)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Chat> {
        override fun createFromParcel(parcel: Parcel) = Chat(parcel)
        override fun newArray(size: Int): Array<Chat?> = arrayOfNulls(size)
    }
}