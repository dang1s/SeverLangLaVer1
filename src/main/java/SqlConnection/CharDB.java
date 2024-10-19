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
            String query = "SELECT thu,idChar FROM player WHERE name = ? LIMIT 1"; // Select only necessary column

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, name);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) { // Check if a matching player was found
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
            String query = "SELECT balance FROM users WHERE username = ? LIMIT 1"; // Select only necessary column

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, pl.user.username);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) { // Check if a matching player was found
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
        try (Connection conn = Connect.getConnection();) {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE  player set thu = ?where name = ?");) {
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
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Connect.getConnection();
            ps = conn.prepareStatement("UPDATE users SET balance = balance - ? WHERE id = ?");
            ps.setInt(1, coinToSubtract);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
    public static void updateTopSm(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = Connect.getConnection();
            ps = conn.prepareStatement("UPDATE users SET topsm = ? WHERE id = ?");
            ps.setInt(1, 1);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }


    public static void logExchange(String name, int coinBefore, int coinAfter) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = Connect.getConnection();
            String sql = "INSERT INTO coin_exchange_log (name, coin_before, coin_after, time) VALUES (?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, coinBefore);
            ps.setInt(3, coinAfter);
            ps.setTimestamp(4, new java.sql.Timestamp(new Date().getTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
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

    private static final String QueryTop = "SELECT `Name`,`level`,`Info`,`Point`,`clan`" +
            "FROM player " +
            "ORDER BY `level` DESC " +
            "LIMIT 100;";

    private static final String QueryTopNhiDong = "SELECT p.`Name`, p.`level`, p.`Info`, p.`Point`, p.`clan` " +
            "FROM player p " +
            "JOIN users u ON p.`IdChar` = u.`id` " +
            "WHERE u.`createtime` > '2025-10-15 00:00:00' " +
            "ORDER BY p.`level` DESC " +
            "LIMIT 100;";


    public static List<InfoTop> getTop(byte type) {
        List<InfoTop> list = new ArrayList<>();
        try (Connection conn = Connect.getConnection();) {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            try (ResultSet rs = stmt.executeQuery(QueryTop)) {
                while (rs.next()) {
                    InfoTop top = new InfoTop();
                    top.name = rs.getString("Name");
                    top.level = rs.getShort("level");
                    JSONArray jArr = (JSONArray) JSONValue.parse(rs.getString("Point"));
                    int len = jArr.size();
                    for (int k = 0; k < len; k++) {
                        JSONObject obj = (JSONObject) jArr.get(k);
                        top.exp = Long.parseLong(obj.get("exp").toString());
                    }
                    jArr = (JSONArray) JSONValue.parse(rs.getString("Info"));
                    len = jArr.size();
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<InfoTop> getTopNhiDong(byte type) {
        List<InfoTop> list = new ArrayList<>();
        try (Connection conn = Connect.getConnection();) {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            try (ResultSet rs = stmt.executeQuery(QueryTopNhiDong)) {
                while (rs.next()) {
                    InfoTop top = new InfoTop();
                    top.name = rs.getString("Name");
                    top.level = rs.getShort("level");
                    JSONArray jArr = (JSONArray) JSONValue.parse(rs.getString("Point"));
                    int len = jArr.size();
                    for (int k = 0; k < len; k++) {
                        JSONObject obj = (JSONObject) jArr.get(k);
                        top.exp = Long.parseLong(obj.get("exp").toString());
                    }
                    jArr = (JSONArray) JSONValue.parse(rs.getString("Info"));
                    len = jArr.size();
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
            }
        } catch (Exception e) {
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


    private static final String TAIPHU = "SELECT `Name`,`toptaiphu`,`Info`,`clan`" +
            "FROM player " +
            "ORDER BY `toptaiphu` DESC " +
            "LIMIT 100;";



    public static List<InfoTop> getTopTaiPhu() {
        List<InfoTop> list = new ArrayList<>();
        try (Connection conn = Connect.getConnection();) {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            try (ResultSet rs = stmt.executeQuery(TAIPHU)) {
                rs.last();
                int i = rs.getRow();
                rs.beforeFirst();
                int j = 0;
                while (rs.next()) {
                    InfoTop top = new InfoTop();
                    top.name = rs.getString("Name");
                    top.taiPhu = rs.getInt("toptaiphu");
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private static final String CHUYENCAN = "SELECT `Name`,`topchuyencan`,`Info`,`clan`" +
            "FROM player " +
            "ORDER BY `topchuyencan` DESC " +
            "LIMIT 100;";



    public static List<InfoTop> getTopChuyenCan() {
        List<InfoTop> list = new ArrayList<>();
        try (Connection conn = Connect.getConnection();) {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            try (ResultSet rs = stmt.executeQuery(CHUYENCAN)) {
                rs.last();
                int i = rs.getRow();
                rs.beforeFirst();
                int j = 0;
                while (rs.next()) {
                    InfoTop top = new InfoTop();
                    top.name = rs.getString("Name");
                    top.chuyencan = rs.getInt("topchuyencan");
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private static final String cuacai = "SELECT `Name`,`topnap`,`Info`,`clan`" +
            "FROM player " +
            "ORDER BY `topnap` DESC " +
            "LIMIT 100;";



    public static List<InfoTop> getTopCuaCai() {
        List<InfoTop> list = new ArrayList<>();
        try (Connection conn = Connect.getConnection();) {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            try (ResultSet rs = stmt.executeQuery(cuacai)) {
                rs.last();
                int i = rs.getRow();
                rs.beforeFirst();
                int j = 0;
                while (rs.next()) {
                    InfoTop top = new InfoTop();
                    top.name = rs.getString("Name");
                    top.pointNap = rs.getInt("topnap");
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public static void initTopGiaToc() {
        try {
            Vector<Clan> ranked = new Vector<>();
            Connection conn = Connect.getConnection();
            PreparedStatement stmt = conn
                    .prepareStatement("SELECT `id` FROM `clan` WHERE `level` > 0 ORDER BY `level` DESC LIMIT 100;");
            ResultSet res = stmt.executeQuery();
            int i = 1;
            while (res.next()) {
                int id = res.getInt("id");
                Optional<Clan> g = Clan.getClanDAO().get(id);
                if (g != null && g.isPresent()) {
                    Clan clan = g.get();
                    ranked.add(clan);
                    i++;
                }
            }
            res.close();
            stmt.close();
            ClickTop.RANKED[2] = ranked;
            Collections.sort(ClickTop.RANKED[2], new Comparator() {

                public int compare(Object o1, Object o2) {

                    Integer level1 = (int) ((Clan) o1).level;
                    Integer level2 = (int) ((Clan) o2).level;
                    int sComp = level2.compareTo(level1);
                    if (sComp != 0) {
                        return sComp;
                    }
                    Integer x1 = ((Clan) o1).getExp();
                    Integer x2 = ((Clan) o2).getExp();
                    return x2.compareTo(x1);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String RESET = "SELECT `Name`,`inventory`" +
            "FROM player " +
            "LIMIT 20000;";

    public static Char[] getpl() {
        Char[] pl = null;
        try (Connection conn = Connect.getConnection();) {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            try (ResultSet rs = stmt.executeQuery(RESET)) {
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
                    if(jArr.size() >0) {
                        for (int k = 0; k < 1; k++) {
                            JSONObject obj = (JSONObject) jArr.get(k);
                            pl[j].Bag.bac = Integer.parseInt(obj.get("bac").toString());
                            pl[j].Bag.bacKhoa = Integer.parseInt(obj.get("backhoa").toString());
                            pl[j].Bag.bacBox = Integer.parseInt(obj.get("bacbox").toString());
                            pl[j].Bag.bacKhoaBox = Integer.parseInt(obj.get("backhoabox").toString());
                            pl[j].Bag.vang = Integer.parseInt(obj.get("vang").toString());
                            pl[j].Bag.vangKhoa = Integer.parseInt(obj.get("vangkhoa").toString());
                            pl[j].Bag.vangKhoaBox = Integer.parseInt(obj.get("vangkhoabox").toString());
                            pl[j].Bag.vangBox = Integer.parseInt(obj.get("vangbox").toString());
                            pl[j].Bag.pointNAP = Integer.parseInt(obj.get("pointnap").toString());
                            pl[j].Bag.stnSo = Byte.parseByte(obj.get("stnso").toString());
                            pl[j].Bag.stnTrung = Byte.parseByte(obj.get("stntrung").toString());
                            pl[j].Bag.stnCao = Byte.parseByte(obj.get("stncao").toString());
                            pl[j].Bag.sknSo = Byte.parseByte(obj.get("sknso").toString());
                            pl[j].Bag.sknTrung = Byte.parseByte(obj.get("skntrung").toString());
                            pl[j].Bag.sknCao = Byte.parseByte(obj.get("skncao").toString());
                            pl[j].Bag.Banh = Byte.parseByte(obj.get("banh").toString());
                            if(obj.containsKey("banhUbao")){
                                pl[j].Bag.banhUBao = Byte.parseByte(obj.get("banhUbao").toString());
                            }
                            if (obj.containsKey("sach")) {
                                pl[j].Bag.itemSach = new Item((JSONObject) obj.get("sach"));
                            }
                        }
                    }
                    j++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pl;
    }
    public static void savePlayer(Char pl){
        JSONArray inventory = new JSONArray();
        inventory.add(pl.Bag.toJSONObject());
        String jiventory = inventory.toJSONString();
        try (Connection conn = Connect.getConnection();) {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE player set inventory = ? where name = ?");) {
                ps.setString(1, jiventory);
                ps.setString(2, pl.Info.name);
                ps.executeUpdate();
            } catch (Exception e) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
