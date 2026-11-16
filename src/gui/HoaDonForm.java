package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import controller.BusinessException;
import controller.HoaDonController;

public class HoaDonForm extends JFrame {

	private static final long serialVersionUID = 1L;

	private final HoaDonController ctrl = new HoaDonController();
	private final String maHD;

	public HoaDonForm(String maHD) {
		this.maHD = maHD;
		setTitle("Hóa đơn " + maHD);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		initUI();
		pack();
		setLocationRelativeTo(null);
	}

	private void initUI() {
		JTextArea txtNoiDung = new JTextArea(24, 50);
		txtNoiDung.setEditable(false);
		txtNoiDung.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

		try {
			txtNoiDung.setText(ctrl.taoNoiDungHoaDon(maHD));
		} catch (BusinessException ex) {
			txtNoiDung.setText(ex.getMessage());
		}

		JButton btnExport = new JButton("Xuất file .txt");
		btnExport.addActionListener(e -> xuatFile());

		JButton btnClose = new JButton("Đóng");
		btnClose.addActionListener(e -> dispose());

		JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnlSouth.add(btnExport);
		pnlSouth.add(btnClose);

		add(new JScrollPane(txtNoiDung), BorderLayout.CENTER);
		add(pnlSouth, BorderLayout.SOUTH);
	}

	private void xuatFile() {
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFile(new File("HoaDon_" + maHD + ".txt"));
		if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file = fc.getSelectedFile();
		if (file.exists()) {
			int confirm = JOptionPane.showConfirmDialog(this, "File đã tồn tại, bạn có muốn ghi đè không?",
					"Xác nhận", JOptionPane.YES_NO_OPTION);
			if (confirm != JOptionPane.YES_OPTION) {
				return;
			}
		}

		try {
			ctrl.xuatFileHoaDon(maHD, file);
			JOptionPane.showMessageDialog(this, "Đã xuất hóa đơn thành công!");
		} catch (BusinessException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}
}
