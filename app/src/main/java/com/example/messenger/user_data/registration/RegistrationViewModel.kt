package com.example.messenger.user_data.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.network.ConnectionManager
import kotlinx.coroutines.launch


class RegistrationViewModel : ViewModel() {
    var isRequesting = MutableLiveData(false)

    lateinit var connectionManager : ConnectionManager

    private val _username = MutableLiveData<String>()
    val username: MutableLiveData<String> = _username

    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: MutableLiveData<String> = _phoneNumber

    private val _password = MutableLiveData<String>()
    val password: MutableLiveData<String> = _password

    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> = _registrationSuccess

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun onUsernameChanged(input: String) {
        _username.value = input.trim()
    }

    fun onPhoneNumberChanged(input: String) {
        _phoneNumber.value = input.trim()
    }

    fun onPasswordChanged(input: String) {
        _password.value = input
    }

    fun onRegisterClicked() {
        val user = username.value.orEmpty()
        val phone = phoneNumber.value.orEmpty()
        val pass = password.value.orEmpty()

        if (user.isBlank() || phone.isBlank() || pass.length < 6) {
            _error.value = "Проверьте введённые данные"
            return
        }

        isRequesting.value = true
        connectionManager.register(user, phone, pass)

    }

}