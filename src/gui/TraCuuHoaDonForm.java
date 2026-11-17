package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MaskFormatter;

import controller.TraCuuHoaDonController;
import controller.TraCuuHoaDonController.ChiTietHoaDonTraCuu;
import entity.ChiTietHoaDon;

public class TraCuuHoaDonForm extends JPanel {
    private static final long serialVersionUID = 1L;

    private final TraCuuHoaDonController traCuuHoaDonController = new TraCuuHoaDonController();
    private static final DateTimeFormatter DINH_DANG_THOI_GIAN_HOA_DON = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final NumberFormat dinhDangTien = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
    private JTextField txtMaHoaDonCanTim;
    private JFormattedTextField txtNgayBatDau, txtNgayKetThuc;
    private JLabel lblMaHoaDon, lblNgayLap, lblNhanVien, lblTrangThai, lblTongTien;
    private DefaultTableModel moHinhHoaDon, moHinhMatHang;

    public TraCuuHoaDonForm() {
        setLayout(new BorderLayout());
        add(giaoDienTraCuuHoaDon(), BorderLayout.CENTER);
    }

    private JPanel giaoDienTraCuuHoaDon() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // bộ lọc tìm kiếm
        JPanel noiDung = new JPanel(new BorderLayout(10, 10));
        JPanel boLoc = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        txtMaHoaDonCanTim = new JTextField(14);

        try {
            MaskFormatter maskTuNgay = new MaskFormatter("##/##/####");
            MaskFormatter maskDenNgay = new MaskFormatter("##/##/####");
            for (MaskFormatter mask : new MaskFormatter[] {maskTuNgay, maskDenNgay}) {
                mask.setPlaceholderCharacter('_');
                mask.setAllowsInvalid(false);
            }
            txtNgayBatDau = new JFormattedTextField(maskTuNgay);
            txtNgayKetThuc = new JFormattedTextField(maskDenNgay);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        boLoc.add(new JLabel("Mã hóa đơn / Bàn / NV:"));
        boLoc.add(txtMaHoaDonCanTim);
        boLoc.add(new JLabel("Từ ngày:"));
        boLoc.add(txtNgayBatDau);
        boLoc.add(new JLabel("Đến ngày:"));
        boLoc.add(txtNgayKetThuc);
        JButton nutTim = new JButton("Tìm kiếm");
        JButton nutDatLai = new JButton("Đặt lại");
        boLoc.add(nutTim);
        boLoc.add(nutDatLai);
        noiDung.add(boLoc, BorderLayout.NORTH);

        // danh sách hóa đơn tra cứu
        JPanel danhSach = new JPanel(new BorderLayout(8, 8));
        danhSach.add(new JLabel("Danh sách hóa đơn"), BorderLayout.NORTH);

        // bảng hóa đơn tra cứu
        moHinhHoaDon = new DefaultTableModel( 
            new Object[] {"Mã hóa đơn", "Thời gian lập", "Nhân viên", "Tổng tiền", "Trạng thái"}, 0) {
                private static final long serialVersionUID = 1L;
                @Override
                public boolean isCellEditable(int dong, int cot) { return false; }
                @Override
                public Class<?> getColumnClass(int cot) {
                    return cot == 1 ? LocalDateTime.class : cot == 3 ? Double.class : String.class;
                }
            };
        JTable bangHoaDon = new JTable(moHinhHoaDon);
        bangHoaDon.setRowSorter(new TableRowSorter<>(moHinhHoaDon));
        bangHoaDon.getColumnModel().getColumn(1).setCellRenderer(
            new DefaultTableCellRenderer() {
                private static final long serialVersionUID = 1L;
                @Override
                protected void setValue(Object giaTri) {
                    setText(giaTri == null ? "" : DINH_DANG_THOI_GIAN_HOA_DON.format((LocalDateTime) giaTri));
                }
            }
        );
        DefaultTableCellRenderer boDinhDangTien = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void setValue(Object giaTri) {
                setText(giaTri == null ? "—" : dinhDangTien.format(giaTri));
                setHorizontalAlignment(SwingConstants.RIGHT);
            }
        };
        bangHoaDon.getColumnModel().getColumn(3).setCellRenderer(boDinhDangTien);

