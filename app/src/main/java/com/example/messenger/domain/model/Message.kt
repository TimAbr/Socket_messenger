package com.example.messenger.domain.model

data class Message(
    val chatId: Int,
    val senderId: Int,
    val senderName: String,
    val text: String
)