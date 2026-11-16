package controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import dao.ThongKeDAO;
import entity.SanPham;

public class ThongKeController {
    public record DuLieuDoanhThu(double doanhThu, int moc) {}
    public record SanPhamBanChay(SanPham sanPham, int soLuongDaBan) {}

    private final ThongKeDAO thongKeDAO = new ThongKeDAO();

    public double tinhDoanhThu(boolean theoThang) throws SQLException {
        return thongKeDAO.tinhDoanhThu(dauKy(theoThang), LocalDateTime.now());
    }

    public int demSoLuongDon(boolean theoThang) throws SQLException {
        return thongKeDAO.demSoLuongDon(dauKy(theoThang), LocalDateTime.now());
    }

    public List<SanPhamBanChay> phanTichSanPhamBanChay(boolean theoThang) throws SQLException {
        List<ThongKeDAO.SanPhamBanChay> raw = thongKeDAO.phanTichSanPhamBanChay(dauKy(theoThang), LocalDateTime.now());
        List<SanPhamBanChay> result = new ArrayList<>();
        for (ThongKeDAO.SanPhamBanChay r : raw) {
            result.add(new SanPhamBanChay(r.sanPham(), r.soLuongDaBan()));
        }
        return result;
    }

    public List<DuLieuDoanhThu> layDuLieuDoanhThu(boolean theoThang) throws SQLException {
        LocalDate homNay = LocalDate.now();
        List<ThongKeDAO.DuLieuDoanhThu> ketQuaTruyVan = thongKeDAO.layDuLieuDoanhThu(
                dauKy(theoThang), LocalDateTime.now(), theoThang);
        double[] tongTheoMoc = new double[theoThang ? 32 : 24];
        for (ThongKeDAO.DuLieuDoanhThu diem : ketQuaTruyVan) {
            if (diem.moc() >= 0 && diem.moc() < tongTheoMoc.length) {
                tongTheoMoc[diem.moc()] = diem.doanhThu();
            }
        }
        int mocCuoi = theoThang ? homNay.getDayOfMonth() : LocalDateTime.now().getHour();
        int mocDau = theoThang ? 1 : 0;
        List<DuLieuDoanhThu> dayDu = new ArrayList<>();
        for (int moc = mocDau; moc <= mocCuoi; moc++) {
            dayDu.add(new DuLieuDoanhThu(tongTheoMoc[moc], moc));
        }
        return dayDu;
    }

    private LocalDateTime dauKy(boolean theoThang) {
        LocalDate homNay = LocalDate.now();
        return (theoThang ? homNay.withDayOfMonth(1) : homNay).atStartOfDay();
    }
}
