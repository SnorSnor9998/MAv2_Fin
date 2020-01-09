package com.example.mav2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyProfileViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String>
        get() = _userName

    init{
        _userName.value="kappa"
    }
}
