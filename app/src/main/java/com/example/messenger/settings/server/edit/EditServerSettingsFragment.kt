package com.example.messenger.settings.server.edit

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.messenger.databinding.FragmentEditServerSettingsBinding
import com.example.messenger.network.ConnectionManager
import com.example.messenger.network.NetworkSettings

class EditServerSettingsFragment : Fragment() {

    var _binding: FragmentEditServerSettingsBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditServerSettingsBinding.inflate(inflater, container, false)

        val viewModel = ViewModelProvider(this)[EditServerSettingsViewModel::class.java]

        binding.viewModel = viewModel

        viewModel.ip.observe(viewLifecycleOwner, Observer {
                newValue: String->

            binding.saveButton.isEnabled = viewModel.isSaveButtonEnabled()
        })

        viewModel.port.observe(viewLifecycleOwner, Observer {
                newValue: String->

            binding.saveButton.isEnabled = viewModel.isSaveButtonEnabled()
        })

        binding.saveButton.setOnClickListener {
            viewModel.editNetworkSettings()

            val networkSettings = requireContext().getSharedPreferences("networkSettings", Context.MODE_PRIVATE)

            val edit = networkSettings.edit()

            NetworkSettings.editSharedPreferences(edit)

            val connectionManager = ConnectionManager(requireContext())
            connectionManager.changeServer(NetworkSettings.serverAddress, NetworkSettings.port)

            findNavController().navigateUp()
        }

        return binding.root
    }
}