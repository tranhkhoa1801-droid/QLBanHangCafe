package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.TaiKhoan;

public class TaiKhoanDAO {

    public List<TaiKhoan> getAll() {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT maTK, tenDangNhap, matKhau FROM TaiKhoan";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new TaiKhoan(
                    rs.getString("maTK"),
                    rs.getString("tenDangNhap"),
                    rs.getString("matKhau")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public TaiKhoan getById(String maTK) {
        String sql = "SELECT maTK, tenDangNhap, matKhau FROM TaiKhoan WHERE maTK = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maTK);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TaiKhoan(
                        rs.getString("maTK"),
                        rs.getString("tenDangNhap"),
                        rs.getString("matKhau")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public TaiKhoan findByUsername(String tenDangNhap) {
        String sql = "SELECT maTK, tenDangNhap, matKhau FROM TaiKhoan WHERE tenDangNhap = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TaiKhoan(
                        rs.getString("maTK"),
                        rs.getString("tenDangNhap"),
                        rs.getString("matKhau")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public TaiKhoan authenticate(String tenDangNhap, String matKhau) {
        String sql = "SELECT maTK, tenDangNhap, matKhau FROM TaiKhoan WHERE tenDangNhap = ? AND matKhau = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TaiKhoan(
                        rs.getString("maTK"),
                        rs.getString("tenDangNhap"),
                        rs.getString("matKhau")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(TaiKhoan tk) {
        String sql = "INSERT INTO TaiKhoan (maTK, tenDangNhap, matKhau) VALUES (?, ?, ?)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tk.getMaTK());
            ps.setString(2, tk.getTenDangNhap());
            ps.setString(3, tk.getMatKhau());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(TaiKhoan tk) {
        String sql = "UPDATE TaiKhoan SET tenDangNhap = ?, matKhau = ? WHERE maTK = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tk.getTenDangNhap());
            ps.setString(2, tk.getMatKhau());
            ps.setString(3, tk.getMaTK());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean changePassword(String maTK, String matKhauMoi) {
        String sql = "UPDATE TaiKhoan SET matKhau = ? WHERE maTK = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, matKhauMoi);
            ps.setString(2, maTK);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maTK) {
        String sql = "DELETE FROM TaiKhoan WHERE maTK = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maTK);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
