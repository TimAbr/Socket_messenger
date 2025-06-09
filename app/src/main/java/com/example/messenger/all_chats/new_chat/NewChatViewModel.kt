package com.example.messenger.all_chats.new_chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewChatViewModel : ViewModel() {
    var isRequesting = MutableLiveData(false)
    var chatName = ""

}