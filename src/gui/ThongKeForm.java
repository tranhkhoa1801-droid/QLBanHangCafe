package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import controller.ThongKeController;
import controller.ThongKeController.DuLieuDoanhThu;
import controller.ThongKeController.SanPhamBanChay;

public class ThongKeForm extends JPanel {
    private final ThongKeController thongKeController = new ThongKeController();
    private final NumberFormat dinhDangTien = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
    private JLabel lblDoanhThuHomNay, lblDoanhThuThangNay, lblSoLuongHoaDon;
    private DefaultTableModel moHinhSanPham;
    private JPanel bieuDoDoanhThu;
    private final List<List<DuLieuDoanhThu>> duLieuBieuDoTheoKy = new ArrayList<>();

    public ThongKeForm() {
        setLayout(new BorderLayout());
        add(giaoDienThongKe(), BorderLayout.CENTER);
    }

    private JPanel giaoDienThongKe() {
        String[] cacKyThongKe = {"Theo tháng", "Theo ngày"};

        JPanel main = new JPanel(new BorderLayout());     
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));   

        // tổng doanh thu
        JPanel khuVucDoanhThu = new JPanel(new GridLayout(2, 2, 8, 8));
        khuVucDoanhThu.add(new JLabel("Doanh thu hôm nay", SwingConstants.LEFT));
        khuVucDoanhThu.add(new JLabel("Doanh thu tháng này", SwingConstants.LEFT));
        lblDoanhThuHomNay = new JLabel("—", SwingConstants.LEFT);
        lblDoanhThuThangNay = new JLabel("—", SwingConstants.LEFT);
        lblDoanhThuHomNay.setFont(lblDoanhThuHomNay.getFont().deriveFont(Font.BOLD, 20f));
        lblDoanhThuThangNay.setFont(lblDoanhThuThangNay.getFont().deriveFont(Font.BOLD, 20f));
        khuVucDoanhThu.add(lblDoanhThuHomNay);
        khuVucDoanhThu.add(lblDoanhThuThangNay);

        // số lượng hóa đơn
        JPanel khuVucSoHoaDon = new JPanel(new BorderLayout());
        JPanel tieuDeSoHoaDon = new JPanel(new BorderLayout());
        JComboBox<String> cboKyHoaDon = new JComboBox<>(cacKyThongKe);
        lblSoLuongHoaDon = new JLabel("—", SwingConstants.LEFT);
        lblSoLuongHoaDon.setFont(lblSoLuongHoaDon.getFont().deriveFont(Font.BOLD, 20f));
        tieuDeSoHoaDon.add(new JLabel("Số lượng hóa đơn", SwingConstants.LEFT), BorderLayout.WEST);
        tieuDeSoHoaDon.add(cboKyHoaDon, BorderLayout.EAST);
        khuVucSoHoaDon.add(tieuDeSoHoaDon, BorderLayout.NORTH);
        khuVucSoHoaDon.add(lblSoLuongHoaDon, BorderLayout.SOUTH);

        // biểu đồ đường doanh thu
        JPanel khuVucBieuDo = new JPanel(new BorderLayout());
        JPanel tieuDeBieuDo = new JPanel(new BorderLayout());
        JComboBox<String> cboKyBieuDo = new JComboBox<>(cacKyThongKe);
        duLieuBieuDoTheoKy.add(List.of());
        duLieuBieuDoTheoKy.add(List.of());
        bieuDoDoanhThu = new JPanel() {
            @Override
            protected void paintComponent(Graphics doHoaGoc) {
                super.paintComponent(doHoaGoc);
                veBieuDoDoanhThu(doHoaGoc, getWidth(), getHeight(), cboKyBieuDo);
            }
        };
        bieuDoDoanhThu.setBackground(Color.WHITE);
        bieuDoDoanhThu.setPreferredSize(new Dimension(500, 400));
        tieuDeBieuDo.add(new JLabel("Biểu đồ đường doanh thu", SwingConstants.LEFT), BorderLayout.WEST);
        tieuDeBieuDo.add(cboKyBieuDo, BorderLayout.EAST);
        khuVucBieuDo.add(tieuDeBieuDo, BorderLayout.NORTH);
        khuVucBieuDo.add(bieuDoDoanhThu, BorderLayout.CENTER);

        // sản phẩm bán chạy
        JPanel khuVucSanPham = new JPanel(new BorderLayout());
        JPanel tieuDeSanPham = new JPanel(new BorderLayout());
        JComboBox<String> cboKySanPham = new JComboBox<>(cacKyThongKe);
        moHinhSanPham = new DefaultTableModel(new Object[] {"Sản phẩm", "Giá bán", "Đã bán"}, 0) {
            @Override
            public boolean isCellEditable(int chiSoDong, int chiSoCot) { return false; }
            @Override
            public Class<?> getColumnClass(int chiSoCot) {
                return chiSoCot == 1 ? Double.class : chiSoCot == 2 ? Integer.class : String.class;
            }
        };
        JTable bangSanPham = new JTable(moHinhSanPham);
        bangSanPham.setAutoCreateRowSorter(true);
        bangSanPham.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object giaTri) {
                setText(giaTri == null ? "—" : dinhDangTien.format(giaTri));
                setHorizontalAlignment(SwingConstants.RIGHT);
            }
        });
        tieuDeSanPham.add(new JLabel("Sản phẩm bán chạy", SwingConstants.LEFT), BorderLayout.WEST);
        tieuDeSanPham.add(cboKySanPham, BorderLayout.EAST);
        khuVucSanPham.add(tieuDeSanPham, BorderLayout.NORTH);
        khuVucSanPham.add(new JScrollPane(bangSanPham), BorderLayout.CENTER);

        // bố cục và kích thước
        JSplitPane khuVucDuoi = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, khuVucBieuDo, khuVucSanPham);
        JSplitPane khuVucChiSo = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, khuVucDoanhThu, khuVucSoHoaDon);
        JPanel noiDung = new JPanel(new BorderLayout(8, 8));
        noiDung.add(khuVucChiSo, BorderLayout.NORTH);
        noiDung.add(khuVucDuoi, BorderLayout.CENTER);
        main.add(noiDung, BorderLayout.CENTER);
        main.setPreferredSize(new Dimension(1000, 650));
        SwingUtilities.invokeLater(() -> {
            khuVucChiSo.setDividerLocation(.5);
            khuVucDuoi.setDividerLocation(.55);
        });

        // chọn kỳ mặc định là ngày
        cboKyHoaDon.setSelectedItem("Theo ngày");
        cboKyBieuDo.setSelectedItem("Theo ngày");
        cboKySanPham.setSelectedItem("Theo ngày");

        // gắn sự kiện
        cboKyHoaDon.addActionListener(e -> capNhatSoLuongDon("Theo tháng".equals(cboKyHoaDon.getSelectedItem())));
        cboKyBieuDo.addActionListener(e -> capNhatBieuDoDoanhThu("Theo tháng".equals(cboKyBieuDo.getSelectedItem())));
        cboKySanPham.addActionListener(e -> capNhatPhanTichSanPhamBanChay("Theo tháng".equals(cboKySanPham.getSelectedItem())));

        // nạp lần đầu
        capNhatDoanhThu();
        capNhatSoLuongDon(false);
        capNhatBieuDoDoanhThu(false);
        capNhatPhanTichSanPhamBanChay(false);

        return main;
    }

    public void reloadData() {
        capNhatDoanhThu();
        capNhatSoLuongDon(false);
        capNhatBieuDoDoanhThu(false);
        capNhatPhanTichSanPhamBanChay(false);
    }

    private void capNhatBieuDoDoanhThu(boolean theoThang) {
        try {
            duLieuBieuDoTheoKy.set(theoThang ? 1 : 0, thongKeController.layDuLieuDoanhThu(theoThang));
            bieuDoDoanhThu.repaint();
        } catch (SQLException e) { hienThiLoiTruyVan(e); }
    }

    private void capNhatDoanhThu() {
        try {
            lblDoanhThuHomNay.setText(dinhDangTien.format(thongKeController.tinhDoanhThu(false)));
            lblDoanhThuThangNay.setText(dinhDangTien.format(thongKeController.tinhDoanhThu(true)));
        } catch (SQLException e) { hienThiLoiTruyVan(e); }
    }

    private void capNhatSoLuongDon(boolean theoThang) {
        try {
            lblSoLuongHoaDon.setText(String.valueOf(thongKeController.demSoLuongDon(theoThang)));
        } catch (SQLException e) { hienThiLoiTruyVan(e); }
    }

    private void capNhatPhanTichSanPhamBanChay(boolean theoThang) {
        moHinhSanPham.setRowCount(0);
        try {
            thongKeController.phanTichSanPhamBanChay(theoThang).forEach(sanPhamBanChay ->
                    moHinhSanPham.addRow(new Object[] {sanPhamBanChay.sanPham().getTenSP(),
                            sanPhamBanChay.sanPham().getGiaBan(), sanPhamBanChay.soLuongDaBan()}));
        } catch (SQLException e) { hienThiLoiTruyVan(e); }
    }

    private void hienThiLoiTruyVan(SQLException e) {
        JOptionPane.showMessageDialog(this, "Không thể tải dữ liệu thống kê: " + e.getMessage(),
                "Lỗi cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
    }

    // vẽ biểu đồ đường doanh thu
    private void veBieuDoDoanhThu(Graphics doHoa, int rong, int cao, JComboBox<String> cboKyBieuDo) {
        Graphics2D g = (Graphics2D) doHoa.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        boolean theoThang = "Theo tháng".equals(cboKyBieuDo.getSelectedItem());
        List<DuLieuDoanhThu> diem = duLieuBieuDoTheoKy.get(theoThang ? 1 : 0);
        double doanhThuMax = diem.stream().mapToDouble(DuLieuDoanhThu::doanhThu).max().orElse(0);
        double buocDoanhThu = 10_000;
        boolean nhanNam = true;
        while (doanhThuMax / buocDoanhThu > 5) { buocDoanhThu *= nhanNam ? 5 : 2; nhanNam = !nhanNam; }
        double max = Math.max(buocDoanhThu, Math.ceil(doanhThuMax / buocDoanhThu) * buocDoanhThu);
        int trai = 105, tren = 30, phai = rong - 20, duoi = cao - 55;
        int rongBieuDo = phai - trai, caoBieuDo = duoi - tren, mocHienTai, yHienTai = -1;
        LocalDateTime hienTai = LocalDateTime.now();
        int mocDau = theoThang ? 1 : 0;
        double mocSpan = theoThang ? hienTai.toLocalDate().lengthOfMonth() - 1.0 : 23;
        mocHienTai = theoThang ? hienTai.getDayOfMonth() : hienTai.getHour();
        int xHienTai = trai + (int) (rongBieuDo * (mocHienTai - mocDau) / (mocSpan > 0 ? mocSpan : 1));
        NumberFormat dinhDangTruc = NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN"));
        Color mauBieuDo = new Color(0, 150, 201);

        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(trai, tren, trai, duoi);
        g.drawLine(trai, duoi, phai, duoi);
        g.setStroke(new BasicStroke(1f));
        for (double v = 0; v <= max; v += buocDoanhThu) {
            int y = duoi - (int) (caoBieuDo * v / max);
            g.drawLine(trai, y, phai, y);
            String nhan = dinhDangTruc.format(v);
            g.setColor(Color.DARK_GRAY);
            g.drawString(nhan, trai - g.getFontMetrics().stringWidth(nhan) - 8, y + 5);
            g.setColor(Color.LIGHT_GRAY);
        }
        g.setColor(Color.DARK_GRAY);
        g.drawString("Doanh thu (đ)", 6, 16);
        g.drawString("Thời gian", (rong - g.getFontMetrics().stringWidth("Thời gian")) / 2, cao - 5);
        g.setColor(mauBieuDo);
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int diemXCu = 0, diemYCu = 0;
        boolean coDiemTruoc = false;

        for (DuLieuDoanhThu p : diem) {
            int moc = p.moc();
            if (moc > mocHienTai) continue;
            int x = trai + (int) (rongBieuDo * (moc - mocDau) / (mocSpan > 0 ? mocSpan : 1));
            int y = duoi - (int) (caoBieuDo * p.doanhThu() / max);
            if (coDiemTruoc) g.drawLine(diemXCu, diemYCu, x, y);
            g.fillOval(x - 4, y - 4, 8, 8);
            if (moc == mocHienTai) yHienTai = y;
            if (moc % 2 == (theoThang ? 1 : 0)) {
                String nhan = String.format(theoThang ? "%d" : "%02dH", moc);
                g.drawString(nhan, x - g.getFontMetrics().stringWidth(nhan) / 2, duoi + 18);
            }
            diemXCu = x;
            diemYCu = y;
            coDiemTruoc = true;
        }

        if (yHienTai == -1) {
            yHienTai = duoi;
        }

        g.setColor(Color.GRAY);
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[] {5f, 5f}, 0f));
        g.drawLine(xHienTai, tren, xHienTai, duoi);
        g.setColor(mauBieuDo);
        g.fillOval(xHienTai - 4, yHienTai - 4, 8, 8);
        g.setColor(Color.GRAY);
        g.setStroke(new BasicStroke(3.5f));
        g.drawOval(xHienTai - 5, yHienTai - 5, 10, 10);
        
        g.dispose();
    }
}
