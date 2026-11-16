package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.DanhMuc;
import entity.SanPham;

public class SanPhamDAO {

    private SanPham mapResultSet(ResultSet rs) throws SQLException {
        String maSP = rs.getString("maSP");
        String tenSP = rs.getString("tenSP");
        double giaVon = rs.getDouble("giaVon");
        double giaBan = rs.getDouble("giaBan");
        String maDM = rs.getString("maDM");
        String tenDM = "";
        try {
            tenDM = rs.getString("tenDM");
        } catch (SQLException ignored) {
        }
        DanhMuc dm = new DanhMuc(maDM, tenDM != null ? tenDM : "");
        return new SanPham(maSP, tenSP, giaVon, giaBan, dm);
    }

    public List<SanPham> getAll() {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT sp.maSP, sp.tenSP, sp.giaVon, sp.giaBan, sp.maDM, dm.tenDM "
                   + "FROM SanPham sp LEFT JOIN DanhMuc dm ON sp.maDM = dm.maDM";

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

    public SanPham getById(String maSP) {
        String sql = "SELECT sp.maSP, sp.tenSP, sp.giaVon, sp.giaBan, sp.maDM, dm.tenDM "
                   + "FROM SanPham sp LEFT JOIN DanhMuc dm ON sp.maDM = dm.maDM WHERE sp.maSP = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maSP);
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

    public List<SanPham> search(String tuKhoa) {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT sp.maSP, sp.tenSP, sp.giaVon, sp.giaBan, sp.maDM, dm.tenDM "
                   + "FROM SanPham sp LEFT JOIN DanhMuc dm ON sp.maDM = dm.maDM "
                   + "WHERE sp.maSP LIKE ? OR sp.tenSP LIKE ? OR dm.tenDM LIKE ?";
        String pattern = "%" + (tuKhoa != null ? tuKhoa.trim() : "") + "%";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
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

    public boolean insert(SanPham sp) {
        String sql = "INSERT INTO SanPham(maSP, tenSP, giaVon, giaBan, maDM) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sp.getMaSP());
            ps.setString(2, sp.getTenSP());
            ps.setDouble(3, sp.getGiaVon());
            ps.setDouble(4, sp.getGiaBan());
            ps.setString(5, sp.getDanhMuc() != null ? sp.getDanhMuc().getMaDM() : null);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(SanPham sp) {
        String sql = "UPDATE SanPham SET tenSP = ?, giaVon = ?, giaBan = ?, maDM = ? WHERE maSP = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sp.getTenSP());
            ps.setDouble(2, sp.getGiaVon());
            ps.setDouble(3, sp.getGiaBan());
            ps.setString(4, sp.getDanhMuc() != null ? sp.getDanhMuc().getMaDM() : null);
            ps.setString(5, sp.getMaSP());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maSP) {
        String sql = "DELETE FROM SanPham WHERE maSP = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maSP);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
