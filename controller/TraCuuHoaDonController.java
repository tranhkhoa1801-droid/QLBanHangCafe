package controller;

import dao.TraCuuHoaDonDAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.NhanVien;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TraCuuHoaDonController {
    public record ChiTietHoaDonTraCuu(HoaDon hoaDon, NhanVien nhanVien, List<ChiTietHoaDon> matHang) {}

    private final TraCuuHoaDonDAO traCuuHoaDonDAO = new TraCuuHoaDonDAO();

    public List<ChiTietHoaDonTraCuu> traCuuHoaDon(String maHoaDonCanTim, LocalDate ngayBatDau,
            LocalDate ngayKetThuc) throws SQLException {
        String tuKhoa = maHoaDonCanTim == null ? "" : maHoaDonCanTim.trim();
        if ((ngayBatDau == null) != (ngayKetThuc == null)) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ cả ngày bắt đầu và ngày kết thúc.");
        }
        if (ngayBatDau == null && tuKhoa.isEmpty()) {
            ngayBatDau = LocalDate.now();
            ngayKetThuc = ngayBatDau;
        }
        if (ngayBatDau != null) {
            LocalDate homNay = LocalDate.now();
            if (ngayBatDau.getYear() <= 1900 || ngayKetThuc.getYear() <= 1900)
                throw new IllegalArgumentException("Năm lọc phải lớn hơn 1900.");
            if (ngayBatDau.isAfter(homNay) || ngayKetThuc.isAfter(homNay))
                throw new IllegalArgumentException("Ngày lọc không được sau ngày hôm nay.");
            if (ngayBatDau.isAfter(ngayKetThuc))
                throw new IllegalArgumentException("Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc.");
        }
        LocalDateTime tu = ngayBatDau == null ? null : ngayBatDau.atStartOfDay();
        LocalDateTime den = ngayKetThuc == null ? null : ngayKetThuc.plusDays(1).atStartOfDay();
        return traCuuHoaDonDAO.traCuuHoaDon(tuKhoa, tu, den);
    }

    public ChiTietHoaDonTraCuu xemChiTietHoaDon(String maHoaDon) throws SQLException {
        if (maHoaDon == null || maHoaDon.isBlank()) throw new IllegalArgumentException("Mã hóa đơn không hợp lệ.");
        return traCuuHoaDonDAO.xemChiTietHoaDon(maHoaDon.trim());
    }
}
