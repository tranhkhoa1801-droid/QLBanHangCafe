package entity;

/** Chi tiết hóa đơn (bảng ChiTietHoaDon). Giá lưu theo thời điểm bán. */
public class ChiTietHoaDon {
    private String maHD;
    private String maSP;
    private String tenSP;
    private int soLuong;
    private double giaBanHienTai;
    private double giaVonHienTai;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(String maHD, String maSP, String tenSP, int soLuong,
                         double giaBanHienTai, double giaVonHienTai) {
        this.maHD = maHD;
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.soLuong = soLuong;
        this.giaBanHienTai = giaBanHienTai;
        this.giaVonHienTai = giaVonHienTai;
    }

    /** Thành tiền của dòng = số lượng x giá bán. */
    public double getThanhTien() { return soLuong * giaBanHienTai; }

    public String getMaHD() { return maHD; }
    public void setMaHD(String maHD) { this.maHD = maHD; }
    public String getMaSP() { return maSP; }
    public void setMaSP(String maSP) { this.maSP = maSP; }
    public String getTenSP() { return tenSP; }
    public void setTenSP(String tenSP) { this.tenSP = tenSP; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public double getGiaBanHienTai() { return giaBanHienTai; }
    public void setGiaBanHienTai(double g) { this.giaBanHienTai = g; }
    public double getGiaVonHienTai() { return giaVonHienTai; }
    public void setGiaVonHienTai(double g) { this.giaVonHienTai = g; }
}
