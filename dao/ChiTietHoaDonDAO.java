package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.ChiTietHoaDon;

/** Truy cập bảng ChiTietHoaDon. Khóa chính (maHD, maSP). */
public class ChiTietHoaDonDAO {

    private static ChiTietHoaDonDAO instance;

    public static synchronized ChiTietHoaDonDAO getInstance() {
        if (instance == null) {
            instance = new ChiTietHoaDonDAO();
        }
        return instance;
    }

    public List<ChiTietHoaDon> listByInvoice(Connection con, String maHD) throws SQLException {
        List<ChiTietHoaDon> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT ct.maHD, ct.maSP, sp.tenSP, ct.soLuong, ct.giaBanHienTai, ct.giaVonHienTai "
                + "FROM ChiTietHoaDon ct JOIN SanPham sp ON sp.maSP = ct.maSP "
                + "WHERE ct.maHD = ? ORDER BY sp.tenSP")) {
            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ChiTietHoaDon(rs.getString(1), rs.getString(2), rs.getString(3),
                            rs.getInt(4), rs.getDouble(5), rs.getDouble(6)));
                }
            }
        }
        return list;
    }

    /** Thêm món; nếu món đã có thì cộng dồn số lượng (giữ nguyên giá lúc thêm lần đầu). */
    public void addOrIncrease(Connection con, String maHD, String maSP, int soLuong,
                              double giaBan, double giaVon) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE ChiTietHoaDon SET soLuong = soLuong + ? WHERE maHD = ? AND maSP = ?")) {
            ps.setInt(1, soLuong);
            ps.setString(2, maHD);
            ps.setString(3, maSP);
            if (ps.executeUpdate() > 0) {
                return;
            }
        }
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO ChiTietHoaDon(maHD, maSP, soLuong, giaBanHienTai, giaVonHienTai) VALUES (?,?,?,?,?)")) {
            ps.setString(1, maHD);
            ps.setString(2, maSP);
            ps.setInt(3, soLuong);
            ps.setDouble(4, giaBan);
            ps.setDouble(5, giaVon);
            ps.executeUpdate();
        }
    }

    public boolean updateQuantity(Connection con, String maHD, String maSP, int soLuong) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE ChiTietHoaDon SET soLuong = ? WHERE maHD = ? AND maSP = ?")) {
            ps.setInt(1, soLuong);
            ps.setString(2, maHD);
            ps.setString(3, maSP);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(Connection con, String maHD, String maSP) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM ChiTietHoaDon WHERE maHD = ? AND maSP = ?")) {
            ps.setString(1, maHD);
            ps.setString(2, maSP);
            return ps.executeUpdate() > 0;
        }
    }

    public int countByInvoice(Connection con, String maHD) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*) FROM ChiTietHoaDon WHERE maHD = ?")) {
            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}
