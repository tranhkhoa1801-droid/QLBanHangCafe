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

/** Controller Bán hàng: chọn bàn, thêm/xóa/sửa món, tính tiền, thanh toán, hủy. */
public class BanHangController {

    public static final List<String> PHUONG_THUC_THANH_TOAN =
            Arrays.asList("Tiền mặt", "Chuyển khoản", "Thẻ", "Ví điện tử");

    private final HoaDonDAO hoaDonDAO = HoaDonDAO.getInstance();
    private final ChiTietHoaDonDAO chiTietDAO = ChiTietHoaDonDAO.getInstance();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    /** DB không có bảng Ban riêng, bàn lưu dạng chuỗi ở cột HoaDon.ban. */
    public List<String> loadDanhSachBan() {
        return Arrays.asList("Mang đi", "Bàn 01", "Bàn 02", "Bàn 03", "Bàn 04", "Bàn 05",
                "Bàn 06", "Bàn 07", "Bàn 08", "Bàn 09", "Bàn 10");
    }

    public List<SanPham> loadSanPham() {
        return sanPhamDAO.getAll();
    }

    /** Các bàn đang có hóa đơn chưa thanh toán. */
    public Set<String> loadBanDangCoKhach() {
        return Tx.read(hoaDonDAO::findOpenTables);
    }

    /** Món đang có trong hóa đơn 'Chưa thanh toán' của bàn. */
    public List<ChiTietHoaDon> loadChiTietTheoBan(String tenBan) {
        if (tenBan == null || tenBan.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Tx.read(con -> {
            String maHD = hoaDonDAO.findOpenInvoiceId(con, tenBan);
            return maHD == null ? new ArrayList<ChiTietHoaDon>() : chiTietDAO.listByInvoice(con, maHD);
        });
    }

    /** Thêm món; tự tạo hóa đơn mới nếu bàn đang trống. */
    public void themMon(String tenBan, String maNV, SanPham sp, int soLuong) {
        if (tenBan == null || tenBan.trim().isEmpty()) {
            throw new BusinessException("Chưa chọn bàn!");
        }
        if (maNV == null || maNV.isEmpty()) {
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

    /** Xóa món; hóa đơn hết món thì xóa luôn hóa đơn rỗng. */
    public void xoaMon(String tenBan, String maSP) {
        Tx.write(con -> {
            String maHD = hoaDonDAO.findOpenInvoiceId(con, tenBan);
            if (maHD != null) {
                chiTietDAO.delete(con, maHD, maSP);
                if (chiTietDAO.countByInvoice(con, maHD) == 0) {
                    hoaDonDAO.deleteOpen(con, maHD);
                }
            }
            return null;
        });
    }

    /** Hủy hóa đơn đang mở của bàn (giữ lịch sử, không tính doanh thu). */
    public void huyHoaDonBan(String tenBan) {
        Tx.write(con -> {
            String maHD = hoaDonDAO.findOpenInvoiceId(con, tenBan);
            if (maHD == null) {
                throw new BusinessException("Bàn này chưa có hóa đơn để hủy!");
            }
            hoaDonDAO.cancel(con, maHD);
            return null;
        });
    }

    // ---- Tính tiền (chỉ để hiển thị; số liệu chốt do stored procedure tính từ DB) ----
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

    /** Thanh toán hóa đơn đang mở của bàn, trả về mã hóa đơn. */
    public String thanhToan(String tenBan, int discountPercent, String phuongThucThanhToan) {
        if (tenBan == null || tenBan.trim().isEmpty()) {
            throw new BusinessException("Chưa chọn bàn!");
        }
        if (discountPercent < 0 || discountPercent > 100) {
            throw new BusinessException("Giảm giá phải từ 0 đến 100%!");
        }
        String pttt = (phuongThucThanhToan == null || phuongThucThanhToan.isEmpty())
                ? "Tiền mặt" : phuongThucThanhToan;
        return Tx.write(con -> {
            String maHD = hoaDonDAO.findOpenInvoiceId(con, tenBan);
            if (maHD == null || chiTietDAO.countByInvoice(con, maHD) == 0) {
                throw new BusinessException("Hóa đơn rỗng, không thể thanh toán!");
            }
            hoaDonDAO.markPaid(con, maHD, discountPercent, pttt);
            return maHD;
        });
    }
}
