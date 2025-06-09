package com.example.messenger.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.lifecycle.Observer
import com.example.messenger.all_chats.chats_database.Chat
import com.example.messenger.all_chats.chats_database.ChatsDatabase
import com.example.messenger.databinding.FragmentChatBinding
import com.example.messenger.network.ClientService
import com.example.messenger.network.ConnectionManager
import com.example.messenger.user_data.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    val binding
        get() = _binding!!
    var chatId = -1

    private val viewModel: ChatViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(layoutInflater, container, false)


        val adapter = ChatAdapter()
        binding.allMessagesView.adapter = adapter
        adapter.data = viewModel.messages.value!!


        viewModel.messages.observe(viewLifecycleOwner, Observer {
            adapter.data = it
        })

        val application = requireNotNull(this.activity).application
        val dao = ChatsDatabase.getInstance(application).chatDao

        viewModel.chatName = "Connecting..."

        val chatId = ChatFragmentArgs.fromBundle(requireArguments()).chatId

        var chat: Chat? = null

        CoroutineScope(Dispatchers.IO).launch {
            while (chat == null) {
                chat = dao.get(chatId.toLong()).value
            }
            viewModel.chatName = chat!!.chatName
        }

        val connectionManager = ConnectionManager(requireContext())
        connectionManager.getMessages(chatId)


        binding.sendButton.setOnClickListener{
            val message = Message(chatId, UserData.id, UserData.name, binding.editMessage.text.toString())
            viewModel.messages.value?.add(message)
            adapter.data = viewModel.messages.value!!

            connectionManager.sendMessage(chatId, message.text)
        }


        val filter = IntentFilter()
        filter.addAction(ClientService.ACTION_MESSAGE_RECEIVED)
        filter.addAction(ClientService.ACTION_MESSAGES_RECEIVED)

        val updateChatReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    ClientService.ACTION_MESSAGE_RECEIVED-> {
                        val message = intent.extras?.get(ClientService.EXTRA_MESSAGE) as Message

                        Toast.makeText(
                            context,
                            "New message",
                            Toast.LENGTH_LONG
                        ).show()

                        viewModel.messages.value?.add(message)
                        adapter.data = viewModel.messages.value!!
                    }

                    ClientService.ACTION_MESSAGES_RECEIVED -> {
                        val parcelables = intent.getParcelableArrayExtra(ClientService.EXTRA_HISTORY)
                        val messages: Array<Message> = parcelables
                            ?.filterIsInstance<Message>()
                            ?.toTypedArray()
                            ?: emptyArray()
                        messages.forEach {
                            viewModel.messages.value!!.add(it)
                            adapter.data = viewModel.messages.value!!
                        }
                    }
                }
            }

        }

        registerReceiver(requireContext(),updateChatReceiver, filter, ContextCompat.RECEIVER_EXPORTED)
        return binding.root
    }
}