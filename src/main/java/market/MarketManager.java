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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
    public MarketManager(){
        productList = new ArrayList<>();
        running=true;
        load();
    }
    public void load(){
        PreparedStatement ps = null;
        try {
            ps = Connect.getConnection()
                    .prepareStatement("SELECT * FROM `market` WHERE `status` = ?");
            ps.setInt(1, STATUS_ON_SALE);
            ResultSet rs = ps.executeQuery();
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
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    public void themItem(Char p, Item item, int price,int time) {
        ItemMarket itemMarket = new ItemMarket();
        itemMarket.setId(id++);
        itemMarket.setName(p.Info.name);
        itemMarket.setPrice(price);
        itemMarket.setTime((int) ((time+System.currentTimeMillis())/1000));
        itemMarket.setItem(item);
        itemMarket.setStatus(STATUS_ON_SALE);
        add(itemMarket);
        Manager.gI().insertItemToStall(itemMarket);
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
            Message m = new Message((byte) 101);
            m.writeShort(index);
            int size = productList.size();
            m.writeShort(size);
            for (ItemMarket market: productList){
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
