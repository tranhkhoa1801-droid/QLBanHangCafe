package controller;

import java.awt.Component;
import java.util.List;
import javax.swing.JOptionPane;

import dao.SanPhamDAO;
import entity.DanhMuc;
import entity.SanPham;

public class SanPhamController {

    private final SanPhamDAO sanPhamDAO;

    public SanPhamController() {
        this.sanPhamDAO = new SanPhamDAO();
    }

    public List<SanPham> getAll() {
        return sanPhamDAO.getAll();
    }

    public List<SanPham> timKiem(String tuKhoa) {
        return sanPhamDAO.search(tuKhoa);
    }

    public boolean kiemTraHopLe(Component parent, String ma, String ten, String von, String ban, Object danhMucSelected) {
        if (ma == null || !ma.matches("^SP\\d{2,}$")) {
            JOptionPane.showMessageDialog(parent, "Mã sản phẩm phải có dạng SP01, SP02...");
            return false;
        }

        if (ten == null || !ten.matches("^[\\p{L}0-9\\s&_\\-\\.,]+$")) {
            JOptionPane.showMessageDialog(parent, "Tên sản phẩm không hợp lệ!");
            return false;
        }

        if (von == null || ban == null || !von.matches("^\\d+(\\.\\d+)?$") || !ban.matches("^\\d+(\\.\\d+)?$")) {
            JOptionPane.showMessageDialog(parent, "Giá vốn và giá bán phải là số!");
            return false;
        }

        double giaVon = Double.parseDouble(von);
        double giaBan = Double.parseDouble(ban);

        if (giaVon <= 0 || giaBan <= 0) {
            JOptionPane.showMessageDialog(parent, "Giá phải lớn hơn 0!");
            return false;
        }

        if (giaBan < giaVon) {
            JOptionPane.showMessageDialog(parent, "Giá bán không được thấp hơn giá vốn!");
            return false;
        }

        if (danhMucSelected == null) {
            JOptionPane.showMessageDialog(parent, "Bạn cần tạo danh mục trước!");
            return false;
        }

        return true;
    }

    public boolean themSanPham(Component parent, String ma, String ten, String von, String ban, Object danhMucSelected) {
        if (!kiemTraHopLe(parent, ma, ten, von, ban, danhMucSelected)) {
            return false;
        }

        DanhMuc dm = new DanhMuc(danhMucSelected.toString(), "");
        SanPham sp = new SanPham(ma, ten, Double.parseDouble(von), Double.parseDouble(ban), dm);

        if (sanPhamDAO.insert(sp)) {
            JOptionPane.showMessageDialog(parent, "Thêm sản phẩm thành công!");
            return true;
        } else {
            JOptionPane.showMessageDialog(parent, "Thêm thất bại! Có thể mã sản phẩm đã tồn tại.");
            return false;
        }
    }

    public boolean suaSanPham(Component parent, int selectedRow, String ma, String ten, String von, String ban, Object danhMucSelected) {
        if (!kiemTraHopLe(parent, ma, ten, von, ban, danhMucSelected)) {
            return false;
        }

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(parent, "Vui lòng chọn sản phẩm cần sửa!");
            return false;
        }

        DanhMuc dm = new DanhMuc(danhMucSelected.toString(), "");
        SanPham sp = new SanPham(ma, ten, Double.parseDouble(von), Double.parseDouble(ban), dm);

        if (sanPhamDAO.update(sp)) {
            JOptionPane.showMessageDialog(parent, "Cập nhật thành công!");
            return true;
        } else {
            JOptionPane.showMessageDialog(parent, "Cập nhật thất bại!");
            return false;
        }
    }

    public boolean xoaSanPham(Component parent, int selectedRow, String ma) {
        if (selectedRow < 0 || ma == null || ma.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Vui lòng chọn sản phẩm cần xóa!");
            return false;
        }

        int chon = JOptionPane.showConfirmDialog(parent,
                "Bạn có chắc muốn xóa sản phẩm " + ma + "?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (chon == JOptionPane.YES_OPTION) {
            if (sanPhamDAO.delete(ma)) {
                JOptionPane.showMessageDialog(parent, "Xóa sản phẩm thành công!");
                return true;
            } else {
                JOptionPane.showMessageDialog(parent,
                        "Không thể xóa! Có thể sản phẩm này đã có trong chi tiết hóa đơn.");
                return false;
            }
        }
        return false;
    }
}
