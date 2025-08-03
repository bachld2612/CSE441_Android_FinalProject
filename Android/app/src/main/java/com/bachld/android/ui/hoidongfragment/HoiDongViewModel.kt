package com.bachld.android.ui.hoidongfragment

import androidx.lifecycle.ViewModel

class HoiDongViewModel: ViewModel() {

    private val _text = androidx.lifecycle.MutableLiveData<String>().apply {
        value = "Trang Hội Đồng"
    }
    val text: androidx.lifecycle.LiveData<String> = _text
}