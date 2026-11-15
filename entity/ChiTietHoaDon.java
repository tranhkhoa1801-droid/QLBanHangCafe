package entity;

import java.util.Objects;

public class ChiTietHoaDon {
	private String maHD;
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
		this.maSP = maSP;
		this.tenSP = tenSP;
		this.soLuong = soLuong;
		this.giaBanHienTai = giaBanHienTai;
		this.giaVonHienTai = giaVonHienTai;
	}

	public double getThanhTien() {
		return soLuong * giaBanHienTai;
	}

	public String getMaHD() {
		return maHD;
	}

	public void setMaHD(String maHD) {
		this.maHD = maHD;
	}

	public String getMaSP() {
		return maSP;
	}

	public void setMaSP(String maSP) {
		this.maSP = maSP;
	}

	public String getTenSP() {
		return tenSP;
	}

	public void setTenSP(String tenSP) {
		this.tenSP = tenSP;
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
		return Objects.hash(maHD, maSP);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChiTietHoaDon other = (ChiTietHoaDon) obj;
		return Objects.equals(maHD, other.maHD) && Objects.equals(maSP, other.maSP);
	}

	@Override
	public String toString() {
		return "ChiTietHoaDon [maHD=" + maHD + ", maSP=" + maSP + ", tenSP=" + tenSP + ", soLuong=" + soLuong
				+ ", giaBanHienTai=" + giaBanHienTai + ", giaVonHienTai=" + giaVonHienTai + "]";
	}
}
