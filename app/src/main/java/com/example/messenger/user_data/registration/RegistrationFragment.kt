package com.example.messenger.user_data.registration

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
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.messenger.R
import com.example.messenger.all_chats.new_chat.NewChatFragmentDirections
import com.example.messenger.databinding.FragmentLoginBinding
import com.example.messenger.databinding.FragmentRegistrationBinding
import com.example.messenger.network.ClientService
import com.example.messenger.network.ClientService.Companion.EXTRA_USER_ID
import com.example.messenger.network.ConnectionManager
import com.example.messenger.user_data.UserData
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar

// Фрагмент регистрации
class RegistrationFragment : Fragment() {
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegistrationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        viewModel.connectionManager = ConnectionManager(requireContext())

        // Связывание полей ввода
        binding.etUsername.doAfterTextChanged { text ->
            viewModel.onUsernameChanged(text.toString())
        }
        binding.etPhone.doAfterTextChanged { text ->
            viewModel.onPhoneNumberChanged(text.toString())
        }
        binding.etPassword.doAfterTextChanged { text ->
            viewModel.onPasswordChanged(text.toString())
        }

        // Обработка кнопок
        binding.btnRegister.setOnClickListener {
            viewModel.onRegisterClicked()
        }
        binding.tvLoginLink.setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
        }

        viewModel.isRequesting.observe(viewLifecycleOwner){ flag ->
            activity?.findViewById<ProgressBar>(R.id.mainProgressBar)?.visibility = if (flag) View.VISIBLE else View.GONE
        }

        // Наблюдатели
        viewModel.registrationSuccess.observe(viewLifecycleOwner, Observer { success ->
            if (success) {
                // Навигация на основной экран или логин
                findNavController().navigate(R.id.action_registrationFragment_to_chatsFragment)
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { errorMsg ->
            errorMsg?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
            }
        })

        val filter = IntentFilter()
        filter.addAction(ClientService.ACTION_REGISTERED)

        val updateUIReciver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                var id = intent.getIntExtra(EXTRA_USER_ID, -1)

                if(id<0) {
                    Toast.makeText(
                        context,
                        "Error. Repeat the input",
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.isRequesting.value = false
                } else {
                    viewModel.isRequesting.value = false

                    UserData.id = id
                    UserData.name = viewModel.username.value!!
                    UserData.phone = viewModel.phoneNumber.value!!

                    val userData = requireContext().getSharedPreferences("userData", Context.MODE_PRIVATE)
                    val edit = userData.edit()
                    UserData.editSharedPreferences(edit)

                    val connectionManager = ConnectionManager(requireContext())
                    connectionManager.getAllChats()

                    activity?.findViewById<MaterialToolbar>(R.id.toolbar)?.visibility = View.VISIBLE
                    findNavController().navigate(R.id.action_registrationFragment_to_chatsFragment)
                }
            }
        }
        registerReceiver(requireContext(),updateUIReciver, filter, ContextCompat.RECEIVER_EXPORTED)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}