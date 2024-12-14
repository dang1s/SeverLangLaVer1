package Manager;

import MapService.world.DaiHoiVoThuat;
import MapService.world.DeadForest;
import MapService.world.SonCapMyo;
import SqlConnection.Connect;
import SqlConnection.DBData;
import com.rewards.RewardTop;
import com.sg188.Shop.ItemShop;
import com.sg188.clan.Clan;
import com.sg188.data.*;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.DanhHieuNew;
import com.sg188.real.Item;
import com.sg188.server.Main;
import com.sg188.server.ServerManager;
import market.ItemMarket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class Manager {
    private static final Manager instance = new Manager();

    public static Manager gI() {
        return instance;
    }

    public List<ItemTemplate> itemTemplates = new ArrayList<>();
    public short[][] listTVMSilver = {//tvm bac
            {327, 919, 7, 688, 187, 373, 163, 8, 919, 7, 428, 644, 192, 551, 7, 516, 192, 161, 8, 266, 277, 687, 599, 7},
            {327, 919, 7, 688, 187, 373, 163, 8, 919, 7, 428, 644, 192, 551, 7, 516, 192, 161, 8, 266, 277, 687, 599, 7},
            {327, 919, 7, 688, 187, 373, 163, 8, 919, 7, 428, 644, 192, 551, 7, 516, 192, 161, 8, 266, 277, 687, 599, 7}

//            {163,277,192,599,7,514,163,8,688,567,353,819,9,163,819,428,644,192,563,8,513,192,565,818},
//            {688,567,8,819,9,163,819,428,644,192,563,8,677,192,565,818,163,277,192,599,7,863,163,353}
    };
    public int[][] amountTVMSilver = {
            {1, 1, 1, 1, 1, 1, 500000, 1, 1, 1, 2, 10, 200, 1, 1, 1, 50, 3, 1, 20, 3, 25, 20, 1},
            {1, 1, 1, 1, 1, 1, 500000, 1, 1, 1, 2, 10, 200, 1, 1, 1, 50, 3, 1, 20, 3, 25, 20, 1},
            {1, 1, 1, 1, 1, 1, 500000, 1, 1, 1, 2, 10, 200, 1, 1, 1, 50, 3, 1, 20, 3, 25, 20, 1}

//            {1000000,3,100,20,1,1,300000,1,1,1,1,5,1,500000,2,2,10,200,1,1,1,50,1,10},
//            {1,1,1,5,1,500000,2,2,10,200,1,1,1,50,1,10,1000000,3,100,20,1,1,300000,1}
    };

    public short[][] listTVM = {//tvm vang
            {723, 528, 623, 702, 788, 790, 719, 704, 940, 466, 860, 463, 998, 687, 525, 529, 726, 932, 563, 565, 567, 353, 603, 163},
            {788, 565, 463, 723, 567, 726, 466, 860, 940, 998, 790, 704, 528, 603, 525, 163, 529, 687, 719, 702, 623, 932, 563, 353},
            {790, 998, 565, 603, 567, 353, 704, 719, 932, 525, 466, 702, 528, 463, 687, 788, 860, 623, 163, 563, 723, 940, 529, 726}
//            {8, 818, 819, 9, 163, 819, 687, 818, 192, 819, 192, 818, 722, 8, 163, 9, 163, 10, 819, 763, 520, 163, 428, 819},
//            {163, 562, 687, 563, 192, 566, 192, 567, 524, 8, 163, 9, 163, 10, 353, 763, 459, 163, 428, 564, 8, 354, 565, 9}
    };
    public int[][] amountTVM = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1000, 1, 1, 1, 100, 100, 100, 100, 100, 1, 500000000},
            {1, 100, 1, 1, 100, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 500000000, 1, 1000, 1, 1, 1, 100, 100, 100},
            {1, 1, 100, 1, 100, 100, 1, 1, 100, 1, 1, 1, 1, 1, 1000, 1, 2, 1, 500000000, 100, 1, 1, 1, 1}
