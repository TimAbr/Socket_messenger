package com.example.messenger

import android.app.Application
import com.example.messenger.all_chats.chats_database.ChatsDatabase

class MyApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        // Получаем экземпляр базы данных
        //val db = ChatsDatabase.getInstance(this)

        //db.clearAllTables()
    }
}
