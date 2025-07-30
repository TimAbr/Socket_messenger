package com.example.messenger.domain.repository

import com.example.messenger.domain.model.Chat
import com.example.messenger.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun getChats(): Flow<List<Chat>>

    fun getChatDetails(chatId: String): Flow<List<Message>>

    suspend fun sendMessage(chatId: String, content: String): Result<Unit>
}