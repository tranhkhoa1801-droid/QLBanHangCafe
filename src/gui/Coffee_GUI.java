package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import controller.DanhMucController;
import controller.LoginController;
import controller.SanPhamController;
import entity.DanhMuc;
import entity.NhanVien;
import entity.SanPham;

public class Coffee_GUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private final LoginController loginController = new LoginController();
    private final DanhMucController danhMucController = new DanhMucController();
    private final SanPhamController sanPhamController = new SanPhamController();
    private final DecimalFormat money = new DecimalFormat("#,###");

    // Login components
    private JTextField txtDangNhap;
    private JPasswordField txtMatKhau;

    // Danh muc components
    private JTextField txtMaDM, txtTenDM;
    private JTable tblDanhMuc;
    private DefaultTableModel modelDM;

    // San pham components
    private JTextField txtMaSP, txtTenSP;
    private JTextField txtGiaVon, txtGiaBan, txtTimKiem;
    private JComboBox<String> cboDanhMuc;
    private JTable tblSanPham;
    private DefaultTableModel modelSP;

    // Sub-forms
    private BanHangForm banHangForm;
    private TraCuuHoaDonForm traCuuForm;
    private ThongKeForm thongKeForm;
    private NhanVienForm nhanVienForm;

    public Coffee_GUI() {
        setTitle("HỆ THỐNG QUẢN LÝ QUÁN CÀ PHÊ");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        giaoDienDangNhap();
    }

    private void giaoDienDangNhap() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(new Color(245, 240, 230));

        JPanel form = new JPanel(new GridLayout(6, 1, 10, 10));
        form.setPreferredSize(new Dimension(380, 320));
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 150, 120), 2),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)));
        form.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("COFFEE MANAGEMENT", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(110, 60, 20));

        JLabel lblSub = new JLabel("Đăng nhập hệ thống", JLabel.CENTER);
        lblSub.setFont(new Font("Arial", Font.PLAIN, 14));

        txtDangNhap = new JTextField("manager_ha");
        txtDangNhap.setToolTipText("Tên đăng nhập (ví dụ: manager_ha, cashier_tung)");
        txtMatKhau = new JPasswordField("password123");

        JButton btnDangNhap = new JButton("ĐĂNG NHẬP");
        btnDangNhap.setBackground(new Color(110, 60, 20));
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setFont(new Font("Arial", Font.BOLD, 14));

        form.add(lblTitle);
        form.add(lblSub);
        form.add(txtDangNhap);
        form.add(txtMatKhau);
        form.add(btnDangNhap);

        btnDangNhap.addActionListener(e -> dangNhap());
        txtDangNhap.addActionListener(e -> dangNhap());
        txtMatKhau.addActionListener(e -> dangNhap());

        main.add(form);
        setContentPane(main);
        revalidate();
        repaint();
    }

    private void dangNhap() {
        String tenDN = txtDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());

        try {
            NhanVien nv = loginController.login(tenDN, matKhau);
            JOptionPane.showMessageDialog(this, "Đăng nhập thành công!\nXin chào: " + nv.getHoTen()
                    + " (" + nv.getVaiTro() + ")");
            giaoDienChinh();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi đăng nhập", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu: " + ex.getMessage(),
                    "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void giaoDienChinh() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 13));

        banHangForm = new BanHangForm();
        traCuuForm = new TraCuuHoaDonForm();
        thongKeForm = new ThongKeForm();
        nhanVienForm = new NhanVienForm();

        tabs.addTab(" Bán hàng ", banHangForm);
        tabs.addTab(" Sản phẩm ", giaoDienSanPham());
        tabs.addTab(" Danh mục ", giaoDienDanhMuc());
        tabs.addTab(" Nhân viên ", nhanVienForm);
        tabs.addTab(" Tra cứu hóa đơn ", traCuuForm);
        tabs.addTab(" Thống kê doanh thu ", thongKeForm);

        tabs.addChangeListener(e -> {
            int idx = tabs.getSelectedIndex();
            if (idx == 0 && banHangForm != null) {
                banHangForm.reloadData();
            } else if (idx == 1) {
                loadSanPham();
                capNhatComboDanhMuc();
            } else if (idx == 2) {
                loadDanhMuc();
            } else if (idx == 3 && nhanVienForm != null) {
                nhanVienForm.reloadData();
            } else if (idx == 4 && traCuuForm != null) {
                traCuuForm.reloadData();
            } else if (idx == 5 && thongKeForm != null) {
                thongKeForm.reloadData();
            }
        });

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(235, 230, 220));
        header.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JLabel title = new JLabel("COFFEE MANAGEMENT SYSTEM");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(90, 50, 15));

        NhanVien user = LoginController.getCurrentUser();
        String userInfo = (user != null) ? user.getHoTen() + " [" + user.getVaiTro() + "]" : "";
        JLabel lblUser = new JLabel("Nhân viên: " + userInfo + "   ");
        lblUser.setFont(new Font("Arial", Font.ITALIC, 14));

        JButton btnDangXuat = new JButton("Đăng xuất");
        btnDangXuat.addActionListener(e -> {
            int chon = JOptionPane.showConfirmDialog(this,
                    "Bạn có muốn đăng xuất không?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION);

            if (chon == JOptionPane.YES_OPTION) {
                loginController.logout();
                giaoDienDangNhap();
            }
        });

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightHeader.setOpaque(false);
        rightHeader.add(lblUser);
        rightHeader.add(btnDangXuat);

        header.add(title, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);

        JPanel main = new JPanel(new BorderLayout());
        main.add(header, BorderLayout.NORTH);
        main.add(tabs, BorderLayout.CENTER);

        setContentPane(main);
        revalidate();
        repaint();
    }

    // Giao diện quản lý Danh Mục
    private JPanel giaoDienDanhMuc() {
        JPanel main = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin danh mục"));

        txtMaDM = new JTextField();
        txtTenDM = new JTextField();

        form.add(new JLabel("Mã danh mục:"));
        form.add(txtMaDM);
        form.add(new JLabel("Tên danh mục:"));
        form.add(txtTenDM);

        JButton btnThem = new JButton("Thêm");
        JButton btnSua = new JButton("Sửa");
        JButton btnXoa = new JButton("Xóa");
        JButton btnLamMoi = new JButton("Làm mới");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(btnThem);
        buttons.add(btnSua);
        buttons.add(btnXoa);
        buttons.add(btnLamMoi);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        modelDM = new DefaultTableModel(
                new String[] { "Mã danh mục", "Tên danh mục" }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblDanhMuc = new JTable(modelDM);
        tblDanhMuc.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(tblDanhMuc);

        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        main.add(top, BorderLayout.NORTH);
        main.add(scroll, BorderLayout.CENTER);

        btnThem.addActionListener(e -> themDanhMuc());
        btnSua.addActionListener(e -> suaDanhMuc());
        btnXoa.addActionListener(e -> xoaDanhMuc());
        btnLamMoi.addActionListener(e -> lamMoiDanhMuc());

        tblDanhMuc.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblDanhMuc.getSelectedRow();
                if (row >= 0) {
                    txtMaDM.setText(modelDM.getValueAt(row, 0).toString());
                    txtTenDM.setText(modelDM.getValueAt(row, 1).toString());
                    txtMaDM.setEditable(false);
                }
            }
        });

        loadDanhMuc();
        return main;
    }

    private void themDanhMuc() {
        String ma = txtMaDM.getText().trim();
        String ten = txtTenDM.getText().trim();
        if (danhMucController.themDanhMuc(this, ma, ten)) {
            lamMoiDanhMuc();
        }
    }

    private void suaDanhMuc() {
        String ma = txtMaDM.getText().trim();
        String ten = txtTenDM.getText().trim();
        if (danhMucController.suaDanhMuc(this, ma, ten)) {
            lamMoiDanhMuc();
        }
    }

    private void xoaDanhMuc() {
        String ma = txtMaDM.getText().trim();
        if (danhMucController.xoaDanhMuc(this, ma)) {
            lamMoiDanhMuc();
        }
    }

    private void loadDanhMuc() {
        if (modelDM == null) return;
        modelDM.setRowCount(0);
        List<DanhMuc> list = danhMucController.getAll();
        for (DanhMuc dm : list) {
            modelDM.addRow(new Object[] { dm.getMaDM(), dm.getTenDM() });
        }
    }

    private void lamMoiDanhMuc() {
        txtMaDM.setText("");
        txtTenDM.setText("");
        txtMaDM.setEditable(true);
        if (tblDanhMuc != null) {
            tblDanhMuc.clearSelection();
        }
        loadDanhMuc();
        capNhatComboDanhMuc();
    }

    // Giao diện quản lý Sản Phẩm
    private JPanel giaoDienSanPham() {
        JPanel main = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(3, 4, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin sản phẩm"));

        txtMaSP = new JTextField();
        txtTenSP = new JTextField();
        txtGiaVon = new JTextField();
        txtGiaBan = new JTextField();
        cboDanhMuc = new JComboBox<>();

        form.add(new JLabel("Mã sản phẩm:"));
        form.add(txtMaSP);
        form.add(new JLabel("Tên sản phẩm:"));
        form.add(txtTenSP);

        form.add(new JLabel("Giá vốn (đ):"));
        form.add(txtGiaVon);
        form.add(new JLabel("Giá bán (đ):"));
        form.add(txtGiaBan);

        form.add(new JLabel("Danh mục:"));
        form.add(cboDanhMuc);

        JButton btnThem = new JButton("Thêm");
        JButton btnSua = new JButton("Sửa");
        JButton btnXoa = new JButton("Xóa");
        JButton btnLamMoi = new JButton("Làm mới");

        txtTimKiem = new JTextField(15);
        JButton btnTim = new JButton("Tìm kiếm");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(btnThem);
        buttons.add(btnSua);
        buttons.add(btnXoa);
        buttons.add(btnLamMoi);
        buttons.add(new JLabel("   Tìm kiếm:"));
        buttons.add(txtTimKiem);
        buttons.add(btnTim);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        modelSP = new DefaultTableModel(
                new String[] { "Mã SP", "Tên sản phẩm", "Giá vốn", "Giá bán", "Mã DM" }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblSanPham = new JTable(modelSP);
        tblSanPham.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(tblSanPham);

        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        main.add(top, BorderLayout.NORTH);
        main.add(scroll, BorderLayout.CENTER);

        btnThem.addActionListener(e -> themSanPham());
        btnSua.addActionListener(e -> suaSanPham());
        btnXoa.addActionListener(e -> xoaSanPham());
        btnLamMoi.addActionListener(e -> lamMoiSanPham());
        btnTim.addActionListener(e -> timKiemSanPham());
        txtTimKiem.addActionListener(e -> timKiemSanPham());

        tblSanPham.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblSanPham.getSelectedRow();
                if (row >= 0) {
                    txtMaSP.setText(modelSP.getValueAt(row, 0).toString());
                    txtTenSP.setText(modelSP.getValueAt(row, 1).toString());
                    txtGiaVon.setText(modelSP.getValueAt(row, 2).toString());
                    txtGiaBan.setText(modelSP.getValueAt(row, 3).toString());
                    Object dmVal = modelSP.getValueAt(row, 4);
                    if (dmVal != null) {
                        cboDanhMuc.setSelectedItem(dmVal.toString());
                    }
                    txtMaSP.setEditable(false);
                }
            }
        });

        capNhatComboDanhMuc();
        loadSanPham();
        return main;
    }

    private void themSanPham() {
        String ma = txtMaSP.getText().trim();
        String ten = txtTenSP.getText().trim();
        String von = txtGiaVon.getText().trim();
        String ban = txtGiaBan.getText().trim();
        Object dm = cboDanhMuc.getSelectedItem();

        if (sanPhamController.themSanPham(this, ma, ten, von, ban, dm)) {
            lamMoiSanPham();
        }
    }

    private void suaSanPham() {
        int row = tblSanPham.getSelectedRow();
        String ma = txtMaSP.getText().trim();
        String ten = txtTenSP.getText().trim();
        String von = txtGiaVon.getText().trim();
        String ban = txtGiaBan.getText().trim();
        Object dm = cboDanhMuc.getSelectedItem();

        if (sanPhamController.suaSanPham(this, row, ma, ten, von, ban, dm)) {
            lamMoiSanPham();
        }
    }

    private void xoaSanPham() {
        int row = tblSanPham.getSelectedRow();
        String ma = txtMaSP.getText().trim();
        if (sanPhamController.xoaSanPham(this, row, ma)) {
            lamMoiSanPham();
        }
    }

    private void timKiemSanPham() {
        String tuKhoa = txtTimKiem.getText().trim();
        modelSP.setRowCount(0);
        List<SanPham> list = sanPhamController.timKiem(tuKhoa);
        for (SanPham sp : list) {
            String maDMStr = (sp.getDanhMuc() != null) ? sp.getDanhMuc().getMaDM() : "";
            modelSP.addRow(new Object[] {
                    sp.getMaSP(), sp.getTenSP(), sp.getGiaVon(), sp.getGiaBan(), maDMStr
            });
        }
    }

    private void loadSanPham() {
        if (modelSP == null) return;
        modelSP.setRowCount(0);
        List<SanPham> list = sanPhamController.getAll();
        for (SanPham sp : list) {
            String maDMStr = (sp.getDanhMuc() != null) ? sp.getDanhMuc().getMaDM() : "";
            modelSP.addRow(new Object[] {
                    sp.getMaSP(), sp.getTenSP(), sp.getGiaVon(), sp.getGiaBan(), maDMStr
            });
        }
    }

    private void lamMoiSanPham() {
        txtMaSP.setText("");
        txtTenSP.setText("");
        txtGiaVon.setText("");
        txtGiaBan.setText("");
        txtTimKiem.setText("");
        txtMaSP.setEditable(true);
        if (tblSanPham != null) {
            tblSanPham.clearSelection();
        }
        if (cboDanhMuc != null && cboDanhMuc.getItemCount() > 0) {
            cboDanhMuc.setSelectedIndex(0);
        }
        loadSanPham();
    }

    private void capNhatComboDanhMuc() {
        if (cboDanhMuc == null) return;
        cboDanhMuc.removeAllItems();
        List<DanhMuc> list = danhMucController.getAll();
        for (DanhMuc dm : list) {
            cboDanhMuc.addItem(dm.getMaDM());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Coffee_GUI().setVisible(true);
        });
    }
}
