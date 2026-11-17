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
        if (me() == null || !me().isManager()) {
            throw new IllegalArgumentException("Bạn cần đăng nhập bằng tài khoản quản lý để thực hiện chức năng này.");
        }
    }

    public List<NhanVien> getAll() {
        if (me() == null) throw new IllegalArgumentException("Vui lòng đăng nhập trước.");
        if (!me().isManager()) {
            NhanVien mine = dao.getById(me().getMaNV());
            return mine != null ? List.of(mine) : List.of();
        }
        return dao.getAll();
    }

    public List<NhanVien> search(String kw) {
        if (me() == null) throw new IllegalArgumentException("Vui lòng đăng nhập trước.");
        if (!me().isManager()) {
            NhanVien mine = dao.getById(me().getMaNV());
            return mine != null ? List.of(mine) : List.of();
        }
        return blank(kw) ? dao.getAll() : dao.search(kw);
    }

    public void validate(NhanVien e, String tenDangNhap, String matKhau, boolean isNew) {
        if (blank(e.getMaNV())) throw new IllegalArgumentException("Mã nhân viên không được trống");
        if (blank(e.getHoTen())) throw new IllegalArgumentException("Họ tên không được trống");
        if (!blank(e.getSdt()) && !e.getSdt().trim().matches("\\d{9,11}")) {
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
        String maNV = nv.getMaNV().trim();
        String username = tenDangNhap.trim();

        if (dao.getById(maNV) != null) throw new IllegalArgumentException("Mã nhân viên đã tồn tại");
        if (taiKhoanDAO.findByUsername(username) != null) throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");

        TaiKhoan tk = new TaiKhoan("TK_" + maNV, username, matKhau);
        nv.setMaNV(maNV);
        nv.setTaiKhoan(tk);
        nv.setActive(true);

        Tx.write(con -> {
            if (!taiKhoanDAO.insert(con, tk)) throw new BusinessException("Không thể tạo tài khoản nhân viên.");
            if (!dao.insert(con, nv)) throw new BusinessException("Không thể thêm nhân viên.");
            return null;
        });
    }

    public void update(NhanVien nv, String matKhauMoi) {
        requireManager();
        validate(nv, null, null, false);
        NhanVien old = dao.getById(nv.getMaNV());
        if (old == null) throw new IllegalArgumentException("Nhân viên không tồn tại");

        // GUI không gửi maTK khi sửa; phải giữ lại tài khoản cũ để tránh ghi NULL vào cột NOT NULL.
        nv.setTaiKhoan(old.getTaiKhoan());
        Tx.write(con -> {
            if (!blank(matKhauMoi)) {
                if (nv.getTaiKhoan() == null || !taiKhoanDAO.changePassword(con, nv.getMaTK(), matKhauMoi)) {
                    throw new BusinessException("Không thể cập nhật mật khẩu tài khoản.");
                }
            }
            if (!dao.update(con, nv)) throw new BusinessException("Không thể cập nhật thông tin nhân viên.");
            return null;
        });
    }

    public void delete(String maNV) {
        requireManager();
        if (maNV == null || maNV.isBlank()) throw new IllegalArgumentException("Mã nhân viên không hợp lệ.");
        if (me().getMaNV().equals(maNV)) throw new IllegalArgumentException("Không thể xóa tài khoản đang đăng nhập");

        Tx.write(con -> {
            if (dao.hasInvoices(con, maNV)) {
                throw new BusinessException("Nhân viên đã phát sinh hóa đơn nên không thể xóa.\nHãy dùng chức năng Khóa tài khoản.");
            }
            NhanVien old = dao.getById(maNV);
            if (old == null) throw new BusinessException("Nhân viên không tồn tại.");
            if (!dao.delete(con, maNV)) throw new BusinessException("Không thể xóa nhân viên.");
            if (old.getMaTK() != null && !taiKhoanDAO.delete(con, old.getMaTK())) {
                throw new BusinessException("Không thể xóa tài khoản đăng nhập của nhân viên.");
            }
            return null;
        });
    }

    public void setActive(String maNV, boolean active) {
        requireManager();
        if (!active && me() != null && me().getMaNV().equals(maNV)) {
            throw new IllegalArgumentException("Không thể khóa tài khoản đang đăng nhập");
        }
        if (!dao.setActive(maNV, active)) throw new IllegalArgumentException("Không thể cập nhật trạng thái nhân viên.");
    }
}
