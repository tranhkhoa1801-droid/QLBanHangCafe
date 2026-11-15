package controller;

import java.sql.Connection;
import java.sql.SQLException;

import connectDB.ConnectDB;

/**
 * Mở Connection, chạy công việc, tự commit/rollback và đóng Connection.
 * Lỗi từ SQL Server (THROW trong trigger/stored procedure) được đổi thành BusinessException
 * nên message tiếng Việt hiển thị thẳng cho người dùng.
 */
public final class Tx {

    @FunctionalInterface
    public interface Work<T> {
        T run(Connection con) throws SQLException;
    }

    private Tx() {
    }

    public static <T> T read(Work<T> work) {
        try (Connection con = ConnectDB.getConnection()) {
            return work.run(con);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(ex.getMessage(), ex);
        }
    }

    public static <T> T write(Work<T> work) {
        try (Connection con = ConnectDB.getConnection()) {
            boolean oldAuto = con.getAutoCommit();
            con.setAutoCommit(false);
            try {
                T result = work.run(con);
                con.commit();
                return result;
            } catch (Exception ex) {
                try {
                    con.rollback();
                } catch (SQLException ignore) {
                    // transaction có thể đã bị rollback bởi trigger
                }
                if (ex instanceof BusinessException) {
                    throw (BusinessException) ex;
                }
                throw new BusinessException(ex.getMessage(), ex);
            } finally {
                con.setAutoCommit(oldAuto);
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(ex.getMessage(), ex);
        }
    }
}
