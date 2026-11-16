package entity;

import java.util.Objects;

public class SanPham {
    private String maSP;
    private String tenSP;
    private double giaVon;
    private double giaBan;
    private DanhMuc danhMuc;

    public SanPham() {
    }

    public SanPham(String maSP) {
        this.maSP = maSP;
    }

    public SanPham(String maSP, String tenSP, double giaVon, double giaBan, DanhMuc danhMuc) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.giaVon = giaVon;
        this.giaBan = giaBan;
        this.danhMuc = danhMuc;
    }

    public double tinhLoiNhuan() {
        return giaBan - giaVon;
    }

    public double getTiLeLoiNhuan() {
        return giaVon > 0 ? (giaBan - giaVon) / giaVon : 0;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public double getGiaVon() {
        return giaVon;
    }

    public void setGiaVon(double giaVon) {
        this.giaVon = giaVon;
    }

    public double getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(double giaBan) {
        this.giaBan = giaBan;
    }

    public DanhMuc getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(DanhMuc danhMuc) {
        this.danhMuc = danhMuc;
    }

    public String getMaDM() {
        return danhMuc != null ? danhMuc.getMaDM() : null;
    }

    public void setMaDM(String maDM) {
        this.danhMuc = (maDM != null) ? new DanhMuc(maDM) : null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSP);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        SanPham other = (SanPham) obj;
        return Objects.equals(maSP, other.maSP);
    }

    @Override
    public String toString() {
        return tenSP != null && !tenSP.isEmpty() ? tenSP : maSP;
    }
}
