package com.bachld.android.ui.view.dangnhap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bachld.android.R
import com.bachld.android.core.UiState
import com.bachld.android.data.dto.request.auth.LoginRequest
import com.bachld.android.databinding.FragmentDangNhapBinding
import com.bachld.android.ui.viewmodel.DangNhapViewModel
import kotlinx.coroutines.launch

class DangNhapFragment : Fragment() {

    private var _binding: FragmentDangNhapBinding? = null
    private val binding get() = _binding!!

    private val vm: DangNhapViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDangNhapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Sự kiện bấm nút đăng nhập
        binding.btnDangNhap.setOnClickListener {
            val email = binding.edtTaiKhoan.text.toString().trim()
            val password = binding.edtMatKhau.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                toast("Vui lòng nhập tài khoản và mật khẩu")
                return@setOnClickListener
            }
            val loginRequest = LoginRequest(email, password).apply {
                this.email = email
                this.password = password
            }
            vm.login(loginRequest)
        }

        // Lắng nghe cả loginState và myInfoState
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Collector cho loginState
                launch {
                    vm.loginState.collect { st ->
                        binding.btnDangNhap.isEnabled = st !is UiState.Loading
                        when (st) {
                            is UiState.Error -> toast(st.message ?: "Đăng nhập thất bại")
                            else -> Unit
                        }
                    }
                }

                // Collector cho myInfoState
                launch {
                    vm.myInfoState.collect { st ->
                        binding.btnDangNhap.isEnabled = st !is UiState.Loading
                        when (st) {
                            is UiState.Error -> {
                                toast(st.message ?: "Lấy thông tin người dùng thất bại")
                            }
                            is UiState.Success -> {
                                val res = st.data
                                if (res.code == 1000 && res.result != null) {
                                    toast("Đăng nhập thành công")
                                    val role = res.result.role?.lowercase()
                                    when (role) {
                                        "sinh_vien", "giang_vien", "admin", "tro_ly_khoa" -> {
                                            findNavController().navigate(R.id.action_global_nav_sinh_vien)
                                        }
                                        else -> toast("Role không hợp lệ: $role")
                                    }
                                } else {
                                    toast(res.message ?: "Có lỗi xảy ra")
                                }
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}
