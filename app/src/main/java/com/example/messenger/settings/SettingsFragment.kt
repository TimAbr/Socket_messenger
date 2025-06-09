package com.example.messenger.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.messenger.R
import com.example.messenger.databinding.FragmentSettingsBinding
import com.example.messenger.settings.server.SettingsListAdapter


class SettingsFragment : Fragment() {
    var _binding: FragmentSettingsBinding? = null
    val binding get() = _binding!!

    val settingsItemsList = listOf(
        SettingsItem("Server",
            R.drawable.baseline_computer_24,
            { findNavController().navigate(R.id.action_settingsFragment_to_serverSettingsFragment) }))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)

        val adapter = SettingsListAdapter(requireContext(),R.layout.settings_list_item, settingsItemsList)
        binding.settingsList.adapter = adapter

        return binding.root
    }


}