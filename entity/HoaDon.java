package entity;

import java.sql.Timestamp;
import java.util.Objects;

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

	public HoaDon(String maHD, Timestamp thoiGianTao, String ban, String maNV, String phuongThucThanhToan,
			String trangThai, double tongTien) {
		this.maHD = maHD;
		this.thoiGianTao = thoiGianTao;
		this.ban = ban;
		this.maNV = maNV;
		this.phuongThucThanhToan = phuongThucThanhToan;
		this.trangThai = trangThai;
		this.tongTien = tongTien;
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
		return maNV;
	}

	public void setMaNV(String maNV) {
		this.maNV = maNV;
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HoaDon other = (HoaDon) obj;
		return Objects.equals(maHD, other.maHD);
	}

	@Override
	public String toString() {
		return "HoaDon [maHD=" + maHD + ", thoiGianTao=" + thoiGianTao + ", ban=" + ban + ", maNV=" + maNV
				+ ", phuongThucThanhToan=" + phuongThucThanhToan + ", trangThai=" + trangThai + ", tongTien=" + tongTien
				+ "]";
	}
}
