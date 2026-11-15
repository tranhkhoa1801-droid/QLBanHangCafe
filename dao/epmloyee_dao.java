package dao;

import entity.Employee;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EmployeeDAO {
    private static Employee map(ResultSet rs) throws SQLException {
        return new Employee(rs.getString("MaNV"), rs.getString("HoTen"), rs.getString("SDT"),
                rs.getString("DiaChi"), rs.getString("TenDangNhap"), rs.getString("MatKhau"),
                rs.getString("VaiTro"), rs.getBoolean("TrangThai"));
    }

    private static Employee first(List<Employee> list) { return list.isEmpty() ? null : list.get(0); }

    public List<Employee> getAll() {
        return DBConnection.query("SELECT * FROM NHANVIEN ORDER BY MaNV", EmployeeDAO::map);
    }

    public List<Employee> search(String kw) {
        String k = "%" + kw.trim() + "%";
        return DBConnection.query(
            "SELECT * FROM NHANVIEN WHERE MaNV LIKE ? OR HoTen LIKE ? OR TenDangNhap LIKE ? ORDER BY MaNV",
            EmployeeDAO::map, k, k, k);
    }

    public Employee findById(String maNV) {
        return first(DBConnection.query("SELECT * FROM NHANVIEN WHERE MaNV=?", EmployeeDAO::map, maNV));
    }

    public Employee findByUsername(String user) {
        return first(DBConnection.query("SELECT * FROM NHANVIEN WHERE TenDangNhap=?", EmployeeDAO::map, user));
    }

    public void insert(Employee e) {
        DBConnection.update(
            "INSERT INTO NHANVIEN(MaNV,HoTen,SDT,DiaChi,TenDangNhap,MatKhau,VaiTro,TrangThai) VALUES(?,?,?,?,?,?,?,?)",
            e.maNV(), e.hoTen(), e.sdt(), e.diaChi(), e.tenDangNhap(), e.matKhau(), e.vaiTro(), e.hoatDong());
    }

    public void update(Employee e) {
        DBConnection.update("UPDATE NHANVIEN SET HoTen=?,SDT=?,DiaChi=?,TenDangNhap=?,MatKhau=?,VaiTro=? WHERE MaNV=?",
            e.hoTen(), e.sdt(), e.diaChi(), e.tenDangNhap(), e.matKhau(), e.vaiTro(), e.maNV());
    }

    public void delete(String maNV) {
        DBConnection.update("DELETE FROM NHANVIEN WHERE MaNV=?", maNV);
    }

    public void setActive(String maNV, boolean active) {
        DBConnection.update("UPDATE NHANVIEN SET TrangThai=? WHERE MaNV=?", active, maNV);
    }

 
    public boolean hasInvoices(String maNV) {
        boolean tableExists = !DBConnection.query("SELECT 1 AS X WHERE OBJECT_ID('HOADON') IS NOT NULL", rs -> 1).isEmpty();
        return tableExists && !DBConnection.query("SELECT 1 FROM HOADON WHERE MaNV=?", rs -> 1, maNV).isEmpty();
    }
}
