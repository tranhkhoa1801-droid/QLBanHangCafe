package controller;

/** Lỗi nghiệp vụ: message hiển thị thẳng cho người dùng. */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
