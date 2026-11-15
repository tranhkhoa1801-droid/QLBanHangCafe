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

/** Xem / xuất hóa đơn đã lập. */
public class HoaDonForm extends JFrame {

    private final HoaDonController ctrl = new HoaDonController();
    private final String maHD;

    public HoaDonForm(String maHD) {
        this.maHD = maHD;
        setTitle("Hóa đơn " + maHD);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JTextArea area = new JTextArea(24, 50);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        try {
            area.setText(ctrl.taoNoiDungHoaDon(maHD));
        } catch (BusinessException ex) {
            area.setText(ex.getMessage());
        }

        JButton btnExport = new JButton("Xuất file .txt");
        btnExport.addActionListener(e -> export());
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnExport);
        south.add(btnClose);

        add(new JScrollPane(area), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
    }

    private void export() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("HoaDon_" + maHD + ".txt"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            ctrl.xuatFileHoaDon(maHD, fc.getSelectedFile());
            JOptionPane.showMessageDialog(this, "Đã xuất hóa đơn!");
        } catch (BusinessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
