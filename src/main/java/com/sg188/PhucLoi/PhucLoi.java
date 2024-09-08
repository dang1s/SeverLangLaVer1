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
        try {
            Connection conn = DBData.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `phucloi`",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.last();
            resultSet.beforeFirst();
            while (resultSet.next()) {
                try {
                    int id = resultSet.getInt("id");
                    int itemID = resultSet.getInt("ID_Item");
                    int IdPhucLoi = resultSet.getInt("ID_PhucLoi");
                    int SoLuong = resultSet.getInt("soluong");
                    boolean lock = resultSet.getBoolean("isLock");
                    String name = resultSet.getString("Name");
                    String strOption = resultSet.getString("StrOption");
                    int yeucau = resultSet.getInt("yeucau");
                    TemplatePL item = new TemplatePL(id,itemID,IdPhucLoi,lock,name,strOption,SoLuong,yeucau);
                    add(item);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            resultSet.close();
            stmt.close();
            Log.debug("ITEM PHUC LOI: "+ itemPL.size());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void loadWelfare() {
        try {
                Connection conn = DBData.getConnection();
            String sql = "SELECT * FROM welfare";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Welfare welfare = new Welfare(
                        rs.getInt("id"),
                        rs.getString("welfare_type"),
                        rs.getInt("welfare_id"),
                        rs.getString("welfare_name"),
                        rs.getBoolean("is_package"),
                        rs.getString("description")
                );
                addWelfare(welfare);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addWelfare(Welfare welfare) {
        String type = welfare.getWelfareType();
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
        return welfareMap.getOrDefault(welfareType, new ArrayList<>());
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
}
