package com.bachld.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bachld.android.data.model.SupervisedStudent
import com.bachld.android.data.repository.GiangVienRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.Normalizer

class DanhSachSinhVienHDViewModel(
    private val repo: GiangVienRepository
) : ViewModel() {

    private val _source = MutableStateFlow<List<SupervisedStudent>>(emptyList())
    val source: StateFlow<List<SupervisedStudent>> = _source

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _query = MutableStateFlow("")
    fun update_query(q: String) { _query.value = q }

    // lọc realtime tại client (không phân biệt dấu/hoa thường)
    val students_filtered: StateFlow<List<SupervisedStudent>> =
        _query
            .debounce(300)
            .distinctUntilChanged()
            .combine(_source) { q, src ->
                if (q.isBlank()) src else {
                    val nq = normalize(q)
                    src.filter { s ->
                        normalize(s.hoTen).contains(nq)
                                || normalize(s.maSV).contains(nq)
                                || normalize(s.tenLop).contains(nq)
                                || (s.soDienThoai?.let { normalize(it).contains(nq) } ?: false)
                                || (s.tenDeTai?.let { normalize(it).contains(nq) } ?: false)
                                || (s.cvUrl?.let { normalize(it).contains(nq) } ?: false)
                    }
                }
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun load_all(q: String? = null) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _source.value = repo.get_sinh_vien_huong_dan_all(q)
            } catch (t: Throwable) {
                _error.value = t.message
            } finally {
                _loading.value = false
            }
        }
    }

    private fun normalize(s: String): String {
        val t = Normalizer.normalize(s.lowercase(), Normalizer.Form.NFD)
        return t.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    }
}