        danhSach.add(new JScrollPane(bangHoaDon), BorderLayout.CENTER);

        // chi tiết hóa đơn
        JPanel chiTiet = new JPanel(new BorderLayout(8, 8));
        JPanel tieuDeChiTiet = new JPanel(new BorderLayout(0, 6));
        tieuDeChiTiet.add(new JLabel("Chi tiết hóa đơn"), BorderLayout.NORTH);
        JPanel thongTin = new JPanel(new GridLayout(0, 2, 6, 6));
        lblMaHoaDon = new JLabel("—");
        lblNgayLap = new JLabel("—");
        lblNhanVien = new JLabel("—");
        lblTrangThai = new JLabel("—");
        lblTongTien = new JLabel("—");
        thongTin.add(new JLabel("Mã hóa đơn:"));
        thongTin.add(lblMaHoaDon);
        thongTin.add(new JLabel("Ngày lập:"));
        thongTin.add(lblNgayLap);
        thongTin.add(new JLabel("Nhân viên:"));
        thongTin.add(lblNhanVien);
        thongTin.add(new JLabel("Trạng thái:"));
        thongTin.add(lblTrangThai);
        tieuDeChiTiet.add(thongTin, BorderLayout.CENTER);
        chiTiet.add(tieuDeChiTiet, BorderLayout.NORTH);

        // bảng chi tiết hóa đơn tra cứu
        moHinhMatHang = new DefaultTableModel(new Object[] {"Sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int dong, int cot) { return false; }
            @Override
            public Class<?> getColumnClass(int cot) {
                return cot == 1 ? Integer.class : cot >= 2 ? Double.class : String.class;
            }
        };
        JTable bangMatHang = new JTable(moHinhMatHang);
        bangMatHang.setRowSorter(new TableRowSorter<>(moHinhMatHang));
        for (int cot : new int[] {2, 3}) {
            bangMatHang.getColumnModel().getColumn(cot).setCellRenderer(boDinhDangTien);
        }

        chiTiet.add(new JScrollPane(bangMatHang), BorderLayout.CENTER);
        JPanel tongTien = new JPanel(new BorderLayout());
        tongTien.add(new JLabel("Tổng tiền:"), BorderLayout.WEST);
        tongTien.add(lblTongTien, BorderLayout.EAST);
        chiTiet.add(tongTien, BorderLayout.SOUTH);

        // bố cục và kích thước
        JSplitPane ketQua = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, danhSach, chiTiet);
        ketQua.setResizeWeight(0.6);
        noiDung.add(ketQua, BorderLayout.CENTER);
        main.add(noiDung, BorderLayout.CENTER);
        
