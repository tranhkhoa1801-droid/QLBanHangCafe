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

        String pttt = hd.getPhuongThucThanhToan();
        if (pttt == null || pttt.trim().isEmpty()) {
            pttt = "Chưa xác định";
        }

        sb.append("              HÓA ĐƠN BÁN HÀNG\n");
        sb.append(line);
        sb.append(String.format("Mã hóa đơn : %s%n", hd.getMaHD()));
        sb.append(String.format("Bàn        : %s%n", hd.getBan()));
        String maNV = (hd.getMaNV() != null) ? hd.getMaNV().getMaNV() : "";
        sb.append(String.format("Mã NV      : %s%n", maNV));
        sb.append(String.format("Thanh toán : %s (%s)%n", pttt, hd.getTrangThai()));

        if (hd.getThoiGianTao() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            sb.append(String.format("Ngày lập   : %s%n", sdf.format(hd.getThoiGianTao())));
        }

        sb.append(line);
        sb.append(String.format("%-20s %4s %9s %10s%n", "Tên món", "SL", "Đơn giá", "Thành tiền"));

        double tamTinh = 0;
        for (ChiTietHoaDon d : details) {
            String ten = (d.getTenSP() != null) ? d.getTenSP() : d.getMaSP();
            tamTinh += d.getThanhTien();
            sb.append(String.format("%-20s %4d %9s %10s%n",
                    rutGonChuoi(ten, 20),
                    d.getSoLuong(),
                    money.format(d.getGiaBanHienTai()),
                    money.format(d.getThanhTien())));
        }

        double giamGia = Math.max(0, tamTinh - hd.getTongTien());
        sb.append(line);
        sb.append(String.format("%-35s %12s%n", "Tạm tính:", money.format(tamTinh)));
        sb.append(String.format("%-35s %12s%n", "Giảm giá:", money.format(giamGia)));
        sb.append(String.format("%-35s %12s%n", "TỔNG THANH TOÁN:", money.format(hd.getTongTien())));
        sb.append(line);
        sb.append("        Cảm ơn quý khách - Hẹn gặp lại!\n");

        return sb.toString();
    }

    public void xuatFileHoaDon(String maHD, File file) {
        try {
            String noiDung = taoNoiDungHoaDon(maHD);
            Files.write(file.toPath(), noiDung.getBytes(StandardCharsets.UTF_8));
        } catch (IOException ex) {
            throw new BusinessException("Không ghi được file: " + ex.getMessage(), ex);
        }
    }

    private String rutGonChuoi(String s, int max) {
        if (s == null) {
            return "";
        }
        if (s.length() <= max) {
            return s;
        }
        return s.substring(0, max - 3) + "...";
    }
}
