/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlConnection;

/**
 *
 * @author ADMIN
 */
import com.sg188.server.Config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Wrapper class sử dụng Connection Pool để tối ưu hiệu suất
 */
public class Connect {

    /**
     * Lấy connection từ connection pool
     * @return Connection từ pool hoặc null nếu có lỗi
     */
    public static Connection getConnection() {
        try {
            return ConnectionPool.getConnection();
        } catch (SQLException ex) {
            com.sg188.lib.Log.error("Failed to get connection from pool", ex);
            return null;
        }
    }
}
