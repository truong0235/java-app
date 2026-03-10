package com.bat.DTO.Statistic;

import java.util.Date;
import java.util.Objects;

public class ThongKeTungNgayTrongThangDTO{
    private Date ngay;
    private Long chiphi;
    private Long doanhthu;
    private Long loinhuan;

    public ThongKeTungNgayTrongThangDTO(Date ngay, Long chiphi, Long doanhthu, Long loinhuan) {
        this.ngay = ngay;
        this.chiphi = chiphi;
        this.doanhthu = doanhthu;
        this.loinhuan = loinhuan;
    }

    public Date getNgay() {
        return ngay;
    }

    public void setNgay(Date ngay) {
        this.ngay = ngay;
    }

    public Long getChiphi() {
        return chiphi;
    }

    public void setChiphi(Long chiphi) {
        this.chiphi = chiphi;
    }

    public Long getDoanhthu() {
        return doanhthu;
    }

    public void setDoanhthu(Long doanhthu) {
        this.doanhthu = doanhthu;
    }

    public Long getLoinhuan() {
        return loinhuan;
    }

    public void setLoinhuan(Long loinhuan) {
        this.loinhuan = loinhuan;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.ngay);
        hash = 29 * hash + Objects.hashCode(this.chiphi);
        hash = 29 * hash + Objects.hashCode(this.doanhthu);
        hash = 29 * hash + Objects.hashCode(this.loinhuan);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ThongKeTungNgayTrongThangDTO other = (ThongKeTungNgayTrongThangDTO) obj;
        if (!Objects.equals(this.chiphi, other.chiphi)) {
            return false;
        }
        if (!Objects.equals(this.doanhthu, other.doanhthu)) {
            return false;
        }
        if (!Objects.equals(this.loinhuan, other.loinhuan)) {
            return false;
        }
        return Objects.equals(this.ngay, other.ngay);
    }

    @Override
    public String toString() {
        return "ThongKeTungNgayTrongThangDTO{" + "ngay=" + ngay + ", chiphi=" + chiphi + ", doanhthu=" + doanhthu + ", loinhuan=" + loinhuan + '}';
    }
    
    
    
}
