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
					setText(sp.getTenSP() + " - " + money.format(sp.getGiaBan()) + " đ");
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

		JPanel pnlRightBottom = new JPanel(new BorderLayout());
		pnlRightBottom.add(pnlSummary, BorderLayout.CENTER);
		pnlRightBottom.add(btnPay, BorderLayout.EAST);

		JPanel pnlRight = new JPanel(new BorderLayout(6, 6));
		pnlRight.add(pnlAdd, BorderLayout.NORTH);
		pnlRight.add(new JScrollPane(tblDetail), BorderLayout.CENTER);
		pnlRight.add(pnlRightBottom, BorderLayout.SOUTH);

		add(leftScroll, BorderLayout.WEST);
		add(pnlRight, BorderLayout.CENTER);

		hookEvents();
	}

	private void hookEvents() {
		tblBan.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && !dangNap) {
				int row = tblBan.getSelectedRow();
				banChon = (row >= 0 && row < banList.size()) ? banList.get(row) : null;
				runSafe(this::loadDetails);
			}
		});

		btnAdd.addActionListener(e -> onAdd());
		btnInc.addActionListener(e -> onAdjustQty(1));
		btnDec.addActionListener(e -> onAdjustQty(-1));
		btnRemove.addActionListener(e -> onRemoveItem());
		btnCancel.addActionListener(e -> onCancelInvoice());
		btnPay.addActionListener(e -> onPay());

		spnDiscount.addChangeListener(e -> updateTotals());
	}

	public void reloadData() {
		loadSanPham();
		runSafe(this::loadBan);
		runSafe(this::loadDetails);
	}

	private void loadSanPham() {
		cboSanPham.removeAllItems();
		cboSanPham.addItem(null);
		for (SanPham sp : ctrl.loadSanPham()) {
			cboSanPham.addItem(sp);
		}
	}

	private void loadBan() {
		dangNap = true;
		try {
			banList = ctrl.loadDanhSachBan();
			banCoKhach = ctrl.loadBanDangCoKhach();

			banModel.setRowCount(0);
			int selectIndex = -1;
			for (int i = 0; i < banList.size(); i++) {
				String b = banList.get(i);
				boolean coKhach = banCoKhach.contains(b);
				banModel.addRow(new Object[] { b, coKhach ? "Có khách" : "Trống" });
				if (b.equals(banChon)) {
					selectIndex = i;
				}
			}

			if (selectIndex >= 0) {
				tblBan.setRowSelectionInterval(selectIndex, selectIndex);
			} else if (!banList.isEmpty()) {
				tblBan.setRowSelectionInterval(0, 0);
				banChon = banList.get(0);
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
		int stt = 1;
		for (ChiTietHoaDon d : detailList) {
			detailModel.addRow(new Object[] {
					stt++,
					d.getMaSP(),
					d.getTenSP(),
					money.format(d.getGiaBanHienTai()),
					d.getSoLuong(),
					money.format(d.getThanhTien())
			});
		}
		updateTotals();
	}

	private void updateTotals() {
		double total = BanHangController.tinhTongTien(detailList);
		int discPercent = (Integer) spnDiscount.getValue();
		double discount = BanHangController.tinhGiamGia(total, discPercent);
		double finalMoney = Math.max(0, total - discount);

		lblTotal.setText("Tổng cộng: " + money.format(total) + " đ");
		lblDiscount.setText("Giảm giá: -" + money.format(discount) + " đ");
		lblFinal.setText("CẦN THANH TOÁN: " + money.format(finalMoney) + " đ");

		boolean hasItems = !detailList.isEmpty();
		btnPay.setEnabled(hasItems);
		btnCancel.setEnabled(hasItems);
		btnInc.setEnabled(hasItems);
		btnDec.setEnabled(hasItems);
		btnRemove.setEnabled(hasItems);
	}

	private void refresh() {
		loadBan();
		loadDetails();
	}

	private String getMaNhanVienDangNhap() {
		var user = LoginController.getCurrentUser();
		if (user == null) {
			return "NV01";
		}
		return user.getMaNV();
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
			throw new BusinessException("Chưa chọn món trong bảng chi tiết!");
		}
		return detailList.get(row);
	}

	private void onAdjustQty(int delta) {
		runSafe(() -> {
			ChiTietHoaDon d = getSelectedDetail();
			int newQty = d.getSoLuong() + delta;
			if (newQty <= 0) {
				int opt = JOptionPane.showConfirmDialog(this,
						"Số lượng về 0, bạn có muốn xóa món \"" + d.getTenSP() + "\" không?",
						"Xác nhận", JOptionPane.YES_NO_OPTION);
				if (opt == JOptionPane.YES_OPTION) {
					ctrl.xoaMon(banChon, d.getMaSP());
					refresh();
				}
				return;
			}
			ctrl.capNhatSoLuong(banChon, d.getMaSP(), newQty);
			refresh();
		});
	}

	private void onRemoveItem() {
		runSafe(() -> {
			ChiTietHoaDon d = getSelectedDetail();
			int opt = JOptionPane.showConfirmDialog(this,
					"Bạn chắc chắn muốn xóa món \"" + d.getTenSP() + "\"?",
					"Xác nhận", JOptionPane.YES_NO_OPTION);
			if (opt == JOptionPane.YES_OPTION) {
				ctrl.xoaMon(banChon, d.getMaSP());
				refresh();
			}
		});
	}

	private void onCancelInvoice() {
		runSafe(() -> {
			if (detailList.isEmpty()) {
				throw new BusinessException("Bàn này chưa có món!");
			}
			int opt = JOptionPane.showConfirmDialog(this,
					"Hủy toàn bộ order của " + banChon + "?",
					"Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (opt == JOptionPane.YES_OPTION) {
				ctrl.huyHoaDonBan(banChon);
				refresh();
			}
		});
	}

	private void onPay() {
		runSafe(() -> {
			if (detailList.isEmpty()) {
				throw new BusinessException("Chưa có món nào để thanh toán!");
			}
			int discount = (Integer) spnDiscount.getValue();
			String pttt = (String) cboPttt.getSelectedItem();

			int opt = JOptionPane.showConfirmDialog(this,
					"Xác nhận thanh toán cho " + banChon + "?\nHình thức: " + pttt,
					"Xác nhận", JOptionPane.YES_NO_OPTION);
			if (opt != JOptionPane.YES_OPTION) {
				return;
			}

			String maHD = ctrl.thanhToan(banChon, discount, pttt);
			JOptionPane.showMessageDialog(this, "Thanh toán thành công hóa đơn " + maHD + "!");
			spnDiscount.setValue(0);
			refresh();

			new HoaDonForm(maHD).setVisible(true);
		});
	}

	private void runSafe(Action action) {
		try {
			action.run();
		} catch (BusinessException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Thông báo", JOptionPane.WARNING_MESSAGE);
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Có lỗi xảy ra: " + ex.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@FunctionalInterface
	private interface Action {
		void run() throws Exception;
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
}
