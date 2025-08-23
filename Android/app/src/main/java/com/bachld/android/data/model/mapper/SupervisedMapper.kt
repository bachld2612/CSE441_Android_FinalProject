package com.bachld.android.data.model.mapper
import com.bachld.android.data.dto.response.giangvien.StundentSupervisedDto
import com.bachld.android.data.model.SupervisedStudent

fun StundentSupervisedDto.toModel() = SupervisedStudent(
    maSV, hoTen, tenLop, soDienThoai, tenDeTai, cvUrl
)
