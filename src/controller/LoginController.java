package controller;

import dao.NhanVienDAO;
import dao.TaiKhoanDAO;
import entity.NhanVien;
import entity.TaiKhoan;

public class LoginController {
    private static NhanVien currentUser;
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    public static NhanVien getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(NhanVien user) {
        currentUser = user;
    }

    public NhanVien login(String user, String pass) {
        if (user == null || user.isBlank() || pass == null || pass.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập tên đăng nhập và mật khẩu");
        }

        TaiKhoan tk = taiKhoanDAO.authenticate(user.trim(), pass);
        if (tk == null) {
            throw new IllegalArgumentException("Sai tên đăng nhập hoặc mật khẩu");
        }

        NhanVien nv = nhanVienDAO.findByUsername(user.trim());
        if (nv == null) {
            nv = new NhanVien("NV_TEMP", tk.getTenDangNhap(), NhanVien.QUANLY, "", tk, "Toàn thời gian", true);
        }

        if (!nv.isActive()) {
            throw new IllegalArgumentException("Tài khoản đã bị khóa, hãy liên hệ quản lý");
        }

        currentUser = nv;
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }
}
