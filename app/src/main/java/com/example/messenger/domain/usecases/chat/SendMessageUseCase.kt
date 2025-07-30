package com.example.messenger.domain.usecases.chat

import com.example.messenger.domain.repository.ChatRepository

class SendMessageUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(chatId: String, content: String): Result<Unit> =
        repository.sendMessage(chatId, content)
}