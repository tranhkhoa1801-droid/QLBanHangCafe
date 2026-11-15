package app;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import dao.TaiKhoanDAO;
import dao.DanhMucDAO;
import dao.SanPhamDAO;

import entity.TaiKhoan;
import entity.DanhMuc;
import entity.SanPham;

public class CoffeeManagement extends JFrame {

    private TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private DanhMucDAO danhMucDAO = new DanhMucDAO();
    private SanPhamDAO sanPhamDAO = new SanPhamDAO();

    private JTextField txtDangNhap;
    private JPasswordField txtMatKhau;

    private JTextField txtMaDM, txtTenDM;
    private JTable tblDanhMuc;
    private DefaultTableModel modelDM;

    private JTextField txtMaSP, txtTenSP;
    private JTextField txtGiaVon, txtGiaBan, txtTimKiem;
    private JComboBox<String> cboDanhMuc;
    private JTable tblSanPham;
    private DefaultTableModel modelSP;

    public CoffeeManagement() {
        setTitle("COFFEE MANAGEMENT");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        giaoDienDangNhap();
    }

    // ================= ĐĂNG NHẬP =================

    private void giaoDienDangNhap() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(new Color(245, 240, 230));

        JPanel form = new JPanel(new GridLayout(5, 1, 10, 10));
        form.setPreferredSize(new Dimension(350, 280));
        form.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel lblTitle = new JLabel("COFFEE MANAGEMENT", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));

        txtDangNhap = new JTextField();
        txtMatKhau = new JPasswordField();

        JButton btnDangNhap = new JButton("ĐĂNG NHẬP");

        form.add(lblTitle);
        form.add(txtDangNhap);
        form.add(txtMatKhau);
        form.add(btnDangNhap);

        btnDangNhap.addActionListener(e -> dangNhap());
        txtMatKhau.addActionListener(e -> dangNhap());

        main.add(form);
        setContentPane(main);
    }

    private void dangNhap() {
        String tenDN = txtDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());

        if (tenDN.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tài khoản và mật khẩu!");
            return;
        }

        TaiKhoan tk = taiKhoanDAO.authenticate(tenDN, matKhau);

        if (tk != null) {
            JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
            giaoDienChinh();
        } else {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!");
        }
    }

    // ================= GIAO DIỆN CHÍNH =================

    private void giaoDienChinh() {
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Danh mục", giaoDienDanhMuc());
        tabs.addTab("Sản phẩm", giaoDienSanPham());

        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("  COFFEE MANAGEMENT");
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JButton btnDangXuat = new JButton("Đăng xuất");
        btnDangXuat.addActionListener(e -> {
            int chon = JOptionPane.showConfirmDialog(this,
                    "Bạn có muốn đăng xuất không?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION);

            if (chon == JOptionPane.YES_OPTION) {
                giaoDienDangNhap();
                revalidate();
                repaint();
            }
        });

        header.add(title, BorderLayout.WEST);
        header.add(btnDangXuat, BorderLayout.EAST);

        JPanel main = new JPanel(new BorderLayout());
        main.add(header, BorderLayout.NORTH);
        main.add(tabs, BorderLayout.CENTER);

        setContentPane(main);
        revalidate();
        repaint();
    }

    // ================= QUẢN LÝ DANH MỤC =================

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

        JPanel buttons = new JPanel();
        buttons.add(btnThem);
        buttons.add(btnSua);
        buttons.add(btnXoa);
        buttons.add(btnLamMoi);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        modelDM = new DefaultTableModel(
                new String[]{"Mã danh mục", "Tên danh mục"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblDanhMuc = new JTable(modelDM);
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

    private boolean kiemTraDanhMuc() {
        String ma = txtMaDM.getText().trim();
        String ten = txtTenDM.getText().trim();

        if (!ma.matches("^DM\\d{2}$")) {
            JOptionPane.showMessageDialog(this, "Mã danh mục phải có dạng DM01, DM02...");
            txtMaDM.requestFocus();
            return false;
        }

        if (!ten.matches("^[\\p{L}0-9\\s&]+$")) {
            JOptionPane.showMessageDialog(this, "Tên danh mục không hợp lệ!");
            txtTenDM.requestFocus();
            return false;
        }

        return true;
    }

    private void themDanhMuc() {
        if (!kiemTraDanhMuc()) return;

        DanhMuc dm = new DanhMuc(txtMaDM.getText().trim(), txtTenDM.getText().trim());

        if (danhMucDAO.insert(dm)) {
            JOptionPane.showMessageDialog(this, "Thêm danh mục thành công!");
            loadDanhMuc();
            capNhatComboDanhMuc();
            lamMoiDanhMuc();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại! Có thể mã danh mục đã tồn tại.");
        }
    }

    private void suaDanhMuc() {
        if (!kiemTraDanhMuc()) return;

        DanhMuc dm = new DanhMuc(txtMaDM.getText().trim(), txtTenDM.getText().trim());

        if (danhMucDAO.update(dm)) {
            JOptionPane.showMessageDialog(this, "Sửa danh mục thành công!");
            loadDanhMuc();
            lamMoiDanhMuc();
        } else {
            JOptionPane.showMessageDialog(this, "Sửa danh mục thất bại!");
        }
    }

    private void xoaDanhMuc() {
        String ma = txtMaDM.getText().trim();

        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục cần xóa!");
            return;
        }

        int chon = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa danh mục " + ma + "?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (chon == JOptionPane.YES_OPTION) {
            if (danhMucDAO.delete(ma)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadDanhMuc();
                capNhatComboDanhMuc();
                lamMoiDanhMuc();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không thể xóa! Có thể danh mục đang được sản phẩm sử dụng.");
            }
        }
    }

    private void loadDanhMuc() {
        modelDM.setRowCount(0);
        List<DanhMuc> list = danhMucDAO.getAll();
        for (int i = 0; i < list.size(); i++) {
            DanhMuc dm = list.get(i);
            modelDM.addRow(new Object[]{dm.getMaDM(), dm.getTenDM()});
        }
        capNhatComboDanhMuc();
    }

    private void lamMoiDanhMuc() {
        txtMaDM.setText("");
        txtTenDM.setText("");
        txtMaDM.setEditable(true);
        tblDanhMuc.clearSelection();
    }

    // ================= QUẢN LÝ SẢN PHẨM =================

    private JPanel giaoDienSanPham() {
        JPanel main = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
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
        form.add(new JLabel("Giá vốn:"));
        form.add(txtGiaVon);
        form.add(new JLabel("Giá bán:"));
        form.add(txtGiaBan);
        form.add(new JLabel("Danh mục:"));
        form.add(cboDanhMuc);

        JButton btnThem = new JButton("Thêm");
        JButton btnSua = new JButton("Sửa");
        JButton btnXoa = new JButton("Xóa");
        JButton btnLamMoi = new JButton("Làm mới");
        JButton btnTim = new JButton("Tìm kiếm");

        txtTimKiem = new JTextField(15);

        JPanel buttons = new JPanel();
        buttons.add(btnThem);
        buttons.add(btnSua);
        buttons.add(btnXoa);
        buttons.add(btnLamMoi);
        buttons.add(txtTimKiem);
        buttons.add(btnTim);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        modelSP = new DefaultTableModel(
                new String[]{"Mã SP", "Tên SP", "Giá vốn", "Giá bán", "Mã danh mục"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblSanPham = new JTable(modelSP);
        JScrollPane scroll = new JScrollPane(tblSanPham);

        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        main.add(top, BorderLayout.NORTH);
        main.add(scroll, BorderLayout.CENTER);

        btnThem.addActionListener(e -> themSanPham());
        btnSua.addActionListener(e -> suaSanPham());
        btnXoa.addActionListener(e -> xoaSanPham());
        btnLamMoi.addActionListener(e -> lamMoiSanPham());
        btnTim.addActionListener(e -> timSanPham());

        tblSanPham.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblSanPham.getSelectedRow();
                if (row >= 0) {
                    txtMaSP.setText(modelSP.getValueAt(row, 0).toString());
                    txtTenSP.setText(modelSP.getValueAt(row, 1).toString());
                    txtGiaVon.setText(modelSP.getValueAt(row, 2).toString());
                    txtGiaBan.setText(modelSP.getValueAt(row, 3).toString());
                    cboDanhMuc.setSelectedItem(modelSP.getValueAt(row, 4).toString());
                    txtMaSP.setEditable(false);
                }
            }
        });

        loadSanPham();

        return main;
    }

    private boolean kiemTraSanPham() {
        String ma = txtMaSP.getText().trim();
        String ten = txtTenSP.getText().trim();
        String von = txtGiaVon.getText().trim();
        String ban = txtGiaBan.getText().trim();

        if (!ma.matches("^SP\\d{2}$")) {
            JOptionPane.showMessageDialog(this, "Mã sản phẩm phải có dạng SP01, SP02...");
            txtMaSP.requestFocus();
            return false;
        }

        if (!ten.matches("^[\\p{L}0-9\\s]+$")) {
            JOptionPane.showMessageDialog(this, "Tên sản phẩm không hợp lệ!");
            txtTenSP.requestFocus();
            return false;
        }

        if (!von.matches("^\\d+(\\.\\d+)?$") || !ban.matches("^\\d+(\\.\\d+)?$")) {
            JOptionPane.showMessageDialog(this, "Giá vốn và giá bán phải là số!");
            return false;
        }

        double giaVon = Double.parseDouble(von);
        double giaBan = Double.parseDouble(ban);

        if (giaVon <= 0 || giaBan <= 0) {
            JOptionPane.showMessageDialog(this, "Giá phải lớn hơn 0!");
            return false;
        }

        if (giaBan < giaVon) {
            JOptionPane.showMessageDialog(this, "Giá bán không được thấp hơn giá vốn!");
            return false;
        }

        if (cboDanhMuc.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Bạn cần tạo danh mục trước!");
            return false;
        }

        return true;
    }

    private SanPham laySanPhamTuForm() {
        String maDM = cboDanhMuc.getSelectedItem() != null ? cboDanhMuc.getSelectedItem().toString() : "";
        DanhMuc dm = new DanhMuc(maDM, "");

        return new SanPham(
                txtMaSP.getText().trim(),
                txtTenSP.getText().trim(),
                Double.parseDouble(txtGiaVon.getText().trim()),
                Double.parseDouble(txtGiaBan.getText().trim()),
                dm
        );
    }

    private void themSanPham() {
        if (!kiemTraSanPham()) return;

        if (sanPhamDAO.insert(laySanPhamTuForm())) {
            JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công!");
            loadSanPham();
            lamMoiSanPham();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại! Có thể mã sản phẩm đã tồn tại.");
        }
    }

    private void suaSanPham() {
        if (!kiemTraSanPham()) return;

        if (tblSanPham.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần sửa!");
            return;
        }

        if (sanPhamDAO.update(laySanPhamTuForm())) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadSanPham();
            lamMoiSanPham();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
        }
    }

    private void xoaSanPham() {
        String ma = txtMaSP.getText().trim();

        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa!");
            return;
        }

        int chon = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa sản phẩm " + ma + "?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (chon == JOptionPane.YES_OPTION) {
            if (sanPhamDAO.delete(ma)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadSanPham();
                lamMoiSanPham();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại! Có thể sản phẩm đang nằm trong hóa đơn.");
            }
        }
    }

    private void timSanPham() {
        String tuKhoa = txtTimKiem.getText().trim();
        modelSP.setRowCount(0);

        List<SanPham> list = sanPhamDAO.search(tuKhoa);

        for (int i = 0; i < list.size(); i++) {
            SanPham sp = list.get(i);
            String maDMStr = (sp.getDanhMuc() != null) ? sp.getDanhMuc().getMaDM() : "";
            
            modelSP.addRow(new Object[]{
                sp.getMaSP(),
                sp.getTenSP(),
                sp.getGiaVon(),
                sp.getGiaBan(),
                maDMStr
            });
        }
    }

    private void loadSanPham() {
        modelSP.setRowCount(0);

        List<SanPham> list = sanPhamDAO.getAll();

        for (int i = 0; i < list.size(); i++) {
            SanPham sp = list.get(i);
            String maDMStr = (sp.getDanhMuc() != null) ? sp.getDanhMuc().getMaDM() : "";
            
            modelSP.addRow(new Object[]{
                sp.getMaSP(),
                sp.getTenSP(),
                sp.getGiaVon(),
                sp.getGiaBan(),
                maDMStr
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

        tblSanPham.clearSelection();

        if (cboDanhMuc.getItemCount() > 0) {
            cboDanhMuc.setSelectedIndex(0);
        }

        loadSanPham();
    }

    private void capNhatComboDanhMuc() {
        if (cboDanhMuc == null) return;

        cboDanhMuc.removeAllItems();

        List<DanhMuc> list = danhMucDAO.getAll();

        for (int i = 0; i < list.size(); i++) {
            DanhMuc dm = list.get(i);
            cboDanhMuc.addItem(dm.getMaDM());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CoffeeManagement().setVisible(true);
        });
    }
}