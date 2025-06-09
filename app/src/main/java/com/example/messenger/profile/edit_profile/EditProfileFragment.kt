package com.example.messenger.profile.edit_profile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.messenger.databinding.FragmentEditProfileBinding
import com.example.messenger.network.ConnectionManager
import com.example.messenger.user_data.UserData

class EditProfileFragment : Fragment() {
    private var _binding : FragmentEditProfileBinding? = null
    val binding get() = _binding!!

    //private val viewModel: MyProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val viewModel = ViewModelProvider(this)[EditProfileViewModel::class.java]

        _binding = FragmentEditProfileBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel

        viewModel.userName.observe(viewLifecycleOwner, Observer {
            newValue: String->

            binding.saveButton.isEnabled = viewModel.isSaveButtonEnabled()
        })

        binding.saveButton.setOnClickListener {
            viewModel.editUserData()

            val userData = requireContext().getSharedPreferences("userData", Context.MODE_PRIVATE)

            val edit = userData.edit()

            UserData.editSharedPreferences(edit)

            val connectionManager = ConnectionManager(requireContext())
            connectionManager.changeUsername(UserData.name)

            findNavController().navigateUp()
        }

        return binding.root
    }
}