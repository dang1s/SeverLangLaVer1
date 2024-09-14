package com.sg188.server;

import Manager.Manager;
import MapService.Map;
import com.event.Event;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.real.ItemMap;
import com.sg188.real.Mob;
import okhttp3.internal.Util;

import javax.swing.text.Utilities;
import java.util.ArrayList;
import java.util.List;

public class BossManager {
    private static final BossManager instance = new BossManager();

    public static BossManager gI() {
        return instance;
    }

    private int[] itemBoss1x = {174, 175, 179, 216, 217, 218, 248, 278, 302, 315};
    private int[] itemBoss2x = {174, 175, 179, 216, 217, 218, 248, 278, 302, 315};
    private int[] itemBoss3x = {174, 175, 179, 216, 217, 218, 248, 278, 302, 315};
    private int[] itemBoss4x = {174, 175, 179, 216, 217, 218, 248, 278, 302, 315};
    private int[] itemBoss5x = {174, 175, 179, 216, 217, 218, 248, 278, 302, 315};
    private int[] itemBossSK={918,918,918,9,9,9,9,8,8,8,10,10,295,296,818,819,818,819,818,819,818,819,818,819,818,819,818,819,818,819,818,819,818,819,818,819,818,819,818,819,818,819,174,175,179,216,217,218,248,278,302,315,174,175,179,216,217,218,248,278,302,315,174,175,179,216,217,218,248,278,302,315,174,175,179,216,217,218,248,278,302,315,174,175,179,216,217,218,248,278,302,315};
    private byte[] idMap = {57, 65, 87, 79, 73};
    private List<Mob> listBoss = new ArrayList<>();
    private List<Mob> bossSK = new ArrayList<>();

    public void initBoss() {
        Mob boss1x = createBoss(199, 100000000, 7500000, 15, (short) 958, (short) 450);
        addItemMap(itemBoss1x, boss1x);
        listBoss.add(boss1x);
        Mob boss2x = createBoss(200, 200000000, 15000000, 25, (short) 906, (short) 137);
        addItemMap(itemBoss2x, boss2x);
        listBoss.add(boss2x);
        Mob boss3x = createBoss(201, 400000000, 22500000, 35, (short) 969, (short) 155);
        addItemMap(itemBoss3x, boss3x);
        listBoss.add(boss3x);
        Mob boss4x = createBoss(202, 1000000000, 30000000, 45, (short) 1036, (short) 186);
        addItemMap(itemBoss4x, boss4x);
        listBoss.add(boss4x);
        Mob boss5x = createBoss(203, 1500000000, 37500000, 55, (short) 751, (short) 190);
        addItemMap(itemBoss5x, boss5x);
        listBoss.add(boss5x);

        Mob bossST = createBoss(293, 2000000000, 37500000, 55, (short) 799, (short) 284);
        addItemMap(itemBossSK, bossST);
        bossSK.add(bossST);
        Mob bossTT = createBoss(294, 2000000000, 37500000, 55, (short) 578, (short) 186);
        addItemMap(itemBossSK, bossTT);
        bossSK.add(bossTT);
    }

