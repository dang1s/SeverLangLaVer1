package com.sg188.PhucLoi;

import SqlConnection.DBData;
import com.sg188.lib.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PhucLoi {
    private static final PhucLoi instance = new PhucLoi();

    public static PhucLoi getInstance() {
        return instance;
    }
    public List<TemplatePL> itemPL = new ArrayList<>();
    public Map<String, List<Welfare>> welfareMap = new HashMap<>();
    public boolean load() {
        Log.info("PhucLoi: Starting load()...");
        try {
            Connection conn = getValidConnectionData();
            if (conn == null) {
                Log.error("PhucLoi: Could not get valid DB connection");
                return false;
            }
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `phucloi`",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                try {
                    int id = resultSet.getInt("id");
                    int itemID = resultSet.getInt("ID_Item");
                    int IdPhucLoi = resultSet.getInt("ID_PhucLoi");
                    int SoLuong = resultSet.getInt("soluong");
                    boolean lock = resultSet.getBoolean("isLock");
                    String name = resultSet.getString("Name");
                    String strOption = resultSet.getString("strOption");
                    int yeucau = resultSet.getInt("yeucau");
                    TemplatePL item = new TemplatePL(id,itemID,IdPhucLoi,lock,name,strOption,SoLuong,yeucau);
                    add(item);
                } catch (Exception e) {
                    Log.error("PhucLoi: Error parsing phucloi record", e);
                    return false;
                }
            }
            resultSet.close();
            stmt.close();
            Log.info("PhucLoi: Loaded " + itemPL.size() + " phucloi items from database.");
            return true;
        } catch (SQLException e) {
            Log.error("PhucLoi: SQL Error during load()", e);
            return false;
        }
    }
    public void loadWelfare() {
        Log.info("PhucLoi: Starting loadWelfare()...");
        try {
            Connection conn = getValidConnectionData();
            if (conn == null) return;
            String sql = "SELECT * FROM welfare";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int welfareId = rs.getInt("welfare_id");
                String welfareType = rs.getString("welfare_type");
                if (welfareType == null || welfareType.trim().isEmpty()) {
                    if (welfareId == 13 || welfareId == 14) {
                        welfareType = "Đầu tư";
                    } else if (welfareId == 15 || welfareId == 16) {
                        welfareType = "Thẻ tháng";
                    }
                }
                Welfare welfare = new Welfare(
                        rs.getInt("id"),
                        welfareType,
                        welfareId,
                        rs.getString("welfare_name"),
                        rs.getBoolean("is_package"),
                        rs.getString("description")
                );
                addWelfare(welfare);
            }
            for (TemplatePL item : itemPL) {
                for (Welfare welfare : getAllWelfares()) {
                    if (welfare.getWelfareId() == item.IDPhucLoi && !welfare.item.contains(item)) {
                        welfare.item.add(item);
                    }
                }
            }
            Log.info("PhucLoi: Loaded welfares for " + welfareMap.size() + " types from database.");
        } catch (SQLException e) {
            Log.error("PhucLoi: SQL Error during loadWelfare()", e);
        }
    }
    public void addWelfare(Welfare welfare) {
        String type = normalizeType(welfare.getWelfareType());
        welfare.setWelfareType(type);
        welfareMap.putIfAbsent(type, new ArrayList<>());
        welfareMap.get(type).add(welfare);
    }
    public Set<String> getAllTypes() {
        return welfareMap.keySet();
    }
    public List<Welfare> getAllWelfares() {
        List<Welfare> allWelfares = new ArrayList<>();
        for (List<Welfare> welfares : welfareMap.values()) {
            allWelfares.addAll(welfares);
        }
        return allWelfares;
    }
    public void add(TemplatePL item) {
        itemPL.add(item);
        for (Welfare welfare:getAllWelfares()){
            if(welfare.getWelfareId()== item.IDPhucLoi){
                welfare.item.add(item);
            }
        }
    }
    public List<Welfare> getWelfaresByType(String welfareType) {
        return welfareMap.getOrDefault(normalizeType(welfareType), new ArrayList<>());
    }

    private String normalizeType(String type) {
        if (type == null) {
            return "";
        }
        String t = type.trim();
        if (t.equalsIgnoreCase("Đầu Tư")) {
            return "Đầu tư";
        }
        if (t.equalsIgnoreCase("Thẻ Tháng")) {
            return "Thẻ tháng";
        }
        if (t.equalsIgnoreCase("Quà Rank")) {
            return "Quà Rank";
        }
        if (t.equalsIgnoreCase("Quà Nạp")) {
            return "Quà nạp";
        }
        if (t.equalsIgnoreCase("Phúc lợi")) {
            return "Phúc lợi";
        }
        return t;
    }

    public void remove(TemplatePL item) {
        itemPL.remove(item);
    }
    public TemplatePL find(int itemID) {
        for (TemplatePL item : itemPL) {
            if (item.Id == itemID) {
                return item;
            }
        }
        return null;
    }

    public TemplatePL findByWelfareId(int welfareId) {
        for (TemplatePL item : itemPL) {
            if (item.IDPhucLoi == welfareId) {
                return item;
            }
        }
        return null;
    }

    public TemplatePL findByWelfareIdAndLevel(int welfareId, int level) {
        TemplatePL bestMatch = null;
        for (TemplatePL item : itemPL) {
            if (item.IDPhucLoi == welfareId && item.yeucau <= level) {
                if (bestMatch == null || item.yeucau > bestMatch.yeucau) {
                    bestMatch = item;
                }
            }
        }
        return bestMatch;
    }

    private Connection getValidConnectionData() throws SQLException {
        Connection conn = DBData.getConnection();
        if (conn == null || conn.isClosed()) {
            DBData.openConnection();
            conn = DBData.getConnection();
        }
        return conn;
    }
}
