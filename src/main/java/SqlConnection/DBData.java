package SqlConnection;

import com.sg188.lib.Log;

import java.sql.Connection;
import java.sql.SQLException;

public class DBData {
    
    public static void openConnection() throws SQLException {
        try {
            Connection testConn = ConnectionPool.getDBDataConnection();
            testConn.close(); // Trả về pool
            Log.info("DBData connection pool verified");
        } catch (SQLException e) {
            Log.error("Failed to verify DBData connection pool", e);
            throw e;
        }
    }

    public static void closeConnection() {
    }

    public static Connection getConnection() {
        try {
            return ConnectionPool.getDBDataConnection();
        } catch (SQLException e) {
            Log.error("Failed to get DBData connection from pool", e);
            return null;
        }
    }
}
