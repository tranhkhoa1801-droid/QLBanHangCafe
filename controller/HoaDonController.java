package controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import dao.ChiTietHoaDonDAO;
import dao.HoaDonDAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;

/** Controller Hóa đơn: xem chi tiết, tạo nội dung in bill, xuất file. */
public class HoaDonController {

    private final DecimalFormat money = new DecimalFormat("#,###");

    public HoaDon getHoaDon(String maHD) {
        HoaDon hd = Tx.read(con -> HoaDonDAO.getInstance().getById(con, maHD));
        if (hd == null) {
            throw new BusinessException("Không tìm thấy hóa đơn " + maHD);
        }
        return hd;
    }

    public List<ChiTietHoaDon> getChiTietHoaDon(String maHD) {
        return Tx.read(con -> ChiTietHoaDonDAO.getInstance().listByInvoice(con, maHD));
    }

    public String taoNoiDungHoaDon(String maHD) {
        HoaDon hd = getHoaDon(maHD);
        List<ChiTietHoaDon> details = getChiTietHoaDon(maHD);
        String line = "------------------------------------------------\n";
        StringBuilder sb = new StringBuilder();

        sb.append("              HÓA ĐƠN BÁN HÀNG\n").append(line);
        sb.append(String.format("Mã hóa đơn : %s%n", hd.getMaHD()));
        sb.append(String.format("Bàn        : %s%n", hd.getBan()));
        sb.append(String.format("Mã NV      : %s%n", hd.getMaNV()));
        sb.append(String.format("Thanh toán : %s (%s)%n",
                hd.getPhuongThucThanhToan() == null ? "—" : hd.getPhuongThucThanhToan(), hd.getTrangThai()));
        if (hd.getThoiGianTao() != null) {
            sb.append(String.format("Ngày lập   : %s%n",
                    new SimpleDateFormat("dd/MM/yyyy HH:mm").format(hd.getThoiGianTao())));
        }
        sb.append(line);
        sb.append(String.format("%-20s %4s %9s %10s%n", "Tên món", "SL", "Đơn giá", "Thành tiền"));

        double tamTinh = 0;
        for (ChiTietHoaDon d : details) {
            String ten = d.getTenSP() != null ? d.getTenSP() : d.getMaSP();
            tamTinh += d.getThanhTien();
            sb.append(String.format("%-20s %4d %9s %10s%n", cut(ten, 20), d.getSoLuong(),
                    money.format(d.getGiaBanHienTai()), money.format(d.getThanhTien())));
        }

        // tongTien trong DB là số tiền sau giảm giá nên giảm giá = tạm tính - tongTien
        double giamGia = Math.max(0, tamTinh - hd.getTongTien());
        sb.append(line);
        sb.append(String.format("%-35s %12s%n", "Tạm tính:", money.format(tamTinh)));
        sb.append(String.format("%-35s %12s%n", "Giảm giá:", money.format(giamGia)));
        sb.append(String.format("%-35s %12s%n", "TỔNG THANH TOÁN:", money.format(hd.getTongTien())));
        sb.append(line).append("        Cảm ơn quý khách - Hẹn gặp lại!\n");
        return sb.toString();
    }

    public void xuatFileHoaDon(String maHD, File file) {
        try {
            Files.write(file.toPath(), taoNoiDungHoaDon(maHD).getBytes(StandardCharsets.UTF_8));
        } catch (IOException ex) {
            throw new BusinessException("Không ghi được file: " + ex.getMessage(), ex);
        }
    }

    private static String cut(String s, int max) {
        return s == null ? "" : (s.length() <= max ? s : s.substring(0, max - 1) + "…");
    }
}
