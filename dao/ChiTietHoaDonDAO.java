package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.ChiTietHoaDon;

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
		String sql = "SELECT ct.maHD, ct.maSP, sp.tenSP, ct.soLuong, ct.giaBanHienTai, ct.giaVonHienTai "
				+ "FROM ChiTietHoaDon ct JOIN SanPham sp ON sp.maSP = ct.maSP "
				+ "WHERE ct.maHD = ? ORDER BY sp.tenSP";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maHD);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(new ChiTietHoaDon(
							rs.getString("maHD"),
							rs.getString("maSP"),
							rs.getString("tenSP"),
							rs.getInt("soLuong"),
							rs.getDouble("giaBanHienTai"),
							rs.getDouble("giaVonHienTai")));
				}
			}
		}
		return list;
	}

	public void addOrIncrease(Connection con, String maHD, String maSP, int soLuong, double giaBan, double giaVon)
			throws SQLException {
		String sqlUpdate = "UPDATE ChiTietHoaDon SET soLuong = soLuong + ? WHERE maHD = ? AND maSP = ?";
		try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
			ps.setInt(1, soLuong);
			ps.setString(2, maHD);
			ps.setString(3, maSP);
			if (ps.executeUpdate() > 0) {
				return;
			}
		}

		String sqlInsert = "INSERT INTO ChiTietHoaDon(maHD, maSP, soLuong, giaBanHienTai, giaVonHienTai) "
				+ "VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
			ps.setString(1, maHD);
			ps.setString(2, maSP);
			ps.setInt(3, soLuong);
			ps.setDouble(4, giaBan);
			ps.setDouble(5, giaVon);
			ps.executeUpdate();
		}
	}

	public boolean updateQuantity(Connection con, String maHD, String maSP, int soLuong) throws SQLException {
		String sql = "UPDATE ChiTietHoaDon SET soLuong = ? WHERE maHD = ? AND maSP = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, soLuong);
			ps.setString(2, maHD);
			ps.setString(3, maSP);
			return ps.executeUpdate() > 0;
		}
	}

	public boolean delete(Connection con, String maHD, String maSP) throws SQLException {
		String sql = "DELETE FROM ChiTietHoaDon WHERE maHD = ? AND maSP = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maHD);
			ps.setString(2, maSP);
			return ps.executeUpdate() > 0;
		}
	}

	public void deleteAllByInvoice(Connection con, String maHD) throws SQLException {
		String sql = "DELETE FROM ChiTietHoaDon WHERE maHD = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maHD);
			ps.executeUpdate();
		}
	}

	public int countByInvoice(Connection con, String maHD) throws SQLException {
		String sql = "SELECT COUNT(*) FROM ChiTietHoaDon WHERE maHD = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maHD);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		}
		return 0;
	}

	public double sumByInvoice(Connection con, String maHD) throws SQLException {
		String sql = "SELECT ISNULL(SUM(soLuong * giaBanHienTai), 0) FROM ChiTietHoaDon WHERE maHD = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maHD);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getDouble(1);
				}
			}
		}
		return 0;
	}
}
