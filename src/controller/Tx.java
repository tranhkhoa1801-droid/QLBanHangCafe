package controller;

import java.sql.Connection;
import java.sql.SQLException;

import connectDB.ConnectDB;

public final class Tx {

    @FunctionalInterface
    public interface Work<T> {
        T run(Connection con) throws SQLException;
    }

    private Tx() {
    }

    private static Connection open() throws SQLException {
        Connection con = ConnectDB.getConnection();
        if (con == null || con.isClosed()) {
            ConnectDB.getInstance().connect();
            con = ConnectDB.getConnection();
        }
        if (con == null) {
            throw new BusinessException("Không thể kết nối đến cơ sở dữ liệu!");
        }
        return con;
    }

    public static <T> T read(Work<T> work) {
        try {
            Connection con = open();
            return work.run(con);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(ex.getMessage(), ex);
        }
    }

    public static <T> T write(Work<T> work) {
        Connection con = null;
        boolean oldAuto = true;
        try {
            con = open();
            oldAuto = con.getAutoCommit();
            con.setAutoCommit(false);

            T result = work.run(con);
            con.commit();
            return result;
        } catch (Exception ex) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ignored) {
                }
            }
            if (ex instanceof BusinessException) {
                throw (BusinessException) ex;
            }
            throw new BusinessException(ex.getMessage(), ex);
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(oldAuto);
                } catch (SQLException ignored) {
                }
            }
        }
    }
}
