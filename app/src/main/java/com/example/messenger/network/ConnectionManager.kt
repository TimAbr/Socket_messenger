// ConnectionManager.kt
package com.example.messenger.network

import android.content.Context
import android.content.Intent
import com.example.messenger.user_data.UserData

class ConnectionManager(private val ctx: Context) {
    fun execute(cmd: Commands, extras: Intent.() -> Unit = {}) {
        val intent = Intent(ctx, ClientService::class.java)
        intent.putExtra(ClientService.EXTRA_COMMAND, cmd.ordinal)
        extras(intent)
        ctx.startService(intent)
    }

    fun connect() = execute(Commands.START_CONNECTION)

    fun register(name: String, phone: String, hash: String) = execute(Commands.REGISTER_USER) {
        putExtra(ClientService.EXTRA_NAME, name)
        putExtra(ClientService.EXTRA_PHONE, phone)
        putExtra(ClientService.EXTRA_HASH, hash)
    }

    fun getAllChats() = execute(Commands.GET_ALL_CHATS){
        putExtra(ClientService.EXTRA_NAME, UserData.name)
    }

    fun getOrCreateChat(user: String) = execute(Commands.GET_OR_CREATE_CHAT) {
        putExtra(ClientService.EXTRA_NAME, user)
    }

    fun login(name: String, hash: String) = execute(Commands.LOGIN_USER) {
        putExtra(ClientService.EXTRA_NAME, name)
        putExtra(ClientService.EXTRA_HASH, hash)
    }

    fun checkPhoneExistence(phone: String) = execute(Commands.CHECK_PHONE_EXISTENCE) {
        putExtra(ClientService.EXTRA_PHONE, phone)
    }

    fun validateCredentials(phone: String, hash: String) = execute(Commands.VALIDATE_CREDENTIALS) {
        putExtra(ClientService.EXTRA_PHONE, phone)
        putExtra(ClientService.EXTRA_HASH, hash)
    }

    fun changePhoneNumber(newPhone: String) = execute(Commands.CHANGE_PHONE_NUMBER) {
        putExtra(ClientService.EXTRA_PHONE, newPhone)
    }

    fun changePassword(newHash: String) = execute(Commands.CHANGE_PASSWORD) {
        putExtra(ClientService.EXTRA_HASH, newHash)
    }

    fun deleteUser() = execute(Commands.DELETE_USER)

    fun checkUserExistence(name: String) = execute(Commands.CHECK_USER_EXISTENCE) {
        putExtra(ClientService.EXTRA_NAME, name)
    }

    fun changeUsername(name: String) = execute(Commands.CHANGE_USERNAME) {
        putExtra(ClientService.EXTRA_NAME, name)
    }

    fun createChat(peerId: Int) = execute(Commands.CREATE_CHAT) {
        putExtra(ClientService.EXTRA_PEER_ID, peerId)
    }

    fun addParticipant(chatId: Int, userId: Int) = execute(Commands.ADD_PARTICIPANT) {
        putExtra(ClientService.EXTRA_CHAT_ID, chatId)
        putExtra(ClientService.EXTRA_USER_ID, userId)
    }

    fun sendMessage(chatId: Int, text: String) = execute(Commands.SEND_MESSAGE) {
        putExtra(ClientService.EXTRA_CHAT_ID, chatId)
        putExtra(ClientService.EXTRA_TEXT, text)
    }

    fun getMessages(chatId: Int) = execute(Commands.GET_MESSAGES) {
        putExtra(ClientService.EXTRA_CHAT_ID, chatId)
    }

    fun changeServer(ip: String, port: Int) {
        NetworkSettings.port = port
        NetworkSettings.serverAddress = ip
        execute(Commands.CHANGE_SERVER)
    }

    fun disconnect() = execute(Commands.DISCONNECT)
}
