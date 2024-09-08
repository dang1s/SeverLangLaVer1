package com.event;

import Data.TYPEMENU;
import SqlConnection.Connect;
import com.event.eventpoint.EventPoint;
import com.event.eventpoint.Point;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sg188.lib.Log;
import com.sg188.lib.RandomCollection;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.server.Config;
import com.sg188.server.Main;
import lombok.Getter;
import lombok.Setter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class Event {
    public static final int GIO_TO_HUNG_VUONG = 0;
    public static final int SU_KIEN_HE=1;
    public static final long EXPIRE_7_DAY = 604800000L;
    public static final long EXPIRE_14_DAY = 1209600000L;
    public static final long EXPIRE_3_DAY = 259200000L;
    public static final long EXPIRE_30_DAY = 2592000000L;
    private static Event instance;
    @Getter
    @Setter
    protected int id;
    protected List<EventPoint> eventPoints;
    @Getter
    protected RandomCollection<Integer> itemsThrownFromMonsters;
    @Getter
    protected RandomCollection<Integer> itemsRecFromCoinItem;
    @Getter
    protected RandomCollection<Integer> itemsRecFromGoldItem;
    @Getter
    protected RandomCollection<Integer> itemsRecFromGold2Item;
    protected Set<String> keyEventPoint;
    protected Calendar endTime = Calendar.getInstance();
    public String menuKhaTienNu="";

    public static void init() {
        if (Config.getInstance().getEvent() != null) {
            try {
                instance = (Event) Class.forName(Config.getInstance().getEvent()).newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                Log.error(ex.getMessage(), ex);
            }
        }
    }
    public static Event getEvent() {
        return instance;
    }

    public static boolean isEvent() {
        return (instance != null && !instance.isEnded());
    }
    public Event() {
        itemsThrownFromMonsters = new RandomCollection<>();
        itemsRecFromCoinItem = new RandomCollection<>();
        itemsRecFromGoldItem = new RandomCollection<>();
        itemsRecFromGold2Item = new RandomCollection<>();
        eventPoints = new ArrayList<>();
        keyEventPoint = new TreeSet<>();
        initRandomItem();
    }
    public EventPoint createEventPoint() {
        EventPoint eventPoint = new EventPoint();
        keyEventPoint.forEach((key) -> {
            eventPoint.add(new Point(key, 0, 0));
        });
        return eventPoint;
    }
    public void addEventPoint(EventPoint eventPoint) {
        synchronized (eventPoints) {
            eventPoints.add(eventPoint);
        }
    }

    public void removeEventPoint(EventPoint eventPoint) {
        synchronized (eventPoints) {
            eventPoints.remove(eventPoint);
        }
    }

    public EventPoint findEventPointByPlayerID(int playerID) {
        synchronized (eventPoints) {
            for (EventPoint ev : eventPoints) {
                if (ev.getPlayerID() == playerID) {
                    return ev;
                }
            }
            return null;
        }
    }
    public boolean useEventItem(Char p, int itemId, RandomCollection<Integer> rc) {
        int[][] itemRequires = new int[][] { { itemId, 1 } };
        return useEventItem(p, 1, itemRequires, 0, 0, 0, rc);
    }

    public boolean useEventItem(Char p, int number, int[][] itemRequire, int gold, int coin, int yen,
                                RandomCollection<Integer> rc) {
        return makeEventItem(p, number, itemRequire, gold, coin, yen, rc, -1);
    }

    public boolean useEventItem(Char p, int number, int gold, int coin, RandomCollection<Integer> rc) {
        return makeEventItem(p, number, new int[][] {}, gold, coin, 0, rc, -1);
    }

    public boolean makeEventItem(Char p, int number, int[][] itemRequire, int gold, int coin, int yen,
                                 int itemIdReceive) {
        return makeEventItem(p, number, itemRequire, gold, coin, yen, null, itemIdReceive);
    }
    public boolean makeEventItem(Char p, int number, int[][] itemRequire, int gold, int coin, int vangkhoa,
                                 RandomCollection<Integer> rc, int itemIdReceive) {
        if (number < 1) {
            p.getService().warningMessage( "Số lượng tối thiểu là 1.");
            return false;
        }

        if (number > 1000) {
            p.getService().warningMessage( "Số lượng tối đa là 1.000.");
            return false;
        }
        int priceGold = number * gold;
        int priceCoin = number * coin;
        int priceGoldLock = number * vangkhoa;
        for (int i = 0; i < itemRequire.length; i++) {
            int itemId = itemRequire[i][0];
            int amount = itemRequire[i][1] * number;
            int index = p.getIndexItemByIdInBag(itemId);
            if (index == -1 || p.Bag.arrItemBag[index] == null || !p.Bag.arrItemBag[index].has(amount)) {
                p.getService().warningMessage( "Không đủ " + new Item(itemId).getItemTemplate().name);
                return false;
            }
        }
        if (p.Bag.vangKhoa < priceGoldLock) {
            p.getService().warningMessage( "Không đủ yên");
            return false;
        } else if (p.Bag.vang < priceGold) {
            p.getService().warningMessage( "Không đủ lượng");
            return false;
        } else if (p.Bag.bac < priceCoin) {
            p.getService().warningMessage( "Không đủ xu");
            return false;
        } else if (rc != null && p.getCountNullItemBag() < number) {
            p.getService().warningMessage("Hành trang không đủ chỗ trống");
            return false;
        } else if (itemIdReceive != -1 && p.getCountNullItemBag() < 1) {
            p.getService().warningMessage( "Hành trang không đủ chỗ trống");
            return false;
        }
        if (priceGoldLock > 0) {
            p.addVangKhoa(-priceGold);
        }
        if (priceGold > 0) {
            p.addVang(-priceGold);
        }
        if (priceCoin > 0) {
            p.addBac(-priceCoin);
        }
        for (int i = 0; i < itemRequire.length; i++) {
            int itemId = itemRequire[i][0];
            int amount = itemRequire[i][1] * number;
            Item item = p.FindItemBag(itemId);
            if(item.getAmount() > amount){
                p.removeItemByAmount(item, amount);
                p.msgUseItemBag(item);
            }else {
                p.removeItem(item, true);
                p.msgRemoveItemBag(item);
            }
        }
        if (rc != null) {
            for (int i = 0; i < number; i++) {
                if (!p.Info.khoaExp){
                    p.addExp(5000000);
                }
                p.addBacKhoa(Utlis.nextInt(20000,30000));
                int itemId = rc.next();
                Item itm = new Item(itemId);
                Item itmUsed = new Item(itemRequire[0][0]); // item used\
                if(itm.getItemTemplate().type== 14) {
                    itm.createOptionCaiTrang();
                }
                if(itm.getItemTemplate().type== 15||itm.getItemTemplate().type== 16){
                    itm.expiry = System.currentTimeMillis()+EXPIRE_14_DAY ;
                    if(itm.getItemTemplate().type== 15) {
                        itm.createOptionTanTo();
                    }else {
                        itm.createOptionTT();
                    }
                    itm.createItemOptions();
                }
                p.addItem(itm);
                p.msgAddItemBag(itm);
//                itm.initExpire();
                if ((itemId >= 295&&itemId <= 297)|| (itemId >= 908&&itemId <= 911)||itemId== 528||itemId==530||itemId== 702||itemId==724||itemId==727||itemId==785||itemId==787
                        ||itemId==824||itemId==812||itemId==871||itemId==881||itemId==882) {
                    Main.HeThongCTG("Người chơi "+
                            p.Info.name + " sử dụng " + itmUsed.getItemTemplate().name + " nhận được " + itm.getItemTemplate().name,2);
                }
            }

        } else if (itemIdReceive != -1) {
            Item itm = new Item(itemIdReceive);
            itm.setAmount(number);
            p.addItem(itm);
            p.msgAddItemBag(itm);
        }

        return true;
    }
    public int randomItemID() {
        return itemsThrownFromMonsters.next();
    }

    public boolean isEnded() {
        return endTime.getTime().getTime() - System.currentTimeMillis() <= 0;
    }
    public void initRandomItem() {
        // item receive from coin item
        itemsRecFromCoinItem.add(20, 5);
        itemsRecFromCoinItem.add(20, 6);
        itemsRecFromCoinItem.add(20, 7);
        itemsRecFromCoinItem.add(10, 8);
        itemsRecFromCoinItem.add(5, 9);
        itemsRecFromCoinItem.add(30, 161);
        itemsRecFromCoinItem.add(30, 277);
        itemsRecFromCoinItem.add(40, 428);
//        itemsRecFromCoinItem.add(2.5, 293);
//        itemsRecFromCoinItem.add(2.5, 298);
//        itemsRecFromCoinItem.add(2.5, 326);
//        itemsRecFromCoinItem.add(2.5, 327);
//        itemsRecFromCoinItem.add(2.5, 372);
//        itemsRecFromCoinItem.add(2.5, 373);
//        itemsRecFromCoinItem.add(2.5, 374);
//        itemsRecFromCoinItem.add(2.5, 375);
//        itemsRecFromCoinItem.add(2.5, 376);
//        itemsRecFromCoinItem.add(2.5, 377);
//        itemsRecFromCoinItem.add(2.5, 429);
//        itemsRecFromCoinItem.add(2.5, 430);
//        itemsRecFromCoinItem.add(2.5, 372);
//        itemsRecFromCoinItem.add(2.5, 431);
//        itemsRecFromCoinItem.add(2.5, 458);
//        itemsRecFromCoinItem.add(2.5, 459);
//        itemsRecFromCoinItem.add(2.5, 460);
//        itemsRecFromCoinItem.add(2.5, 461);
//        itemsRecFromCoinItem.add(2.5, 465);
//        itemsRecFromCoinItem.add(2.5, 464);

        itemsRecFromCoinItem.add(0.2, 295);//ruong bac vang bach kim
        itemsRecFromCoinItem.add(0.2, 296);
        itemsRecFromCoinItem.add(0.02, 297);

//        itemsRecFromCoinItem.add(2.5, 461);
//        itemsRecFromCoinItem.add(2.5, 465);
//        itemsRecFromCoinItem.add(2.5, 464);

        itemsRecFromCoinItem.add(0.3, 294);//tu luyen bi kip
        itemsRecFromCoinItem.add(20, 599);// manh huyet ke gioi han
        itemsRecFromCoinItem.add(40, 434);

        itemsRecFromCoinItem.add(2, 562);//da myo,sharin,byo,rinne
        itemsRecFromCoinItem.add(2, 564);
        itemsRecFromCoinItem.add(2, 566);
        itemsRecFromCoinItem.add(1, 354);

        itemsRecFromCoinItem.add(0.5, 563);//ngoc myo,sharin,byo,rinne
        itemsRecFromCoinItem.add(0.5, 565);
        itemsRecFromCoinItem.add(0.5, 567);
        itemsRecFromCoinItem.add(0.1, 353);

        itemsRecFromCoinItem.add(0.1, 368);//banh tiem nang
        itemsRecFromCoinItem.add(0.1, 369);// banh ky nang


        itemsRecFromCoinItem.add(40, 355);//duoc pham cap 2
        itemsRecFromCoinItem.add(40, 356);
        itemsRecFromCoinItem.add(40, 357);

        itemsRecFromCoinItem.add(40, 358);//duoc pham cap 3
        itemsRecFromCoinItem.add(40, 359);
        itemsRecFromCoinItem.add(40, 360);
        itemsRecFromCoinItem.add(8, 462);
        itemsRecFromCoinItem.add(8, 174);//lb hkg
        itemsRecFromCoinItem.add(8, 175);
        itemsRecFromCoinItem.add(8, 179);
        itemsRecFromCoinItem.add(8, 216);
        itemsRecFromCoinItem.add(8, 217);
        itemsRecFromCoinItem.add(8, 218);
        itemsRecFromCoinItem.add(8, 248);
        itemsRecFromCoinItem.add(8, 278);
        itemsRecFromCoinItem.add(8, 302);
        itemsRecFromCoinItem.add(8, 315);
        itemsRecFromCoinItem.add(0.5, 917);//the bai gia toc
        itemsRecFromCoinItem.add(0.5, 915);

        // item receive from gold item
        itemsRecFromGoldItem.add(10, 7);
        itemsRecFromGoldItem.add(5, 8);
        itemsRecFromGoldItem.add(2, 9);
        itemsRecFromGoldItem.add(1, 10);
        itemsRecFromGoldItem.add(40, 428);
        itemsRecFromGoldItem.add(0.02, 11);
//        itemsRecFromGoldItem.add(2.5, 514);//cai trang
//        itemsRecFromGoldItem.add(2.5, 515);
//        itemsRecFromGoldItem.add(2.5, 516);
//        itemsRecFromGoldItem.add(2.5, 517);
//        itemsRecFromGoldItem.add(2.5, 518);
//        itemsRecFromGoldItem.add(2.5, 519);
//        itemsRecFromGoldItem.add(2.5, 520);
//        itemsRecFromGoldItem.add(2.5, 521);
//        itemsRecFromGoldItem.add(2.5, 522);
//        itemsRecFromGoldItem.add(2.5, 523);
//        itemsRecFromGoldItem.add(2.5, 524);
//        itemsRecFromGoldItem.add(2.5, 525);
//        itemsRecFromGoldItem.add(2.5, 526);
//        itemsRecFromGoldItem.add(2.5, 527);
//        itemsRecFromGoldItem.add(2.5, 528);
//        itemsRecFromGoldItem.add(2.5, 529);
//        itemsRecFromGoldItem.add(2.5, 856);
//        itemsRecFromGoldItem.add(2.5, 886);
//        itemsRecFromGoldItem.add(2.5, 887);
//        itemsRecFromGoldItem.add(0.005, 528);//ct hiem
//        itemsRecFromGoldItem.add(0.005, 530);
//        itemsRecFromGoldItem.add(0.005, 702);

        itemsRecFromGoldItem.add(0.2, 529);//ct new
        itemsRecFromGoldItem.add(0.2, 555);
        itemsRecFromGoldItem.add(0.2, 556);
        itemsRecFromGoldItem.add(0.2, 856);
        itemsRecFromGoldItem.add(0.2, 521);
//        itemsRecFromGoldItem.add(0.001, 914);//ngoi sao hiem
        itemsRecFromGoldItem.add(0.2, 871);//tanto doc la binh duong
        itemsRecFromGoldItem.add(0.2, 881);
        itemsRecFromGoldItem.add(0.2, 882);
//        itemsRecFromGoldItem.add(0.1, 911);
        itemsRecFromGoldItem.add(1, 688);//ky nang vi thu
        itemsRecFromGoldItem.add(40, 763);//chakra vi thu
        itemsRecFromGoldItem.add(0.4, 724);//thoi trang
        itemsRecFromGoldItem.add(0.4, 727);
        itemsRecFromGoldItem.add(0.4, 785);
        itemsRecFromGoldItem.add(0.4, 787);
        itemsRecFromGoldItem.add(0.4, 820);
        itemsRecFromGoldItem.add(0.4, 812);
        itemsRecFromGoldItem.add(30, 294);//tu luyen bi kip
        itemsRecFromGoldItem.add(50, 599);// manh huyet ke gioi han
        itemsRecFromGoldItem.add(40, 434);
        itemsRecFromGoldItem.add(0.3, 295);//ruong bac vang bach kim
        itemsRecFromGoldItem.add(0.3, 296);
        itemsRecFromGoldItem.add(0.01, 297);
        itemsRecFromGoldItem.add(5, 562);//da myo,sharin,byo,rinne
        itemsRecFromGoldItem.add(5, 564);
        itemsRecFromGoldItem.add(5, 566);
        itemsRecFromGoldItem.add(3, 354);
        itemsRecFromGoldItem.add(2, 563);//ngoc myo,sharin,byo,rinne
        itemsRecFromGoldItem.add(2, 565);
        itemsRecFromGoldItem.add(2, 567);
        itemsRecFromGoldItem.add(2, 353);
        itemsRecFromGoldItem.add(1, 368);//banh tiem nang
        itemsRecFromGoldItem.add(1, 369);// banh ky nang
        itemsRecFromGoldItem.add(15, 355);//duoc pham cap 2
        itemsRecFromGoldItem.add(15, 356);
        itemsRecFromGoldItem.add(15, 357);
        itemsRecFromGoldItem.add(15, 358);//duoc pham cap 3
        itemsRecFromGoldItem.add(15, 359);
        itemsRecFromGoldItem.add(15, 360);
        itemsRecFromGoldItem.add(3, 462);
        itemsRecFromGoldItem.add(10, 174);//lb hkg
        itemsRecFromGoldItem.add(10, 175);
        itemsRecFromGoldItem.add(10, 179);
        itemsRecFromGoldItem.add(10, 216);
        itemsRecFromGoldItem.add(10, 217);
        itemsRecFromGoldItem.add(10, 218);
        itemsRecFromGoldItem.add(10, 248);
        itemsRecFromGoldItem.add(10, 278);
        itemsRecFromGoldItem.add(10, 302);
        itemsRecFromGoldItem.add(10, 315);
        itemsRecFromGoldItem.add(1, 917);//the bai gia toc
        itemsRecFromGoldItem.add(1, 915);

        // item receive from gold 2 item
        itemsRecFromGold2Item.add(0.005, 2);
    }

    public void viewTop(Char p, String key,String title, String format) {
        List<EventPoint> list = eventPoints.stream().sorted((o1, o2) -> {
            int p1 = o1.getPoint(key);
            int p2 = o2.getPoint(key);
            return p2 - p1;
        }).limit(10).filter(t -> t.getPoint(key) > 0).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        int rank = 1;
        for (EventPoint t : list) {
            sb.append(String.format(format,rank++, t.getPlayerName(), t.getPoint(key)))
                    .append("\n;");
        }
        p.typeMenu = TYPEMENU.BXH;
        p.getService().sendTextNPC(title,sb.toString());
    }

    public void action(Char p, int type, int amount){

    }

    public void useItem(Char p, Item item){

    }

    public void menu(Char p,int index,int index2){

    }
    public void loadEventPoint() {
        try {
            eventPoints.clear();
            PreparedStatement ps = Connect.getConnection()
                    .prepareStatement("SELECT `event_points`.*, `player`.`Name` FROM `event_points`, `player` WHERE `event_points`.`event_id` = ? AND `player`.`IdChar` = `event_points`.`player_id`;");
            ps.setInt(1, this.id);
            ResultSet rs = ps.executeQuery();
            Gson g = new Gson();
            while (rs.next()) {
                EventPoint eventPoint = createEventPoint();
                int id = rs.getInt("id");
                int playerID = rs.getInt("player_id");
                String name = rs.getString("name");
                ArrayList<Point> points = g.fromJson(rs.getString("point"), new TypeToken<ArrayList<Point>>() {
                }.getType());
                eventPoint.setId(id);
                eventPoint.setPlayerID(playerID);
                eventPoint.setPlayerName(name);
                eventPoint.setPoints(points);
                eventPoint.addIfMissing(keyEventPoint);
                eventPoints.add(eventPoint);
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Event.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
