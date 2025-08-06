package com.bachld.android.ui.hoidongfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class HoiDongViewModel: ViewModel() {

    private val _text = androidx.lifecycle.MutableLiveData<String>().apply {
        value = "Trang Hội Đồng"
    }
    val text: LiveData<String> = _text
}