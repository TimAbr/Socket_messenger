// Client.kt
package com.example.messenger.network

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.messenger.all_chats.chats_database.Chat
import com.example.messenger.chat.Message
import com.example.messenger.user_data.UserData
import kotlinx.coroutines.*
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

class Client(
    private val serverIp: String = NetworkSettings.serverAddress,
    private val serverPort: Int = NetworkSettings.port
) {
    private lateinit var socket: Socket
    private lateinit var input: DataInputStream
    private lateinit var output: DataOutputStream

    private var isConnected = false
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mainHandler = Handler(Looper.getMainLooper())

    // ========== Callbacks ==========
    var onRegistered: ((Int) -> Unit)? = null
    var onLogin: ((Int) -> Unit)? = null
    var onStartConnection: ((Boolean) -> Unit)? = null
    var onUserExistenceChecked: ((Boolean) -> Unit)? = null
    var onUsernameChanged: ((Boolean) -> Unit)? = null

    // новые колбэки
    var onPhoneExistenceChecked: ((Boolean) -> Unit)? = null
    var onCredentialsValidated: ((Boolean) -> Unit)? = null
    var onPhoneChanged: ((Boolean) -> Unit)? = null
    var onPasswordChanged: ((Boolean) -> Unit)? = null
    var onUserDeleted: ((Boolean) -> Unit)? = null
    var onGetAllChats: ((List<Chat>) -> Unit)? = null

    var onChatCreated: ((Int) -> Unit)? = null
    var onGetOrCreateChat: ((Int, String) -> Unit)? = null
    var onParticipantAdded: ((Boolean) -> Unit)? = null
    var onMessageSent: ((Int) -> Unit)? = null
    var onMessagesReceived: ((List<Message>) -> Unit)? = null
    var onMessageReceived: ((Message) -> Unit)? = null
    var onDisconnected: (() -> Unit)? = null
    var onError: ((Commands) -> Unit)? = null

    fun connect() {
        if (isConnected) return
        ioScope.launch {
            try {
                socket = Socket(serverIp, serverPort)
                input  = DataInputStream(socket.getInputStream())
                output = DataOutputStream(socket.getOutputStream())
                isConnected = true
                startReading()

                output.sendCommand(Commands.START_CONNECTION)
                output.sendIntValue(UserData.id)

                // сразу регистрация по сохранённым данным
                //if (UserData.id>=0) {
                //    registerUser(UserData.name, UserData.phone, UserData.passwordHash)
                //}
            } catch (e: Exception) {
                scheduleReconnect()
            }
        }
    }

    private fun startReading() {
        ioScope.launch {
            try {
                while (isConnected) {
                    val type = MessageType.values()[input.readInt()]
                    if (type == MessageType.COMMAND_MESSAGE) {
                        val cmd = Commands.values()[input.readInt()]
                        handleServerCommand(cmd)
                    } else {
                        // пропускаем неожиданные data
                        val skip = input.readInt()
                        input.skipBytes(skip)
                    }
                }
            } catch (e: Exception) {
                Log.d("Client", "readLoop ended: ${e.message}")
                disconnect()
            }
        }
    }



    private suspend fun readDataInt(): Int {
        val dt = input.readInt()
        if (MessageType.values()[dt] != MessageType.DATA_MESSAGE)
            throw IllegalStateException("Expected DATA_MESSAGE")
        input.readInt() // size
        return input.readInt()
    }

    private suspend fun readDataString(): String {
        val dt = input.readInt()
        if (MessageType.values()[dt] != MessageType.DATA_MESSAGE)
            throw IllegalStateException("Expected DATA_MESSAGE")
        val len = input.readInt()
        val buf = ByteArray(len)
        input.readFully(buf)
        return String(buf, Charsets.UTF_8)
    }

    private suspend fun handleServerCommand(cmd: Commands) {
        when (cmd) {
            // регистрация/аутентификация
            Commands.START_CONNECTION ->{
                val flag = readDataInt()
                onStartConnection?.invoke(flag != 0)
            }

            Commands.REGISTER_USER -> {
                val userId = readDataInt()
                UserData.id = userId
                onRegistered?.invoke(userId)
            }
            Commands.LOGIN_USER -> {
                val userId = readDataInt()
                UserData.id = userId
                onLogin?.invoke(userId)
            }
            Commands.CHECK_USER_EXISTENCE -> {
                val flag = readDataInt()
                onUserExistenceChecked?.invoke(flag != 0)
            }
            Commands.CHECK_PHONE_EXISTENCE -> {
                val flag = readDataInt()
                onPhoneExistenceChecked?.invoke(flag != 0)
            }
            Commands.VALIDATE_CREDENTIALS -> {
                val ok = readDataInt() != 0
                onCredentialsValidated?.invoke(ok)
            }
            Commands.CHANGE_PHONE_NUMBER -> {
                val ok = readDataInt() != 0
                onPhoneChanged?.invoke(ok)
            }
            Commands.CHANGE_PASSWORD -> {
                val ok = readDataInt() != 0
                onPasswordChanged?.invoke(ok)
            }
            Commands.DELETE_USER -> {
                val ok = readDataInt() != 0
                onUserDeleted?.invoke(ok)
            }
            // профиль
            Commands.CHANGE_USERNAME -> {
                val ok = readDataInt() != 0
                onUsernameChanged?.invoke(ok)
            }

            Commands.GET_OR_CREATE_CHAT -> {
                val id = readDataInt()
                val name = readDataString()
                onGetOrCreateChat?.invoke(id, name)
            }

            Commands.GET_ALL_CHATS -> {
                val count = readDataInt()
                val chatList = mutableListOf<Chat>()
                repeat(count){
                    val chat = Chat()

                    val id = readDataInt()
                    val name = readDataString()

                    chat.chatId = id.toLong()
                    chat.chatName = name

                    chatList.add(chat)
                }

                onGetAllChats?.invoke(chatList)
            }


            // чат
            Commands.CREATE_CHAT -> {
                val chatId = readDataInt()
                onChatCreated?.invoke(chatId)
            }
            Commands.ADD_PARTICIPANT -> {
                val ok = readDataInt() != 0
                onParticipantAdded?.invoke(ok)
            }
            Commands.SEND_MESSAGE -> {
                val msgId = readDataInt()
                onMessageSent?.invoke(msgId)
            }
            Commands.GET_MESSAGES -> {
                val count = readDataInt()
                val list = mutableListOf<Message>()
                repeat(count) {
                    val chatId     = readDataInt()
                    val senderId   = readDataInt()
                    val senderName = readDataString()
                    val text       = readDataString()
                    list += Message(chatId, senderId, senderName, text)
                }
                onMessagesReceived?.invoke(list)
            }
            Commands.GET_MESSAGE -> {
                val chatId     = readDataInt()
                val senderId   = readDataInt()
                val senderName = readDataString()
                val text       = readDataString()
                onMessageReceived?.invoke(Message(chatId, senderId, senderName, text))
            }

            Commands.DISCONNECT -> {
                onDisconnected?.invoke()
                disconnect()
            }
            Commands.ERROR -> {
                onError?.invoke(cmd)
            }
            else -> {
                onError?.invoke(cmd)
            }
        }

    }

    // ====== API методы ======
    fun disconnect() {
        isConnected = false
        ioScope.launch {
            try { output.sendCommand(Commands.DISCONNECT) } catch (_: Exception) {}
            try { socket.close() } catch (_: Exception) {}
        }
    }

    fun getAllChats(name: String) = ioScope.launch{
        output.sendCommand(Commands.GET_ALL_CHATS)
        output.sendString(name)
    }

    fun getOrCreateChat(user1: String, user2: String) = ioScope.launch{
        output.sendCommand(Commands.GET_OR_CREATE_CHAT)
        output.sendString(user1)
        output.sendString(user2)
    }

    fun registerUser(username: String, phone: String, hash: String) = ioScope.launch {
        output.sendCommand(Commands.REGISTER_USER)
        output.sendString(username)
        output.sendString(phone)
        output.sendString(hash)
    }

    fun loginUser(username: String, hash: String) = ioScope.launch {
        output.sendCommand(Commands.LOGIN_USER)
        output.sendString(username)
        output.sendString(hash)
    }

    fun checkUserExistence(username: String) = ioScope.launch {
        output.sendCommand(Commands.CHECK_USER_EXISTENCE)
        output.sendString(username)
    }

    fun checkPhoneExistence(phone: String) = ioScope.launch {
        output.sendCommand(Commands.CHECK_PHONE_EXISTENCE)
        output.sendString(phone)
    }

    fun validateCredentials(phone: String, hash: String) = ioScope.launch {
        output.sendCommand(Commands.VALIDATE_CREDENTIALS)
        output.sendString(phone)
        output.sendString(hash)
    }

    fun changeUsername(newName: String) = ioScope.launch {
        output.sendCommand(Commands.CHANGE_USERNAME)
        output.sendString(newName)
    }

    fun changePhoneNumber(newPhone: String) = ioScope.launch {
        output.sendCommand(Commands.CHANGE_PHONE_NUMBER)
        output.sendString(newPhone)
    }

    fun changePassword(newHash: String) = ioScope.launch {
        output.sendCommand(Commands.CHANGE_PASSWORD)
        output.sendString(newHash)
    }

    fun deleteUser() = ioScope.launch {
        output.sendCommand(Commands.DELETE_USER)
    }

    fun createChat(peerId: Int) = ioScope.launch {
        output.sendCommand(Commands.CREATE_CHAT)
        output.sendIntValue(peerId)
        output.sendIntValue(UserData.id)
    }

    fun addParticipant(chatId: Int, userId: Int) = ioScope.launch {
        output.sendCommand(Commands.ADD_PARTICIPANT)
        output.sendIntValue(chatId)
        output.sendIntValue(userId)
    }

    fun sendMessage(chatId: Int, senderId: Int, text: String, senderName: String) = ioScope.launch {
        output.sendCommand(Commands.SEND_MESSAGE)
        output.sendIntValue(chatId)
        output.sendIntValue(senderId)
        output.sendString(senderName)
        output.sendString(text)
    }

    fun getMessages(chatId: Int) = ioScope.launch {
        output.sendCommand(Commands.GET_MESSAGES)
        output.sendIntValue(chatId)
    }

    private fun scheduleReconnect() {
        mainHandler.postDelayed({ connect() }, 1000)
    }
}
