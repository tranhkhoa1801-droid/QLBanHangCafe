package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import dao.ChiTietHoaDonDAO;
import dao.HoaDonDAO;
import dao.SanPhamDAO;
import entity.ChiTietHoaDon;
import entity.SanPham;

public class BanHangController {

	public static final List<String> PHUONG_THUC_THANH_TOAN = Arrays.asList("Tiền mặt", "Chuyển khoản", "Thẻ",
			"Ví điện tử");

	private final HoaDonDAO hoaDonDAO = HoaDonDAO.getInstance();
	private final ChiTietHoaDonDAO chiTietDAO = ChiTietHoaDonDAO.getInstance();
	private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

	public List<String> loadDanhSachBan() {
		return Arrays.asList("Mang đi", "Bàn 01", "Bàn 02", "Bàn 03", "Bàn 04", "Bàn 05", "Bàn 06", "Bàn 07", "Bàn 08",
				"Bàn 09", "Bàn 10");
	}

	public List<SanPham> loadSanPham() {
		return sanPhamDAO.getAll();
	}

	public Set<String> loadBanDangCoKhach() {
		return Tx.read(con -> hoaDonDAO.findOpenTables(con));
	}

	public List<ChiTietHoaDon> loadChiTietTheoBan(String tenBan) {
		if (tenBan == null || tenBan.trim().isEmpty()) {
			return new ArrayList<>();
		}
		return Tx.read(con -> {
			String maHD = hoaDonDAO.findOpenInvoiceId(con, tenBan);
			if (maHD == null) {
				return new ArrayList<>();
			}
			return chiTietDAO.listByInvoice(con, maHD);
		});
	}

	public void themMon(String tenBan, String maNV, SanPham sp, int soLuong) {
		if (tenBan == null || tenBan.trim().isEmpty()) {
			throw new BusinessException("Chưa chọn bàn!");
		}
		if (maNV == null || maNV.trim().isEmpty()) {
			throw new BusinessException("Chưa đăng nhập!");
		}
		if (sp == null || sp.getMaSP() == null) {
			throw new BusinessException("Chưa chọn sản phẩm!");
		}
		if (soLuong <= 0) {
			throw new BusinessException("Số lượng phải lớn hơn 0!");
		}
		Tx.write(con -> {
			String maHD = hoaDonDAO.findOpenInvoiceId(con, tenBan);
			if (maHD == null) {
				maHD = hoaDonDAO.taoMaHoaDonMoi(con);
				hoaDonDAO.insertOpen(con, maHD, tenBan, maNV);
			}
			chiTietDAO.addOrIncrease(con, maHD, sp.getMaSP(), soLuong, sp.getGiaBan(), sp.getGiaVon());
			return null;
		});
	}

	public void capNhatSoLuong(String tenBan, String maSP, int soLuongMoi) {
		if (soLuongMoi <= 0) {
			throw new BusinessException("Số lượng phải lớn hơn 0! Hãy dùng chức năng xóa món.");
		}
		Tx.write(con -> {
			String maHD = hoaDonDAO.findOpenInvoiceId(con, tenBan);
			if (maHD == null || !chiTietDAO.updateQuantity(con, maHD, maSP, soLuongMoi)) {
				throw new BusinessException("Không tìm thấy món cần sửa!");
			}
			return null;
		});
	}

	public void xoaMon(String tenBan, String maSP) {
		Tx.write(con -> {
			String maHD = hoaDonDAO.findOpenInvoiceId(con, tenBan);
			if (maHD != null) {
				chiTietDAO.delete(con, maHD, maSP);
				if (chiTietDAO.countByInvoice(con, maHD) == 0) {
					chiTietDAO.deleteAllByInvoice(con, maHD);
					hoaDonDAO.deleteOpen(con, maHD);
				}
			}
			return null;
		});
	}

	public void huyHoaDonBan(String tenBan) {
		Tx.write(con -> {
			String maHD = hoaDonDAO.findOpenInvoiceId(con, tenBan);
			if (maHD == null) {
				throw new BusinessException("Bàn này chưa có hóa đơn để hủy!");
			}
			if (!hoaDonDAO.cancel(con, maHD)) {
				throw new BusinessException("Không thể hủy hóa đơn này!");
			}
			return null;
		});
	}

	public static double tinhTongTien(List<ChiTietHoaDon> details) {
		double total = 0;
		for (ChiTietHoaDon d : details) {
			total += d.getThanhTien();
		}
		return total;
	}

	public static double tinhGiamGia(double total, int percent) {
		return Math.round(total * percent / 100.0);
	}

	public String thanhToan(String tenBan, int discountPercent, String phuongThucThanhToan) {
		if (tenBan == null || tenBan.trim().isEmpty()) {
			throw new BusinessException("Chưa chọn bàn!");
		}
		if (discountPercent < 0 || discountPercent > 100) {
			throw new BusinessException("Giảm giá phải từ 0 đến 100%!");
		}

		String pttt = phuongThucThanhToan;
		if (pttt == null || pttt.trim().isEmpty()) {
			pttt = "Tiền mặt";
		}

		final String hinhThucTT = pttt;
		return Tx.write(con -> {
			String maHD = hoaDonDAO.findOpenInvoiceId(con, tenBan);
			if (maHD == null || chiTietDAO.countByInvoice(con, maHD) == 0) {
				throw new BusinessException("Hóa đơn rỗng, không thể thanh toán!");
			}
			double tamTinh = chiTietDAO.sumByInvoice(con, maHD);
			double giamGia = tinhGiamGia(tamTinh, discountPercent);
			double thanhTien = Math.max(0, tamTinh - giamGia);

			if (!hoaDonDAO.markPaid(con, maHD, hinhThucTT, thanhTien)) {
				throw new BusinessException("Hóa đơn đã được thanh toán trước đó!");
			}
			return maHD;
		});
	}
}
