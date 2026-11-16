package entity;

import java.sql.Timestamp;
import java.util.Objects;

public class HoaDon {
    public static final String CHUA_THANH_TOAN = "Chưa thanh toán";
    public static final String DA_THANH_TOAN = "Đã thanh toán";
    public static final String DA_HUY = "Đã hủy";

    private String maHD;
    private Timestamp thoiGianTao;
    private String ban;
    private NhanVien nhanVien;
    private String maNV;
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

    public HoaDon(String maHD, Timestamp thoiGianTao, String ban, String maNV, String phuongThucThanhToan,
            String trangThai, double tongTien) {
        this.maHD = maHD;
        this.thoiGianTao = thoiGianTao;
        this.ban = ban;
        this.maNV = maNV;
        this.nhanVien = maNV != null ? new NhanVien(maNV) : null;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.trangThai = trangThai;
        this.tongTien = tongTien;
    }

    public HoaDon(String maHD, Timestamp thoiGianTao, String ban, NhanVien nhanVien, String phuongThucThanhToan,
            String trangThai, double tongTien) {
        this.maHD = maHD;
        this.thoiGianTao = thoiGianTao;
        this.ban = ban;
        this.nhanVien = nhanVien;
        this.maNV = nhanVien != null ? nhanVien.getMaNV() : null;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.trangThai = trangThai;
        this.tongTien = tongTien;
    }

    public boolean isPaid() {
        return DA_THANH_TOAN.equalsIgnoreCase(trangThai);
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public Timestamp getThoiGianTao() {
        return thoiGianTao;
    }

    public void setThoiGianTao(Timestamp thoiGianTao) {
        this.thoiGianTao = thoiGianTao;
    }

    public String getBan() {
        return ban;
    }

    public void setBan(String ban) {
        this.ban = ban;
    }

    public String getMaNV() {
        return nhanVien != null ? nhanVien.getMaNV() : maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
        if (this.nhanVien == null && maNV != null) {
            this.nhanVien = new NhanVien(maNV);
        } else if (this.nhanVien != null) {
            this.nhanVien.setMaNV(maNV);
        }
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
        if (nhanVien != null) {
            this.maNV = nhanVien.getMaNV();
        }
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