    public void spawnBoss() {
        for (int i = 0; i < listBoss.size(); i++) {
            Mob boss = listBoss.get(i).cloneMob();
            Map.maps[idMap[i]].addBoss(Utlis.nextInt(0, 8), boss);
        }
        thongBaoBoss();
    }
    public void spawnBossSK() {
        for (int i = 0; i < bossSK.size(); i++) {
            Mob boss = bossSK.get(i).cloneMob();
            Map.maps[i== 0?70:66].addBoss(Utlis.nextInt(0, 8), boss);
        }
        Main.HeThongCTG("Boss sự kiện đã xuất hiện,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);

    }


    public void thongBaoBoss() {
        Main.HeThongCTG("Cao thủ nhẫn giả đã xuất hiện,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
    }

    private void addItemMap(int[] idItem, Mob mob) {
        try {
            for (int i = 0; i < idItem.length; i++) {
                Item item = new Item(idItem[i]);

                // Kiểm tra nếu là itemBoss1x, thì set quantity = 2-4
                for (int idBossItem : itemBoss1x) {
                    if (idItem[i] == idBossItem) {
                        item.amount = 1;
                        break;
                    }
                }


                // Kiểm tra nếu là itemBoss2x, thì set quantity = 2-4
                for (int idBossItem : itemBoss2x) {
                    if (idItem[i] == idBossItem) {
                        item.amount = 2;
                        break;
                    }
                }
                // Kiểm tra nếu là itemBoss3x, thì set quantity = 2-4
                for (int idBossItem : itemBoss3x) {
                    if (idItem[i] == idBossItem) {
                        item.amount = 3;
                        break;
                    }
                }
                // Kiểm tra nếu là itemBoss4x, thì set quantity = 2-4
                for (int idBossItem : itemBoss4x) {
                    if (idItem[i] == idBossItem) {
                        item.amount = 4;
                        break;
                    }
                }
                // Kiểm tra nếu là itemBoss5x, thì set quantity = 2-4
                for (int idBossItem : itemBoss5x) {
                    if (idItem[i] == idBossItem) {
                        item.amount = 6;
                        break;
                    }
                }

//                if (item.isItemTrangBi()) {
//                    if (item.isVuKhi()) {
//                        Item.setOptionsVuKhi(item, item.getItemTemplate().levelNeed);
//                    } else {
//                        Item.setOptionsTrangBiPhuKien(item, item.getItemTemplate().levelNeed);
//                    }
//                }
                mob.itemBoss.add(item);
            }
        } catch (Exception e) {

        }
    }
    public void createBossTest(){
        Mob boss = bossSK.get(0).cloneMob();
        Map.maps[70].addBoss(Utlis.nextInt(0, 8), boss);
        boss = bossSK.get(1).cloneMob();
        Map.maps[66].addBoss(Utlis.nextInt(0, 8), boss);
        Main.HeThongCTG("Boss sự kiện đã xuất hiện,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
    }

    public void updateBoss(int hours, int minutes, int seconds) {
        Utlis.schedule(() -> {
            spawnBoss();
            if(Event.getEvent()!=null) {
                if (hours == 6) {
                    Mob boss = bossSK.get(0).cloneMob();
                    Map.maps[70].addBoss(Utlis.nextInt(0, 8), boss);
                    Main.HeThongCTG("Sơn Tinh đã xuất hiện,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
                }
                if (hours == 9) {
                    Mob boss = bossSK.get(0).cloneMob();
                    Map.maps[70].addBoss(Utlis.nextInt(0, 8), boss);
                    Main.HeThongCTG("Sơn Tinh đã xuất hiện,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
                }
                if (hours == 12) {
                    Mob boss = bossSK.get(0).cloneMob();
                    Map.maps[70].addBoss(Utlis.nextInt(0, 8), boss);
                    Main.HeThongCTG("Sơn Tinh đã xuất hiện,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
                }
                if (hours == 18) {
                    Mob boss = bossSK.get(1).cloneMob();
                    Map.maps[66].addBoss(Utlis.nextInt(0, 8), boss);
                    Main.HeThongCTG("Thủy Tinh đã xuất hiện,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
                }
                if (hours == 20) {
                    Mob boss = bossSK.get(1).cloneMob();
                    Map.maps[66].addBoss(Utlis.nextInt(0, 8), boss);
                    Main.HeThongCTG("Thủy Tinh đã xuất hiện,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
                }
                if (hours == 22) {
                    Mob boss = bossSK.get(1).cloneMob();
                    Map.maps[66].addBoss(Utlis.nextInt(0, 8), boss);
                    Main.HeThongCTG("Thủy Tinh đã xuất hiện,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
                }
            }
        }, hours, minutes, seconds);
    }
    public void updateBossSK(int hours, int minutes, int seconds) {
        Utlis.schedule(() -> {
            if(Event.getEvent()!=null) {
                if (hours == 6) {
                    Mob boss = bossSK.get(0).cloneMob();
                    Map.maps[70].addBoss(Utlis.nextInt(0, 8), boss);
                    Main.HeThongCTG("Sơn Tinh đã xuất hiện,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
                }
                if (hours == 9) {
                    Mob boss = bossSK.get(0).cloneMob();
                    Map.maps[70].addBoss(Utlis.nextInt(0, 8), boss);
                    Main.HeThongCTG("Sơn Tinh đã xuất hiện,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
                }
                if (hours == 12) {
                    Mob boss = bossSK.get(0).cloneMob();
                    Map.maps[70].addBoss(Utlis.nextInt(0, 8), boss);
                    Main.HeThongCTG("Sơn Tinh đã xuất hiện,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
                }
                if (hours == 18) {
                    Mob boss = bossSK.get(1).cloneMob();
                    Map.maps[66].addBoss(Utlis.nextInt(0, 8), boss);
                    Main.HeThongCTG("Thủy Tinh đã xuất hiện,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
                }
                if (hours == 20) {
                    Mob boss = bossSK.get(1).cloneMob();
                    Map.maps[66].addBoss(Utlis.nextInt(0, 8), boss);
                    Main.HeThongCTG("Thủy Tinh đã xuất hiện,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
                }
                if (hours == 22) {
                    Mob boss = bossSK.get(1).cloneMob();
                    Map.maps[66].addBoss(Utlis.nextInt(0, 8), boss);
                    Main.HeThongCTG("Thủy Tinh đã xuất hiện,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
                }
            }
        }, hours, minutes, seconds);
    }

    public Mob createBoss(int id, int hp, int exp, int level, short cx, short cy) {
        Mob mob = new Mob();
        mob.id = id;
        mob.idEntity = 99999;
        mob.hp = mob.hpFull = mob.hpGoc = hp;
        mob.level = level;
        mob.levelBoss = 10;
        mob.exp = exp;
        mob.cx = cx;
        mob.cy = cy;
        mob.status = 0;
        mob.isReSpawn = false;
        return mob;
    }
}
