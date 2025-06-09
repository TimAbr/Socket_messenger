package com.example.messenger.all_chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messenger.all_chats.chats_database.Chat
import com.example.messenger.all_chats.chats_database.ChatsDao

class ChatsViewModel(val dao: ChatsDao) : ViewModel() {
    var chats = dao.getAll()
    var isRequesting = MutableLiveData(true)
}