package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import controller.NhanVienController;
import entity.NhanVien;

public class NhanVienForm extends JPanel {
    private static final long serialVersionUID = 1L;

    private final NhanVienController controller = new NhanVienController();

    private JTextField txtMaNV, txtHoTen, txtSDT, txtCaLamViec;
    private JTextField txtTenDangNhap, txtTimKiem;
    private JPasswordField txtMatKhau;
    private JComboBox<String> cboVaiTro, cboTrangThai;
    private JTable tblNhanVien;
    private DefaultTableModel modelNV;

    public NhanVienForm() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel form = new JPanel(new GridLayout(4, 4, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin nhân viên"));

        txtMaNV = new JTextField();
        txtHoTen = new JTextField();
        txtSDT = new JTextField();
        txtCaLamViec = new JTextField("Ca sáng (6:00 - 14:00)");
        txtTenDangNhap = new JTextField();
        txtMatKhau = new JPasswordField();

        cboVaiTro = new JComboBox<>(new String[] { "Quản Lý", "Thu Ngân", "Pha Chế", "Phục Vụ" });
        cboTrangThai = new JComboBox<>(new String[] { "Đang làm việc", "Đã nghỉ việc / Khóa" });

        form.add(new JLabel("Mã NV:"));
        form.add(txtMaNV);
        form.add(new JLabel("Họ tên:"));
        form.add(txtHoTen);

        form.add(new JLabel("Số điện thoại:"));
        form.add(txtSDT);
        form.add(new JLabel("Vai trò:"));
        form.add(cboVaiTro);

        form.add(new JLabel("Ca làm việc:"));
        form.add(txtCaLamViec);
        form.add(new JLabel("Trạng thái:"));
        form.add(cboTrangThai);

        form.add(new JLabel("Tên đăng nhập:"));
        form.add(txtTenDangNhap);
        form.add(new JLabel("Mật khẩu:"));
        form.add(txtMatKhau);

        // Buttons
        JButton btnThem = new JButton("Thêm NV");
        JButton btnSua = new JButton("Cập nhật");
        JButton btnXoa = new JButton("Xóa");
        JButton btnKhoa = new JButton("Khóa/Mở khóa");
        JButton btnLamMoi = new JButton("Làm mới");

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        pnlButtons.add(btnThem);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnKhoa);
        pnlButtons.add(btnLamMoi);

        // Search panel
        txtTimKiem = new JTextField(15);
        JButton btnTim = new JButton("Tìm kiếm");
        pnlButtons.add(new JLabel("   Tìm kiếm:"));
        pnlButtons.add(txtTimKiem);
        pnlButtons.add(btnTim);

        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.add(form, BorderLayout.CENTER);
        top.add(pnlButtons, BorderLayout.SOUTH);

        // Table
        modelNV = new DefaultTableModel(new String[] {
                "Mã NV", "Họ tên", "Vai trò", "SĐT", "Tài khoản", "Ca làm việc", "Trạng thái"
        }, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblNhanVien = new JTable(modelNV);
        tblNhanVien.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(tblNhanVien);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // Events
        btnThem.addActionListener(e -> themNhanVien());
        btnSua.addActionListener(e -> suaNhanVien());
        btnXoa.addActionListener(e -> xoaNhanVien());
        btnKhoa.addActionListener(e -> khoaNhanVien());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnTim.addActionListener(e -> timKiem());

        tblNhanVien.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblNhanVien.getSelectedRow();
                if (row >= 0) {
                    txtMaNV.setText(modelNV.getValueAt(row, 0).toString());
                    txtHoTen.setText(modelNV.getValueAt(row, 1).toString());
                    cboVaiTro.setSelectedItem(modelNV.getValueAt(row, 2).toString());
                    txtSDT.setText(modelNV.getValueAt(row, 3) != null ? modelNV.getValueAt(row, 3).toString() : "");
                    txtTenDangNhap.setText(modelNV.getValueAt(row, 4) != null ? modelNV.getValueAt(row, 4).toString() : "");
                    txtCaLamViec.setText(modelNV.getValueAt(row, 5) != null ? modelNV.getValueAt(row, 5).toString() : "");
                    cboTrangThai.setSelectedItem(modelNV.getValueAt(row, 6).toString());

                    txtMaNV.setEditable(false);
                    txtTenDangNhap.setEditable(false);
                }
            }
        });
    }

    public void reloadData() {
        loadData();
    }

    private void loadData() {
        modelNV.setRowCount(0);
        List<NhanVien> list = controller.getAll();
        for (NhanVien nv : list) {
            modelNV.addRow(new Object[] {
                    nv.getMaNV(),
                    nv.getHoTen(),
                    nv.getVaiTro(),
                    nv.getsDT(),
                    nv.getTaiKhoan() != null ? nv.getTaiKhoan().getTenDangNhap() : "",
                    nv.getCaLamViec(),
                    nv.isActive() ? "Đang làm việc" : "Đã nghỉ việc / Khóa"
            });
        }
    }

    private void timKiem() {
        String kw = txtTimKiem.getText().trim();
        modelNV.setRowCount(0);
        List<NhanVien> list = controller.search(kw);
        for (NhanVien nv : list) {
            modelNV.addRow(new Object[] {
                    nv.getMaNV(),
                    nv.getHoTen(),
                    nv.getVaiTro(),
                    nv.getsDT(),
                    nv.getTaiKhoan() != null ? nv.getTaiKhoan().getTenDangNhap() : "",
                    nv.getCaLamViec(),
                    nv.isActive() ? "Đang làm việc" : "Đã nghỉ việc / Khóa"
            });
        }
    }

    private void themNhanVien() {
        try {
            String maNV = txtMaNV.getText().trim();
            String hoTen = txtHoTen.getText().trim();
            String vaiTro = (String) cboVaiTro.getSelectedItem();
            String sdt = txtSDT.getText().trim();
            String ca = txtCaLamViec.getText().trim();
            String user = txtTenDangNhap.getText().trim();
            String pass = new String(txtMatKhau.getPassword()).trim();

            NhanVien nv = new NhanVien(maNV, hoTen, vaiTro, sdt, (String) null, ca, true);
            controller.add(nv, user, pass);

            JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
            lamMoi();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaNhanVien() {
        try {
            String maNV = txtMaNV.getText().trim();
            String hoTen = txtHoTen.getText().trim();
            String vaiTro = (String) cboVaiTro.getSelectedItem();
            String sdt = txtSDT.getText().trim();
            String ca = txtCaLamViec.getText().trim();
            boolean active = cboTrangThai.getSelectedIndex() == 0;
            String pass = new String(txtMatKhau.getPassword()).trim();

            NhanVien nv = new NhanVien(maNV, hoTen, vaiTro, sdt, (String) null, ca, active);
            controller.update(nv, pass.isEmpty() ? null : pass);

            JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!");
            lamMoi();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaNhanVien() {
        int row = tblNhanVien.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa!");
            return;
        }
        String maNV = modelNV.getValueAt(row, 0).toString();
        int opt = JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn xóa nhân viên " + maNV + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            try {
                controller.delete(maNV);
                JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!");
                lamMoi();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void khoaNhanVien() {
        int row = tblNhanVien.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!");
            return;
        }
        String maNV = modelNV.getValueAt(row, 0).toString();
        String currentStatus = modelNV.getValueAt(row, 6).toString();
        boolean willActive = !currentStatus.equals("Đang làm việc");

        try {
            controller.setActive(maNV, willActive);
            JOptionPane.showMessageDialog(this, (willActive ? "Mở khóa" : "Khóa") + " nhân viên thành công!");
            lamMoi();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void lamMoi() {
        txtMaNV.setText("");
        txtHoTen.setText("");
        txtSDT.setText("");
        txtCaLamViec.setText("Ca sáng (6:00 - 14:00)");
        txtTenDangNhap.setText("");
        txtMatKhau.setText("");
        txtTimKiem.setText("");
        txtMaNV.setEditable(true);
        txtTenDangNhap.setEditable(true);
        tblNhanVien.clearSelection();
        loadData();
    }
}
