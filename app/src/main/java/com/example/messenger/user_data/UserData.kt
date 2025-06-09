package com.example.messenger.user_data

import android.content.SharedPreferences
import java.io.BufferedReader
import java.io.BufferedWriter

object UserData{
    var name = ""
    var id = -1
    var phone = ""
    var passwordHash = ""

    fun readFromFile(reader: BufferedReader){
        name = reader.readLine()
    }

    fun saveToFile(writer: BufferedWriter){
        writer.write(name)
        writer.newLine()
    }

    fun editSharedPreferences(edit:  SharedPreferences.Editor){
        edit.putString("userName", name)
        edit.putInt("userId", id)
        edit.putString("phone", phone)
        edit.commit()
    }
}