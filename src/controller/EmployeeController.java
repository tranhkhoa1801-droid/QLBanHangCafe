package controller;

import java.util.List;

import dao.NhanVienDAO;
import entity.NhanVien;

public class EmployeeController extends NhanVienController {
    private final NhanVienDAO dao = new NhanVienDAO();

    public List<NhanVien> searchEmployee(String kw) {
        return search(kw);
    }
}
