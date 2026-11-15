package entity;

import java.sql.Timestamp;

/** Hóa đơn (bảng HoaDon). tongTien là số tiền sau giảm giá. */
public class HoaDon {
    private String maHD;
    private Timestamp thoiGianTao;
    private String ban;
    private String maNV;
    private String phuongThucThanhToan;
    private String trangThai;
    private double tongTien;

    public HoaDon() {
    }

    public HoaDon(String maHD, Timestamp thoiGianTao, String ban, String maNV,
                  String phuongThucThanhToan, String trangThai, double tongTien) {
        this.maHD = maHD;
        this.thoiGianTao = thoiGianTao;
        this.ban = ban;
        this.maNV = maNV;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.trangThai = trangThai;
        this.tongTien = tongTien;
    }

    public String getMaHD() { return maHD; }
    public void setMaHD(String maHD) { this.maHD = maHD; }
    public Timestamp getThoiGianTao() { return thoiGianTao; }
    public void setThoiGianTao(Timestamp thoiGianTao) { this.thoiGianTao = thoiGianTao; }
    public String getBan() { return ban; }
    public void setBan(String ban) { this.ban = ban; }
    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }
    public String getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public void setPhuongThucThanhToan(String p) { this.phuongThucThanhToan = p; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
}
