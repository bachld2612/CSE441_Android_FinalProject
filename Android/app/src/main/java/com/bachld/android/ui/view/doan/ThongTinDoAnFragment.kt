package com.bachld.android.ui.view.doan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bachld.android.core.UserPrefs
import com.bachld.android.data.remote.client.ApiClient
import com.bachld.android.data.repository.impl.DeTaiRepositoryImpl
import com.bachld.android.data.dto.response.DeTaiResponse
import com.bachld.android.databinding.FragmentThongTinDoAnBinding
import com.bachld.android.ui.viewmodel.DoAnDetailViewModel
import com.bachld.android.ui.viewmodel.SharedDeTaiViewModel
import kotlinx.coroutines.launch

class ThongTinDoAnFragment : Fragment() {

    private var _binding: FragmentThongTinDoAnBinding? = null
    private val binding get() = _binding!!

    private val shared by activityViewModels<SharedDeTaiViewModel>()

    private val repo by lazy {
        DeTaiRepositoryImpl(
            api = ApiClient.deTaiApi,
            prefs = UserPrefs(requireContext().applicationContext)
        )
    }

    private val vm: DoAnDetailViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = DeTaiRepositoryImpl(ApiClient.deTaiApi, UserPrefs(requireContext()))
                return DoAnDetailViewModel(repo) as T
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentThongTinDoAnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { vm.project.collect { render(it) } }
                // (tuỳ chọn) để biết đang lỗi gì
                launch { vm.error.collect { it?.let { msg ->
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                } } }
            }
        }

        binding.btnNopDeCuong.setOnClickListener {
            findNavController().navigate(
                com.bachld.android.R.id.action_navigation_do_an_to_de_cuong
            )
        }

        vm.load(forceRefresh = true)
    }

    private fun render(p: DeTaiResponse?) = with(binding) {
        if (p == null) {
            layoutDaDangKy.visibility = View.GONE
            layoutChuaDangKy.visibility = View.VISIBLE
            return@with
        }

        //TRUYỀN deTaiId SANG TOÀN BỘ CÁC MÀN KHÁC
        shared.setDeTaiId(p.id)

        layoutDaDangKy.visibility = View.VISIBLE
        layoutChuaDangKy.visibility = View.GONE

        tvDeTai.text = "Đề tài: ${p.title}"

        val name = p.advisorName
        if (name.isNullOrBlank()) {
            tvGvhd.visibility = View.GONE
        } else {
            tvGvhd.visibility = View.VISIBLE
            tvGvhd.text = "GVHD: $name"
        }

        tvTrangThai.text = "Trạng thái: ${p.status}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
