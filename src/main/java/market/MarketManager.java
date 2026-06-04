package market;

import Manager.Manager;
import SqlConnection.CharDB;
import SqlConnection.Connect;
import Template.TemplateThu;
import com.sg188.lib.Log;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.server.ServerManager;
import com.sg188.server.lib.Message;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MarketManager implements Runnable {
    public static final byte STATUS_ON_SALE = 0;
    public static final byte STATUS_BOUGHT = 1;
    public static final byte STATUS_RECEIVED = 2;
    private static long id = 0;

    private static final MarketManager instance = new MarketManager();

    public static MarketManager gI() {
        return instance;
    }

    private List<ItemMarket> productList;
    private boolean saving;
    private long lastUpdate;
    private boolean running;
    // Set để track các item đang được xử lý (playerName:itemIndex)
    private Set<String> processingItems;
    // Map để track cooldown của mỗi player (playerName -> lastSellTime)
    private ConcurrentHashMap<String, Long> playerCooldowns;
    // Thread pool để xử lý database insert bất đồng bộ
    private ExecutorService dbExecutor;
    private static final long SELL_COOLDOWN_MS = 2000; // 2 giây cooldown giữa các lần bán
    
    public MarketManager(){
        productList = new ArrayList<>();
        running=true;
        processingItems = ConcurrentHashMap.newKeySet();
        playerCooldowns = new ConcurrentHashMap<>();
        dbExecutor = Executors.newFixedThreadPool(5); // 5 threads để xử lý DB
        load();
    }
    public void load(){
        // Sử dụng try-with-resources để tự động đóng connection
        try (Connection conn = Connect.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM `market` WHERE `status` = ?")) {
            ps.setInt(1, STATUS_ON_SALE);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String seller = rs.getString("seller");
                    int price = rs.getInt("price");
                    byte status = rs.getByte("status");
                    int time = rs.getInt("time");
                    JSONObject obj = (JSONObject) JSONValue.parse(rs.getString("item"));
                    ItemMarket item = new ItemMarket();
                    item.setId(id);
                    item.setPrice(price);
                    item.setName(seller);
                    item.setStatus(status);
                    item.setTime(time);
                    item.setItem(new Item(obj));
                    productList.add(item);
                }
                Log.info("Market size: "+productList.size());
                if(productList.size() > 0)
                    id=productList.get(productList.size()-1).getId()+1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Kiểm tra xem player có đang trong cooldown không
     */
    public boolean isPlayerOnCooldown(String playerName) {
        Long lastSellTime = playerCooldowns.get(playerName);
        if (lastSellTime == null) {
            return false;
        }
        long timeSinceLastSell = System.currentTimeMillis() - lastSellTime;
        return timeSinceLastSell < SELL_COOLDOWN_MS;
    }
    
    /**
     * Kiểm tra xem item có đang được xử lý không
     */
    public boolean isItemProcessing(String playerName, short itemIndex) {
        String key = playerName + ":" + itemIndex;
        return processingItems.contains(key);
    }
    
    /**
     * Đánh dấu item đang được xử lý
     */
    private void markItemProcessing(String playerName, short itemIndex) {
        String key = playerName + ":" + itemIndex;
        processingItems.add(key);
    }
    
    /**
     * Bỏ đánh dấu item đã xử lý xong
     */
    private void unmarkItemProcessing(String playerName, short itemIndex) {
        String key = playerName + ":" + itemIndex;
        processingItems.remove(key);
    }
    
    /**
     * Thêm item vào chợ với cơ chế chống duplicate
     */
    public boolean themItem(Char p, Item item, int price, int time, short itemIndex) {
        String playerName = p.Info.name;
        
        // Kiểm tra cooldown
        if (isPlayerOnCooldown(playerName)) {
            p.getService().serverMessage("Vui lòng đợi một chút trước khi bán tiếp!");
            return false;
        }
        
        // Kiểm tra item đang được xử lý
        if (isItemProcessing(playerName, itemIndex)) {
            p.getService().serverMessage("Vật phẩm đang được xử lý, vui lòng đợi!");
            return false;
        }
        
        // Đánh dấu đang xử lý
        markItemProcessing(playerName, itemIndex);
        
        try {
            // Tạo ItemMarket và thêm vào memory ngay lập tức
            ItemMarket itemMarket = new ItemMarket();
            itemMarket.setId(id++);
            itemMarket.setName(playerName);
            itemMarket.setPrice(price);
            itemMarket.setTime((int) ((time+System.currentTimeMillis())/1000));
            itemMarket.setItem(item);
            itemMarket.setStatus(STATUS_ON_SALE);
            add(itemMarket);
            
            // Cập nhật cooldown
            playerCooldowns.put(playerName, System.currentTimeMillis());
            
            // Lưu vào database bất đồng bộ
            final ItemMarket finalItem = itemMarket;
            final String finalPlayerName = playerName;
            final short finalItemIndex = itemIndex;
            
            dbExecutor.submit(() -> {
                try {
                    Manager.gI().insertItemToStall(finalItem);
                } catch (Exception e) {
                    Log.error("Lỗi khi lưu item vào database: " + finalItem.getId(), e);
                    // Nếu lưu DB thất bại, xóa khỏi memory
                    remove(finalItem);
                } finally {
                    // Bỏ đánh dấu sau khi xử lý xong
                    unmarkItemProcessing(finalPlayerName, finalItemIndex);
                }
            });
            
            return true;
        } catch (Exception e) {
            Log.error("Lỗi khi thêm item vào chợ", e);
            unmarkItemProcessing(playerName, itemIndex);
            return false;
        }
    }
    public void add(ItemMarket item) {
        synchronized (productList) {
            productList.add(item);
        }
    }

    public void remove(ItemMarket item) {
        synchronized (productList) {
            productList.remove(item);
        }
    }

    public void show(Char p,byte type,byte type2,short index){
        try {
//            Message m = new Message((byte) 101);
//            m.writeShort(index);
//            int size = productList.size();
//            m.writeShort(size);
            List<ItemMarket> DataCho_OK = new ArrayList<>();
            for (ItemMarket cho : productList) {
                if (cho.getStatus() == 0 && cho.getTime() > System.currentTimeMillis() / 1000L) {
                    DataCho_OK.add(cho);
                }
            }
            // 1. Lọc danh sách dựa trên `type`
            List<ItemMarket> filteredList = filterList(DataCho_OK, type);
            // 2. Sắp xếp danh sách dựa trên `type2`
            sortList(filteredList, type2);

            // mỗi trang có 30 items
            int itemsPerPage = 20;
            int totalItems = filteredList.size();
            int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
            if(index >=  totalPages) index = (short) ((short) totalPages-1);
            if(index <= Short.MIN_VALUE) index = 0;

            // 3. Phân trang
            List<ItemMarket> pageItems = getPageItems(filteredList, index, itemsPerPage);

            Message m = new Message((byte) 101);
            m.writeShort(index);
            m.writeShort(pageItems.size());

            for (ItemMarket market: pageItems){
                m.writeLong(market.getId());
                m.writeUTF(market.getName());
                m.writeInt(market.getPrice());
                m.writeInt(market.getTime());
                market.getItem().write(m.writer);
            }
            p.getService().sendMessage(m);
        } catch (IOException e) {

        }
    }
    private List<ItemMarket> filterList(List<ItemMarket> DataCho, byte timtheo) {
        List<ItemMarket> filteredList = new ArrayList<>();
        for (ItemMarket cho : DataCho) {
            if(cho.getStatus() != 0) continue;
            if (timtheo == 0) { // Tất cả
                filteredList.add(cho);
            } else if (timtheo >= 1 && timtheo <= 27) {
                if (matchesCriteria(cho, timtheo)) {
                    filteredList.add(cho);
                }
            } else if (timtheo == 28) {
                if (!matchesAnyCriteria(cho, (byte) 1, (byte) 27)) {
                    filteredList.add(cho);
                }
            }
        }
        return filteredList;
    }
    private List<ItemMarket> getPageItems(List<ItemMarket> list, int page, int itemsPerPage) {
        int startIndex =  page * itemsPerPage;
        // Kiểm tra để đảm bảo không vượt quá kích thước danh sách
        if (startIndex > list.size()) {
            //startIndex = list.size()-itemsPerPage;
            startIndex = Math.max(list.size() - itemsPerPage, 0);
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        int endIndex = startIndex + itemsPerPage;


        if (endIndex > list.size()) {
            endIndex = list.size();
        }

        return new ArrayList<>(list.subList(startIndex, endIndex));
    }
    private void sortList(List<ItemMarket> list, byte sapxep) {
        Comparator<ItemMarket> comparator;

        switch (sapxep) {
            case 0: // Mới nhất
                comparator = Comparator.comparing(ItemMarket::getTime).reversed();
                break;
            case 1: // Giá
                comparator = Comparator.comparing(ItemMarket::getPrice);
                break;
            case 2: // Loại vật phẩm
                comparator = Comparator.comparing(ItemMarket::getItemType);
                break;
            case 3: // Thời gian bán
                comparator = Comparator.comparing(ItemMarket::getTime);
                break;
            case 4: // Tên người dùng
                comparator = Comparator.comparing(ItemMarket::getName);
                break;
            case 5: // Cấp vật phẩm
                comparator = Comparator.comparing(ItemMarket::getItemLevel);
                break;
            default:
                return; // Không sắp xếp nếu không có tiêu chí phù hợp
        }

        list.sort(comparator);
    }
    private boolean matchesCriteria(ItemMarket cho, byte criteria) {
        switch (criteria) {
            case 1: return cho.getItem().getItemTemplate().type == 21; // tất cả đá
            case 2: return cho.getItem().getItemTemplate().id == 0; // Đá cấp 1
            case 3: return cho.getItem().getItemTemplate().id == 1; // Đá cấp 2
            case 4: return cho.getItem().getItemTemplate().id == 2; // Đá cấp 3
            case 5: return cho.getItem().getItemTemplate().id == 3; // Đá cấp 4
            case 6: return cho.getItem().getItemTemplate().id == 4; // Đá cấp 5
            case 7: return cho.getItem().getItemTemplate().id == 5; // Đá cấp 6
            case 8: return cho.getItem().getItemTemplate().id == 6; // Đá cấp 7
            case 9: return cho.getItem().getItemTemplate().id == 7; // Đá cấp 8
            case 10: return cho.getItem().getItemTemplate().id == 8; // Đá cấp 9
            case 11: return cho.getItem().getItemTemplate().id == 9; // Đá cấp 10
            case 12: return cho.getItem().getItemTemplate().id == 10; // Đá cấp 11
            case 13: return cho.getItem().getItemTemplate().id == 11; // Đá cấp 12
            case 14: return cho.getItem().isTypeTrangBi(); // tất cả trang bị
            case 15: return cho.getItem().getItemTemplate().type == 1; // vũ khí
            case 16: return cho.getItem().getItemTemplate().type == 3; // dây thừng
            case 17: return cho.getItem().getItemTemplate().type == 5; // móc sắt
            case 18: return cho.getItem().getItemTemplate().type == 7; // Ống tiêu
            case 19: return cho.getItem().getItemTemplate().type == 9; // Túi Nhẫn Giả
            case 20: return cho.getItem().getItemTemplate().type == 0; // đai
            case 21: return cho.getItem().getItemTemplate().type == 2; // Áo
            case 22: return cho.getItem().getItemTemplate().type == 4; // Bao tay
            case 23: return cho.getItem().getItemTemplate().type == 6; // Quần
            case 24: return cho.getItem().getItemTemplate().type == 8; // Giày
            case 25: return cho.getItem().getItemTemplate().name.contains("Lệnh bài"); // Lệnh bài
            case 26: return cho.getItem().getItemTemplate().name.contains("Vỏ sò"); // vỏ sò
            case 27: return cho.getItem().getItemTemplate().type == 32; // ngọc khảm
            default: return false;
        }
    }
    private boolean matchesAnyCriteria(ItemMarket cho, byte start, byte end) {
        for (byte i = start; i <= end; i++) {
            if (matchesCriteria(cho, i)) {
                return true;
            }
        }
        return false;
    }
    public void showListSell(Char p){
        List<ItemMarket>itemSell = new ArrayList<>();
        synchronized (productList) {
            for (ItemMarket market : productList) {
                if (market.getName().equals(p.Info.name)) {
                    itemSell.add(market);
                }
            }
        }
        try {
            Message m = new Message((byte) 100);
            int size = itemSell.size();
            m.writeShort(size);
            for (ItemMarket market: itemSell){
                m.writeLong(market.getId());
                m.writeInt(market.getPrice());
                m.writeInt(market.getTime());
                market.getItem().write(m.writer);
            }
            p.getService().sendMessage(m);
        } catch (IOException e) {

        }
    }
    public ItemMarket find(int id) {
        synchronized (productList) {
            for (ItemMarket item : productList) {
                if (item.getStatus() == MarketManager.STATUS_ON_SALE && item.getId() == id) {
                    return item;
                }
            }
            return null;
        }
    }
    public void buy(Char p,int id){
        if(!p.user.actived){
            p.getService().serverMessage("Vui lòng kích hoạt tài khoản để sử dụng tính năng này");
            return;
        }
        if (p.isSecurity && !p.isUnlockSecurity) {
            p.service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
            return;
        }

        ItemMarket itemMarket = find(id);
        if(itemMarket == null) {
            p.getService().serverMessage("Vật phẩm không tồn tại!");
            return;
        }
        int price = itemMarket.getPrice();
        if(p.Bag.bac < price){
            p.getService().serverMessage("Không đủ bạc!");
            return;
        }
        String seller = itemMarket.getName();
        remove(itemMarket);
        Manager.gI().deleteItemFromStall(itemMarket.getId());
        p.addBac(-price);
        p.addItem(itemMarket.getItem());
        p.msgAddItemBag(itemMarket.getItem());
        p.getService().buyMarket();
        String itemDetails = itemMarket.getItem().toJSONObject().toJSONString();
        Manager.gI().logItemSale(seller, p.getName(), itemMarket.getId(), itemDetails, price);
        Char _char = ServerManager.findCharByName(seller);
        price -= price * 5 / 100;
        TemplateThu thu = reciveCoin(p, price);
        if(_char == null){
            try {
                Char plOffline = CharDB.getCharByName(seller);
                if(plOffline!=null) {
                    int idThu = plOffline.letters.size()+1;
                    if (plOffline.letters.size() > 0) {
                        idThu = plOffline.letters.get(plOffline.letters.size() - 1).id + 1;
                    }
                    thu.id = (short) idThu;
                    plOffline.letters.add(thu);
                    CharDB.updateDBThu(plOffline, seller);
                }
            } catch (Exception e) {
                Log.error("Loi gui thu tra thuong vxmm ",e);
            }
        } else if (!_char.isClean&&_char.user!=null) {
            int idThu = _char.letters.size()+1;
            if (_char.letters.size() > 0) {
                idThu = _char.letters.get(_char.letters.size() - 1).id + 1;
            }
            thu.id = (short) idThu;
            _char.letters.add(thu);
            _char.getService().reloadLetter();
        }
    }

    @NotNull
    private static TemplateThu reciveCoin(Char p, int price) {
        TemplateThu thu = new TemplateThu();
        thu.Bac = price;
        thu.BacKhoa = 0;
        thu.Vang = 0;
        thu.VangKhoa = 0;
        thu.Exp = 0;
        thu.Title = "Vật phẩm đã được bán thành công";
        thu.NameNguoiGui = "Hệ thống";
        thu.NoiDungThu = "Vật phẩm đã được bán cho nhẫn giả "+ p.Info.name+ " thành công";
        thu.TimeEnd = System.currentTimeMillis() + 864000000;
        return thu;
    }


    public void update() {
        List<ItemMarket> expiredProductList = new ArrayList<>();
        synchronized (productList) {
            productList.forEach((t) -> {
                if (t.getTime() <= System.currentTimeMillis()/1000) {
                    expiredProductList.add(t);
                }
            });
            this.productList.removeAll(expiredProductList);
        }
        try {
            for (ItemMarket itemMarket: expiredProductList){
                String seller = itemMarket.getName();
                Char pl = ServerManager.findCharByName(seller);
                TemplateThu thu = new TemplateThu();
                thu.Bac = 0;
                thu.BacKhoa = 0;
                thu.Vang = 0;
                thu.VangKhoa = 0;
                thu.Exp = 0;
                thu.Title = "Vật phẩm hết hạn treo bán";
                thu.NameNguoiGui = "Hệ thống";
                thu.NoiDungThu = "";
                thu.TimeEnd = System.currentTimeMillis() + 864000000;
                thu.Item = itemMarket.getItem();
                if(pl!=null) {
                    int idThu = pl.letters.size()+1;
                    if (pl.letters.size() > 0) {
                        idThu = pl.letters.get(pl.letters.size() - 1).id + 1;
                    }
                    thu.id = (short) idThu;
                    pl.letters.add(thu);
                    pl.getService().reloadLetter();
                }else {
                    Char plOffline = CharDB.getCharByName(seller);
                    if(plOffline!=null) {
                        int idThu = plOffline.letters.size()+1;
                        if (plOffline.letters.size() > 0) {
                            idThu = plOffline.letters.get(plOffline.letters.size() - 1).id + 1;
                        }
                        thu.id = (short) idThu;
                        plOffline.letters.add(thu);
                        CharDB.updateDBThu(plOffline, seller);
                    }
                }
                Manager.gI().deleteItemFromStall(itemMarket.getId());
            }

        } catch (Exception e) {
            Log.error("Loi clear item market het han ",e);
        }
//        long l = System.currentTimeMillis();
//        if (l - lastUpdate > 900000) {
//            lastUpdate = l;
//            save();
//        }
    }


    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (running) {
            long l1 = System.currentTimeMillis();
            update();
            long l2 = System.currentTimeMillis();
            if (l2 - l1 < 1000) {
                try {
                    Thread.sleep(1000 - (l2 - l1));
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        this.running = false;
        if (dbExecutor != null) {
            dbExecutor.shutdown();
        }
    }
    private void save() {
//        if (!saving) {
//            saving = true;
//            try {
//                LinkedList<ItemMarket> list = new LinkedList<>();
//                synchronized (productList) {
//                    for (ItemMarket item : productList) {
//                            list.add(item);
//                    }
//                }
//                list.forEach((item) -> {
//                    Manager.gI().updateProduct(item);
//                });
//            } finally {
//                saving = false;
//            }
//        }
    }
}
