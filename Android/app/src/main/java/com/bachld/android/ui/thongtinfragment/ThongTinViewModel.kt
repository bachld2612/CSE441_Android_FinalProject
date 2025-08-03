package com.bachld.android.ui.thongtinfragment

import androidx.lifecycle.ViewModel

class ThongTinViewModel: ViewModel() {

    private val _text = androidx.lifecycle.MutableLiveData<String>().apply {
        value = "This is Thong Tin Fragment"
    }
    val text: androidx.lifecycle.LiveData<String> = _text
}