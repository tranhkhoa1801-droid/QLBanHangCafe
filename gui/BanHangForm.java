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

import controller.BanHangController;
import controller.BusinessException;
import controller.LoginController;
import entity.ChiTietHoaDon;
import entity.SanPham;

public class BanHangForm extends JPanel {

	private static final long serialVersionUID = 1L;

	private final BanHangController ctrl = new BanHangController();
	private final DecimalFormat money = new DecimalFormat("#,###");

	private final DefaultTableModel banModel = createReadOnlyModel("Bàn", "Trạng thái");
	private final DefaultTableModel detailModel = createReadOnlyModel("STT", "Mã SP", "Tên sản phẩm", "Đơn giá",
			"Số lượng", "Thành tiền");

	private final JTable tblBan = new JTable(banModel);
	private final JTable tblDetail = new JTable(detailModel);
	private final JComboBox<SanPham> cboSanPham = new JComboBox<>();
	private final JComboBox<String> cboPttt = new JComboBox<>(
			BanHangController.PHUONG_THUC_THANH_TOAN.toArray(new String[0]));

	private final JSpinner spnQty = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
	private final JSpinner spnDiscount = new JSpinner(new SpinnerNumberModel(0, 0, 100, 5));

	private final JLabel lblTotal = new JLabel();
	private final JLabel lblDiscount = new JLabel();
	private final JLabel lblFinal = new JLabel();

