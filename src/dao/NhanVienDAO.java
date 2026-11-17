package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.NhanVien;
import entity.TaiKhoan;

public class NhanVienDAO {

    private NhanVien mapResultSet(ResultSet rs) throws SQLException {
        String maNV = rs.getString("maNV");
        String hoTen = rs.getString("hoTen");
        String vaiTro = rs.getString("vaiTro");
        String sdt = rs.getString("sdt");
        String maTK = rs.getString("maTK");
        String tenDangNhap = null;
        String matKhau = null;
        try {
            tenDangNhap = rs.getString("tenDangNhap");
            matKhau = rs.getString("matKhau");
        } catch (SQLException ignored) {
        }
        String caLamViec = rs.getString("caLamViec");
        boolean isActive = rs.getBoolean("isActive");

        TaiKhoan tk = (maTK != null) ? new TaiKhoan(maTK, tenDangNhap != null ? tenDangNhap : "", matKhau != null ? matKhau : "") : null;
        return new NhanVien(maNV, hoTen, vaiTro, sdt, tk, caLamViec, isActive);
    }

    public List<NhanVien> getAll() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT nv.maNV, nv.hoTen, nv.vaiTro, nv.sdt, nv.maTK, nv.caLamViec, nv.isActive, tk.tenDangNhap "
                   + "FROM NhanVien nv LEFT JOIN TaiKhoan tk ON nv.maTK = tk.maTK ORDER BY nv.maNV";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public NhanVien getById(String maNV) {
        String sql = "SELECT nv.maNV, nv.hoTen, nv.vaiTro, nv.sdt, nv.maTK, nv.caLamViec, nv.isActive, tk.tenDangNhap "
                   + "FROM NhanVien nv LEFT JOIN TaiKhoan tk ON nv.maTK = tk.maTK WHERE nv.maNV = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public NhanVien findByUsername(String user) {
        String sql = "SELECT nv.maNV, nv.hoTen, nv.vaiTro, nv.sdt, nv.maTK, nv.caLamViec, nv.isActive, tk.tenDangNhap "
                   + "FROM NhanVien nv JOIN TaiKhoan tk ON nv.maTK = tk.maTK WHERE tk.tenDangNhap = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<NhanVien> search(String kw) {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT nv.maNV, nv.hoTen, nv.vaiTro, nv.sdt, nv.maTK, nv.caLamViec, nv.isActive, tk.tenDangNhap "
                   + "FROM NhanVien nv LEFT JOIN TaiKhoan tk ON nv.maTK = tk.maTK "
                   + "WHERE nv.maNV LIKE ? OR nv.hoTen LIKE ? OR tk.tenDangNhap LIKE ? OR nv.sdt LIKE ? ORDER BY nv.maNV";
        String pattern = "%" + (kw != null ? kw.trim() : "") + "%";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Connection con, NhanVien nv) throws SQLException {
        String sql = "INSERT INTO NhanVien (maNV, hoTen, vaiTro, sdt, maTK, caLamViec, isActive) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getHoTen());
            ps.setString(3, nv.getVaiTro());
            ps.setString(4, nv.getSdt());
            ps.setString(5, nv.getMaTK());
            ps.setString(6, nv.getCaLamViec());
            ps.setBoolean(7, nv.isActive());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean insert(NhanVien nv) {
        String sql = "INSERT INTO NhanVien (maNV, hoTen, vaiTro, sdt, maTK, caLamViec, isActive) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getHoTen());
            ps.setString(3, nv.getVaiTro());
            ps.setString(4, nv.getSdt());
            ps.setString(5, nv.getMaTK());
            ps.setString(6, nv.getCaLamViec());
            ps.setBoolean(7, nv.isActive());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Connection con, NhanVien nv) throws SQLException {
        String sql = "UPDATE NhanVien SET hoTen = ?, vaiTro = ?, sdt = ?, maTK = ?, caLamViec = ?, isActive = ? WHERE maNV = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getVaiTro());
            ps.setString(3, nv.getSdt());
            ps.setString(4, nv.getMaTK());
            ps.setString(5, nv.getCaLamViec());
            ps.setBoolean(6, nv.isActive());
            ps.setString(7, nv.getMaNV());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(NhanVien nv) {
        String sql = "UPDATE NhanVien SET hoTen = ?, vaiTro = ?, sdt = ?, maTK = ?, caLamViec = ?, isActive = ? WHERE maNV = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getVaiTro());
            ps.setString(3, nv.getSdt());
            ps.setString(4, nv.getMaTK());
            ps.setString(5, nv.getCaLamViec());
            ps.setBoolean(6, nv.isActive());
            ps.setString(7, nv.getMaNV());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Connection con, String maNV) throws SQLException {
        String sql = "DELETE FROM NhanVien WHERE maNV = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String maNV) {
        String sql = "DELETE FROM NhanVien WHERE maNV = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setActive(String maNV, boolean active) {
        String sql = "UPDATE NhanVien SET isActive = ? WHERE maNV = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setString(2, maNV);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasInvoices(Connection con, String maNV) throws SQLException {
        String sql = "SELECT TOP 1 1 FROM HoaDon WHERE maNV = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public boolean hasInvoices(String maNV) {
        String sql = "SELECT TOP 1 1 FROM HoaDon WHERE maNV = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }
}
