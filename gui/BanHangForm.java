package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import controller.BusinessException;
import controller.BanHangController;
import controller.LoginController;
import entity.ChiTietHoaDon;
import entity.SanPham;

/** Màn hình bán hàng, đặt vào JTabbedPane: tabs.addTab("Bán hàng", new BanHangForm()). */
public class BanHangForm extends JPanel {

    private final BanHangController ctrl = new BanHangController();
    private final DecimalFormat money = new DecimalFormat("#,###");

    private final DefaultTableModel banModel = readOnlyModel("Bàn", "Trạng thái");
    private final DefaultTableModel detailModel =
            readOnlyModel("STT", "Mã SP", "Tên sản phẩm", "Đơn giá", "Số lượng", "Thành tiền");
    private final JTable tblBan = new JTable(banModel);
    private final JTable tblDetail = new JTable(detailModel);
    private final JComboBox<SanPham> cboSanPham = new JComboBox<>();
    private final JComboBox<String> cboPttt =
            new JComboBox<>(BanHangController.PHUONG_THUC_THANH_TOAN.toArray(new String[0]));
    private final JSpinner spnQty = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private final JSpinner spnDiscount = new JSpinner(new SpinnerNumberModel(0, 0, 100, 5));
    private final JLabel lblTotal = new JLabel();
    private final JLabel lblDiscount = new JLabel();
    private final JLabel lblFinal = new JLabel();
    private final JButton btnAdd = new JButton("Thêm món");
    private final JButton btnInc = new JButton("+");
    private final JButton btnDec = new JButton("−");
    private final JButton btnRemove = new JButton("Xóa món");
    private final JButton btnCancel = new JButton("Hủy hóa đơn");
    private final JButton btnPay = new JButton("Thanh toán");

    private List<String> banList = new ArrayList<>();
    private Set<String> banCoKhach = new HashSet<>();
    private List<ChiTietHoaDon> detailList = new ArrayList<>();
    private String banChon;
    private boolean dangNap;

