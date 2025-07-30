package com.example.messenger.domain.usecases.chat

import com.example.messenger.domain.model.Message
import com.example.messenger.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetChatDetailsUseCase(
    private val repository: ChatRepository
) {
    operator fun invoke(chatId: String): Flow<List<Message>> =
        repository.getChatDetails(chatId)
}