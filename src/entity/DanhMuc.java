package entity;

import java.util.List;
import java.util.Objects;
import dao.DanhMucDAO;
import dao.SanPhamDAO;

public class DanhMuc {
    private String maDM;
    private String tenDM;

    public DanhMuc() {
    }

    public DanhMuc(String maDM) {
        this.maDM = maDM;
    }

    public DanhMuc(String maDM, String tenDM) {
        this.maDM = maDM;
        this.tenDM = tenDM;
    }

    // Class diagram methods
    public boolean save() {
        DanhMucDAO dao = new DanhMucDAO();
        if (dao.getById(maDM) != null) {
            return dao.update(this);
        } else {
            return dao.insert(this);
        }
    }

    public List<DanhMuc> getAll() {
        return new DanhMucDAO().getAll();
    }

    public int getQuantity() {
        int count = 0;
        List<SanPham> list = new SanPhamDAO().getAll();
        for (SanPham sp : list) {
            if (sp.getDanhMuc() != null && maDM != null && maDM.equals(sp.getDanhMuc().getMaDM())) {
                count++;
            }
        }
        return count;
    }

    public String getMaDM() {
        return maDM;
    }

    public void setMaDM(String maDM) {
        this.maDM = maDM;
    }

    public String getTenDM() {
        return tenDM;
    }

    public void setTenDM(String tenDM) {
        this.tenDM = tenDM;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maDM);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        DanhMuc other = (DanhMuc) obj;
        return Objects.equals(maDM, other.maDM);
    }

    @Override
    public String toString() {
        return tenDM != null && !tenDM.isEmpty() ? tenDM : maDM;
    }
}
