package entity;

import java.util.Objects;

public class NhanVien {
    public static final String QUANLY = "Quản Lý";
    public static final String NHANVIEN = "Nhân Viên";

    private String maNV;
    private String hoTen;
    private String vaiTro;
    private String sDT;
    private TaiKhoan taiKhoan;
    private String caLamViec;
    private boolean isActive;

    public NhanVien() {
        this.isActive = true;
    }

    public NhanVien(String maNV) {
        this.maNV = maNV;
        this.isActive = true;
    }

    public NhanVien(String maNV, String hoTen, String vaiTro, String sDT, TaiKhoan taiKhoan, String caLamViec,
            boolean isActive) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.vaiTro = vaiTro;
        this.sDT = sDT;
        this.taiKhoan = taiKhoan;
        this.caLamViec = caLamViec;
        this.isActive = isActive;
    }

    public NhanVien(String maNV, String hoTen, String vaiTro, String sDT, String maTK, String caLamViec,
            boolean isActive) {
        this(maNV, hoTen, vaiTro, sDT, maTK != null ? new TaiKhoan(maTK) : null, caLamViec, isActive);
    }

    public boolean isManager() {
        return vaiTro != null && vaiTro.trim().equalsIgnoreCase(QUANLY);
    }

    // Standard Getters & Setters
    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getsDT() {
        return sDT;
    }

    public void setsDT(String sDT) {
        this.sDT = sDT;
    }

    public String getSdt() {
        return sDT;
    }

    public void setSdt(String sdt) {
        this.sDT = sdt;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public void setTaiKhoan(String maTK) {
        this.taiKhoan = (maTK != null) ? new TaiKhoan(maTK) : null;
    }

    public String getMaTK() {
        return (taiKhoan != null) ? taiKhoan.getMaTK() : null;
    }

    public String getCaLamViec() {
        return caLamViec;
    }

    public void setCaLamViec(String caLamViec) {
        this.caLamViec = caLamViec;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    // Convenience accessors
    public String maNV() {
        return maNV;
    }

    public String hoTen() {
        return hoTen;
    }

    public String vaiTro() {
        return vaiTro;
    }

    public String sdt() {
        return sDT;
    }

    public String diaChi() {
        return "";
    }

    public String tenDangNhap() {
        return taiKhoan != null ? taiKhoan.getTenDangNhap() : "";
    }

    public String matKhau() {
        return taiKhoan != null ? taiKhoan.getMatKhau() : "";
    }

    public boolean hoatDong() {
        return isActive;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNV);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        NhanVien other = (NhanVien) obj;
        return Objects.equals(maNV, other.maNV);
    }

    @Override
    public String toString() {
        return hoTen != null && !hoTen.isEmpty() ? hoTen : maNV;
    }
}
