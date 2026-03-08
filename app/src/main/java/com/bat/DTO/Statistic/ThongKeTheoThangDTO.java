package com.bat.DTO.Statistic;

import java.util.Objects;


public class ThongKeTheoThangDTO {
    private int thang;
    private Long chiphi;
    private Long doanhthu;
    private Long loinhuan;
    
    public ThongKeTheoThangDTO(){
        
    }
    
    public ThongKeTheoThangDTO(int thang, Long chiphi, Long doanhthu, Long loinhuan){
        this.thang = thang;
        this.chiphi = chiphi;
        this.doanhthu = doanhthu;
        this.loinhuan = loinhuan;
    }

    public int getThang() {
        return thang;
    }

    public void setThang(int thang) {
        this.thang = thang;
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
        int hash = 7;
        hash = 59 * hash + this.thang;
        hash = 59 * hash + Objects.hashCode(this.chiphi);
        hash = 59 * hash + Objects.hashCode(this.doanhthu);
        hash = 59 * hash + Objects.hashCode(this.loinhuan);
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
        final ThongKeTheoThangDTO other = (ThongKeTheoThangDTO) obj;
        if (this.thang != other.thang) {
            return false;
        }
        if (!Objects.equals(this.chiphi, other.chiphi)) {
            return false;
        }
        if (!Objects.equals(this.doanhthu, other.doanhthu)) {
            return false;
        }
        return Objects.equals(this.loinhuan, other.loinhuan);
    }


    @Override
    public String toString() {
        return "ThongKeTheoThangDTO{" + "thang=" + thang + ", chiphi=" + chiphi + ", doanhthu=" + doanhthu + ", loinhuan=" + loinhuan + '}';
    }
    
}
