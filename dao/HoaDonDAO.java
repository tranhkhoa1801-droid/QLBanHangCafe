package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import entity.HoaDon;

public class HoaDonDAO {

	public static final String DANG_MO = "Chưa thanh toán";
	public static final String DA_THANH_TOAN = "Đã thanh toán";
	public static final String DA_HUY = "Đã hủy";

	private static HoaDonDAO instance;

	public static synchronized HoaDonDAO getInstance() {
		if (instance == null) {
			instance = new HoaDonDAO();
		}
		return instance;
	}

	public String findOpenInvoiceId(Connection con, String ban) throws SQLException {
		String sql = "SELECT TOP 1 maHD FROM HoaDon WHERE ban = ? AND trangThai = ? ORDER BY thoiGianTao DESC";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, ban);
			ps.setString(2, DANG_MO);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getString("maHD");
				}
			}
		}
		return null;
	}

	public Set<String> findOpenTables(Connection con) throws SQLException {
		Set<String> set = new HashSet<>();
		String sql = "SELECT ban FROM HoaDon WHERE trangThai = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, DANG_MO);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					set.add(rs.getString("ban"));
				}
			}
		}
		return set;
	}

	public String taoMaHoaDonMoi(Connection con) throws SQLException {
		String sql = "SELECT ISNULL(MAX(TRY_CAST(SUBSTRING(maHD, 3, 18) AS INT)), 0) + 1 "
				+ "FROM HoaDon WITH (UPDLOCK, HOLDLOCK) WHERE maHD LIKE 'HD%'";
		try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
			if (rs.next()) {
				return String.format("HD%03d", rs.getInt(1));
			}
		}
		return "HD001";
	}

	public void insertOpen(Connection con, String maHD, String ban, String maNV) throws SQLException {
		String sql = "INSERT INTO HoaDon(maHD, thoiGianTao, ban, maNV, trangThai, tongTien) "
				+ "VALUES (?, GETDATE(), ?, ?, ?, 0)";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maHD);
			ps.setString(2, ban);
			ps.setString(3, maNV);
			ps.setString(4, DANG_MO);
			ps.executeUpdate();
		}
	}

	public boolean markPaid(Connection con, String maHD, String phuongThucThanhToan, double tongTien)
			throws SQLException {
		String sql = "UPDATE HoaDon SET phuongThucThanhToan = ?, trangThai = ?, tongTien = ? "
				+ "WHERE maHD = ? AND trangThai = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, phuongThucThanhToan);
			ps.setString(2, DA_THANH_TOAN);
			ps.setDouble(3, tongTien);
			ps.setString(4, maHD);
			ps.setString(5, DANG_MO);
			return ps.executeUpdate() > 0;
		}
	}

	public boolean cancel(Connection con, String maHD) throws SQLException {
		String sql = "UPDATE HoaDon SET trangThai = ? WHERE maHD = ? AND trangThai = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, DA_HUY);
			ps.setString(2, maHD);
			ps.setString(3, DANG_MO);
			return ps.executeUpdate() > 0;
		}
	}

	public void deleteOpen(Connection con, String maHD) throws SQLException {
		String sql = "DELETE FROM HoaDon WHERE maHD = ? AND trangThai = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maHD);
			ps.setString(2, DANG_MO);
			ps.executeUpdate();
		}
	}

	public HoaDon getById(Connection con, String maHD) throws SQLException {
		String sql = "SELECT maHD, thoiGianTao, ban, maNV, phuongThucThanhToan, trangThai, tongTien "
				+ "FROM HoaDon WHERE maHD = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maHD);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return new HoaDon(
							rs.getString("maHD"),
							rs.getTimestamp("thoiGianTao"),
							rs.getString("ban"),
							rs.getString("maNV"),
							rs.getString("phuongThucThanhToan"),
							rs.getString("trangThai"),
							rs.getDouble("tongTien"));
				}
			}
		}
		return null;
	}
}
