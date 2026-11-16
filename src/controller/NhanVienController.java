package controller;

import java.util.List;

import dao.NhanVienDAO;
import dao.TaiKhoanDAO;
import entity.NhanVien;
import entity.TaiKhoan;

public class NhanVienController {
    private final NhanVienDAO dao = new NhanVienDAO();
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    private static boolean blank(String s) {
        return s == null || s.isBlank();
    }

    private static NhanVien me() {
        return LoginController.getCurrentUser();
    }

    private static void requireManager() {
        if (me() != null && !me().isManager()) {
            throw new IllegalArgumentException("Bạn không có quyền thực hiện chức năng này");
        }
    }

    public List<NhanVien> getAll() {
        return dao.getAll();
    }

    public List<NhanVien> search(String kw) {
        if (me() != null && !me().isManager()) {
            NhanVien mine = dao.getById(me().getMaNV());
            return mine != null ? List.of(mine) : List.of();
        }
        return blank(kw) ? dao.getAll() : dao.search(kw);
    }

    public void validate(NhanVien e, String tenDangNhap, String matKhau, boolean isNew) {
        if (blank(e.getMaNV())) throw new IllegalArgumentException("Mã nhân viên không được trống");
        if (blank(e.getHoTen())) throw new IllegalArgumentException("Họ tên không được trống");
        if (!blank(e.getsDT()) && !e.getsDT().trim().matches("\\d{9,11}")) {
            throw new IllegalArgumentException("Số điện thoại phải gồm 9-11 chữ số");
        }
        if (isNew) {
            if (blank(tenDangNhap)) throw new IllegalArgumentException("Tên đăng nhập không được trống");
            if (blank(matKhau)) throw new IllegalArgumentException("Mật khẩu không được trống");
            if (matKhau.length() < 4) throw new IllegalArgumentException("Mật khẩu phải có ít nhất 4 ký tự");
        }
    }

    public void add(NhanVien nv, String tenDangNhap, String matKhau) {
        requireManager();
        validate(nv, tenDangNhap, matKhau, true);

        if (dao.getById(nv.getMaNV().trim()) != null) {
            throw new IllegalArgumentException("Mã nhân viên đã tồn tại");
        }
        if (taiKhoanDAO.findByUsername(tenDangNhap.trim()) != null) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }

        String maTK = "TK_" + nv.getMaNV().trim();
        TaiKhoan tk = new TaiKhoan(maTK, tenDangNhap.trim(), matKhau);
        taiKhoanDAO.insert(tk);

        nv.setTaiKhoan(tk);
        nv.setActive(true);
        dao.insert(nv);
    }

    public void update(NhanVien nv, String matKhauMoi) {
        requireManager();
        validate(nv, null, null, false);

        NhanVien old = dao.getById(nv.getMaNV());
        if (old == null) {
            throw new IllegalArgumentException("Nhân viên không tồn tại");
        }

        if (nv.getTaiKhoan() != null && !blank(matKhauMoi)) {
            taiKhoanDAO.changePassword(nv.getTaiKhoan().getMaTK(), matKhauMoi);
        }

        dao.update(nv);
    }

    public void delete(String maNV) {
        requireManager();
        if (me() != null && me().getMaNV().equals(maNV)) {
            throw new IllegalArgumentException("Không thể xóa tài khoản đang đăng nhập");
        }
        if (dao.hasInvoices(maNV)) {
            throw new IllegalArgumentException("Nhân viên đã phát sinh hóa đơn nên không thể xóa.\nHãy dùng chức năng Khóa tài khoản.");
        }
        NhanVien old = dao.getById(maNV);
        dao.delete(maNV);
        if (old != null && old.getMaTK() != null) {
            taiKhoanDAO.delete(old.getMaTK());
        }
    }

    public void setActive(String maNV, boolean active) {
        requireManager();
        if (!active && me() != null && me().getMaNV().equals(maNV)) {
            throw new IllegalArgumentException("Không thể khóa tài khoản đang đăng nhập");
        }
        dao.setActive(maNV, active);
    }
}
