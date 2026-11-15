package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import entity.HoaDon;

/** Truy cập bảng HoaDon. Mọi method nhận Connection để Tx điều khiển transaction. */
public class HoaDonDAO {

    public static final String DANG_MO = "Chưa thanh toán";

    private static HoaDonDAO instance;

    public static synchronized HoaDonDAO getInstance() {
        if (instance == null) {
            instance = new HoaDonDAO();
        }
        return instance;
    }

    /** Mã hóa đơn đang mở của bàn, null nếu bàn đang trống. */
    public String findOpenInvoiceId(Connection con, String ban) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT TOP 1 maHD FROM HoaDon WHERE ban = ? AND trangThai = N'Chưa thanh toán' ORDER BY thoiGianTao DESC")) {
            ps.setString(1, ban);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    /** Tên các bàn đang có hóa đơn mở (để hiển thị "Có khách"). */
    public Set<String> findOpenTables(Connection con) throws SQLException {
        Set<String> set = new HashSet<>();
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT ban FROM HoaDon WHERE trangThai = N'Chưa thanh toán'");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                set.add(rs.getString(1));
            }
        }
        return set;
    }

    /** Sinh mã HD001, HD002... Phải gọi trong transaction; UPDLOCK + HOLDLOCK chống 2 máy sinh trùng. */
    public String taoMaHoaDonMoi(Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT ISNULL(MAX(TRY_CAST(SUBSTRING(maHD, 3, 18) AS INT)), 0) + 1 "
                + "FROM HoaDon WITH (UPDLOCK, HOLDLOCK) WHERE maHD LIKE 'HD%'");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return String.format("HD%03d", rs.getInt(1));
        }
    }

    /** Tạo hóa đơn mới ở trạng thái đang mở. */
    public void insertOpen(Connection con, String maHD, String ban, String maNV) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO HoaDon(maHD, thoiGianTao, ban, maNV, trangThai, tongTien) "
                + "VALUES (?, GETDATE(), ?, ?, N'Chưa thanh toán', 0)")) {
            ps.setString(1, maHD);
            ps.setString(2, ban);
            ps.setString(3, maNV);
            ps.executeUpdate();
        }
    }

    /** Thanh toán: stored procedure tự tính tổng từ chi tiết, áp giảm giá, đổi trạng thái. */
    public void markPaid(Connection con, String maHD, int phanTramGiam, String pttt) throws SQLException {
        try (CallableStatement cs = con.prepareCall("{call sp_ThanhToanHoaDon(?,?,?)}")) {
            cs.setString(1, maHD);
            cs.setInt(2, phanTramGiam);
            cs.setString(3, pttt);
            cs.execute();
        }
    }

    /** Hủy hóa đơn đang mở (giữ lịch sử, trạng thái 'Đã hủy'). */
    public void cancel(Connection con, String maHD) throws SQLException {
        try (CallableStatement cs = con.prepareCall("{call sp_HuyHoaDon(?)}")) {
            cs.setString(1, maHD);
            cs.execute();
        }
    }

    /** Xóa hóa đơn đang mở (dùng khi hết món); trigger tự xóa chi tiết còn sót. */
    public void deleteOpen(Connection con, String maHD) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM HoaDon WHERE maHD = ? AND trangThai = N'Chưa thanh toán'")) {
            ps.setString(1, maHD);
            ps.executeUpdate();
        }
    }

    public HoaDon getById(Connection con, String maHD) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT maHD, thoiGianTao, ban, maNV, phuongThucThanhToan, trangThai, tongTien "
                + "FROM HoaDon WHERE maHD = ?")) {
            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new HoaDon(rs.getString("maHD"), rs.getTimestamp("thoiGianTao"), rs.getString("ban"),
                        rs.getString("maNV"), rs.getString("phuongThucThanhToan"),
                        rs.getString("trangThai"), rs.getDouble("tongTien"));
            }
        }
    }
}
