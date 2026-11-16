package entity;

import java.util.Objects;

public class ChiTietHoaDon {
    private HoaDon hoaDon;
    private String maHD;
    private SanPham sanPham;
    private String maSP;
    private String tenSP;
    private int soLuong;
    private double giaBanHienTai;
    private double giaVonHienTai;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(String maHD, String maSP, String tenSP, int soLuong, double giaBanHienTai,
            double giaVonHienTai) {
        this.maHD = maHD;
        this.hoaDon = maHD != null ? new HoaDon(maHD) : null;
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.sanPham = maSP != null ? new SanPham(maSP, tenSP, giaVonHienTai, giaBanHienTai, null) : null;
        this.soLuong = soLuong;
        this.giaBanHienTai = giaBanHienTai;
        this.giaVonHienTai = giaVonHienTai;
    }

    public ChiTietHoaDon(HoaDon hoaDon, SanPham sanPham, int soLuong, double giaBanHienTai, double giaVonHienTai) {
        this.hoaDon = hoaDon;
        this.maHD = hoaDon != null ? hoaDon.getMaHD() : null;
        this.sanPham = sanPham;
        this.maSP = sanPham != null ? sanPham.getMaSP() : null;
        this.tenSP = sanPham != null ? sanPham.getTenSP() : null;
        this.soLuong = soLuong;
        this.giaBanHienTai = giaBanHienTai;
        this.giaVonHienTai = giaVonHienTai;
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

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
        if (hoaDon != null) {
            this.maHD = hoaDon.getMaHD();
        }
    }

    public String getMaHD() {
        return (hoaDon != null) ? hoaDon.getMaHD() : maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
        if (this.hoaDon == null && maHD != null) {
            this.hoaDon = new HoaDon(maHD);
        } else if (this.hoaDon != null) {
            this.hoaDon.setMaHD(maHD);
        }
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
        if (sanPham != null) {
            this.maSP = sanPham.getMaSP();
            this.tenSP = sanPham.getTenSP();
        }
    }

    public String getMaSP() {
        return (sanPham != null) ? sanPham.getMaSP() : maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
        if (this.sanPham == null && maSP != null) {
            this.sanPham = new SanPham(maSP);
        } else if (this.sanPham != null) {
            this.sanPham.setMaSP(maSP);
        }
    }

    public String getTenSP() {
        return (sanPham != null && sanPham.getTenSP() != null) ? sanPham.getTenSP() : tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
        if (this.sanPham != null) {
            this.sanPham.setTenSP(tenSP);
        }
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
