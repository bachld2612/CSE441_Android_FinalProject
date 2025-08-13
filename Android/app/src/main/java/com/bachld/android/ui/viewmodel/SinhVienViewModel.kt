package com.bachld.android.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SinhVienViewModel: ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Trang Sinh Viên"
    }
    val text: LiveData<String> = _text

}