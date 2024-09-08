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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Connect {

    private static final String URL = "jdbc:mysql://"+ Config.getInstance().dbHost+":"+Config.getInstance().dbPort+"/"+Config.getInstance().dbName;
    private static final String USERNAME = Config.getInstance().dbUser;
    private static final String PASSWORD = Config.getInstance().dbPassword;

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException ex) {
            return null;
        }
    }
}
