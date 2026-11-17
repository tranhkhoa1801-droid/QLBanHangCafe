package entity;

import java.util.Objects;
import dao.NhanVienDAO;
import dao.TaiKhoanDAO;

public class TaiKhoan {
    private String maTK;
    private String tenDangNhap;
    private String matKhau;

    public TaiKhoan() {
    }

    public TaiKhoan(String maTK) {
        this.maTK = maTK;
    }

    public TaiKhoan(String maTK, String tenDangNhap, String matKhau) {
        this.maTK = maTK;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
    }

    // Class diagram methods
    public NhanVien authenticate() {
        TaiKhoanDAO dao = new TaiKhoanDAO();
        TaiKhoan tk = dao.authenticate(this.tenDangNhap, this.matKhau);
        if (tk != null) {
            NhanVienDAO nvDao = new NhanVienDAO();
            for (NhanVien nv : nvDao.getAll()) {
                if (nv.getTaiKhoan() != null && tk.getMaTK().equals(nv.getTaiKhoan().getMaTK())) {
                    return nv;
                }
            }
        }
        return null;
    }

    public boolean changePassword() {
        if (maTK == null || matKhau == null) return false;
        return new TaiKhoanDAO().changePassword(maTK, matKhau);
    }

    public boolean changePassword(String newPassword) {
        if (maTK == null) return false;
        boolean ok = new TaiKhoanDAO().changePassword(maTK, newPassword);
        if (ok) {
            this.matKhau = newPassword;
        }
        return ok;
    }

    public String getMaTK() {
        return maTK;
    }

    public void setMaTK(String maTK) {
        this.maTK = maTK;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maTK);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        TaiKhoan other = (TaiKhoan) obj;
        return Objects.equals(maTK, other.maTK);
    }

    @Override
    public String toString() {
        return "TaiKhoan [maTK=" + maTK + ", tenDangNhap=" + tenDangNhap + "]";
    }
}
