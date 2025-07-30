package com.example.messenger.domain.usecases.all_chats

import com.example.messenger.domain.model.Chat
import com.example.messenger.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetChatsUseCase(
    private val repository: ChatRepository
) {
    operator fun invoke(): Flow<List<Chat>> = repository.getChats()
}