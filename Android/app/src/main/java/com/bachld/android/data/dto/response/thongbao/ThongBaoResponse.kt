package com.bachld.android.data.dto.response.thongbao

import java.time.LocalDate

data class ThongBaoResponse(
    val id: Long,
    val tieuDe: String,
    val noiDung: String,
    val fileUrl: String?,
    val createdAt: LocalDate
)

data class PageResponse<T>(
    val content: List<T>,
    val pageable: Pageable,
    val last: Boolean,
    val totalElements: Int,
    val totalPages: Int,
    val size: Int,
    val number: Int,
    val sort: Sort,
    val first: Boolean,
    val numberOfElements: Int,
    val empty: Boolean
)

data class Pageable(
    val pageNumber: Int,
    val pageSize: Int,
    val offset: Int,
    val paged: Boolean,
    val unpaged: Boolean,
    val sort: Sort
)

data class Sort(
    val empty: Boolean,
    val sorted: Boolean,
    val unsorted: Boolean
)