        // gắn sự kiện
        nutTim.addActionListener(e -> traCuuHoaDon());
        nutDatLai.addActionListener(e -> datLaiBoLoc());
        txtMaHoaDonCanTim.addActionListener(e -> traCuuHoaDon());
        bangHoaDon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int dong = bangHoaDon.getSelectedRow();
                xemChiTietHoaDon(dong < 0 ? -1 : bangHoaDon.convertRowIndexToModel(dong));
            }
        });

        // nạp lần đầu
        traCuuHoaDon();

        return main;
    }

    public void reloadData() {
        traCuuHoaDon();
    }
    
    private void traCuuHoaDon() {
        try {
            DateTimeFormatter dinhDangNgay = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
            String chuoiTuNgay = txtNgayBatDau.getText(), chuoiDenNgay = txtNgayKetThuc.getText();
            boolean trongTuNgay = chuoiTuNgay.equals("__/__/____");
            boolean trongDenNgay = chuoiDenNgay.equals("__/__/____");
            
            LocalDate tuNgay = trongTuNgay ? null : LocalDate.parse(chuoiTuNgay, dinhDangNgay);
            LocalDate denNgay = trongDenNgay ? null : LocalDate.parse(chuoiDenNgay, dinhDangNgay);
            List<ChiTietHoaDonTraCuu> ketQua = traCuuHoaDonController.traCuuHoaDon(
                    txtMaHoaDonCanTim.getText().trim(), tuNgay, denNgay);
            capNhatDanhSachHoaDon(ketQua);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Điều kiện tìm kiếm không hợp lệ",
                    JOptionPane.WARNING_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Ngày nhập không tồn tại hoặc sai định dạng. Vui lòng nhập theo dd/MM/yyyy.",
                    "Kiểm tra ngày", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Không thể tra cứu hóa đơn: " + e.getMessage(),
                    "Lỗi cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private LocalDateTime chuyenDoiThoiGian(java.util.Date date) {
        if (date == null) return null;
        if (date instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) date).toLocalDateTime();
        }
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
    }

    public void capNhatDanhSachHoaDon(List<ChiTietHoaDonTraCuu> ketQuaTraCuu) {
        moHinhHoaDon.setRowCount(0);
        if (ketQuaTraCuu != null) {
            for (ChiTietHoaDonTraCuu kq : ketQuaTraCuu) {
                var hd = kq.hoaDon();
                String ten = kq.nhanVien() == null ? null : kq.nhanVien().getHoTen();
                String trangThai = hd.getTrangThai();
                moHinhHoaDon.addRow(new Object[] {
                        hd.getMaHD(),
                        hd.getThoiGianTao() == null ? null : chuyenDoiThoiGian(hd.getThoiGianTao()),
                        ten == null || ten.isBlank() ? "—" : ten,
                        hd.getTongTien(),
                        trangThai == null || trangThai.isBlank() ? "—" : trangThai
                });
            }
        }
        capNhatChiTietHoaDon(null);
    }

    private void datLaiBoLoc() {
        txtMaHoaDonCanTim.setText("");
        for (JFormattedTextField truongNgay : new JFormattedTextField[] {txtNgayBatDau, txtNgayKetThuc}) {
            truongNgay.setValue(null);
        }
        traCuuHoaDon();
    }

    private void xemChiTietHoaDon(int chiSoDong) {
        if (chiSoDong < 0 || chiSoDong >= moHinhHoaDon.getRowCount()) {
            capNhatChiTietHoaDon(null);
            return;
        }
        String maHoaDon = moHinhHoaDon.getValueAt(chiSoDong, 0).toString();
        try {
            ChiTietHoaDonTraCuu chiTiet = traCuuHoaDonController.xemChiTietHoaDon(maHoaDon);
            capNhatChiTietHoaDon(chiTiet);
        } catch (SQLException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Không thể tải chi tiết hóa đơn: " + e.getMessage(),
                    "Lỗi tra cứu", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void capNhatChiTietHoaDon(ChiTietHoaDonTraCuu ketQua) {
        moHinhMatHang.setRowCount(0);
        var hoaDon = ketQua == null ? null : ketQua.hoaDon();
        if (hoaDon == null) {
            lblMaHoaDon.setText("—");
            lblNgayLap.setText("—");
            lblNhanVien.setText("—");
            lblTrangThai.setText("—");
            lblTongTien.setText("");
            return;
        }
        String maHoaDon = hoaDon.getMaHD();
        lblMaHoaDon.setText(maHoaDon == null || maHoaDon.isBlank() ? "—" : maHoaDon);
        lblNgayLap.setText(hoaDon.getThoiGianTao() == null ? "—"
                : DINH_DANG_THOI_GIAN_HOA_DON.format(chuyenDoiThoiGian(hoaDon.getThoiGianTao())));
        String tenNhanVien = ketQua.nhanVien() == null ? null : ketQua.nhanVien().getHoTen();
        lblNhanVien.setText(tenNhanVien == null || tenNhanVien.isBlank() ? "—" : tenNhanVien);
        String trangThai = hoaDon.getTrangThai();
        lblTrangThai.setText(trangThai == null || trangThai.isBlank() ? "—" : trangThai);
        lblTongTien.setText(dinhDangTien.format(hoaDon.getTongTien()));

        if (ketQua.matHang() != null) {
            for (ChiTietHoaDon ct : ketQua.matHang()) {
                String tenSanPham = ct.getTenSP() == null || ct.getTenSP().isBlank()
                        ? ct.getMaSP() : ct.getMaSP() + " - " + ct.getTenSP();
                moHinhMatHang.addRow(new Object[] {
                        tenSanPham,
                        ct.getSoLuong(),
                        ct.getGiaBanHienTai(),
                        ct.getGiaBanHienTai() * ct.getSoLuong()
                });
            }
        }
    }
}
