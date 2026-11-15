package controller;

import dao.EmployeeDAO;
import entity.Employee;


public class LoginController {
    private static Employee currentUser;
    private final EmployeeDAO dao = new EmployeeDAO();

    public static Employee getCurrentUser() 
    { return currentUser; 
    	}
    
    public void login(String user, String pass) {
        if (user == null || user.isBlank() || pass == null || pass.isEmpty())
            throw new IllegalArgumentException("Vui lòng nhập tên đăng nhập và mật khẩu");
        Employee e = dao.findByUsername(user.trim());
        if (e == null || !e.matKhau().equals(pass))
            throw new IllegalArgumentException("Sai tên đăng nhập hoặc mật khẩu");
        if (!e.hoatDong())
            throw new IllegalArgumentException("Tài khoản đã bị khóa, hãy liên hệ quản lý");
        currentUser = e;
    }

    public void logout() { currentUser = null; }
}
