package controller;

import java.util.List;
import javax.swing.JOptionPane;
import java.awt.Component;

import dao.DanhMucDAO;
import entity.DanhMuc;

public class DanhMucController {

    private DanhMucDAO danhMucDAO;

    public DanhMucController() {
        this.danhMucDAO = new DanhMucDAO();
    }

    public List<DanhMuc> getAll() {
        return danhMucDAO.getAll();
    }

    public boolean kiemTraHopLe(Component parent, String ma, String ten) {
        if (!ma.matches("^DM\\d{2}$")) {
            JOptionPane.showMessageDialog(parent, "Mã danh mục phải có dạng DM01, DM02...");
            return false;
        }

        if (!ten.matches("^[\\p{L}0-9\\s&]+$")) {
            JOptionPane.showMessageDialog(parent, "Tên danh mục không hợp lệ!");
            return false;
        }

        return true;
    }

    public boolean themDanhMuc(Component parent, String ma, String ten) {
        if (!kiemTraHopLe(parent, ma, ten)) {
            return false;
        }

        DanhMuc dm = new DanhMuc(ma, ten);
        if (danhMucDAO.insert(dm)) {
            JOptionPane.showMessageDialog(parent, "Thêm danh mục thành công!");
            return true;
        } else {
            JOptionPane.showMessageDialog(parent, "Thêm thất bại! Có thể mã danh mục đã tồn tại.");
            return false;
        }
    }

    public boolean suaDanhMuc(Component parent, String ma, String ten) {
        if (!kiemTraHopLe(parent, ma, ten)) {
            return false;
        }

        DanhMuc dm = new DanhMuc(ma, ten);
        if (danhMucDAO.update(dm)) {
            JOptionPane.showMessageDialog(parent, "Sửa danh mục thành công!");
            return true;
        } else {
            JOptionPane.showMessageDialog(parent, "Sửa danh mục thất bại!");
            return false;
        }
    }

    public boolean xoaDanhMuc(Component parent, String ma) {
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Vui lòng chọn danh mục cần xóa!");
            return false;
        }

        int chon = JOptionPane.showConfirmDialog(parent,
                "Bạn có chắc muốn xóa danh mục " + ma + "?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (chon == JOptionPane.YES_OPTION) {
            if (danhMucDAO.delete(ma)) {
                JOptionPane.showMessageDialog(parent, "Xóa thành công!");
                return true;
            } else {
                JOptionPane.showMessageDialog(parent,
                        "Không thể xóa! Có thể danh mục đang được sản phẩm sử dụng.");
                return false;
            }
        }
        return false;
    }
}
