/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data;

import SqlConnection.DBData;
import com.sg188.data.DataCenter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author ADMIN
 */
public class DataMenuNpc {

    private static final String Npc9 = "Thành lập,Xin vào gia tộc,Mở cửa ải gia tộc,Vào ải gia tộc,Trang bị Byakugan";
     private static final String Npc3 = "Ghép đá,Cường hóa,Nâng cấp bùa nổ,Tách cường hóa,Dịch chuyển trang bị,Khảm ngọc,Tách ngọc khảm,Ghép cải trang,Tách cải trang";
    public static String[] textAll;

    private static String getSplitText(String str) {
        StringBuilder str2 = new StringBuilder();
        String[] str3 = str.split(",");
        int i = 0;
        for (String str4 : str3) {
            if (i > 0) {
                str2.append(";");
            }
            str2.append(str4);
            i++;
        }
        return str2.toString();
    }
    public static void loadDataText() {
        try {
            Connection conn = DBData.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT npc_id, menu_text FROM npc_menu");
            ResultSet resultSet = stmt.executeQuery();

            // Đọc dữ liệu từ ResultSet và lưu vào mảng textAll
            while (resultSet.next()) {
                short npcId = resultSet.getShort("npc_id");
                String menuText = resultSet.getString("menu_text");
                textAll[npcId] = menuText;
            }

            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void LoadTextNpc() {
        textAll = new String[DataCenter.gI().NpcTemplate.length];
//        textAll[9] = getSplitText(Npc9);
//         textAll[3] = getSplitText(Npc3);

    }

    public static String textNpc(short idNpc) {
        String text;
        try {
            return textAll[idNpc];
        } catch (Exception e) {
            return "";
        }

    }
}
