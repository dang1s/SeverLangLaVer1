package com.sg188.Shop;

import SqlConnection.DBData;
import com.sg188.lib.Log;
import com.sg188.real.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiscountStore {
    private static final DiscountStore instance = new DiscountStore();

    public static DiscountStore getInstance() {
        return instance;
    }
    public List<ItemShop> items = new ArrayList<>();



//    public boolean purchaseItem(int itemId, int quantity) {
//        String sqlSelect = "SELECT `conlai` FROM `discount_store` WHERE `id` = ?";
//        String sqlUpdate = "UPDATE `discount_store` SET `conlai` = ? WHERE `id` = ?";
//
//        Connection conn = null; // Khai báo kết nối bên ngoài try-with-resources
//        try {
//            DBData.openConnection(); // Mở kết nối
//            conn = DBData.getConnection(); // Lấy kết nối vừa mở
//
//            // Sử dụng try-with-resources cho PreparedStatement và ResultSet
//            try (PreparedStatement stmtSelect = conn.prepareStatement(sqlSelect);
//                 PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
//
//                // Truy vấn số lượng hiện có trong bảng
//                stmtSelect.setInt(1, itemId);
//                try (ResultSet rs = stmtSelect.executeQuery()) {
//                    if (rs.next()) {
//                        int soLuong = rs.getInt("conlai");
//
//                        // Kiểm tra xem có đủ số lượng để mua không
//                        if (soLuong >= quantity) {
//                            soLuong -= quantity; // Giảm số lượng tồn kho
//
//                            // Cập nhật lại số lượng trong cơ sở dữ liệu
//                            stmtUpdate.setInt(1, soLuong);
//                            stmtUpdate.setInt(2, itemId);
//
//                            int rowsAffected = stmtUpdate.executeUpdate();
//                            return rowsAffected > 0; // Trả về true nếu cập nhật thành công
//                        } else {
//                            System.out.println("Không đủ số lượng để mua.");
//                            return false;
//                        }
//                    } else {
//                        System.out.println("Item không tồn tại.");
//                        return false;
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace(); // In ra lỗi để debug
//            return false; // Trả về false nếu có lỗi
//        } finally {
//            DBData.closeConnection(); // Đảm bảo đóng kết nối khi hoàn tất
//        }
//    }

    //new
    public boolean purchaseItem(int itemId, int quantity) {
        String sqlSelect = "SELECT `conlai` FROM `discount_store` WHERE `id` = ?";
        String sqlUpdate = "UPDATE `discount_store` SET `conlai` = ? WHERE `id` = ?";

        Connection conn = null;
        try {
            DBData.openConnection();
            conn = DBData.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            try (PreparedStatement stmtSelect = conn.prepareStatement(sqlSelect);
                 PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {

                // Truy vấn số lượng hiện có trong bảng
                stmtSelect.setInt(1, itemId);
                try (ResultSet rs = stmtSelect.executeQuery()) {
                    if (rs.next()) {
                        int soLuong = rs.getInt("conlai");

                        // Kiểm tra xem có đủ số lượng để mua không
                        if (soLuong >= quantity) {
                            soLuong -= quantity; // Giảm số lượng tồn kho

                            // Cập nhật lại số lượng trong cơ sở dữ liệu
                            stmtUpdate.setInt(1, soLuong);
                            stmtUpdate.setInt(2, itemId);

                            int rowsAffected = stmtUpdate.executeUpdate();
                            if (rowsAffected > 0) {
                                conn.commit(); // Commit transaction nếu thành công
                                return true;
                            } else {
                                conn.rollback(); // Rollback nếu không thành công
                                return false;
                            }
                        } else {
                            System.out.println("Không đủ số lượng để mua.");
                            conn.rollback(); // Rollback nếu không đủ số lượng
                            return false;
                        }
                    } else {
                        System.out.println("Item không tồn tại.");
                        conn.rollback(); // Rollback nếu item không tồn tại
                        return false;
                    }
                }
            } catch (SQLException e) {
                if (conn != null) {
                    conn.rollback(); // Rollback nếu có lỗi
                }
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true); // Trở về chế độ mặc định
                DBData.closeConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean load() {
        try {
            Connection conn = DBData.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `discount_store`",
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
                    int giacu = resultSet.getInt("giacu");
                    int conlai = resultSet.getInt("conlai");
                    ItemShop item = new ItemShop(id, itemID, He, (byte) typeShop,TinhThach, Bac, BacKhoa, Vang,VangKhoa, lock, expire, strOption,yeucau,amount, giacu, conlai);
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

    public boolean updateItemQuantity(int itemId, int quantityToDeduct) {
        synchronized (items) {
            for (ItemShop item : items) {
                if (item.id == itemId) { // Kiểm tra ID của item
                    if (item.conLai >= quantityToDeduct) { // Kiểm tra số lượng còn lại
                        item.conLai -= quantityToDeduct; // Giảm số lượng
                        return true; // Cập nhật thành công
                    } else {
                        System.out.println("Không đủ số lượng để giảm."); // Ghi log thông báo
                        return false;
                    }
                }
            }
            System.out.println("Không tìm thấy món đồ với ID: " + itemId);
            return false;
        }
    }

}
