package entity;

import java.util.List;
import java.util.Objects;
import dao.ChiTietHoaDonDAO;

public class ChiTietHoaDon {
    private HoaDon hoaDon;
    private SanPham sanPham;
    private int soLuong;
    private double giaBanHienTai;
    private double giaVonHienTai;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(HoaDon hoaDon, SanPham sanPham, int soLuong, double giaBanHienTai, double giaVonHienTai) {
        this.hoaDon = hoaDon;
        this.sanPham = sanPham;
        this.soLuong = soLuong;
        this.giaBanHienTai = giaBanHienTai;
        this.giaVonHienTai = giaVonHienTai;
    }

    public ChiTietHoaDon(String maHD, String maSP, String tenSP, int soLuong, double giaBanHienTai,
            double giaVonHienTai) {
        this.hoaDon = maHD != null ? new HoaDon(maHD) : null;
        this.sanPham = maSP != null ? new SanPham(maSP, tenSP, giaVonHienTai, giaBanHienTai, null) : null;
        this.soLuong = soLuong;
        this.giaBanHienTai = giaBanHienTai;
        this.giaVonHienTai = giaVonHienTai;
    }

    // Class diagram methods
    public List<ChiTietHoaDon> getByInvoice(String maHD) {
        return ChiTietHoaDonDAO.getInstance().listByInvoice(maHD);
    }

    public List<ChiTietHoaDon> getByInvoice() {
        return getByInvoice(getMaHD());
    }

    public double tinhThanhTien() {
        return soLuong * giaBanHienTai;
    }

    public double getThanhTien() {
        return tinhThanhTien();
    }

    public double tinhLoiNhuanGop() {
        return soLuong * (giaBanHienTai - giaVonHienTai);
    }

    // Standard getters and setters
    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getGiaBanHienTai() {
        return giaBanHienTai;
    }

    public void setGiaBanHienTai(double giaBanHienTai) {
        this.giaBanHienTai = giaBanHienTai;
    }

    public double getGiaVonHienTai() {
        return giaVonHienTai;
    }

    public void setGiaVonHienTai(double giaVonHienTai) {
        this.giaVonHienTai = giaVonHienTai;
    }

    // Convenience delegates for compatibility
    public String getMaHD() {
        return hoaDon != null ? hoaDon.getMaHD() : null;
    }

    public void setMaHD(String maHD) {
        if (hoaDon == null) {
            hoaDon = new HoaDon(maHD);
        } else {
            hoaDon.setMaHD(maHD);
        }
    }

    public String getMaSP() {
        return sanPham != null ? sanPham.getMaSP() : null;
    }

    public void setMaSP(String maSP) {
        if (sanPham == null) {
            sanPham = new SanPham(maSP);
        } else {
            sanPham.setMaSP(maSP);
        }
    }

    public String getTenSP() {
        return sanPham != null ? sanPham.getTenSP() : null;
    }

    public void setTenSP(String tenSP) {
        if (sanPham == null) {
            sanPham = new SanPham();
        }
        sanPham.setTenSP(tenSP);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaHD(), getMaSP());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ChiTietHoaDon other = (ChiTietHoaDon) obj;
        return Objects.equals(getMaHD(), other.getMaHD()) && Objects.equals(getMaSP(), other.getMaSP());
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon [maHD=" + getMaHD() + ", maSP=" + getMaSP() + ", tenSP=" + getTenSP()
                + ", soLuong=" + soLuong + ", giaBanHienTai=" + giaBanHienTai + "]";
    }
}