    public BanHangForm() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUi();
        loadSanPham();
        guard(this::loadBan);
        guard(this::loadDetails);
    }

    // ------------------------------------------------------------ UI
    private void buildUi() {
        Font f = new Font("Tahoma", Font.PLAIN, 14);
        tblBan.setFont(f);
        tblDetail.setFont(f);
        tblBan.setRowHeight(24);
        tblDetail.setRowHeight(24);
        tblBan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblDetail.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane left = new JScrollPane(tblBan);
        left.setPreferredSize(new Dimension(240, 400));
        left.setBorder(BorderFactory.createTitledBorder("Danh sách bàn"));

        cboSanPham.setPreferredSize(new Dimension(280, 28));
        cboSanPham.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean selected, boolean focus) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                setText(value instanceof SanPham sp
                        ? sp.getTenSP() + " - " + money.format(sp.getGiaBan())
                        : "---- Chọn sản phẩm ----");
                return this;
            }
        });

        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.add(cboSanPham);
        addPanel.add(new JLabel("SL:"));
        addPanel.add(spnQty);
        addPanel.add(btnAdd);

        JPanel itemBtns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemBtns.add(new JLabel("Món đang chọn:"));
        itemBtns.add(btnDec);
        itemBtns.add(btnInc);
        itemBtns.add(btnRemove);
        itemBtns.add(btnCancel);

        JPanel payRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        payRow.add(new JLabel("Giảm giá (%):"));
        payRow.add(spnDiscount);
        payRow.add(new JLabel("Thanh toán bằng:"));
        payRow.add(cboPttt);

        lblFinal.setFont(new Font("Tahoma", Font.BOLD, 18));
        btnPay.setFont(new Font("Tahoma", Font.BOLD, 16));
        JPanel summary = new JPanel(new GridLayout(0, 1));
        summary.add(itemBtns);
        summary.add(payRow);
        summary.add(lblTotal);
        summary.add(lblDiscount);
        summary.add(lblFinal);
        summary.add(btnPay);

        JScrollPane detailScroll = new JScrollPane(tblDetail);
        detailScroll.setBorder(BorderFactory.createTitledBorder("Hóa đơn của bàn"));
        JPanel center = new JPanel(new BorderLayout());
        center.add(addPanel, BorderLayout.NORTH);
        center.add(detailScroll, BorderLayout.CENTER);
        center.add(summary, BorderLayout.SOUTH);

        add(left, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);

        tblBan.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || dangNap) {
                return;
            }
            int row = tblBan.getSelectedRow();
            if (row < 0 || row >= banList.size()) {
                return;
            }
            banChon = banList.get(row);
            guard(this::loadDetails);
        });
        tblDetail.getSelectionModel().addListSelectionListener(e -> updateButtons());
        spnDiscount.addChangeListener(e -> updateTotals());
        btnAdd.addActionListener(e -> onAdd());
        btnInc.addActionListener(e -> onChange(1));
        btnDec.addActionListener(e -> onChange(-1));
        btnRemove.addActionListener(e -> onRemove());
        btnCancel.addActionListener(e -> onCancel());
        btnPay.addActionListener(e -> onPay());
    }

    private static DefaultTableModel readOnlyModel(String... cols) {
        return new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    // ------------------------------------------------------------ Data
    private void loadSanPham() {
        cboSanPham.addItem(null);
        guard(() -> {
            for (SanPham sp : ctrl.loadSanPham()) {
                cboSanPham.addItem(sp);
            }
        });
    }

    private void loadBan() {
        dangNap = true;
        try {
            banList = ctrl.loadDanhSachBan();
            banCoKhach = ctrl.loadBanDangCoKhach();
            banModel.setRowCount(0);
            int sel = -1;
            for (int i = 0; i < banList.size(); i++) {
                String ban = banList.get(i);
                banModel.addRow(new Object[]{ban, banCoKhach.contains(ban) ? "Có khách" : "Trống"});
                if (ban.equals(banChon)) {
                    sel = i;
                }
            }
            if (sel >= 0) {
                tblBan.setRowSelectionInterval(sel, sel);
            }
        } finally {
            dangNap = false;
        }
    }

    private void loadDetails() {
        detailList = banChon == null ? new ArrayList<ChiTietHoaDon>() : ctrl.loadChiTietTheoBan(banChon);
        detailModel.setRowCount(0);
        for (int i = 0; i < detailList.size(); i++) {
            ChiTietHoaDon d = detailList.get(i);
            detailModel.addRow(new Object[]{i + 1, d.getMaSP(), d.getTenSP(),
                money.format(d.getGiaBanHienTai()), d.getSoLuong(), money.format(d.getThanhTien())});
        }
        updateTotals();
        updateButtons();
    }

    private void updateTotals() {
        double total = BanHangController.tinhTongTien(detailList);
        double discount = BanHangController.tinhGiamGia(total, (Integer) spnDiscount.getValue());
        lblTotal.setText("Tổng tiền: " + money.format(total));
        lblDiscount.setText("Giảm giá: " + money.format(discount));
        lblFinal.setText("THÀNH TIỀN: " + money.format(total - discount));
    }

    private void updateButtons() {
        boolean hasRow = tblDetail.getSelectedRow() >= 0;
        btnAdd.setEnabled(banChon != null);
        btnInc.setEnabled(hasRow);
        btnDec.setEnabled(hasRow);
        btnRemove.setEnabled(hasRow);
        btnCancel.setEnabled(!detailList.isEmpty());
        btnPay.setEnabled(!detailList.isEmpty());
    }

    private void refresh() {
        loadBan();
        loadDetails();
    }

    // ------------------------------------------------------------ Actions
    private String maNhanVienDangNhap() {
        var user = LoginController.getCurrentUser();
        if (user == null) {
            throw new BusinessException("Chưa đăng nhập!");
        }
        return user.maNV();
    }

    private void onAdd() {
        SanPham sp = (SanPham) cboSanPham.getSelectedItem();
        guard(() -> {
            ctrl.themMon(banChon, maNhanVienDangNhap(), sp, (Integer) spnQty.getValue());
            cboSanPham.setSelectedIndex(0);
            spnQty.setValue(1);
            refresh();
        });
    }

    private ChiTietHoaDon selectedDetail() {
        int row = tblDetail.getSelectedRow();
        return (row < 0 || row >= detailList.size()) ? null : detailList.get(row);
    }

    private void onChange(int delta) {
        ChiTietHoaDon d = selectedDetail();
        if (d == null) {
            return;
        }
        int newQty = d.getSoLuong() + delta;
        if (newQty <= 0) {
            onRemove();
            return;
        }
        guard(() -> {
            ctrl.capNhatSoLuong(banChon, d.getMaSP(), newQty);
            loadDetails();
        });
    }

    private void onRemove() {
        ChiTietHoaDon d = selectedDetail();
        if (d == null) {
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Xóa món \"" + d.getTenSP() + "\" khỏi hóa đơn?",
                "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        guard(() -> {
            ctrl.xoaMon(banChon, d.getMaSP());
            refresh();
        });
    }

    private void onCancel() {
        if (JOptionPane.showConfirmDialog(this, "Hủy toàn bộ hóa đơn của " + banChon + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        guard(() -> {
            ctrl.huyHoaDonBan(banChon);
            refresh();
        });
    }

    private void onPay() {
        int pct = (Integer) spnDiscount.getValue();
        double total = BanHangController.tinhTongTien(detailList);
        double finalPrice = total - BanHangController.tinhGiamGia(total, pct);
        if (JOptionPane.showConfirmDialog(this, "Xác nhận thanh toán " + money.format(finalPrice) + " ?",
                "Thanh toán", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        guard(() -> {
            String maHD = ctrl.thanhToan(banChon, pct, (String) cboPttt.getSelectedItem());
            spnDiscount.setValue(0);
            refresh();
            new HoaDonForm(maHD).setVisible(true);
        });
    }

    private void guard(Runnable r) {
        try {
            r.run();
        } catch (BusinessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }
}
