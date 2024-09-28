package com.example.zedkashop.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoritesViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is the favorites fragment"
    }
    val text: LiveData<String> = _text
}