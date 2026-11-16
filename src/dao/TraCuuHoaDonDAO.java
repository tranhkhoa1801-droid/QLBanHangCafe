package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import connectDB.ConnectDB;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.NhanVien;

public class TraCuuHoaDonDAO {

    public static record ChiTietHoaDonTraCuu(HoaDon hoaDon, NhanVien nhanVien, List<ChiTietHoaDon> matHang) {}

    public List<ChiTietHoaDonTraCuu> traCuuHoaDon(String tuKhoa, LocalDateTime tu, LocalDateTime den)
            throws SQLException {
        String sql = "SELECT hd.maHD, hd.thoiGianTao, hd.ban, hd.maNV, hd.phuongThucThanhToan, hd.trangThai, hd.tongTien, nv.hoTen "
                + "FROM HoaDon hd JOIN NhanVien nv ON nv.maNV = hd.maNV WHERE 1=1"
                + (tuKhoa == null || tuKhoa.isEmpty() ? "" : " AND (hd.maHD LIKE ? OR hd.ban LIKE ? OR nv.hoTen LIKE ?)")
                + (tu == null ? "" : " AND hd.thoiGianTao >= ?")
                + (den == null ? "" : " AND hd.thoiGianTao < ?")
                + " ORDER BY hd.thoiGianTao DESC";
        List<ChiTietHoaDonTraCuu> ketQua = new ArrayList<>();
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int i = 1;
            if (tuKhoa != null && !tuKhoa.isEmpty()) {
                String pattern = "%" + tuKhoa + "%";
                ps.setString(i++, pattern);
                ps.setString(i++, pattern);
                ps.setString(i++, pattern);
            }
            if (tu != null) ps.setTimestamp(i++, Timestamp.valueOf(tu));
            if (den != null) ps.setTimestamp(i++, Timestamp.valueOf(den));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ketQua.add(new ChiTietHoaDonTraCuu(mapHoaDon(rs), mapNhanVien(rs), Collections.emptyList()));
                }
            }
        }
        return ketQua;
    }

    public ChiTietHoaDonTraCuu xemChiTietHoaDon(String maHoaDon) throws SQLException {
        String sql = "SELECT hd.maHD, hd.thoiGianTao, hd.ban, hd.maNV, hd.phuongThucThanhToan, hd.trangThai, hd.tongTien, "
                + "nv.hoTen, ct.maSP, sp.tenSP AS tenSanPham, ct.soLuong, ct.giaBanHienTai, ct.giaVonHienTai "
                + "FROM HoaDon hd "
                + "JOIN NhanVien nv ON nv.maNV = hd.maNV "
                + "LEFT JOIN ChiTietHoaDon ct ON ct.maHD = hd.maHD "
                + "LEFT JOIN SanPham sp ON sp.maSP = ct.maSP WHERE hd.maHD = ? ORDER BY sp.tenSP";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                HoaDon hoaDon = mapHoaDon(rs);
                NhanVien nhanVien = mapNhanVien(rs);
                List<ChiTietHoaDon> matHang = new ArrayList<>();
                do {
                    if (rs.getString("maSP") != null) {
                        matHang.add(new ChiTietHoaDon(rs.getString("maHD"), rs.getString("maSP"),
                                rs.getString("tenSanPham"), rs.getInt("soLuong"),
                                rs.getDouble("giaBanHienTai"), rs.getDouble("giaVonHienTai")));
                    }
                } while (rs.next());
                return new ChiTietHoaDonTraCuu(hoaDon, nhanVien, matHang);
            }
        }
    }

    private HoaDon mapHoaDon(ResultSet rs) throws SQLException {
        return new HoaDon(rs.getString("maHD"), rs.getTimestamp("thoiGianTao"), rs.getString("ban"),
                rs.getString("maNV"), rs.getString("phuongThucThanhToan"), rs.getString("trangThai"),
                rs.getDouble("tongTien"));
    }

    private NhanVien mapNhanVien(ResultSet rs) throws SQLException {
        NhanVien nv = new NhanVien();
        nv.setMaNV(rs.getString("maNV"));
        nv.setHoTen(rs.getString("hoTen"));
        return nv;
    }
}
