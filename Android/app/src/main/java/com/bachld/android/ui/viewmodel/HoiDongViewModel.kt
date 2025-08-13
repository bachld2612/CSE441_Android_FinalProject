package com.bachld.android.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HoiDongViewModel: ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Trang Hội Đồng"
    }
    val text: LiveData<String> = _text
}