package com.example.messenger.network

import android.content.SharedPreferences

object NetworkSettings {
    var port = 8080
    var serverAddress = "127.0.0.1"

    fun editSharedPreferences(edit:  SharedPreferences.Editor){
        edit.putString("ip", serverAddress)
        edit.putInt("port", port)
        edit.commit()
    }
}