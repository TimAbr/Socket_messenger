package com.example.messenger.settings.server.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messenger.network.NetworkSettings

class EditServerSettingsViewModel : ViewModel() {
    val ip = MutableLiveData<String>("")
    val port = MutableLiveData<String>("")

    fun editNetworkSettings(){
        NetworkSettings.port = port.value!!.toInt()
        NetworkSettings.serverAddress = ip.value!!
    }

    private var zeroTo255 = ("(\\d{1,2}|(0|1)\\"
            + "d{2}|2[0-4]\\d|25[0-5])")


    private var regex = (zeroTo255 + "\\."
            + zeroTo255 + "\\."
            + zeroTo255 + "\\."
            + zeroTo255)

    private val regIP = Regex(regex)
    private val regPort = Regex("[0-9]+")

    fun isSaveButtonEnabled(): Boolean{
        return regPort.matches(port.value!!) && regIP.matches(ip.value!!)
    }
}