package com.bachld.android.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedDeTaiViewModel : ViewModel() {
    private val _deTaiId = MutableLiveData<Long>()
    val deTaiId: LiveData<Long> get() = _deTaiId

    fun setDeTaiId(id: Long?) {
        if (id != null && id > 0) _deTaiId.value = id
    }
}
