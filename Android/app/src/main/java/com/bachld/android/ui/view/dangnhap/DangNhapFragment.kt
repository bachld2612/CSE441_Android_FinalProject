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
import com.bachld.android.core.UserPrefs
import com.bachld.android.data.dto.request.auth.LoginRequest
import com.bachld.android.databinding.FragmentDangNhapBinding
import com.bachld.android.MainActivity   // <- để gọi applyRoleUI
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

        // Bấm Đăng nhập
        binding.btnDangNhap.setOnClickListener {
            val email = binding.edtTaiKhoan.text.toString().trim()
            val password = binding.edtMatKhau.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                toast("Vui lòng nhập tài khoản và mật khẩu")
                return@setOnClickListener
            }
            vm.login(LoginRequest(email, password))
        }

        // Lắng nghe state
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 1) Login state
                launch {
                    vm.loginState.collect { st ->
                        binding.btnDangNhap.isEnabled = st !is UiState.Loading
                        when (st) {
                            is UiState.Loading -> { /* có thể show loading */ }
                            is UiState.Error -> toast("Tài khoản hoặc mật khẩu không chính xác")
                            is UiState.Success -> {
                                // ĐÃ CÓ TOKEN -> gọi myInfo để lấy role thật
                                vm.fetchMyInfo() // <- gọi hàm trong ViewModel của bạn
                            }
                            else -> Unit
                        }
                    }
                }

                // 2) MyInfo state
                launch {
                    vm.myInfoState.collect { st ->
                        binding.btnDangNhap.isEnabled = st !is UiState.Loading
                        when (st) {
                            is UiState.Loading -> { /* có thể show loading */ }
                            is UiState.Error -> toast("Lấy thông tin người dùng thất bại")
                            is UiState.Success -> {
                                val res = st.data
                                val info = res.result
                                if (res.code == 1000 && info != null) {
                                    // LƯU CACHE để các màn sau dùng
                                    UserPrefs(requireContext()).save(info)

                                    val role = info.role?.lowercase()
                                    val isGV = role == "giang_vien" || role == "truong_bo_mon" || role == "tro_ly_khoa"

                                    // ĐỔI UI THEO ROLE (menu + appbar + graph)
                                    (requireActivity() as MainActivity).applyRoleUI(isGV)

                                    // ĐIỀU HƯỚNG sang graph đúng role (reset stack bằng global action)
                                    findNavController().navigate(
                                        if (isGV) R.id.action_global_nav_giang_vien
                                        else R.id.action_global_nav_sinh_vien
                                    )

                                    toast("Đăng nhập thành công")
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
