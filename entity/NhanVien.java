package entity;

	public class NhanVien1 {
		private String maNV; 
		private String hoTen ; 
		private String vaiTro; 
		private String sDT; 
		private String taiKhoan; 
		private String caLamViec; 
		private boolean isActive;
	
	public NhanVien1 (String maNhanVien) {
		this.maNV = maNhanVien; 
	}

	public NhanVien1(String maNV, String hoTen, String vaiTro, String sDT, String taiKhoan, String caLamViec,
			boolean isActive) {
		super();
		this.maNV = maNV;
		this.hoTen = hoTen;
		this.vaiTro = vaiTro;
		this.sDT = sDT;
		this.taiKhoan = taiKhoan;
		this.caLamViec = caLamViec;
		this.isActive = isActive;
	}

	public String getMaNV() {
		return maNV;
	}

	public void setMaNV(String maNV) {
		this.maNV = maNV;
	}

	public String getHoTen() {
		return hoTen;
	}

	public void setHoTen(String hoTen) {
		this.hoTen = hoTen;
	}

	public String getVaiTro() {
		return vaiTro;
	}

	public void setVaiTro(String vaiTro) {
		this.vaiTro = vaiTro;
	}

	public String getsDT() {
		return sDT;
	}

	public void setsDT(String sDT) {
		this.sDT = sDT;
	}

	public String getTaiKhoan() {
		return taiKhoan;
	}

	public void setTaiKhoan(String taiKhoan) {
		this.taiKhoan = taiKhoan;
	}

	public String getCaLamViec() {
		return caLamViec;
	}

	public void setCaLamViec(String caLamViec) {
		this.caLamViec = caLamViec;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	
}
