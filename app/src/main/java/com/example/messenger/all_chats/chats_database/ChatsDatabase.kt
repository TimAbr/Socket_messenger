package com.example.messenger.all_chats.chats_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Chat::class], version = 2, exportSchema = false)
abstract class ChatsDatabase: RoomDatabase() {
    abstract val chatDao: ChatsDao


    companion object{

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // создаём уникальный индекс на chat_id
                db.execSQL("CREATE UNIQUE INDEX index_chats_table_chat_id ON chats_table(chat_id)")
            }
        }

        @Volatile
        private var INSTANCE: ChatsDatabase? = null

        fun getInstance(context: Context): ChatsDatabase {
            synchronized(this){
                var instance = INSTANCE
                if (instance==null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ChatsDatabase::class.java,
                        "chats_database"
                    ).addMigrations(MIGRATION_1_2)
                        .build()
                    INSTANCE=instance
                }
                return  instance
            }

        }
    }



}