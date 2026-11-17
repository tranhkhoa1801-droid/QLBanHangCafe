package entity;

import java.util.List;
import java.util.Objects;
import dao.NhanVienDAO;

public class NhanVien {
    public static final String QUANLY = "Quản Lý";
    public static final String NHANVIEN = "Nhân Viên";

    private String maNV;
    private String hoTen;
    private String vaiTro;
    private String sdt;
    private TaiKhoan tk;
    private String caLamViec;
    private boolean isActive;

    public NhanVien() {
        this.isActive = true;
    }

    public NhanVien(String maNV) {
        this.maNV = maNV;
        this.isActive = true;
    }

    public NhanVien(String maNV, String hoTen, String vaiTro, String sdt, TaiKhoan tk, String caLamViec,
            boolean isActive) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.vaiTro = vaiTro;
        this.sdt = sdt;
        this.tk = tk;
        this.caLamViec = caLamViec;
        this.isActive = isActive;
    }

    public NhanVien(String maNV, String hoTen, String vaiTro, String sdt, String maTK, String caLamViec,
            boolean isActive) {
        this(maNV, hoTen, vaiTro, sdt, maTK != null ? new TaiKhoan(maTK) : null, caLamViec, isActive);
    }

    // Class diagram methods
    public List<NhanVien> getAll() {
        return new NhanVienDAO().getAll();
    }

    public boolean save() {
        NhanVienDAO dao = new NhanVienDAO();
        if (dao.getById(maNV) != null) {
            return dao.update(this);
        } else {
            return dao.insert(this);
        }
    }

    public boolean delete() {
        return new NhanVienDAO().delete(maNV);
    }

    public List<NhanVien> search() {
        return new NhanVienDAO().search(hoTen != null ? hoTen : maNV);
    }

    public List<NhanVien> search(String tuKhoa) {
        return new NhanVienDAO().search(tuKhoa);
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

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getsDT() {
        return sdt;
    }

    public void setsDT(String sDT) {
        this.sdt = sDT;
    }

    public TaiKhoan getTk() {
        return tk;
    }

    public void setTk(TaiKhoan tk) {
        this.tk = tk;
    }

    public TaiKhoan getTaiKhoan() {
        return tk;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.tk = taiKhoan;
    }

    public void setTaiKhoan(String maTK) {
        this.tk = (maTK != null) ? new TaiKhoan(maTK) : null;
    }

    public String getMaTK() {
        return (tk != null) ? tk.getMaTK() : null;
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
