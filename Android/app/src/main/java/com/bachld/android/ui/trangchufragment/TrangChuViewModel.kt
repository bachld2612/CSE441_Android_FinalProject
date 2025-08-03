package com.bachld.android.ui.trangchufragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TrangChuViewModel: ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Trang Chủ"
    }
    val text: LiveData<String> = _text

}