package com.sg188.Shop;

import SqlConnection.DBData;
import com.sg188.data.ItemOption;
import com.sg188.lib.Log;

import com.sg188.real.Item;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Store {
    private static final Store instance = new Store();

    public static Store getInstance() {
        return instance;
    }
    public List<ItemShop> items = new ArrayList<>();


    public boolean load() {
        try {
            Connection conn = DBData.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `store_data`",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = stmt.executeQuery();
            resultSet.last();
            resultSet.beforeFirst();
            while (resultSet.next()) {
                try {
                    int id = resultSet.getInt("id");
                    int itemID = resultSet.getInt("item_id");
                    int typeShop = resultSet.getInt("store");
                    boolean lock = resultSet.getBoolean("lock");
                    int TinhThach = resultSet.getInt("TinhThach");
                    int Bac = resultSet.getInt("Bac");
                    int BacKhoa = resultSet.getInt("BacKhoa");
                    int Vang = resultSet.getInt("Vang");
                    int VangKhoa = resultSet.getInt("VangKhoa");
                    byte He = resultSet.getByte("He");
                    long expire = resultSet.getLong("expire");
                    String strOption = resultSet.getString("options");
                    int yeucau = resultSet.getInt("yeucau");
                    int amount = resultSet.getInt("soluong");
                    ItemShop item = new ItemShop(id, itemID, He, (byte) typeShop,TinhThach, Bac, BacKhoa, Vang,VangKhoa, lock, expire, strOption,yeucau,amount);
                    add(item);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            resultSet.close();
            stmt.close();
            loadShopGen();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void loadShopGen(){
        List<ItemShop> newItems = new ArrayList<>();
        ItemShop shop = items.get(items.size()-1);
        for (ItemShop itemShop: items){
            if(itemShop.TypeShop == 30) continue;
            Item item = new Item(itemShop.itemID);
            itemShop.he = 1;

            if (item.isItemTrangBi()) {
                for (int j = 2; j < 6; j++) {
                    ItemShop itemShop1 = new ItemShop(
                            shop.id + newItems.size(), // Ensure unique ID
                            itemShop.itemID,
                            (byte) j,
                            (byte) itemShop.TypeShop,
                            itemShop.TinhThach,
                            itemShop.Bac,
                            itemShop.BacKhoa,
                            itemShop.Vang,
                            itemShop.VangKhoa,
                            itemShop.isLock,
                            itemShop.expire,
                            itemShop.strOption,
                            itemShop.yeuCau,
                            itemShop.amount
                    );
                    newItems.add(itemShop1);
                }
            }
        }
        items.addAll(newItems);
        Log.debug("ItemShop: " + items.size());
    }
    public int count() {
        return items.size();
    }

    public void add(ItemShop item) {
        items.add(item);
    }

    public void remove(ItemShop item) {
        items.remove(item);
    }

    public ItemShop find(int itemID) {
        for (ItemShop item : items) {
            if (item.id == itemID) {
                return item;
            }
        }
        return null;
    }

    public ItemShop get(int index) {
        if (index < 0 || index >= items.size()) {
            return null;
        }
        return items.get(index);
    }

}
