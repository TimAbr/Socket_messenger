package com.example.messenger.user_data.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.network.ConnectionManager
import kotlinx.coroutines.launch

// ViewModel для экрана входа
class LoginViewModel : ViewModel() {
    var isRequesting = MutableLiveData(false)

    private val _username = MutableLiveData<String>()
    val username: MutableLiveData<String> = _username

    lateinit var connectionManager : ConnectionManager

    private val _password = MutableLiveData<String>()
    val password: MutableLiveData<String> = _password

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun onUsernameChanged(input: String) {
        _username.value = input.trim()
    }

    fun onPasswordChanged(input: String) {
        _password.value = input
    }

    fun onLoginClicked() {
        val user = username.value.orEmpty()
        val pass = password.value.orEmpty()
        if (user.isBlank() || pass.isBlank()) {
            _error.value = "Введите имя пользователя и пароль"
            return
        }
        isRequesting.value = true
        connectionManager.login(user, pass)
    }

}