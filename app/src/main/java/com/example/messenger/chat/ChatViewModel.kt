package com.example.messenger.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel() {

    val messages = MutableLiveData(mutableListOf<Message>())

    var chatName = ""
}