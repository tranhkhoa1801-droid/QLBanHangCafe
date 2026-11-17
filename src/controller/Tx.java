package controller;

import java.sql.Connection;
import java.sql.SQLException;

import connectDB.ConnectDB;

/** Chạy một nhóm thao tác DAO trên cùng một Connection và transaction. */
public final class Tx {
    @FunctionalInterface
    public interface Work<T> { T run(Connection con) throws SQLException; }

    private Tx() { }

    public static <T> T read(Work<T> work) {
        try (Connection con = ConnectDB.getConnection()) {
            return work.run(con);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("Lỗi truy xuất dữ liệu: " + ex.getMessage(), ex);
        }
    }

    public static <T> T write(Work<T> work) {
        try (Connection con = ConnectDB.getConnection()) {
            boolean oldAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                T result = work.run(con);
                con.commit();
                return result;
            } catch (Exception ex) {
                try { con.rollback(); } catch (SQLException rollbackError) { ex.addSuppressed(rollbackError); }
                if (ex instanceof BusinessException businessException) throw businessException;
                throw new BusinessException("Giao dịch thất bại: " + ex.getMessage(), ex);
            } finally {
                try { con.setAutoCommit(oldAutoCommit); } catch (SQLException ignored) { }
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("Không thể thực hiện giao dịch: " + ex.getMessage(), ex);
        }
    }
}
