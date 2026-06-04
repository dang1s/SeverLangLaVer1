/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SqlConnection;

import EventClick.ClickTop;
import EventClick.InfoTop;
import InfoChar.InfoChar;
import InfoChar.InfoInventory;
import Template.TemplateThu;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg188.clan.Clan;
import com.sg188.lib.Log;
import com.sg188.real.Char;
import com.sg188.real.Item;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * @author ADMIN
 */
public class CharDB {

    public static Object lock_Login = new Object();
    public static Object lock_CreateChar = new Object();
    public static Object lock_UpdateChar = new Object();

//    public static Char[] getDbChar(int id) {
//        Char[] pl = null;
//        try (Connection conn = Connect.getConnection();) {
//            try (PreparedStatement ps = conn.prepareStatement("SELECT * from player where idchar = ?",ResultSet.TYPE_SCROLL_INSENSITIVE, // Cho phép di chuyển lùi
//                    ResultSet.CONCUR_READ_ONLY);) {
//                ps.setInt(1, id);
//                // ps.setString(2, name);
//                try (ResultSet rs = ps.executeQuery();) {
//                    rs.last();
//                    int i = rs.getRow();
//                    pl = new Char[i];
//                    rs.beforeFirst();
//                    int j = 0;
//                    while (rs.next()) {
//                        ObjectMapper s = new ObjectMapper();
//                        pl[j] = new Char();
//                        pl[j].Info = s.readValue(rs.getString("info"), InfoChar.class);
//                        pl[j].Bag = s.readValue(rs.getString("bag"), InfoInventory.class);
//                        pl[j].Skill = s.readValue(rs.getString("skill"), InfoSkill.class);
//                        pl[j].Point = s.readValue(rs.getString("point"), InfoPoint.class);
//                        pl[j].TuongKhac = s.readValue(rs.getString("tuongkhac"), InfoTuongKhac.class);
//                        pl[j].GiftCode = s.readValue(rs.getString("code"), InfoGiftCode.class);
//                        pl[j].Thu = s.readValue(rs.getString("thu"), InfoThu.class);
//                        pl[j].listEffect = Utlis.convertStringToEffectList(rs.getString("effect"));
//                        String phucLoi = rs.getString("PhucLoi");
//                        if (!phucLoi.isEmpty()) {
//                            pl[j].phucLoi = s.readValue(phucLoi, InfoPhucLoi.class);
//                        } else {
//                            pl[j].phucLoi = new InfoPhucLoi();
//                        }
//                        j++;
//                    }
//                }
//
//                return pl;
//            } }catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
    public static Char getCharByName(String name) {
        Char pl = null;
        try (Connection conn = Connect.getConnection()) {
            String query = "SELECT thu,idChar FROM player WHERE name = ? LIMIT 1";

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, name);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ObjectMapper s = new ObjectMapper();
                        pl = new Char();
                        JSONArray jArr = (JSONArray) JSONValue.parse(rs.getString("Thu"));
                        int len = jArr.size();
                        pl.id = rs.getInt("idChar");
                        if (jArr != null) {
                            for (int i = 0; i < len; i++) {
                                JSONObject obj = (JSONObject) jArr.get(i);
                                TemplateThu templateThu = new TemplateThu();
                                templateThu.id = Short.parseShort(obj.get("id").toString());
                                templateThu.isSucess = Boolean.parseBoolean(obj.get("isSucess").toString());
                                templateThu.NameNguoiGui = obj.get("nguoigui").toString();
                                templateThu.Title = obj.get("title").toString();
                                templateThu.NoiDungThu = obj.get("noidung").toString();
                                templateThu.Bac = Integer.parseInt(obj.get("bac").toString());
                                templateThu.BacKhoa = Integer.parseInt(obj.get("backhoa").toString());
                                templateThu.Vang = Integer.parseInt(obj.get("vang").toString());
                                templateThu.VangKhoa = Integer.parseInt(obj.get("vangkhoa").toString());
                                templateThu.Exp = Long.parseLong(obj.get("exp").toString());
                                templateThu.TimeEnd = Long.parseLong(obj.get("timeend").toString());
                                if (obj.containsKey("item")) {
                                    Item it = new Item((JSONObject) obj.get("item"));
                                    if (it != null) {
                                        templateThu.Item = it;
                                    }
                                }
                                pl.letters.add(templateThu);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pl;
    }

    public static int getCoinByName(Char pl) {
        int coin = 0;
        try (Connection conn = Connect.getConnection()) {
            String query = "SELECT balance FROM users WHERE username = ? LIMIT 1";

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, pl.user.username);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        coin = rs.getInt("balance");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return coin;
    }

    public static void updateDBThu(Char c, String name) {
        try (Connection conn = Connect.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE  player set thu = ?where name = ?")) {
                JSONArray thu = new JSONArray();
                for (int i = 0; i < c.letters.size(); i++) {
                    thu.add(c.letters.get(i).toJSONObject());
                }
                String jthu = thu.toJSONString();
                ps.setString(1, jthu);
                ps.setString(2, name);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void updateDBcoin(int coinToSubtract, int id) {
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE users SET balance = balance - ? WHERE id = ?")) {
            ps.setInt(1, coinToSubtract);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateTopSm(int id) {
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE users SET topsm = ? WHERE id = ?")) {
            ps.setInt(1, 1);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void logExchange(String name, int coinBefore, int coinAfter) {
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO coin_exchange_log (name, coin_before, coin_after, time) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, name);
            ps.setInt(2, coinBefore);
            ps.setInt(3, coinAfter);
            ps.setTimestamp(4, new java.sql.Timestamp(new Date().getTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
//
//    public static void updateDB(Char c) {
//        synchronized (lock_UpdateChar) {
//            try (Connection conn = Connect.getConnection();) {
//                try (PreparedStatement ps = conn.prepareStatement("UPDATE  player set info = ? ,bag = ? , skill = ? , point = ? ,thu = ?,code = ?,tuongkhac = ?,effect =?,phucLoi =? where name = ?");) {
//                    ObjectMapper json = new ObjectMapper();
//                    String info = json.writeValueAsString(c.Info);
//                    String skill = json.writeValueAsString(c.Skill);
//                    String point = json.writeValueAsString(c.Point);
//                    String bag = json.writeValueAsString(c.Bag);
//                    String tuongKhac = json.writeValueAsString(c.TuongKhac);
//                    String thu = json.writeValueAsString(c.Thu);
//                    String code = json.writeValueAsString(c.GiftCode);
//                    String phucloi = json.writeValueAsString(c.phucLoi);
//                    String eff = Utlis.convertEffectListToString(c.listEffect);
//                    ps.setString(1, info);
//                    ps.setString(2, bag);
//                    ps.setString(3, skill);
//                    ps.setString(4, point);
//                    ps.setString(5, thu);
//                    ps.setString(6, code);
//                    ps.setString(7, tuongKhac);
//                    ps.setString(8, eff);
//                    ps.setString(9, phucloi);
//                    ps.setString(10, c.Info.name);
//                    ps.executeUpdate();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//
//            }
//        }
//    }

//    public static void createCharDB(Char c, int numberC, int _userid) {
//        synchronized (lock_CreateChar) {
//            try (Connection conn = Connect.getConnection();) {
//                PreparedStatement ps = conn.prepareStatement("Insert Into player set idchar = ? ,indexchar = ?,name = ?, info = ? ,bag = ? , skill = ? , point = ? ,code = ?,thu = ? ,tuongkhac = ?,effect = ?,phucloi=?");
//                ps.setInt(1, _userid);
//                ps.setInt(2, numberC + 1);
//                ps.setString(3, c.Info.name);
//                c.Info.idEntity = _userid;
//                ObjectMapper json = new ObjectMapper();
//                String info = json.writeValueAsString(c.Info);
//                String skill = json.writeValueAsString(c.Skill);
//                String point = json.writeValueAsString(c.Point);
//                String bag = json.writeValueAsString(c.Bag);
//                String tuongKhac = json.writeValueAsString(c.TuongKhac);
//                String thu = json.writeValueAsString(c.Thu);
//                String code = json.writeValueAsString(c.GiftCode);
//                String eff = Utlis.convertEffectListToString(c.listEffect);
//                String phucloi = json.writeValueAsString(c.phucLoi);
////                Log.debug("INFO " + info);
////                Log.debug("skill " + skill);
////                Log.debug("point " + point);
////                Log.debug("bag " + bag);
//                ps.setString(4, info);
//                ps.setString(5, bag);
//                ps.setString(6, skill);
//                ps.setString(7, point);
//                ps.setString(8, code);
//                ps.setString(9, thu);
//                ps.setString(10, tuongKhac);
//                ps.setString(11, eff);
//                ps.setString(12, phucloi);
//                ps.executeUpdate();
//                ps.close();
//
//                /**
//                 *
//                 * *********** INSERT TO ARR SUB NAME USERS************
//                 */
//                if (c.user.subNameChar == null) {
//                    c.user.subNameChar = new String[3];
//                }
//                c.user.subNameChar[numberC] = c.Info.name;
//                ps = conn.prepareStatement("UPDATE USERS SET quantitychar = ? , arrsubname = ? where username = ?");
//                ps.setInt(1, numberC + 1);
//                ps.setString(2, json.writeValueAsString(c.user.subNameChar));
//                ps.setString(3, c.user.username);
//                ps.executeUpdate();
//                ps.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
    private static final String QueryTop = "SELECT `Name`,`level`,`Info`,`Point`,`clan`"
            + "FROM player "
            + "ORDER BY `level` DESC "
            + "LIMIT 100;";

    public static List<InfoTop> getTop(byte type) {
        List<InfoTop> list = new ArrayList<>();
        try (Connection conn = Connect.getConnection(); Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ResultSet rs = stmt.executeQuery(QueryTop)) {
            while (rs.next()) {
                try {
                    InfoTop top = new InfoTop();
                    top.name = rs.getString("Name");
                    top.level = rs.getShort("level");

                    // Parse Point với kiểm tra null
                    String pointStr = rs.getString("Point");
                    if (pointStr != null && !pointStr.isEmpty()) {
                        JSONArray jArr = (JSONArray) JSONValue.parse(pointStr);
                        if (jArr != null && !jArr.isEmpty()) {
                            JSONObject obj = (JSONObject) jArr.get(0);
                            if (obj != null && obj.get("exp") != null) {
                                top.exp = Long.parseLong(obj.get("exp").toString());
                            }
                        }
                    }

                    // Parse Info với kiểm tra null
                    String infoStr = rs.getString("Info");
                    if (infoStr != null && !infoStr.isEmpty()) {
                        JSONArray jArr = (JSONArray) JSONValue.parse(infoStr);
                        if (jArr != null && !jArr.isEmpty()) {
                            JSONObject obj = (JSONObject) jArr.get(0);
                            if (obj != null && obj.get("idhe") != null) {
                                top.idHe = Byte.parseByte(obj.get("idhe").toString());
                            }
                        }
                    }

                    // Clan với kiểm tra null
                    int clanId = rs.getInt("clan");
                    if (!rs.wasNull() && clanId > 0) {
                        Optional<Clan> g = Clan.getClanDAO().get(clanId);
                        if (g != null && g.isPresent()) {
                            Clan clan = g.get();
                            if (clan != null && clan.name != null) {
                                top.clanName = clan.name;
                            }
                        }
                    }
                    list.add(top);
                } catch (Exception e) {
                    Log.error("Error parsing top player data", e);
                    // Tiếp tục với player tiếp theo
                }
            }
        } catch (Exception e) {
            Log.error("Error getting top players", e);
            e.printStackTrace();
        }
        return list;
    }

    private static final String QueryTopNhiDong = "SELECT p.`Name`, p.`level`, p.`Info`, p.`Point`, p.`clan` "
            + "FROM player p "
            + "JOIN users u ON p.`IdChar` = u.`id` "
            + "WHERE u.`createtime` > '2026-03-11 20:00:00' "
            + "ORDER BY p.`level` DESC "
            + "LIMIT 100;";

    public static List<InfoTop> getTopNhiDong(byte type) {
        List<InfoTop> list = new ArrayList<>();
        try (Connection conn = Connect.getConnection(); Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ResultSet rs = stmt.executeQuery(QueryTopNhiDong)) {
            while (rs.next()) {
                try {
                    InfoTop top = new InfoTop();
                    top.name = rs.getString("Name");
                    top.level = rs.getShort("level");

                    // Parse Point với kiểm tra null
                    String pointStr = rs.getString("Point");
                    if (pointStr != null && !pointStr.isEmpty()) {
                        JSONArray jArr = (JSONArray) JSONValue.parse(pointStr);
                        if (jArr != null && !jArr.isEmpty()) {
                            JSONObject obj = (JSONObject) jArr.get(0);
                            if (obj != null && obj.get("exp") != null) {
                                top.exp = Long.parseLong(obj.get("exp").toString());
                            }
                        }
                    }

                    // Parse Info với kiểm tra null
                    String infoStr = rs.getString("Info");
                    if (infoStr != null && !infoStr.isEmpty()) {
                        JSONArray jArr = (JSONArray) JSONValue.parse(infoStr);
                        if (jArr != null && !jArr.isEmpty()) {
                            JSONObject obj = (JSONObject) jArr.get(0);
                            if (obj != null && obj.get("idhe") != null) {
                                top.idHe = Byte.parseByte(obj.get("idhe").toString());
                            }
                        }
                    }

                    // Clan với kiểm tra null
                    int clanId = rs.getInt("clan");
                    if (!rs.wasNull() && clanId > 0) {
                        Optional<Clan> g = Clan.getClanDAO().get(clanId);
                        if (g != null && g.isPresent()) {
                            Clan clan = g.get();
                            if (clan != null && clan.name != null) {
                                top.clanName = clan.name;
                            }
                        }
                    }
                    list.add(top);
                } catch (Exception e) {
                    Log.error("Error parsing top nhi dong player data", e);
                    // Tiếp tục với player tiếp theo
                }
            }
        } catch (Exception e) {
            Log.error("Error getting top nhi dong players", e);
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<HashMap<String, Object>> convertResultSetToList(ResultSet rs) {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        try {
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int count = resultSetMetaData.getColumnCount();
            while (rs.next()) {
                HashMap<String, Object> map = new HashMap<>();
                int index = 1;
                while (index <= count) {
                    int type = resultSetMetaData.getColumnType(index);
                    String name = resultSetMetaData.getColumnName(index);
                    switch (type) {
                        case -5: {
                            map.put(name, rs.getLong(index));
                            break;
                        }
                        case 6: {
                            map.put(name, rs.getFloat(index));
                            break;
                        }
                        case 12: {
                            map.put(name, rs.getString(index));
                            break;
                        }
                        case 1: {
                            map.put(name, rs.getString(index));
                            break;
                        }
                        case 4: {
                            map.put(name, rs.getInt(index));
                            break;
                        }
                        case 16: {
                            map.put(name, rs.getBoolean(index));
                            break;
                        }
                        case -7: {
                            map.put(name, rs.getByte(index));
                            break;
                        }

                        default: {
                            map.put(name, rs.getObject(index));
                            break;
                        }
                    }
                    ++index;
                }
                list.add(map);
            }
            rs.close();
        } catch (SQLException sQLException) {
            Log.error("convertResultSetToList ex: " + sQLException.getMessage());
        }
        return list;
    }

    private static final String bug = "SELECT *,JSON_EXTRACT(Point, '$.exp') AS `exp`\n"
            + "FROM player\n"
            + "ORDER BY CAST(JSON_EXTRACT(Point, '$.exp') AS DECIMAL(23,0)) DESC "
            + "LIMIT 15000";

    private static final String TAIPHU = "SELECT `Name`,`toptaiphu`,`Info`,`clan`"
            + "FROM player "
            + "ORDER BY `toptaiphu` DESC "
            + "LIMIT 100;";

    public static List<InfoTop> getTopTaiPhu() {
        List<InfoTop> list = new ArrayList<>();
        try (Connection conn = Connect.getConnection(); Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ResultSet rs = stmt.executeQuery(TAIPHU)) {
            while (rs.next()) {
                try {
                    InfoTop top = new InfoTop();
                    top.name = rs.getString("Name");
                    top.taiPhu = rs.getInt("toptaiphu");

                    // Parse Info với kiểm tra null
                    String infoStr = rs.getString("Info");
                    if (infoStr != null && !infoStr.isEmpty()) {
                        JSONArray jArr = (JSONArray) JSONValue.parse(infoStr);
                        if (jArr != null && !jArr.isEmpty()) {
                            JSONObject obj = (JSONObject) jArr.get(0);
                            if (obj != null && obj.get("idhe") != null) {
                                top.idHe = Byte.parseByte(obj.get("idhe").toString());
                            }
                        }
                    }

                    // Clan với kiểm tra null
                    int clanId = rs.getInt("clan");
                    if (!rs.wasNull() && clanId > 0) {
                        Optional<Clan> g = Clan.getClanDAO().get(clanId);
                        if (g != null && g.isPresent()) {
                            Clan clan = g.get();
                            if (clan != null && clan.name != null) {
                                top.clanName = clan.name;
                            }
                        }
                    }
                    list.add(top);
                } catch (Exception e) {
                    Log.error("Error parsing top tai phu player data", e);
                    // Tiếp tục với player tiếp theo
                }
            }
        } catch (Exception e) {
            Log.error("Error getting top tai phu players", e);
        }
        return list;
    }
    private static final String NHIDONGTAIPHU = "SELECT `Name`, `toptaiphu`, `Info`, `clan` "
            + "FROM player p "
            + "JOIN users u ON p.`IdChar` = u.`id` "
            + "WHERE u.`createtime` > '2024-12-01 19:00:00' "
            + "ORDER BY p.`toptaiphu` DESC "
            + "LIMIT 100;";

    public static List<InfoTop> getTopNhiDongTaiPhu() {
        List<InfoTop> list = new ArrayList<>();
        try (Connection conn = Connect.getConnection(); Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ResultSet rs = stmt.executeQuery(NHIDONGTAIPHU)) {
            while (rs.next()) {
                try {
                    InfoTop top = new InfoTop();
                    top.name = rs.getString("Name");
                    top.taiPhu = rs.getInt("toptaiphu");

                    // Parse Info với kiểm tra null
                    String infoStr = rs.getString("Info");
                    if (infoStr != null && !infoStr.isEmpty()) {
                        JSONArray jArr = (JSONArray) JSONValue.parse(infoStr);
                        if (jArr != null && !jArr.isEmpty()) {
                            JSONObject obj = (JSONObject) jArr.get(0);
                            if (obj != null && obj.get("idhe") != null) {
                                top.idHe = Byte.parseByte(obj.get("idhe").toString());
                            }
                        }
                    }

                    // Clan với kiểm tra null
                    int clanId = rs.getInt("clan");
                    if (!rs.wasNull() && clanId > 0) {
                        Optional<Clan> g = Clan.getClanDAO().get(clanId);
                        if (g != null && g.isPresent()) {
                            Clan clan = g.get();
                            if (clan != null && clan.name != null) {
                                top.clanName = clan.name;
                            }
                        }
                    }
                    list.add(top);
                } catch (Exception e) {
                    Log.error("Error parsing top nhi dong tai phu player data", e);
                    // Tiếp tục với player tiếp theo
                }
            }
        } catch (Exception e) {
            Log.error("Error getting top nhi dong tai phu players", e);
            e.printStackTrace();
        }
        return list;
    }
    private static final String CHUYENCAN = "SELECT `Name`,`topchuyencan`,`Info`,`clan`"
            + "FROM player "
            + "ORDER BY `topchuyencan` DESC "
            + "LIMIT 100;";

    public static List<InfoTop> getTopChuyenCan() {
        List<InfoTop> list = new ArrayList<>();
        try (Connection conn = Connect.getConnection(); Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ResultSet rs = stmt.executeQuery(CHUYENCAN)) {
            while (rs.next()) {
                try {
                    InfoTop top = new InfoTop();
                    top.name = rs.getString("Name");
                    top.chuyencan = rs.getInt("topchuyencan");

                    // Parse Info với kiểm tra null
                    String infoStr = rs.getString("Info");
                    if (infoStr != null && !infoStr.isEmpty()) {
                        JSONArray jArr = (JSONArray) JSONValue.parse(infoStr);
                        if (jArr != null && !jArr.isEmpty()) {
                            JSONObject obj = (JSONObject) jArr.get(0);
                            if (obj != null && obj.get("idhe") != null) {
                                top.idHe = Byte.parseByte(obj.get("idhe").toString());
                            }
                        }
                    }

                    // Clan với kiểm tra null
                    int clanId = rs.getInt("clan");
                    if (!rs.wasNull() && clanId > 0) {
                        Optional<Clan> g = Clan.getClanDAO().get(clanId);
                        if (g != null && g.isPresent()) {
                            Clan clan = g.get();
                            if (clan != null && clan.name != null) {
                                top.clanName = clan.name;
                            }
                        }
                    }
                    list.add(top);
                } catch (Exception e) {
                    Log.error("Error parsing top chuyen can player data", e);
                    // Tiếp tục với player tiếp theo
                }
            }
        } catch (Exception e) {
            Log.error("Error getting top chuyen can players", e);
            e.printStackTrace();
        }
        return list;
    }

    public static List<InfoTop> getTopChuyenCanTuan() {
        List<InfoTop> list = new ArrayList<>();
        try (Connection conn = Connect.getConnection(); Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ResultSet rs = stmt.executeQuery(CHUYENCAN)) {
            while (rs.next()) {
                try {
                    InfoTop top = new InfoTop();
                    top.name = rs.getString("Name");

                    // Parse Info với kiểm tra null
                    JSONArray infoArr = (JSONArray) JSONValue.parse(rs.getString("Info"));
                    if (infoArr != null) {
                        for (Object o : infoArr) {
                            JSONObject obj = (JSONObject) o;
                            if (obj.containsKey("chuyencantuan")) {
                                top.chuyenCanTuan = Integer.parseInt(obj.get("chuyencantuan").toString());
                            }
                            if (obj.containsKey("idhe")) {
                                top.idHe = Byte.parseByte(obj.get("idhe").toString());
                            }
                        }
                    }

                    // Clan với kiểm tra null
                    int clanId = rs.getInt("clan");
                    if (!rs.wasNull() && clanId > 0) {
                        Optional<Clan> g = Clan.getClanDAO().get(clanId);
                        if (g != null && g.isPresent()) {
                            Clan clan = g.get();
                            if (clan != null && clan.name != null) {
                                top.clanName = clan.name;
                            }
                        }
                    }
                    list.add(top);
                } catch (Exception e) {
                    Log.error("Error parsing top chuyen can tuan player data", e);
                    // Tiếp tục với player tiếp theo
                }
            }
        } catch (Exception e) {
            Log.error("Error getting top chuyen can tuan players", e);
            e.printStackTrace();
        }
        return list;
    }
    private static final String CUACAI = "SELECT `Name`, `clan`, `Info`, "
            + "CAST(JSON_UNQUOTE(JSON_EXTRACT(`Info`, '$[0].cuacai')) AS UNSIGNED) AS `temp_cuacai` "
            + "FROM `player` "
            + "ORDER BY `temp_cuacai` DESC LIMIT 100;";

    public static List<InfoTop> getTopCuaCai() {
        List<InfoTop> list = new ArrayList<>();
        try (Connection conn = Connect.getConnection(); Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ResultSet rs = stmt.executeQuery(CUACAI)) {
            while (rs.next()) {
                try {
                    InfoTop top = new InfoTop();
                    top.name = rs.getString("Name");

                    // Parse Info với kiểm tra null
                    String infoStr = rs.getString("Info");
                    if (infoStr != null && !infoStr.isEmpty()) {
                        JSONArray jArr = (JSONArray) JSONValue.parse(infoStr);
                        if (jArr != null && !jArr.isEmpty()) {
                            JSONObject obj = (JSONObject) jArr.get(0);
                            if (obj != null && obj.get("idhe") != null) {
                                top.idHe = Byte.parseByte(obj.get("idhe").toString());
                            }
                            if (obj != null && obj.get("cuacai") != null) {
                                top.cuaCai = Integer.parseInt(obj.get("cuacai").toString());
                            }
                        }
                    }

                    // Clan với kiểm tra null
                    int clanId = rs.getInt("clan");
                    if (!rs.wasNull() && clanId > 0) {
                        Optional<Clan> g = Clan.getClanDAO().get(clanId);
                        if (g != null && g.isPresent()) {
                            Clan clan = g.get();
                            if (clan != null && clan.name != null) {
                                top.clanName = clan.name;
                            }
                        }
                    }

                    list.add(top);
                } catch (Exception e) {
                    Log.error("Error parsing top cua cai player data", e);
                }
            }
        } catch (Exception e) {
            Log.error("Error getting top cua cai players", e);
        }
        return list;
    }

   public final static String napTuan = "SELECT `Name`,\n" +
            "       JSON_EXTRACT(`PhucLoi`, '$[0].naptuan') AS naptuan,\n" +
            "       `Info`,\n" +
            "       `clan`\n" +
            "FROM player\n" +
            "ORDER BY naptuan DESC\n" +
            "LIMIT 100;";

    public static List<InfoTop> getTopNapTuan() {
        return getTopNapTuanCurrentAll();
    }

    public static List<InfoTop> getTopNapTuanCurrentAll() {
        List<InfoTop> list = new ArrayList<>();
        try (Connection conn = Connect.getConnection();
             Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = stmt.executeQuery(napTuan)) {
            while (rs.next()) {
                InfoTop top = new InfoTop();
                top.name = rs.getString("Name");
                top.pointNapTuan = rs.getInt("naptuan");
                JSONArray jArr = (JSONArray) JSONValue.parse(rs.getString("Info"));
                int len = jArr.size();
                for (int k = 0; k < len; k++) {
                    JSONObject obj = (JSONObject) jArr.get(k);
                    top.idHe = Byte.parseByte(obj.get("idhe").toString());
                }
                int clanId = rs.getInt("clan");
                Optional<Clan> g = Clan.getClanDAO().get(clanId);
                if (g != null && g.isPresent()) {
                    Clan clan = g.get();
                    top.clanName = clan.name;
                }
                list.add(top);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void initTopGiaToc() {
        try (Connection conn = Connect.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT `id` FROM `clan` WHERE `level` > 0 ORDER BY `level` DESC LIMIT 100");
             ResultSet res = stmt.executeQuery()) {
            Vector<Clan> ranked = new Vector<>();
            while (res.next()) {
                int id = res.getInt("id");
                Optional<Clan> g = Clan.getClanDAO().get(id);
                if (g != null && g.isPresent()) {
                    Clan clan = g.get();
                    ranked.add(clan);
                }
            }
            ClickTop.RANKED[2] = ranked;
            if (ClickTop.RANKED[2] != null && !ClickTop.RANKED[2].isEmpty()) {
                Collections.sort(ClickTop.RANKED[2], new Comparator<Clan>() {

                    public int compare(Clan o1, Clan o2) {

                        Integer level1 = (int) ((Clan) o1).level;
                        Integer level2 = (int) ((Clan) o2).level;
                        int sComp = level2.compareTo(level1);
                        if (sComp != 0) {
                            return sComp;
                        }
                        Integer x1 = o1.getExp();
                        Integer x2 = o2.getExp();
                        return x2.compareTo(x1);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (ClickTop.RANKED[2] == null) {
                ClickTop.RANKED[2] = new Vector<>();
            }
        }
    }

    private static final String RESET = "SELECT `Name`,`inventory`"
            + "FROM player "
            + "LIMIT 20000;";

    public static Char[] getpl() {
        Char[] pl = null;
        try (Connection conn = Connect.getConnection();
             Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = stmt.executeQuery(RESET)) {
            rs.last();
            int i = rs.getRow();
            pl = new Char[i];
            rs.beforeFirst();
            int j = 0;
            while (rs.next()) {
                ObjectMapper s = new ObjectMapper();
                pl[j] = new Char();
                pl[j].Info = new InfoChar();
                pl[j].Info.name = rs.getString("Name");
                JSONArray jArr = (JSONArray) JSONValue.parse(rs.getString("inventory"));
                int len = jArr.size();
                pl[j].Bag = new InfoInventory();
                if (jArr.size() > 0) {
                    for (int k = 0; k < 1; k++) {
                        JSONObject obj = (JSONObject) jArr.get(k);
                        pl[j].Bag.bac = Long.parseLong(obj.get("bac").toString());
                        pl[j].Bag.bacKhoa = Long.parseLong(obj.get("backhoa").toString());
                        pl[j].Bag.bacBox = Long.parseLong(obj.get("bacbox").toString());
                        pl[j].Bag.bacKhoaBox = Long.parseLong(obj.get("backhoabox").toString());
                        pl[j].Bag.vang = Long.parseLong(obj.get("vang").toString());
                        pl[j].Bag.vangKhoa = Long.parseLong(obj.get("vangkhoa").toString());
                        pl[j].Bag.vangKhoaBox = Long.parseLong(obj.get("vangkhoabox").toString());
                        pl[j].Bag.vangBox = Long.parseLong(obj.get("vangbox").toString());
                        pl[j].Bag.pointNAP = Integer.parseInt(obj.get("pointnap").toString());
                        pl[j].Bag.stnSo = Byte.parseByte(obj.get("stnso").toString());
                        pl[j].Bag.stnTrung = Byte.parseByte(obj.get("stntrung").toString());
                        pl[j].Bag.stnCao = Byte.parseByte(obj.get("stncao").toString());
                        pl[j].Bag.sknSo = Byte.parseByte(obj.get("sknso").toString());
                        pl[j].Bag.sknTrung = Byte.parseByte(obj.get("skntrung").toString());
                        pl[j].Bag.sknCao = Byte.parseByte(obj.get("skncao").toString());
                        pl[j].Bag.Banh = Byte.parseByte(obj.get("banh").toString());
                        if (obj.containsKey("banhUbao")) {
                            pl[j].Bag.banhUBao = Byte.parseByte(obj.get("banhUbao").toString());
                        }
                        if (obj.containsKey("sach")) {
                            pl[j].Bag.itemSach = new Item((JSONObject) obj.get("sach"));
                        }
                    }
                }
                j++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pl;
    }

    public static List<InfoTop> getTopLuyenTap(long start, long end) {
        List<InfoTop> list = new ArrayList<>();
        // Sử dụng try-with-resources để tự động đóng kết nối
        try (Connection conn = Connect.getConnection()) {
            String sql = "SELECT p.Name, p.Info, p.clan FROM player p LIMIT 10000";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    String infoStr = rs.getString("Info");
                    if (infoStr == null || infoStr.isEmpty()) {
                        continue;
                    }

                    InfoTop top = new InfoTop();
                    top.name = rs.getString("Name");
                    top.clanName = "";

                    Object objRoot = JSONValue.parse(infoStr);
                    if (objRoot instanceof JSONArray) {
                        JSONArray infoArr = (JSONArray) objRoot;
                        for (Object o : infoArr) {
                            JSONObject obj = (JSONObject) o;

                            if (obj.containsKey("idhe")) {
                                top.idHe = Byte.parseByte(obj.get("idhe").toString());
                            }

                            if (obj.containsKey("luyentap")) {
                                top.luyenTap = Integer.parseInt(obj.get("luyentap").toString());
                            }
                        }
                    }

                    if (top.luyenTap > 0) {
                        int clanId = rs.getInt("clan");
                        if (clanId > 0) {
                            Optional<Clan> g = Clan.getClanDAO().get(clanId);
                            if (g.isPresent()) {
                                top.clanName = g.get().name;
                            }
                        }
                        list.add(top);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<InfoTop> getTopCuongHoaCurrent() {
        List<InfoTop> list = new ArrayList<>();
        try (Connection conn = Connect.getConnection()) {
            String sql = "SELECT p.Name, p.Info, p.clan FROM player p LIMIT 20000";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    InfoTop top = new InfoTop();
                    top.name = rs.getString("Name");

                    JSONArray infoArr = (JSONArray) JSONValue.parse(rs.getString("Info"));
                    if (infoArr != null) {
                        for (Object o : infoArr) {
                            JSONObject obj = (JSONObject) o;
                            if (obj.containsKey("idhe")) {
                                try {
                                    top.idHe = Byte.parseByte(obj.get("idhe").toString());
                                } catch (Exception ignored) {
                                }
                            }
                            if (obj.containsKey("cuonghoa")) {
                                try {
                                    top.cuongHoa = Integer.parseInt(obj.get("cuonghoa").toString());
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    }

                    int clanId = rs.getInt("clan");
                    Optional<Clan> g = Clan.getClanDAO().get(clanId);
                    if (g.isPresent()) {
                        top.clanName = g.get().name;
                    }
                    list.add(top);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<InfoTop> getTopCuongHoa(long start, long end) {
        List<InfoTop> list = new ArrayList<>();

        try (Connection conn = Connect.getConnection()) {

            String sql
                    = "SELECT p.Name, p.Info, p.clan, u.createtime "
                    + "FROM player p "
                    + "JOIN users u ON u.id = p.IdChar "
                    + "LIMIT 20000";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                while (rs.next()) {

                    String dateStr = rs.getString("createtime");
                    long createTime = 0;

                    if (dateStr != null && !dateStr.isEmpty()) {
                        try {
                            createTime = sdf.parse(dateStr).getTime();
                        } catch (Exception ignored) {
                        }
                    }

                    if (createTime < start || createTime > end) {
                        continue;
                    }

                    InfoTop top = new InfoTop();
                    top.name = rs.getString("Name");

                    JSONArray infoArr = (JSONArray) JSONValue.parse(rs.getString("Info"));
                    if (infoArr != null) {
                        for (Object o : infoArr) {
                            JSONObject obj = (JSONObject) o;

                            if (obj.containsKey("idhe")) {
                                try {
                                    top.idHe = Byte.parseByte(obj.get("idhe").toString());
                                } catch (Exception ignored) {
                                }
                            }

                            if (obj.containsKey("cuonghoa")) {
                                try {
                                    top.cuongHoa = Integer.parseInt(obj.get("cuonghoa").toString());
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    }

                    int clanId = rs.getInt("clan");
                    Optional<Clan> g = Clan.getClanDAO().get(clanId);
                    if (g.isPresent()) {
                        top.clanName = g.get().name;
                    }

                    list.add(top);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void savePlayer(Char pl) {
        // Kiểm tra null để tránh NullPointerException
        if (pl == null) {
            Log.warn("Attempted to save null player");
            return;
        }
        if (pl.Bag == null) {
            Log.warn("Attempted to save player with null Bag: " + (pl.Info != null ? pl.Info.name : "unknown"));
            return;
        }
        if (pl.Info == null || pl.Info.name == null || pl.Info.name.isEmpty()) {
            Log.warn("Attempted to save player with null or empty name");
            return;
        }

        try {
            JSONArray inventory = new JSONArray();
            inventory.add(pl.Bag.toJSONObject());
            String jiventory = inventory.toJSONString();
            try (Connection conn = Connect.getConnection(); PreparedStatement ps = conn.prepareStatement("UPDATE player set inventory = ? where name = ?")) {
                ps.setString(1, jiventory);
                ps.setString(2, pl.Info.name);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            Log.error("Error saving player: " + (pl.Info != null ? pl.Info.name : "unknown"), e);
            e.printStackTrace();
        }
    }

    public static void activedUser(int id) {
        try (Connection conn = Connect.getConnection(); PreparedStatement ps = conn.prepareStatement("UPDATE users SET activated = 0 WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Missing methods for compilation
    public static void updateDBCuongHoa(String playerName, int cuongHoa, int cuongHoaTuan) {
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE player SET info = JSON_SET(JSON_SET(info, '$[0].cuonghoa', ?), '$[0].cuonghoatuan', ?) WHERE Name = ?")) {
            ps.setInt(1, cuongHoa);
            ps.setInt(2, cuongHoaTuan);
            ps.setString(3, playerName);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<InfoTop> getTopNhiDong(long start, long end) {
        return getTopNhiDong((byte) 0);
    }

    public static List<InfoTop> getTopDaiHoiVoThuat(Char _char) {
        return getTop((byte) 0);
    }

    public static List<InfoTop> getTopCuaCaiTuan() {

        List<InfoTop> list = new ArrayList<>();

        try (Connection conn = Connect.getConnection()) {

            String sql = "SELECT p.Name, p.Info, p.clan FROM player p LIMIT 20000";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    InfoTop t = new InfoTop();
                    t.name = rs.getString("Name");

                    JSONArray infoArr = (JSONArray) JSONValue.parse(rs.getString("Info"));
                    if (infoArr != null) {
                        for (Object o : infoArr) {
                            JSONObject obj = (JSONObject) o;

                            if (obj.containsKey("cuacaituan")) {
                                t.cuaCaiTuan = Integer.parseInt(obj.get("cuacaituan").toString());
                            }

                            if (obj.containsKey("idhe")) {
                                t.idHe = Byte.parseByte(obj.get("idhe").toString());
                            }
                        }
                    }

                    int clanId = rs.getInt("clan");
                    Optional<Clan> g = Clan.getClanDAO().get(clanId);
                    if (g.isPresent()) {
                        t.clanName = g.get().name;
                    }

                    list.add(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<InfoTop> getTopCuongHoaTuan() {

        List<InfoTop> list = new ArrayList<>();

        try (Connection conn = Connect.getConnection()) {

            String sql = "SELECT p.Name, p.Info, p.clan FROM player p LIMIT 20000";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    InfoTop t = new InfoTop();
                    t.name = rs.getString("Name");

                    JSONArray infoArr = (JSONArray) JSONValue.parse(rs.getString("Info"));
                    if (infoArr != null) {
                        for (Object o : infoArr) {
                            JSONObject obj = (JSONObject) o;

                            if (obj.containsKey("cuonghoatuan")) {
                                t.cuongHoaTuan = Integer.parseInt(obj.get("cuonghoatuan").toString());
                            }

                            if (obj.containsKey("idhe")) {
                                t.idHe = Byte.parseByte(obj.get("idhe").toString());
                            }
                        }
                    }

                    int clanId = rs.getInt("clan");
                    Optional<Clan> g = Clan.getClanDAO().get(clanId);
                    if (g.isPresent()) {
                        t.clanName = g.get().name;
                    }

                    list.add(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<InfoTop> getTopLoiDaiThang() {

        List<InfoTop> list = new ArrayList<>();

        try (Connection conn = Connect.getConnection()) {

            String sql = "SELECT p.Name, p.Info, p.clan FROM player p LIMIT 20000";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    InfoTop t = new InfoTop();
                    t.name = rs.getString("Name");

                    JSONArray infoArr = (JSONArray) JSONValue.parse(rs.getString("Info"));
                    if (infoArr != null) {
                        for (Object o : infoArr) {
                            JSONObject obj = (JSONObject) o;

                            if (obj.containsKey("loidai")) {
                                t.loiDai = Integer.parseInt(obj.get("loidai").toString());
                            }

                            if (obj.containsKey("idhe")) {
                                t.idHe = Byte.parseByte(obj.get("idhe").toString());
                            }
                        }
                    }

                    int clanId = rs.getInt("clan");
                    Optional<Clan> g = Clan.getClanDAO().get(clanId);
                    if (g.isPresent()) {
                        t.clanName = g.get().name;
                    }

                    list.add(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<InfoTop> getTopCongHienTuan() {

        List<InfoTop> list = new ArrayList<>();

        try (Connection conn = Connect.getConnection()) {

            String sql = "SELECT p.Name, p.Info, p.clan FROM player p LIMIT 20000";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    InfoTop t = new InfoTop();
                    t.name = rs.getString("Name");

                    JSONArray infoArr = (JSONArray) JSONValue.parse(rs.getString("Info"));
                    if (infoArr != null) {
                        for (Object o : infoArr) {
                            JSONObject obj = (JSONObject) o;

                            if (obj.containsKey("conghientuan")) {
                                t.congHienTuan = Integer.parseInt(obj.get("conghientuan").toString());
                            }

                            if (obj.containsKey("idhe")) {
                                t.idHe = Byte.parseByte(obj.get("idhe").toString());
                            }
                        }
                    }

                    int clanId = rs.getInt("clan");
                    Optional<Clan> g = Clan.getClanDAO().get(clanId);
                    if (g.isPresent()) {
                        t.clanName = g.get().name;
                    }

                    list.add(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
