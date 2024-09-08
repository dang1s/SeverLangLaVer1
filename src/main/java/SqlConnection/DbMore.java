/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlConnection;

import Data.DataCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg188.lib.Log;
import com.sg188.server.CreateGiftCode.Code;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class DbMore {

    public static boolean InsertGiftCode(List<Code> codes) {
        try (Connection conn = Connect.getConnection();) {
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO giftcode set code = ? , infoitem = ?")) {
                ObjectMapper json = new ObjectMapper();
                for (Code code : codes) {
                    String info = json.writeValueAsString(code);
                    ps.setString(1, code.Code);
                    ps.setString(2, info);
                    ps.executeUpdate();
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void LoadGiftCode() {
        try (Connection conn = Connect.getConnection();) {
            try (ResultSet rs = conn.createStatement().executeQuery("Select * from giftcode")) {
                ObjectMapper json = new ObjectMapper();
                while (rs.next()) {
                    Code code = json.readValue(rs.getString("infoitem"), Code.class);
                    code.Code = rs.getString("Code");
                    DataCode.Codes.add(code);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.debug("code load size " + DataCode.Codes.size());
    }
    public static void saveGiftcode(Code code) {
        try (Connection conn = Connect.getConnection();) {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE giftcode set InfoItem = ?where Code = ?");) {
                ObjectMapper json = new ObjectMapper();
                String info = json.writeValueAsString(code);
                ps.setString(1, info);
                ps.setString(2, code.Code);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
