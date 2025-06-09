package com.example.messenger.all_chats.chats_database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ChatsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(chat: Chat)

    @Insert
    suspend fun insertAll(chats: List<Chat>)

    @Update
    suspend fun update(chat: Chat)

    @Delete
    suspend fun delete(chat: Chat)

    @Query("SELECT * FROM chats_table WHERE chat_id = :chatId")
    fun get(chatId: Long): LiveData<Chat>

    @Query("SELECT * FROM chats_table ORDER BY id DESC")
    fun getAll(): LiveData<List<Chat>>
}