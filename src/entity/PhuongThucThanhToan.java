package entity;

public enum PhuongThucThanhToan {
    TIEN_MAT("Tiền mặt"),
    CHUYEN_KHOAN("Chuyển khoản"),
    THE("Thẻ"),
    VI_DIEN_TU("Ví điện tử");

    private final String ten;

    PhuongThucThanhToan(String ten) {
        this.ten = ten;
    }

    public String getTen() {
        return ten;
    }

    @Override
    public String toString() {
        return ten;
    }

    public static PhuongThucThanhToan fromString(String text) {
        if (text == null) return TIEN_MAT;
        for (PhuongThucThanhToan p : values()) {
            if (p.ten.equalsIgnoreCase(text.trim()) || p.name().equalsIgnoreCase(text.trim())) {
                return p;
            }
        }
        return TIEN_MAT;
    }
}
