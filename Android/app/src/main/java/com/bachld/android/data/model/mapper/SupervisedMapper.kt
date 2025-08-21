package com.bachld.android.data.model.mapper
import com.bachld.android.data.dto.response.giangvien.SinhVienSupervisedDto
import com.bachld.android.data.model.SupervisedStudent

fun SinhVienSupervisedDto.to_model() = SupervisedStudent(
    maSV, hoTen, tenLop, soDienThoai, tenDeTai
)
