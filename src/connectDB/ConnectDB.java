package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    private static Connection con = null;
    private static final ConnectDB instance = new ConnectDB();

    private static String url = "jdbc:sqlserver://localhost:1433;databaseName=QLQuanCaPhe;trustServerCertificate=true";
    private static String user = "sa";
    private static String password = "sapassword";

    private ConnectDB() {
    }

    public static ConnectDB getInstance() {
        return instance;
    }

    public synchronized void connect() throws SQLException {
        if (con == null || con.isClosed()) {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            } catch (ClassNotFoundException ignored) {
            }
            con = DriverManager.getConnection(url, user, password);
        }
    }

    public synchronized void disconnect() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                con = null;
            }
        }
    }

    public static synchronized Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                instance.connect();
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        }
        return con;
    }

    public static void setConfig(String dbUrl, String dbUser, String dbPassword) {
        url = dbUrl;
        user = dbUser;
        password = dbPassword;
    }
}