	private final JButton btnAdd = new JButton("Thêm món");
	private final JButton btnInc = new JButton("+");
	private final JButton btnDec = new JButton("-");
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
		initUI();
		loadSanPham();
		runSafe(this::loadBan);
		runSafe(this::loadDetails);
	}

	private void initUI() {
		Font fontThuong = new Font("Tahoma", Font.PLAIN, 14);
		tblBan.setFont(fontThuong);
		tblDetail.setFont(fontThuong);
		tblBan.setRowHeight(24);
		tblDetail.setRowHeight(24);
		tblBan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblDetail.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane leftScroll = new JScrollPane(tblBan);
		leftScroll.setPreferredSize(new Dimension(240, 400));
		leftScroll.setBorder(BorderFactory.createTitledBorder("Danh sách bàn"));

		cboSanPham.setPreferredSize(new Dimension(280, 28));
		cboSanPham.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof SanPham) {
					SanPham sp = (SanPham) value;
					setText(sp.getTenSP() + " - " + money.format(sp.getGiaBan()));
				} else {
					setText("---- Chọn sản phẩm ----");
				}
				return this;
			}
		});

		JPanel pnlAdd = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnlAdd.add(cboSanPham);
		pnlAdd.add(new JLabel("SL:"));
		pnlAdd.add(spnQty);
		pnlAdd.add(btnAdd);

		JPanel pnlItemBtns = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnlItemBtns.add(new JLabel("Món đang chọn:"));
		pnlItemBtns.add(btnDec);
		pnlItemBtns.add(btnInc);
		pnlItemBtns.add(btnRemove);
		pnlItemBtns.add(btnCancel);

		JPanel pnlPayRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnlPayRow.add(new JLabel("Giảm giá (%):"));
		pnlPayRow.add(spnDiscount);
		pnlPayRow.add(new JLabel("Thanh toán bằng:"));
		pnlPayRow.add(cboPttt);

		lblFinal.setFont(new Font("Tahoma", Font.BOLD, 18));
		btnPay.setFont(new Font("Tahoma", Font.BOLD, 16));

		JPanel pnlSummary = new JPanel(new GridLayout(0, 1));
		pnlSummary.add(pnlItemBtns);
		pnlSummary.add(pnlPayRow);
		pnlSummary.add(lblTotal);
		pnlSummary.add(lblDiscount);
		pnlSummary.add(lblFinal);
		pnlSummary.add(btnPay);

		JScrollPane detailScroll = new JScrollPane(tblDetail);
		detailScroll.setBorder(BorderFactory.createTitledBorder("Hóa đơn của bàn"));

		JPanel pnlCenter = new JPanel(new BorderLayout());
		pnlCenter.add(pnlAdd, BorderLayout.NORTH);
		pnlCenter.add(detailScroll, BorderLayout.CENTER);
		pnlCenter.add(pnlSummary, BorderLayout.SOUTH);

		add(leftScroll, BorderLayout.WEST);
		add(pnlCenter, BorderLayout.CENTER);

		tblBan.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting() || dangNap) {
				return;
			}
			int row = tblBan.getSelectedRow();
			if (row < 0 || row >= banList.size()) {
				return;
			}
			banChon = banList.get(row);
			runSafe(this::loadDetails);
		});

		tblDetail.getSelectionModel().addListSelectionListener(e -> updateButtons());
		spnDiscount.addChangeListener(e -> updateTotals());
		btnAdd.addActionListener(e -> onAdd());
		btnInc.addActionListener(e -> onChangeQuantity(1));
		btnDec.addActionListener(e -> onChangeQuantity(-1));
		btnRemove.addActionListener(e -> onRemove());
		btnCancel.addActionListener(e -> onCancel());
		btnPay.addActionListener(e -> onPay());
	}

	private static DefaultTableModel createReadOnlyModel(String... cols) {
		return new DefaultTableModel(cols, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
	}

	private void loadSanPham() {
		cboSanPham.addItem(null);
		runSafe(() -> {
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
			int selectedIndex = -1;

			for (int i = 0; i < banList.size(); i++) {
				String ban = banList.get(i);
				String trangThai = banCoKhach.contains(ban) ? "Có khách" : "Trống";
				banModel.addRow(new Object[] { ban, trangThai });
				if (ban.equals(banChon)) {
					selectedIndex = i;
				}
			}

			if (selectedIndex >= 0) {
				tblBan.setRowSelectionInterval(selectedIndex, selectedIndex);
			}
		} finally {
			dangNap = false;
		}
	}

	private void loadDetails() {
		if (banChon == null) {
			detailList = new ArrayList<>();
		} else {
			detailList = ctrl.loadChiTietTheoBan(banChon);
		}

		detailModel.setRowCount(0);
		for (int i = 0; i < detailList.size(); i++) {
			ChiTietHoaDon d = detailList.get(i);
			detailModel.addRow(new Object[] {
					i + 1,
					d.getMaSP(),
					d.getTenSP(),
					money.format(d.getGiaBanHienTai()),
					d.getSoLuong(),
					money.format(d.getThanhTien())
			});
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

	private String getMaNhanVienDangNhap() {
		var user = LoginController.getCurrentUser();
		if (user == null) {
			throw new BusinessException("Chưa đăng nhập!");
		}
		return user.maNV();
	}

	private void onAdd() {
		SanPham sp = (SanPham) cboSanPham.getSelectedItem();
		runSafe(() -> {
			ctrl.themMon(banChon, getMaNhanVienDangNhap(), sp, (Integer) spnQty.getValue());
			cboSanPham.setSelectedIndex(0);
			spnQty.setValue(1);
			refresh();
		});
	}

	private ChiTietHoaDon getSelectedDetail() {
		int row = tblDetail.getSelectedRow();
		if (row < 0 || row >= detailList.size()) {
			return null;
		}
		return detailList.get(row);
	}

	private void onChangeQuantity(int delta) {
		ChiTietHoaDon d = getSelectedDetail();
		if (d == null) {
			return;
		}
		int newQty = d.getSoLuong() + delta;
		if (newQty <= 0) {
			onRemove();
			return;
		}
		runSafe(() -> {
			ctrl.capNhatSoLuong(banChon, d.getMaSP(), newQty);
			loadDetails();
		});
	}

	private void onRemove() {
		ChiTietHoaDon d = getSelectedDetail();
		if (d == null) {
			return;
		}
		int confirm = JOptionPane.showConfirmDialog(this, "Xóa món \"" + d.getTenSP() + "\" khỏi hóa đơn?",
				"Xác nhận", JOptionPane.YES_NO_OPTION);
		if (confirm != JOptionPane.YES_OPTION) {
			return;
		}
		runSafe(() -> {
			ctrl.xoaMon(banChon, d.getMaSP());
			refresh();
		});
	}

	private void onCancel() {
		int confirm = JOptionPane.showConfirmDialog(this, "Hủy toàn bộ hóa đơn của " + banChon + "?",
				"Xác nhận", JOptionPane.YES_NO_OPTION);
		if (confirm != JOptionPane.YES_OPTION) {
			return;
		}
		runSafe(() -> {
			ctrl.huyHoaDonBan(banChon);
			refresh();
		});
	}

	private void onPay() {
		int pct = (Integer) spnDiscount.getValue();
		double total = BanHangController.tinhTongTien(detailList);
		double finalPrice = total - BanHangController.tinhGiamGia(total, pct);

		int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận thanh toán " + money.format(finalPrice) + " ?",
				"Thanh toán", JOptionPane.YES_NO_OPTION);
		if (confirm != JOptionPane.YES_OPTION) {
			return;
		}
		runSafe(() -> {
			String maHD = ctrl.thanhToan(banChon, pct, (String) cboPttt.getSelectedItem());
			spnDiscount.setValue(0);
			refresh();
			new HoaDonForm(maHD).setVisible(true);
		});
	}

	private void runSafe(Runnable action) {
		try {
			action.run();
		} catch (BusinessException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Thông báo", JOptionPane.WARNING_MESSAGE);
		}
	}
}