//            {1, 50, 20, 10, 5000000, 15, 75, 40, 1000, 40, 500, 100, 1, 1, 5000000, 1, 2000000, 1, 20, 8, 1, 1000000, 6, 15},
//            {5000000, 5, 75, 2, 1000, 5, 500, 2, 1, 1, 5000000, 1, 2000000, 1, 2, 8, 1, 1000000, 6, 5, 1, 5, 2, 1},
    };
    public Map<String, List<Integer>> playerPurchases = new HashMap<>();
    public Map<String, Integer> useItem = new HashMap<>();
    public Map<String, Integer> useItem_2 = new HashMap<>();
    public Map<String, Integer> reciveKey = new HashMap<>();
    public List<Item> tb1x = new ArrayList<>();
    public List<Item> tb2x = new ArrayList<>();
    public List<Item> tb3x = new ArrayList<>();
    public List<Item> tb4x = new ArrayList<>();
    public List<Item> tb5x = new ArrayList<>();
    public List<RewardTop> rewardTops = new ArrayList<>();
    public int countTheThang;
    public int countDauTu;
    public int rankCaoNhat;
    public int countRank;
    public List<ItemShop>shopRank= new ArrayList<>();
    public Set<Integer> visitedPlayers=new HashSet<>();
    public List<DanhHieuNew>danhHieuNews=new ArrayList<>();

    public void addPlayer(int id) {
        synchronized (visitedPlayers) {
            visitedPlayers.add(id);
        }
    }
    public boolean checkPlayer(int id) {
        synchronized (visitedPlayers) {
            return visitedPlayers.contains(id);
        }
    }
    public void loadListTrangBi() {
        for (ItemTemplate itemTemplateitem : DataCenter.gI().ItemTemplate) {
            Item item = new Item(itemTemplateitem.id);
            if (item.isItemTrangBi()) {
                if (item.isVuKhi()) {
                    Item.setOptionsVuKhi(item, item.getItemTemplate().levelNeed);
                } else if (item.isPhuKien() || item.isTrangBi()) {
               //     Item.setOptionsTrangBiPhuKien( item, item.getItemTemplate().levelNeed);
                }
                if (itemTemplateitem.levelNeed < 20) {
                    tb1x.add(item);
                } else if (itemTemplateitem.levelNeed < 30) {
                    tb2x.add(item);
                } else if (itemTemplateitem.levelNeed < 40) {
                    tb3x.add(item);
                } else if (itemTemplateitem.levelNeed < 50) {
                    tb4x.add(item);
                } else if (itemTemplateitem.levelNeed < 60&&itemTemplateitem.id!=314) {
                    tb5x.add(item);
                }
            }
        }
    }

    public void saveToFile() {
        try {
            FileWriter fileWriter = new FileWriter("dataserver.txt");
            PrintWriter printWriter = new PrintWriter(fileWriter);

            // Ghi giá trị vào file
            printWriter.println("cuontthethang: " + this.countTheThang);
            printWriter.println("countdautu: " + this.countDauTu);
            printWriter.println("rankcaonhat: " + this.rankCaoNhat);
            printWriter.println("countrank: " + this.countRank);

            printWriter.close(); // Đóng PrintWriter sau khi ghi xong
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    public void saveDropItemPlayer(String str, String name) {
        try {
            FileWriter fileWriter = new FileWriter("dropitem/" + name + ".txt", true);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            // Ghi giá trị vào file
            printWriter.println(str);

            printWriter.close(); // Đóng PrintWriter sau khi ghi xong
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    public void loadCountServer() {
        try {
            // Mở file và đọc dữ liệu
            BufferedReader reader = new BufferedReader(new FileReader("dataserver.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                // Phân tích cú pháp dữ liệu từ mỗi dòng
                if (line.startsWith("cuontthethang: ")) {
                    countTheThang = Integer.parseInt(line.replace("cuontthethang: ", ""));
                } else if (line.startsWith("countdautu: ")) {
                    countDauTu = Integer.parseInt(line.replace("countdautu: ", ""));
                } else if (line.startsWith("rankcaonhat: ")) {
                    rankCaoNhat = Integer.parseInt(line.replace("rankcaonhat: ", ""));
                } else if (line.startsWith("countrank: ")) {
                    countRank = Integer.parseInt(line.replace("countrank: ", ""));
                }
            }
            reader.close(); // Đóng BufferedReader sau khi đọc xong
        } catch (IOException e) {
            System.out.println("An error occurred while reading from the file.");
            e.printStackTrace();
        }
    }

    public void loadItemPurchases() {
        try (FileReader fileReader = new FileReader("log/shoprank.txt")) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String playerName = line;
                List<Integer> items = new ArrayList<>();
                line = bufferedReader.readLine();
                if (line != null && !line.isEmpty()) {
                    items = Arrays.stream(line.split(","))
                            .map(Integer::parseInt).collect(Collectors.toList());
                }
                playerPurchases.put(playerName, items);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasPurchasedItem(String playerName, int itemId) {
        return playerPurchases.containsKey(playerName) && playerPurchases.get(playerName).contains(itemId);
    }

    public void purchaseItem(String playerName, int itemId) {
        List<Integer> items = playerPurchases.getOrDefault(playerName, new ArrayList<>());
        items.add(itemId);
        playerPurchases.put(playerName, items);
    }


    public void saveFilePurchases() {
        try (FileWriter fileWriter = new FileWriter("log/shoprank.txt")) {
            for (Map.Entry<String, List<Integer>> entry : playerPurchases.entrySet()) {
                fileWriter.write(entry.getKey() + "\n");
                fileWriter.write(String.join(",", entry.getValue().stream().map(String::valueOf).toList()) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadItem() {
        Connection conn = DBData.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("SELECT * FROM `item_template`;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                ItemTemplate item = new ItemTemplate(id);
                item.name = rs.getString("name");
                item.detail = rs.getString("detail");
                item.isXepChong = rs.getBoolean("is_xep_chong");
                item.gioiTinh = rs.getByte("gioi_tinh");
                item.type = rs.getByte("type");
                item.idClass = rs.getByte("id_class");
                item.idIcon = rs.getShort("id_icon");
                item.levelNeed = rs.getShort("level_need");
                item.taiPhuNeed = rs.getInt("tai_phu_need");
                item.idMob = rs.getShort("id_mob");
                item.idChar = rs.getShort("id_char");
                itemTemplates.add(item);
            }
            Log.info("Load itemtempalte: " + itemTemplates.size());
        } catch (SQLException e) {
        }
    }
    public void loadDanhHieuNew(){
        Connection conn = DBData.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("SELECT * FROM `danhhieu`;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DanhHieuNew danhHieuNew = new DanhHieuNew();
                danhHieuNew.idIitem = rs.getInt("idItem");
                danhHieuNew.x = rs.getInt("x");
                danhHieuNew.y = rs.getInt("y");
                danhHieuNew.timeMs = rs.getInt("timeMs");
                danhHieuNew.size = rs.getInt("size");
                String input = rs.getString("data");
                input = input.replace("{", "").replace("}", ""); // Loại bỏ dấu ngoặc nhọn
                String[] stringArray = input.split(","); // Tách chuỗi thành mảng chuỗi

                int[] intArray = new int[stringArray.length]; // Khởi tạo mảng số nguyên
                for (int i = 0; i < stringArray.length; i++) {
                    intArray[i] = Integer.parseInt(stringArray[i]); // Chuyển đổi từng phần tử của mảng chuỗi thành số nguyên
                }
                danhHieuNew.data = intArray;
                danhHieuNews.add(danhHieuNew);
            }
            Log.info("Load danh hieu new: " + danhHieuNews.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadRewardTop() {
        Connection conn = DBData.getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("SELECT * from rewards where type = ?");
            ps.setInt(1, 0);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                List<Item> items = new ArrayList<>();
                int id = rs.getInt("player_id");
                int gold = rs.getInt("gold");
                int silver = rs.getInt("silver");
                int gold_locked = rs.getInt("gold_locked");
                int silver_locked = rs.getInt("silver_locked");
                JSONArray jArr = (JSONArray) JSONValue.parse(rs.getString("items_list"));
                int len = jArr.size();
                if (jArr != null) {
                    for (int i = 0; i < len; i++) {
                        Item it = new Item((JSONObject) jArr.get(i));
                        if (it == null) {
                            continue;
                        }
                        items.add(it);
                    }
                }
                RewardTop rw = new RewardTop();
                rw.setId(id);
                rw.setGold(gold);
                rw.setGold_lock(gold_locked);
                rw.setSliver(silver);
                rw.setSliver_lock(silver_locked);
                rw.setItems(items);
                rewardTops.add(rw);
            }
            Log.info("Load rewardtop done : " + rewardTops.size());
        } catch (SQLException e) {
        }
    }

    public RewardTop getRewardTopByID(int id) {
        for (RewardTop rw : rewardTops) {
            if (rw.getId() == id) {
                return rw;
            }
        }
        return null;
    }

    public void updateDeadForest(int hours, int minutes, int seconds) {
        Utlis.schedule(() -> {
            createDeadForest();
        }, hours, minutes, seconds);
    }

    public void updateDaiHoi(int hours, int minutes, int seconds) {
        Utlis.schedule(() -> {
            createDaiHoi();
        }, hours, minutes, seconds);
    }

    private void createDaiHoi() {
        DaiHoiVoThuat.DAIHOI = DaiHoiVoThuat.gI();
        Main.HeThongCTG("Đại hội nhẫn giả đã mở báo danh hãy nhanh tay đến báo danh nào",2);
    }

    private void createDeadForest() {
        DeadForest.DeadForest_2x = new DeadForest(600, Utlis.nextInt(20, 25));
        DeadForest.DeadForest_3x = new DeadForest(600, Utlis.nextInt(30, 35));
        DeadForest.DeadForest_4x = new DeadForest(600, Utlis.nextInt(40, 45));
        DeadForest.DeadForest_5x = new DeadForest(600, Utlis.nextInt(50, 55));
        DeadForest.DeadForest_6x = new DeadForest(600, Utlis.nextInt(60, 65));
        Main.HeThongCTG("Khu rừng chết đã mở báo danh, Các nhân giả hãy nhanh chân tới báo danh nào", 2);
    }

    public void updatePhucLoi(int hours, int minutes, int seconds) {
        Utlis.schedule(() -> {
            updatePhucLoiNewDay();
        }, hours, minutes, seconds);
    }

    private void updatePhucLoiNewDay() {
        try {
            useItem.clear();
            useItem_2.clear();
            reciveKey.clear();
            visitedPlayers.clear();
            List<Char> charList = ServerManager.getChars();
            for (Char pl : charList) {
                if (pl != null && pl.user != null && !pl.isClean) {
                    pl.resetNewDay();
                    pl.Info.timeLogin = System.currentTimeMillis();
                }
            }
            SonCapMyo.listCharIdInSonCap.clear();
            List<Clan> clans = Clan.getClanDAO().getAll();
            Connection conn = Connect.getConnection();
            java.util.Date now = new java.util.Date();
            synchronized (clans) {
                for (Clan clan : clans) {
                    clan.openDun = 1;
                    clan.countKick = 5;
                    clan.countInvite = 20;
                    PreparedStatement stmt3 = conn.prepareStatement(
                            "UPDATE `clan` SET `open_dun` = 1,`countinvite` = 20,`countkick` = 5, `updated_at` = ? WHERE `id` = ? LIMIT 1;");
                    stmt3.setString(1, Utlis.dateToString(now, "yyyy-MM-dd"));
                    stmt3.setInt(2, clan.id);
                    stmt3.executeUpdate();
                    stmt3.close();
                }
            }
        } catch (SQLException e) {
        }
    }

    public void updateProduct(ItemMarket item) {
    }
    public void readShopRank() {
        String sql = "SELECT shop_item_id, price, yeucau, items FROM shoprank";

        try (PreparedStatement stmt = DBData.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int shopItemId = rs.getShort("shop_item_id");
                ItemShop itemShop = new ItemShop(
                        shopItemId,
                        1,
                        (byte) 1,
                        (byte) 39,
                        0,
                        0,
                        0,
                        rs.getInt("price"),
                        0,
                        true,
                        (long) -1,
                        "",
                        rs.getByte("yeucau"),
                        0
                );
                JSONArray jArr = (JSONArray) JSONValue.parse(rs.getString("items"));
                int len = jArr.size();
                for (int i = 0; i < len; i++) {
                    JSONObject obj = (JSONObject) jArr.get(i);
                    Item item = new Item(Integer.parseInt(obj.get("id").toString()));
                    item.isLock = Boolean.parseBoolean(obj.get("locked").toString());
                    item.expiry = Long.parseLong(obj.get("expire_time").toString());
                    item.strOptions = obj.get("options").toString();
                    item.amount = Integer.parseInt(obj.get("amount").toString());
                    itemShop.items.add(item);
                }
                shopRank.add(itemShop);
                // Process the data as needed
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertItemToStall(ItemMarket item) {
        try {
            PreparedStatement stmt = Connect.getConnection().prepareStatement("INSERT INTO `market`(`id`, `seller`, `item`, `price`, `status`, `time`) VALUES (?,?,?,?,?,?)");
            stmt.setLong(1, item.getId());
            stmt.setString(2, item.getName());
            stmt.setString(3, item.getItem().toJSONObject().toJSONString());
            stmt.setInt(4, item.getPrice());
            stmt.setInt(5, item.getStatus());
            stmt.setInt(6, item.getTime());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void deleteItemFromStall(long id) {
        try {
            PreparedStatement stmt = Connect.getConnection().prepareStatement("DELETE FROM `market` WHERE `id` = ?");
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected < 0) {
                Log.info("No item found with id " + id);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void logItemSale(String seller, String buyer, long itemId, String itemDetails, int price) {
        try {
            PreparedStatement stmt = Connect.getConnection().prepareStatement(
                    "INSERT INTO market_log (seller, buyer, item_id, item_details, price) VALUES (?, ?, ?, ?, ?)"
            );
            stmt.setString(1, seller);
            stmt.setString(2, buyer);
            stmt.setLong(3, itemId);
            stmt.setString(4, itemDetails);
            stmt.setInt(5, price);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ItemShop findShopRank(short idBuy) {
        for (ItemShop item: shopRank){
            if(item.id == idBuy){
                return item;
            }
        }
        return null;
    }
}
