package com.example.messenger.all_chats.new_chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.messenger.R
import com.example.messenger.databinding.FragmentNewChatBinding
import com.example.messenger.network.ClientService
import com.example.messenger.network.ClientService.Companion.EXTRA_EXISTS
import com.example.messenger.network.ConnectionManager


class NewChatFragment : Fragment() {

    private val viewModel: NewChatViewModel by viewModels()

    var _binding: FragmentNewChatBinding? = null
    val binding get() = _binding!!

    private var updateUIReceiver: BroadcastReceiver? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewChatBinding.inflate(inflater, container, false)

        //viewModel.isRequesting.value = activity?.findViewById<ProgressBar>(R.id.mainProgressBar)?.visibility==View.VISIBLE

        viewModel.isRequesting.observe(viewLifecycleOwner){ flag ->
            activity?.findViewById<ProgressBar>(R.id.mainProgressBar)?.visibility = if (flag) View.VISIBLE else View.GONE
        }

        binding.viewModel = viewModel

        binding.button.setOnClickListener{
            if (!viewModel.isRequesting.value!!){
                viewModel.isRequesting.value = true

                val connectionManager = ConnectionManager(requireContext())
                connectionManager.checkUserExistence(viewModel.chatName)
            }
        }

        val filter = IntentFilter()
        filter.addAction(ClientService.ACTION_USER_EXISTS)
        filter.addAction(ClientService.ACTION_GET_OR_CREATE_CHAT)

        updateUIReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                when (intent.action){
                    ClientService.ACTION_USER_EXISTS->{


                        if(!intent.getBooleanExtra(ClientService.EXTRA_EXISTS, false)) {
                            Toast.makeText(
                                context,
                                "No such user",
                                Toast.LENGTH_LONG
                            ).show()
                            viewModel.isRequesting.value = false
                        } else {
                            //val action = NewChatFragmentDirections.actionNewChatFragmentToChatFragment(viewModel.chatName)
                            //requireView().findNavController().navigate(action)

                            val connectionManager = ConnectionManager(requireContext())
                            connectionManager.getOrCreateChat(viewModel.chatName)
                        }
                    }

                    ClientService.ACTION_GET_OR_CREATE_CHAT->{
                        val chatId = intent.getIntExtra(ClientService.EXTRA_CHAT_ID, -1)
                        val name = intent.getStringExtra(ClientService.EXTRA_NAME) ?: ""

                        if(chatId==-1){
                            Toast.makeText(
                                context,
                                "Can not create chat",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            viewModel.isRequesting.value = false

                            if (view?.isShown == true && viewModel.chatName==name) {

                                val action = NewChatFragmentDirections.actionNewChatFragmentToChatFragment(chatId)
                                view?.findNavController()?.navigate(action)
                            }
                        }
                    }
                }

            }
        }
        registerReceiver(requireContext(),updateUIReceiver, filter, ContextCompat.RECEIVER_EXPORTED)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        updateUIReceiver?.let { receiver ->
            requireContext().unregisterReceiver(receiver)
            updateUIReceiver = null
        }
        _binding = null
    }

}