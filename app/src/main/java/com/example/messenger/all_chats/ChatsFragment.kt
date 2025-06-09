package com.example.messenger.all_chats

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messenger.R
import com.example.messenger.all_chats.chats_database.Chat
import com.example.messenger.all_chats.chats_database.ChatsDatabase
import com.example.messenger.all_chats.new_chat.NewChatFragmentDirections
import com.example.messenger.databinding.FragmentChatsBinding
import com.example.messenger.network.ClientService
import com.example.messenger.network.ConnectionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ChatsFragment : Fragment() {
    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ChatsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChatsBinding.inflate(inflater, container,false)

        val application = requireNotNull(this.activity).application
        val dao = ChatsDatabase.getInstance(application).chatDao

        val viewModelFactory = ChatsViewModelFactory(dao)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[ChatsViewModel::class.java]

        binding.newChatButton.setOnClickListener {
            findNavController().navigate(R.id.action_chatsFragment_to_newChatFragment)
        }

        val adapter = ChatsAdapter(){chat ->
            val action = ChatsFragmentDirections.actionChatsFragmentToChatFragment(chat.chatId.toInt())
            findNavController().navigate(action)
        }
        binding.allChatsView.adapter = adapter

        viewModel.chats.observe(viewLifecycleOwner, Observer {
            adapter.data = it
        })

        viewModel.isRequesting.observe(viewLifecycleOwner){ flag ->
            activity?.findViewById<ProgressBar>(R.id.mainProgressBar)?.visibility = if (flag) View.VISIBLE else View.GONE
        }

        val filter = IntentFilter()
        filter.addAction(ClientService.ACTION_GET_OR_CREATE_CHAT)
        filter.addAction(ClientService.ACTION_GET_ALL_CHATS)
        filter.addAction(ClientService.ACTION_START_CONNECTION)

        val updateChatReceiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onReceive(context: Context, intent: Intent) {

                when(intent.action) {
                    ClientService.ACTION_GET_OR_CREATE_CHAT -> {
                        val chatId = intent.getIntExtra(ClientService.EXTRA_CHAT_ID, -1)
                        val name = intent.getStringExtra(ClientService.EXTRA_NAME) ?: ""

                        if (chatId >= 0) {

                            viewModel.chats.value
                            CoroutineScope(Dispatchers.IO).launch {
                            val chat = dao.get(chatId.toLong()).value

                                if (chat == null) {
                                    val newChat = Chat()
                                    newChat.chatId = chatId.toLong()
                                    newChat.chatName = name

                                    dao.insert(newChat)

                                } else {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            "Existing chat",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }

                            }


                        }
                    }

                    ClientService.ACTION_GET_ALL_CHATS -> {
                        val parcelables = intent.getParcelableArrayExtra(ClientService.EXTRA_CHATS)
                        val chats: Array<Chat> = parcelables
                            ?.filterIsInstance<Chat>()
                            ?.toTypedArray()
                            ?: emptyArray()
                        CoroutineScope(Dispatchers.IO).launch {
                            for (chat in chats) {
                                dao.insert(chat)
                            }
                        }
                        viewModel.isRequesting.value = false
                    }

                    ClientService.ACTION_START_CONNECTION -> {
                        val exists = intent.getBooleanExtra(ClientService.EXTRA_EXISTS, false)
                        if (exists){
                            val connectionManager = ConnectionManager(requireContext())
                            connectionManager.getAllChats()
                        }
                    }
                }
            }
        }

        registerReceiver(requireContext(),updateChatReceiver, filter, ContextCompat.RECEIVER_EXPORTED)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}