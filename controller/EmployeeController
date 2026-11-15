package controller;

import dao.EmployeeDAO;
import entity.Employee;
import java.util.List;

/** Nghiệp vụ nhân viên: kiểm tra dữ liệu, phân quyền, gọi DAO. */
public class EmployeeController {
    private final EmployeeDAO dao = new EmployeeDAO();

    private static boolean blank(String s) { return s == null || s.isBlank(); }

    private static Employee me() { return LoginController.getCurrentUser(); }

    private static void requireManager() {
        if (me() == null || !me().isManager()) throw new IllegalArgumentException("Bạn không có quyền thực hiện chức năng này");
    }

    /** Quản lý: xem/tìm tất cả. Nhân viên: chỉ thấy thông tin của chính mình. */
    public List<Employee> search(String kw) {
        if (!me().isManager()) return List.of(dao.findById(me().maNV()));
        return blank(kw) ? dao.getAll() : dao.search(kw);
    }

    private void validate(Employee e, boolean isNew) {
        if (blank(e.maNV())) throw new IllegalArgumentException("Mã nhân viên không được trống");
        if (blank(e.hoTen())) throw new IllegalArgumentException("Họ tên không được trống");
        if (blank(e.tenDangNhap())) throw new IllegalArgumentException("Tên đăng nhập không được trống");
        if (!blank(e.sdt()) && !e.sdt().trim().matches("\\d{9,11}"))
            throw new IllegalArgumentException("Số điện thoại phải gồm 9-11 chữ số");
        if (!Employee.QUANLY.equals(e.vaiTro()) && !Employee.NHANVIEN.equals(e.vaiTro()))
            throw new IllegalArgumentException("Vai trò không hợp lệ");
        if (isNew && blank(e.matKhau())) throw new IllegalArgumentException("Mật khẩu không được trống");
        if (!blank(e.matKhau()) && e.matKhau().length() < 4)
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 4 ký tự");
    }

    private Employee clean(Employee e, String pass, boolean active) {
        return new Employee(e.maNV().trim(), e.hoTen().trim(), e.sdt() == null ? "" : e.sdt().trim(),
                e.diaChi() == null ? "" : e.diaChi().trim(), e.tenDangNhap().trim(), pass, e.vaiTro(), active);
    }

    public void add(Employee e) {
        requireManager();
        validate(e, true);
        if (dao.findById(e.maNV().trim()) != null) throw new IllegalArgumentException("Mã nhân viên đã tồn tại");
        if (dao.findByUsername(e.tenDangNhap().trim()) != null) throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        dao.insert(clean(e, e.matKhau(), true));
    }

    /** Mật khẩu để trống = giữ nguyên mật khẩu cũ. */
    public void update(Employee e) {
        requireManager();
        validate(e, false);
        Employee old = dao.findById(e.maNV());
        if (old == null) throw new IllegalArgumentException("Nhân viên không tồn tại");
        Employee same = dao.findByUsername(e.tenDangNhap().trim());
        if (same != null && !same.maNV().equals(e.maNV())) throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        dao.update(clean(e, blank(e.matKhau()) ? old.matKhau() : e.matKhau(), old.hoatDong()));
    }

    public void delete(String maNV) {
        requireManager();
        if (me().maNV().equals(maNV)) throw new IllegalArgumentException("Không thể xóa tài khoản đang đăng nhập");
        if (dao.hasInvoices(maNV))
            throw new IllegalArgumentException("Nhân viên đã phát sinh hóa đơn nên không thể xóa.\nHãy dùng chức năng Khóa tài khoản.");
        dao.delete(maNV);
    }

    public void setActive(String maNV, boolean active) {
        requireManager();
        if (!active && me().maNV().equals(maNV)) throw new IllegalArgumentException("Không thể khóa tài khoản đang đăng nhập");
        dao.setActive(maNV, active);
    }
}
