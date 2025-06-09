package com.example.messenger.profile

import androidx.lifecycle.ViewModel
import com.example.messenger.user_data.UserData

class ProfileViewModel : ViewModel() {
    var userName = ""

    fun editUserData(){
        UserData.name = userName
    }
}