package entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import connectDB.ConnectDB;
import dao.HoaDonDAO;

public class HoaDon {
    public static final String CHUA_THANH_TOAN = "Chưa thanh toán";
    public static final String DA_THANH_TOAN = "Đã thanh toán";
    public static final String DA_HUY = "Đã hủy";

    private String maHD;
    private Date thoiGianTao;
    private String ban;
    private NhanVien maNV;
    private String phuongThucThanhToan;
    private String trangThai;
    private double tongTien;

    public HoaDon() {
        this.trangThai = CHUA_THANH_TOAN;
    }

    public HoaDon(String maHD) {
        this.maHD = maHD;
        this.trangThai = CHUA_THANH_TOAN;
    }

    public HoaDon(String maHD, Date thoiGianTao, String ban, NhanVien maNV, String phuongThucThanhToan,
            String trangThai, double tongTien) {
        this.maHD = maHD;
        this.thoiGianTao = thoiGianTao;
        this.ban = ban;
        this.maNV = maNV;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.trangThai = trangThai;
        this.tongTien = tongTien;
    }

    public HoaDon(String maHD, Date thoiGianTao, String ban, String maNV, String phuongThucThanhToan,
            String trangThai, double tongTien) {
        this(maHD, thoiGianTao, ban, maNV != null ? new NhanVien(maNV) : null, phuongThucThanhToan, trangThai, tongTien);
    }

    // Class diagram methods
    public boolean isPaid() {
        return DA_THANH_TOAN.equalsIgnoreCase(trangThai);
    }

    public boolean save() {
        String empId = (maNV != null) ? maNV.getMaNV() : null;
        String checkSql = "SELECT maHD FROM HoaDon WHERE maHD = ?";
        String insertSql = "INSERT INTO HoaDon(maHD, thoiGianTao, ban, maNV, phuongThucThanhToan, trangThai, tongTien) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE HoaDon SET thoiGianTao = ?, ban = ?, maNV = ?, phuongThucThanhToan = ?, "
                + "trangThai = ?, tongTien = ? WHERE maHD = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement checkPs = con.prepareStatement(checkSql)) {
            checkPs.setString(1, maHD);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    try (PreparedStatement updatePs = con.prepareStatement(updateSql)) {
                        updatePs.setTimestamp(1, thoiGianTao != null ? new Timestamp(thoiGianTao.getTime())
                                : new Timestamp(System.currentTimeMillis()));
                        updatePs.setString(2, ban);
                        updatePs.setString(3, empId);
                        updatePs.setString(4, phuongThucThanhToan);
                        updatePs.setString(5, trangThai);
                        updatePs.setDouble(6, tongTien);
                        updatePs.setString(7, maHD);
                        return updatePs.executeUpdate() > 0;
                    }
                } else {
                    try (PreparedStatement insertPs = con.prepareStatement(insertSql)) {
                        insertPs.setString(1, maHD);
                        insertPs.setTimestamp(2, thoiGianTao != null ? new Timestamp(thoiGianTao.getTime())
                                : new Timestamp(System.currentTimeMillis()));
                        insertPs.setString(3, ban);
                        insertPs.setString(4, empId);
                        insertPs.setString(5, phuongThucThanhToan);
                        insertPs.setString(6, trangThai);
                        insertPs.setDouble(7, tongTien);
                        return insertPs.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<HoaDon> getAll() {
        return HoaDonDAO.getInstance().getAll();
    }

    public List<HoaDon> findByKeyWord() {
        return HoaDonDAO.getInstance().findByKeyWord("");
    }

    public List<HoaDon> findByKeyWord(String keyword) {
        return HoaDonDAO.getInstance().findByKeyWord(keyword);
    }

    // Getters and Setters matching Class Diagram
    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public Date getThoiGianTao() {
        return thoiGianTao;
    }

    public void setThoiGianTao(Date thoiGianTao) {
        this.thoiGianTao = thoiGianTao;
    }

    public String getBan() {
        return ban;
    }

    public void setBan(String ban) {
        this.ban = ban;
    }

    public NhanVien getMaNV() {
        return maNV;
    }

    public void setMaNV(NhanVien maNV) {
        this.maNV = maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV != null ? new NhanVien(maNV) : null;
    }

    public NhanVien getNhanVien() {
        return maNV;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.maNV = nhanVien;
    }

    public String getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHD);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        HoaDon other = (HoaDon) obj;
        return Objects.equals(maHD, other.maHD);
    }

    @Override
    public String toString() {
        return "HoaDon [maHD=" + maHD + ", ban=" + ban + ", tongTien=" + tongTien + "]";
    }
}
