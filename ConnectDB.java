
package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {

    private static final String URL =
        "jdbc:sqlserver://localhost:1433;"
        + "databaseName=QLQuanCaPhe;"
        + "integratedSecurity=true;"
        + "encrypt=true;"
        + "trustServerCertificate=true;";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Ket noi database thanh cong!");
            }
        } catch (SQLException e) {
            System.out.println("Ket noi that bai!");
            e.printStackTrace();
        }
    }
}
