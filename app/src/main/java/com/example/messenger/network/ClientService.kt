// ClientService.kt
package com.example.messenger.network

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.messenger.user_data.UserData

class ClientService : Service() {

    private val TAG = "ClientService"
    private var client: Client? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val cmdOrd = intent?.getIntExtra(EXTRA_COMMAND, -1) ?: -1
        val cmd = Commands.values().getOrNull(cmdOrd)
        if (cmd == null) {
            Log.w(TAG, "Unknown or missing command in Intent")
            return START_NOT_STICKY
        }

        if (client == null && cmd != Commands.START_CONNECTION) {
            Log.w(TAG, "Client not connected yet, ignoring $cmd")
            return START_STICKY
        }

        when (cmd) {
            Commands.START_CONNECTION -> {
                client = Client(NetworkSettings.serverAddress, NetworkSettings.port).apply {
                    setupCallbacks()
                    connect()
                }
            }

            Commands.GET_ALL_CHATS -> intent?.let{
                val name = it.getStringExtra(EXTRA_NAME).orEmpty()
                client?.getAllChats(name)
            }

            Commands.REGISTER_USER -> intent?.let {
                val name = it.getStringExtra(EXTRA_NAME).orEmpty()
                val phone = it.getStringExtra(EXTRA_PHONE).orEmpty()
                val hash = it.getStringExtra(EXTRA_HASH).orEmpty()
                client?.registerUser(name, phone, hash)
            }
            Commands.GET_OR_CREATE_CHAT -> intent?.let{
                val name = it.getStringExtra(EXTRA_NAME).orEmpty()
                client?.getOrCreateChat(UserData.name, name)
            }
            Commands.LOGIN_USER -> intent?.let {
                val name = it.getStringExtra(EXTRA_NAME).orEmpty()
                val hash = it.getStringExtra(EXTRA_HASH).orEmpty()
                client?.loginUser(name, hash)
            }
            Commands.CHECK_PHONE_EXISTENCE -> intent?.getStringExtra(EXTRA_PHONE)?.let { phone ->
                client?.checkPhoneExistence(phone)
            }
            Commands.VALIDATE_CREDENTIALS -> intent?.let {
                val phone = it.getStringExtra(EXTRA_PHONE).orEmpty()
                val hash  = it.getStringExtra(EXTRA_HASH).orEmpty()
                client?.validateCredentials(phone, hash)
            }
            Commands.CHANGE_PHONE_NUMBER -> intent?.getStringExtra(EXTRA_PHONE)?.let { newPhone ->
                client?.changePhoneNumber(newPhone)
            }
            Commands.CHANGE_PASSWORD -> intent?.getStringExtra(EXTRA_HASH)?.let { newHash ->
                client?.changePassword(newHash)
            }
            Commands.DELETE_USER -> {
                client?.deleteUser()
            }

            // управление аккаунтом
            Commands.CHECK_USER_EXISTENCE -> intent?.getStringExtra(EXTRA_NAME)?.let { name ->
                client?.checkUserExistence(name)
            }

            Commands.CHANGE_USERNAME -> intent?.getStringExtra(EXTRA_NAME)?.let { newName ->
                client?.changeUsername(newName)
            }

            // чат
            Commands.CREATE_CHAT -> intent?.getIntExtra(EXTRA_PEER_ID, -1)
                ?.takeIf { it >= 0 }
                ?.let { peerId ->
                    client?.createChat(peerId)
                }
            Commands.ADD_PARTICIPANT -> {
                val chatId = intent?.getIntExtra(EXTRA_CHAT_ID, -1) ?: -1
                val userId = intent?.getIntExtra(EXTRA_USER_ID, -1) ?: -1
                if (chatId >= 0 && userId >= 0) {
                    client?.addParticipant(chatId, userId)
                }
            }
            Commands.SEND_MESSAGE -> {
                val chatId = intent?.getIntExtra(EXTRA_CHAT_ID, -1) ?: -1
                val text   = intent?.getStringExtra(EXTRA_TEXT).orEmpty()
                if (chatId >= 0) {
                    client?.sendMessage(chatId, UserData.id, text, UserData.name)
                }
            }
            Commands.GET_MESSAGES -> intent?.getIntExtra(EXTRA_CHAT_ID, -1)
                ?.takeIf { it >= 0 }
                ?.let { chatId ->
                    client?.getMessages(chatId)
                }

            // изменение сервера
            Commands.CHANGE_SERVER -> {
                client?.disconnect()
                client = Client(NetworkSettings.serverAddress, NetworkSettings.port).apply {
                    setupCallbacks()
                    connect()
                }
            }

            Commands.DISCONNECT -> {
                client?.disconnect()
                sendBroadcast(Intent(ACTION_DISCONNECTED))
            }
            Commands.ERROR -> {
                sendBroadcast(Intent(ACTION_ERROR))
            }
            else -> { }
        }

