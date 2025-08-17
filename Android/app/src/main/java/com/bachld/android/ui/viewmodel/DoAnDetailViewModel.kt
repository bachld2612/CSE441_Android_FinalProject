package com.bachld.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bachld.android.data.dto.response.DeTaiResponse
import com.bachld.android.data.repository.impl.DeTaiRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class DoAnDetailViewModel(
    private val repo: DeTaiRepositoryImpl
) : ViewModel() {

    private val _project = MutableStateFlow<DeTaiResponse?>(null)
    val project: StateFlow<DeTaiResponse?> = _project

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun load(forceRefresh: Boolean = false) {
        _loading.value = true
        viewModelScope.launch {
            try {
                _project.value = repo.getMyProject(forceRefresh)
                _error.value = null
            } catch (t: Throwable) {
                _error.value = t.message ?: "Có lỗi xảy ra"
            } finally {
                _loading.value = false
            }
        }
    }
}