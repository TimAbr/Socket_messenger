package com.example.messenger

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.transition.Visibility
import com.example.messenger.all_chats.chats_database.ChatsDatabase
import com.example.messenger.databinding.ActivityMainBinding
import com.example.messenger.databinding.UserHeaderBinding
import com.example.messenger.network.ConnectionManager
import com.example.messenger.network.NetworkSettings
import com.example.messenger.user_data.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var isRegistered = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val builder = AppBarConfiguration.Builder(navController.graph)
        builder.setOpenableLayout(binding.drawerLayout)
        val appBarConfiguration = builder.build()
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        val userHeaderBinding = UserHeaderBinding.bind(binding.navView.getHeaderView(0))
        userHeaderBinding.userData = UserData

        userHeaderBinding.logOutText.setOnClickListener{
            val userData = getSharedPreferences("userData", Context.MODE_PRIVATE)

            val edit = userData.edit()

            edit.remove("userName")
            edit.remove("userId")
            edit.remove("phone")

            edit.commit()

            UserData.name = ""
            UserData.id = -1
            UserData.phone = ""

            //navController.navigate(R.id.action_chatsFragment_to_registrationFragment)
            startActivity(Intent(this, SplashActivity::class.java))

            binding.toolbar.visibility = View.GONE
        }

        NavigationUI.setupWithNavController(binding.navView, navController)

        getUserData()

        val connectionManager = ConnectionManager(this)
        connectionManager.connect()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.chatsFragment -> {
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
                else -> {
                    binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
            }
        }

        if(isRegistered) {

        } else {
            navController.navigate(R.id.action_chatsFragment_to_registrationFragment)
            binding.toolbar.visibility = View.GONE
        }


    }



    fun getUserData(){
        val userData = getSharedPreferences("userData", Context.MODE_PRIVATE)

        if (userData.contains("userName")) {
            UserData.name = userData.getString("userName", "") ?: ""
        } else {
            isRegistered = false
        }

        if (userData.contains("userId")) {
            UserData.id = userData.getInt("userId", -1)
        } else {
            isRegistered = false
        }

        if (userData.contains("phone")) {
            UserData.phone = userData.getString("phone", "")!!
        } else {
            isRegistered = false
        }

    }
}