        return START_STICKY
    }

    private fun Client.setupCallbacks() {

        onRegistered             = { userId ->
            //UserData.id = userId
            sendBroadcast(Intent(ACTION_REGISTERED).apply {
                putExtra(EXTRA_USER_ID, userId)
            })
        }

        onGetOrCreateChat       = { chatId, name ->
            sendBroadcast(Intent(ACTION_GET_OR_CREATE_CHAT).apply {
                putExtra(EXTRA_NAME, name)
                putExtra(EXTRA_CHAT_ID, chatId)
            })
        }

        onStartConnection = { flag ->
            sendBroadcast(Intent(ACTION_START_CONNECTION).apply {
                putExtra(EXTRA_EXISTS, flag)
            })
        }

        onGetAllChats = {list ->
            sendBroadcast(Intent(ACTION_GET_ALL_CHATS).apply {
                putExtra(EXTRA_CHATS, list.toTypedArray())
            })
        }

        onLogin             = { userId ->
            //UserData.id = userId
            sendBroadcast(Intent(ACTION_LOGIN).apply {
                putExtra(EXTRA_USER_ID, userId)
            })
        }

        onUserExistenceChecked   = { exists ->
            sendBroadcast(Intent(ACTION_USER_EXISTS).apply {
                putExtra(EXTRA_EXISTS, exists)
            })
        }
        onUsernameChanged        = { ok ->
            sendBroadcast(Intent(ACTION_USERNAME_CHANGED).apply {
                putExtra(EXTRA_SUCCESS, ok)
            })
        }

        // новые колбэки
        onPhoneExistenceChecked  = { exists ->
            sendBroadcast(Intent(ACTION_PHONE_EXISTS).apply {
                putExtra(EXTRA_EXISTS, exists)
            })
        }
        onCredentialsValidated   = { ok ->
            sendBroadcast(Intent(ACTION_CREDENTIALS_VALIDATED).apply {
                putExtra(EXTRA_SUCCESS, ok)
            })
        }
        onPhoneChanged           = { ok ->
            sendBroadcast(Intent(ACTION_PHONE_CHANGED).apply {
                putExtra(EXTRA_SUCCESS, ok)
            })
        }
        onPasswordChanged        = { ok ->
            sendBroadcast(Intent(ACTION_PASSWORD_CHANGED).apply {
                putExtra(EXTRA_SUCCESS, ok)
            })
        }
        onUserDeleted            = { ok ->
            sendBroadcast(Intent(ACTION_USER_DELETED).apply {
                putExtra(EXTRA_SUCCESS, ok)
            })
        }

        // чат
        onChatCreated            = { chatId ->
            sendBroadcast(Intent(ACTION_CHAT_CREATED).apply {
                putExtra(EXTRA_CHAT_ID, chatId)
            })
        }
        onParticipantAdded       = { ok ->
            sendBroadcast(Intent(ACTION_PARTICIPANT_ADDED).apply {
                putExtra(EXTRA_SUCCESS, ok)
            })
        }
        onMessageSent            = { msgId ->
            sendBroadcast(Intent(ACTION_MESSAGE_SENT).apply {
                putExtra(EXTRA_MESSAGE_ID, msgId)
            })
        }
        onMessagesReceived       = { history ->
            sendBroadcast(Intent(ACTION_MESSAGES_RECEIVED).apply {
                putExtra(EXTRA_HISTORY, history.toTypedArray())
            })
        }
        onMessageReceived        = { m ->
            sendBroadcast(Intent(ACTION_MESSAGE_RECEIVED).apply {
                putExtra(EXTRA_MESSAGE, m)
            })
        }
        onDisconnected           = {
            sendBroadcast(Intent(ACTION_DISCONNECTED))
        }
        onError                  = { errorCmd ->
            sendBroadcast(Intent(ACTION_ERROR).apply {
                putExtra(EXTRA_ERROR_CMD, errorCmd.ordinal)
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        client?.disconnect()
    }

    companion object {
        // Intent actions
        const val ACTION_REGISTERED              = "com.example.messenger.ACTION_REGISTERED"
        const val ACTION_GET_ALL_CHATS           = "com.example.messenger.ACTION_GET_ALL_CHATS"
        const val ACTION_GET_OR_CREATE_CHAT      = "com.example.messenger.ACTION_GET_OR_CREATE_CHAT"
        const val ACTION_START_CONNECTION        = "com.example.messenger.ACTION_START_CONNECTION"
        const val ACTION_LOGIN                   = "com.example.messenger.ACTION_LOGIN"
        const val ACTION_USER_EXISTS             = "com.example.messenger.ACTION_USER_EXISTS"
        const val ACTION_USERNAME_CHANGED        = "com.example.messenger.ACTION_USERNAME_CHANGED"
        const val ACTION_PHONE_EXISTS            = "com.example.messenger.ACTION_PHONE_EXISTS"
        const val ACTION_CREDENTIALS_VALIDATED   = "com.example.messenger.ACTION_CREDENTIALS_VALIDATED"
        const val ACTION_PHONE_CHANGED           = "com.example.messenger.ACTION_PHONE_CHANGED"
        const val ACTION_PASSWORD_CHANGED        = "com.example.messenger.ACTION_PASSWORD_CHANGED"
        const val ACTION_USER_DELETED            = "com.example.messenger.ACTION_USER_DELETED"

        const val ACTION_CHAT_CREATED            = "com.example.messenger.ACTION_CHAT_CREATED"
        const val ACTION_PARTICIPANT_ADDED       = "com.example.messenger.ACTION_PARTICIPANT_ADDED"
        const val ACTION_MESSAGE_SENT            = "com.example.messenger.ACTION_MESSAGE_SENT"
        const val ACTION_MESSAGES_RECEIVED       = "com.example.messenger.ACTION_MESSAGES_RECEIVED"
        const val ACTION_DISCONNECTED            = "com.example.messenger.ACTION_DISCONNECTED"
        const val ACTION_ERROR                   = "com.example.messenger.ACTION_ERROR"
        const val ACTION_MESSAGE_RECEIVED        = "com.example.messenger.ACTION_MESSAGE_RECEIVED"

        // Intent extras
        const val EXTRA_COMMAND    = "extra_command"
        const val EXTRA_NAME       = "extra_name"
        const val EXTRA_CHATS      = "extra_chats"
        const val EXTRA_PHONE      = "extra_phone"
        const val EXTRA_HASH       = "extra_hash"
        const val EXTRA_PEER_ID    = "extra_peer_id"
        const val EXTRA_CHAT_ID    = "extra_chat_id"
        const val EXTRA_USER_ID    = "extra_user_id"
        const val EXTRA_MESSAGE_ID = "extra_message_id"
        const val EXTRA_TEXT       = "extra_text"
        const val EXTRA_EXISTS     = "extra_exists"
        const val EXTRA_SUCCESS    = "extra_success"
        const val EXTRA_HISTORY    = "extra_history"
        const val EXTRA_ERROR_CMD  = "extra_error_cmd"
        const val EXTRA_MESSAGE    = "extra_message"
    }
}
