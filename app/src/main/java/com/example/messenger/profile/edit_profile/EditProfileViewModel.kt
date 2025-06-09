package com.example.messenger.profile.edit_profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messenger.user_data.UserData

class EditProfileViewModel : ViewModel() {

    val userName = MutableLiveData<String>("")

    fun editUserData(){
        UserData.name = userName.value!!
    }

    fun isSaveButtonEnabled(): Boolean{
        return userName.value!! != ""
    }
}