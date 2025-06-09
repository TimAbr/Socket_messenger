package com.example.messenger.all_chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.messenger.all_chats.chats_database.ChatsDao

class ChatsViewModelFactory(private val dao: ChatsDao): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatsViewModel::class.java)){
            return ChatsViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}