package com.bachld.android.ui.doanfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DoAnViewModel: ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Trang Đồ Án"
    }
    val text: LiveData<String> = _text

}