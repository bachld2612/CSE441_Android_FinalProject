package com.bachld.project.backend.entity;

import com.bachld.project.backend.enums.HoanState;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "don_hoan_do_an",
        indexes = {
                @Index(name="idx_sv_trangthai", columnList = "sinh_vien_id,trangThai")
        })
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DonHoanDoAn extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sinh_vien_id", nullable = false)
    SinhVien sinhVien;

    //xoa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "de_tai_id")
    DeTai deTai;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    HoanState trangThai;

    @Column(columnDefinition = "TEXT")
    String lyDo;

    String minhChungUrl;

    LocalDateTime requestedAt;
    LocalDateTime decidedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_phe_duyet_id")
    TaiKhoan nguoiPheDuyet;

    @Column(columnDefinition = "TEXT")
    String ghiChuQuyetDinh;
}
