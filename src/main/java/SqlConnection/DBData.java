package SqlConnection;

import com.sg188.server.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBData {
    private static final String URL = "jdbc:mysql://"+ Config.getInstance().dbHost+":"+Config.getInstance().dbPort+"/"+Config.getInstance().dbData;
    private static final String USERNAME = Config.getInstance().dbUser;
    private static final String PASSWORD = Config.getInstance().dbPassword;

    private static Connection connection;

    // Phương thức để tạo kết nối
    public static void openConnection() throws SQLException {
        connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    // Phương thức để đóng kết nối
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Phương thức để lấy kết nối (nếu cần)
    public static Connection getConnection() {
        return connection;
    }
}
