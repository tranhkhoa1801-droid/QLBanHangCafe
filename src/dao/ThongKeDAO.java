package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.SanPham;

public class ThongKeDAO {

    public static record DuLieuDoanhThu(double doanhThu, int moc) {}
    public static record SanPhamBanChay(SanPham sanPham, int soLuongDaBan) {}

    public double tinhDoanhThu(LocalDateTime tu, LocalDateTime den) throws SQLException {
        String sql = "SELECT COALESCE(SUM(tongTien), 0) FROM HoaDon WHERE trangThai = N'Đã thanh toán' AND thoiGianTao >= ? AND thoiGianTao < ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ganKhoang(ps, tu, den);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0;
    }

    public int demSoLuongDon(LocalDateTime tu, LocalDateTime den) throws SQLException {
        String sql = "SELECT COUNT(*) FROM HoaDon WHERE trangThai = N'Đã thanh toán' AND thoiGianTao >= ? AND thoiGianTao < ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ganKhoang(ps, tu, den);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public List<SanPhamBanChay> phanTichSanPhamBanChay(LocalDateTime tu, LocalDateTime den) throws SQLException {
        String sql = "SELECT sp.maSP, sp.tenSP, sp.giaBan, SUM(ct.soLuong) AS daBan "
                + "FROM ChiTietHoaDon ct JOIN SanPham sp ON sp.maSP = ct.maSP "
                + "JOIN HoaDon hd ON hd.maHD = ct.maHD "
                + "WHERE hd.trangThai = N'Đã thanh toán' AND hd.thoiGianTao >= ? AND hd.thoiGianTao < ? "
                + "GROUP BY sp.maSP, sp.tenSP, sp.giaBan ORDER BY daBan DESC";
        List<SanPhamBanChay> ketQua = new ArrayList<>();
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ganKhoang(ps, tu, den);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SanPham sp = new SanPham(rs.getString("maSP"), rs.getString("tenSP"), 0,
                            rs.getDouble("giaBan"), null);
                    ketQua.add(new SanPhamBanChay(sp, rs.getInt("daBan")));
                }
            }
        }
        return ketQua;
    }

    public List<DuLieuDoanhThu> layDuLieuDoanhThu(LocalDateTime tu, LocalDateTime den, boolean theoThang)
            throws SQLException {
        String moc = theoThang ? "DAY(thoiGianTao)" : "DATEPART(hour, thoiGianTao)";
        String sql = "SELECT " + moc + " AS moc, SUM(tongTien) AS doanhThu FROM HoaDon "
                + "WHERE trangThai = N'Đã thanh toán' AND thoiGianTao >= ? AND thoiGianTao < ? "
                + "GROUP BY " + moc + " ORDER BY moc";
        List<DuLieuDoanhThu> ketQua = new ArrayList<>();
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ganKhoang(ps, tu, den);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ketQua.add(new DuLieuDoanhThu(rs.getDouble("doanhThu"), rs.getInt("moc")));
                }
            }
        }
        return ketQua;
    }

    private void ganKhoang(PreparedStatement ps, LocalDateTime tu, LocalDateTime den) throws SQLException {
        ps.setTimestamp(1, Timestamp.valueOf(tu));
        ps.setTimestamp(2, Timestamp.valueOf(den));
    }
}
