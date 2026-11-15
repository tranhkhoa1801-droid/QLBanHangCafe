package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.DanhMuc;
import entity.SanPham;

public class SanPhamDAO {

    public List<SanPham> getAll() {
        List<SanPham> list = new ArrayList<>();

        String sql = "SELECT * FROM SanPham";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String maSP = rs.getString("maSP");
                String tenSP = rs.getString("tenSP");
                double giaVon = rs.getDouble("giaVon");
                double giaBan = rs.getDouble("giaBan");
                String maDM = rs.getString("maDM");

                // Tạo đối tượng DanhMuc truyền vào SanPham
                DanhMuc dm = new DanhMuc(maDM, ""); 

                list.add(new SanPham(maSP, tenSP, giaVon, giaBan, dm));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insert(SanPham sp) {
        String sql = "INSERT INTO SanPham"
                   + "(maSP, tenSP, giaVon, giaBan, maDM) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sp.getMaSP());
            ps.setString(2, sp.getTenSP());
            ps.setDouble(3, sp.getGiaVon());
            ps.setDouble(4, sp.getGiaBan());
            // Lấy mã danh mục từ đối tượng danhMuc bên trong SanPham
            ps.setString(5, sp.getDanhMuc() != null ? sp.getDanhMuc().getMaDM() : null);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(SanPham sp) {
        String sql = "UPDATE SanPham SET "
                   + "tenSP = ?, giaVon = ?, giaBan = ?, maDM = ? "
                   + "WHERE maSP = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sp.getTenSP());
            ps.setDouble(2, sp.getGiaVon());
            ps.setDouble(3, sp.getGiaBan());
            ps.setString(4, sp.getDanhMuc() != null ? sp.getDanhMuc().getMaDM() : null);
            ps.setString(5, sp.getMaSP());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
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

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<SanPham> search(String tuKhoa) {
        List<SanPham> list = new ArrayList<>();

        String sql = "SELECT * FROM SanPham "
                   + "WHERE maSP LIKE ? OR tenSP LIKE ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + tuKhoa + "%");
            ps.setString(2, "%" + tuKhoa + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maSP = rs.getString("maSP");
                    String tenSP = rs.getString("tenSP");
                    double giaVon = rs.getDouble("giaVon");
                    double giaBan = rs.getDouble("giaBan");
                    String maDM = rs.getString("maDM");

                    DanhMuc dm = new DanhMuc(maDM, "");

                    list.add(new SanPham(maSP, tenSP, giaVon, giaBan, dm));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
