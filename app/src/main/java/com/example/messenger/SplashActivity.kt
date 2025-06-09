package com.example.messenger

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.messenger.all_chats.chats_database.ChatsDatabase
import com.example.messenger.network.ConnectionManager
import com.example.messenger.network.NetworkSettings
import com.example.messenger.user_data.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // Если используете layout:
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {

                CoroutineScope(Dispatchers.IO).launch {
                    val db = ChatsDatabase.getInstance(this@SplashActivity.baseContext)

                    db.clearAllTables()

                    withContext(Dispatchers.Main){
                        getNetworkSettings()
                        finish()
                    }
                }

            }
        }

    }

    override fun onPause() {
        super.onPause()
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
    }

    fun getNetworkSettings(){
        val networkSettings = getSharedPreferences("networkSettings", Context.MODE_PRIVATE)
        NetworkSettings.serverAddress = networkSettings.getString("ip", "192.168.1.107")!!
        NetworkSettings.port = networkSettings.getInt("port",8000)
    }

}
