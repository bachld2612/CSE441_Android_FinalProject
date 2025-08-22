package com.bachld.android.ui.view.hoidong

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bachld.android.R
import com.bachld.android.core.UiState
import com.bachld.android.databinding.FragmentHoiDongBinding
import com.bachld.android.ui.adapter.HoiDongAdapter
import com.bachld.android.ui.viewmodel.HoiDongViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HoiDongFragment : Fragment() {

    private var _vb: FragmentHoiDongBinding? = null
    private val vb get() = _vb!!

    private val vm: HoiDongViewModel by viewModels()
    private lateinit var adapter: HoiDongAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _vb = FragmentHoiDongBinding.inflate(inflater, container, false)
        return vb.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = HoiDongAdapter { item ->
            val args = bundleOf(
                "hoiDongId" to item.id,
                "tenHoiDong" to item.tenHoiDong
            )
            findNavController().navigate(R.id.action_hoiDong_to_chiTietHoiDong, args)
        }

        vb.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        vb.recyclerView.adapter = adapter

        // SearchView trong layout là androidx.appcompat.widget.SearchView
        (vb.searchView as SearchView).apply {
            setIconifiedByDefault(false)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(q: String?): Boolean { vm.load(q); return true }
                override fun onQueryTextChange(newText: String?): Boolean { vm.load(newText); return true }
            })
        }

        vm.load() // lần đầu

        viewLifecycleOwner.lifecycleScope.launch {
            vm.state.collectLatest { st ->
                when (st) {
                    is UiState.Success -> adapter.submit(st.data)
                    is UiState.Error   -> { /* TODO: show error */ }
                    else               -> Unit
                }
            }
        }
    }

    override fun onDestroyView() { _vb = null; super.onDestroyView() }
}