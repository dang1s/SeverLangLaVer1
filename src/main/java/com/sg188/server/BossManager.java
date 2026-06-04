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

    private int[] itemBoss1x = {174, 175, 179, 216, 217, 218, 248, 278, 302, 315, 296,919 , 735};
    private int[] itemBoss1x2 = {296};
    private int[] itemBoss2x = {174, 175, 179, 216, 217, 218, 248, 278, 302, 315, 296,919 , 735};
    private int[] itemBoss2x2 = {296};
    private int[] itemBoss3x = {174, 175, 179, 216, 217, 218, 248, 278, 302, 315,919, 735};
    private int[] itemBoss3x2 = {296};
    private int[] itemBoss4x = {174, 175, 179, 216, 217, 218, 248, 278, 302, 315, 296,919, 735};
    private int[] itemBoss4x2 = {296};
    private int[] itemBoss5x = {174, 175, 179, 216, 217, 218, 248, 278, 302, 315,919, 735};
    private int[] itemBoss5x2 = {296};
    private int[] itemBossSK={174, 175, 179, 216, 217, 218, 248, 278, 302, 315, 10, 10, 10};
    private int[] itemViThu = {763};
    private int[] itemViThu2 = {919};
    private int[] itemBossMadara = {932, 932};
    private int[] itemBossHangViThu = {735};
    private int[] itemBossHangViThu2 = {919};
    private byte[] idMap = {57, 65, 87, 79, 73};
    private byte[] idMapViThu = {59, 69, 60, 71, 85, 103, 73, 102, 106};///
    private byte[] idMapBossMadara = {86};
    private List<Mob> listBoss = new ArrayList<>();
    private List<Mob> listBossMadara = new ArrayList<>();
    private List<Mob> bossSK = new ArrayList<>();
    private List<Mob> listBossViThu = new ArrayList<>();
    private List<Mob> listBossHangViThu = new ArrayList<>();
    public void initBoss() {
        Mob boss1x = createBoss(199, 1000000000, 7500000, 45, (short) 958, (short) 450);
        addItemMap(itemBoss1x, boss1x);
        addItemMap(itemBoss1x2, boss1x);
        listBoss.add(boss1x);
        Mob boss2x = createBoss(200, 2000000000, 15000000, 45, (short) 906, (short) 137);
        addItemMap(itemBoss2x, boss2x);
        addItemMap(itemBoss2x2, boss2x);
        listBoss.add(boss2x);
        Mob boss3x = createBoss(201, 400000000, 22500000, 45, (short) 969, (short) 155);
        addItemMap(itemBoss3x, boss3x);
        addItemMap(itemBoss3x2, boss3x);
        listBoss.add(boss3x);
        Mob boss4x = createBoss(202, 1000000000, 30000000, 45, (short) 1036, (short) 186);
        addItemMap(itemBoss4x, boss4x);
        addItemMap(itemBoss4x2, boss4x);
        listBoss.add(boss4x);
        Mob boss5x = createBoss(203, 1500000000, 37500000, 55, (short) 751, (short) 190);
        addItemMap(itemBoss5x, boss5x);
        addItemMap(itemBoss5x2, boss5x);
        listBoss.add(boss5x);

        Mob bossST = createBoss(293, 2000000000, 37500000, 55, (short) 799, (short) 284);
        addItemMap(itemBossSK, bossST);
        bossSK.add(bossST);
        Mob bossTT = createBoss(294, 2000000000, 37500000, 55, (short) 578, (short) 186);
        addItemMap(itemBossSK, bossTT);
        bossSK.add(bossTT);
    }
    public void initBossViThu() {
        Mob nhatVi = createBoss(251, 1000000000, 7500000, 59, (short) 1479, (short) 395);
        addItemMap(itemViThu, nhatVi);
        addItemMap(itemViThu2, nhatVi);
        listBossViThu.add(nhatVi);
        Mob nhiVi = createBoss(252, 1200000000, 15000000, 59, (short) 364, (short) 443);
        addItemMap(itemViThu, nhiVi);
        listBossViThu.add(nhiVi);
        Mob tamVi = createBoss(253, 1300000000, 22500000, 59, (short) 1375, (short) 289);
        addItemMap(itemViThu, tamVi);
        listBossViThu.add(tamVi);
        Mob tuVi = createBoss(254, 1400000000, 30000000, 59, (short) 1000, (short) 120);
        addItemMap(itemViThu, tuVi);
        listBossViThu.add(tuVi);
        Mob nguVi = createBoss(255, 1500000000, 37500000, 59, (short) 1062, (short) 275);
        addItemMap(itemViThu, nguVi);
        listBossViThu.add(nguVi);
        Mob lucVi = createBoss(256, 1600000000, 40000000, 59, (short) 171, (short) 194);
        addItemMap(itemViThu, lucVi);
        listBossViThu.add(lucVi);
        Mob thatVi = createBoss(257, 1700000000, 50000000, 59, (short) 1559, (short) 539);
        addItemMap(itemViThu, thatVi);
        listBossViThu.add(thatVi);
        Mob batVy = createBoss(258, 1800000000, 60000000, 59, (short) 892, (short) 401);
        addItemMap(itemViThu, batVy);
        listBossViThu.add(batVy);
        Mob cuuVi = createBoss(259, 1900000000, 70000000, 59, (short) 1144, (short) 178);
        addItemMap(itemViThu, cuuVi);
        listBossViThu.add(cuuVi);
    }

    public void initBossMadara() {
        Mob madara = createBoss(273, 200000000, 200000000, 59, (short) 2295, (short) 315);
        addItemMap(itemBossMadara, madara);
        listBossMadara.add(madara);
    }

    public void initBossHangViThu() {
        Mob boss1 = createBoss(285, 2000000000, 50000000, 70, (short) 743, (short) 542);
        addItemMap(itemBossHangViThu, boss1);
        addItemMap(itemBossHangViThu2, boss1);
        listBossHangViThu.add(boss1);
        Mob boss2 = createBoss(286, 2000000000, 50000000, 70, (short) 921, (short) 677);
        addItemMap(itemBossHangViThu, boss2);
        addItemMap(itemBossHangViThu2, boss2);
        listBossHangViThu.add(boss2);
        Mob boss3 = createBoss(287, 2000000000, 50000000, 70, (short) 303, (short) 210);
        addItemMap(itemBossHangViThu, boss3);
        addItemMap(itemBossHangViThu2, boss3);
        listBossHangViThu.add(boss3);
        Mob boss4 = createBoss(288, 2000000000, 50000000, 70, (short) 672, (short) 210);
        addItemMap(itemBossHangViThu, boss4);
        addItemMap(itemBossHangViThu2, boss4);
        listBossHangViThu.add(boss4);
        Mob boss5 = createBoss(289, 2000000000, 50000000, 70, (short) 1144, (short) 178);
        addItemMap(itemBossHangViThu, boss5);
        addItemMap(itemBossHangViThu2, boss5);
        listBossHangViThu.add(boss5);
    }

    public void spawnBossMadara() {
        for (int i = 0; i < listBossMadara.size(); i++) {
            Mob boss = listBossMadara.get(i).cloneMob();
            Map.maps[idMapBossMadara[i]].addBoss(0, boss);
        }
        thongBaoBossMadara();
    }
    public void spawnBoss() {
        for (int i = 0; i < listBoss.size(); i++) {
            Mob boss = listBoss.get(i).cloneMob();
            Map.maps[idMap[i]].addBoss(Utlis.nextInt(0, 8), boss);
        }
        thongBaoBoss();
    }

    public void spawnBossViThu() {
        for (int i = 0; i < listBossViThu.size(); i++) {
            Mob boss = listBossViThu.get(i).cloneMob();
            Map.maps[idMapViThu[i]].addBoss(0, boss);
        }
        thongBaoBossViThu();
    }

    public void spawnBossHangViThu() {
        for (int i = 0; i < listBossHangViThu.size(); i++) {
            Mob boss = listBossHangViThu.get(i).cloneMob();
            Map.maps[90].addBoss(0, boss);
        }
        thongBaoBossHangViThu();
    }
    public void spawnBossSK() {
        for (int i = 0; i < bossSK.size(); i++) {
            Mob boss = bossSK.get(i).cloneMob();
            Map.maps[i== 0?70:66].addBoss(Utlis.nextInt(0, 8), boss);
        }
        Main.HeThongCTG("Boss sự kiện đã xuất hiện,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);

    }

    public void thongBaoBossMadara() {
        Main.HeThongCTG("Madara đã xuất hiện,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
    }
    public void thongBaoBoss() {
        Main.HeThongCTG("Cao thủ nhẫn giả đã xuất hiện,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
    }

    public void thongBaoBossViThu() {
        Main.HeThongCTG("Vĩ thú đã xuất hiện,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
    }

    public void thongBaoBossHangViThu() {
        Main.HeThongCTG("Boss Hàng Vĩ Thú đã xuất hiện tại Hàng Vĩ Thú,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
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
                for (int idBossItem : itemBoss1x2) {
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
                for (int idBossItem : itemBoss2x2) {
                    if (idItem[i] == idBossItem) {
                        item.amount = 1;
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
                for (int idBossItem : itemBoss3x2) {
                    if (idItem[i] == idBossItem) {
                        item.amount = 1;
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
                for (int idBossItem : itemBoss4x2) {
                    if (idItem[i] == idBossItem) {
                        item.amount = 1;
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
                for (int idBossItem : itemBoss5x2) {
                    if (idItem[i] == idBossItem) {
                        item.amount = 1;
                        break;
                    }
                }


                for (int idBossItem : itemViThu) {
                    if (idItem[i] == idBossItem) {
                        item.amount = 1000;
                        break;
                    }
                }
                for (int idBossItem : itemViThu2) {
                    if (idItem[i] == idBossItem) {
                        item.amount = 1;
                        break;
                    }
                }

                for(int idBossItem: itemBossMadara) {
                    if (idItem[i] == idBossItem) {
                        item.amount = 20;
                        break;
                    }
                }

                for (int idBossItem : itemBossHangViThu) {
                    if (idItem[i] == idBossItem) {
                        item.amount = 50;
                        break;
                    }
                }
                for (int idBossItem : itemBossHangViThu2) {
                    if (idItem[i] == idBossItem) {
                        item.amount = 1;
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

//    private void addItemMapViThu(Mob mob) {
//        try {
//            Item item = new Item(723);
//            if(mob.id == 251) {
//                item.amount = 100;
//            }else if(mob.id == 252) {
//                item.amount = 100;
//            }else if(mob.id == 253) {
//                item.amount = 100;
//            }else if(mob.id == 254) {
//                item.amount = 100;
//            }else if(mob.id == 255) {
//                item.amount = 100;
//            }else if(mob.id == 256) {
//                item.amount = 100;
//            }else if(mob.id == 257) {
//                item.id = 687;
//                item.amount = 30;
//            }else if(mob.id == 258) {
//                item.id = 687;
//                item.amount = 40;
//            }else if(mob.id == 259) {
//                item.id = 687;
//                item.amount = 50;
//            }
//            mob.itemBoss.add(item);
//
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
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

    public void updateBossViThu(int hours, int minutes, int seconds) {
        Utlis.schedule(() -> {
            spawnBossViThu();
        }, hours, minutes, seconds);
    }

    public void updateBossHangViThu(int hours, int minutes, int seconds) {
        Utlis.schedule(() -> {
            spawnBossHangViThu();
        }, hours, minutes, seconds);
    }

    public void updateBossMadara(int hours, int minutes, int seconds) {
        Utlis.schedule(() -> {
            spawnBossMadara();
        }, hours, minutes, seconds);
    }
    public void updateBossSK(int hours, int minutes, int seconds) {
        Utlis.schedule(() -> {
            if(Event.getEvent()!=null || true) {
                if (hours == 6) {
                    Mob boss = bossSK.get(0).cloneMob();
                    Map.maps[56].addBoss(Utlis.nextInt(0, 8), boss);
                    Main.HeThongCTG("Sơn Tinh đã xuất hiện tại đồi trung tâm ,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
                }
                if (hours == 9) {
                    Mob boss = bossSK.get(0).cloneMob();
                    Map.maps[70].addBoss(Utlis.nextInt(0, 8), boss);
                    Main.HeThongCTG("Sơn Tinh đã xuất hiện,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
                }
                if (hours == 12) {
                    Mob boss = bossSK.get(0).cloneMob();
                    Map.maps[42].addBoss(Utlis.nextInt(0, 8), boss);
                    Main.HeThongCTG("Sơn Tinh đã xuất hiện đồi cát,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
                }
                if (hours == 18) {
                    Mob boss = bossSK.get(1).cloneMob();
                    Map.maps[66].addBoss(Utlis.nextInt(0, 8), boss);
                    Main.HeThongCTG("Thủy Tinh đã xuất hiện hẻm núi ,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
                }
                if (hours == 20) {
                    Mob boss = bossSK.get(1).cloneMob();
                    Map.maps[57].addBoss(Utlis.nextInt(0, 8), boss);
                    Main.HeThongCTG("Thủy Tinh đã xuất hiện,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
                }
                if (hours == 22) {
                    Mob boss = bossSK.get(1).cloneMob();
                    Map.maps[61].addBoss(Utlis.nextInt(0, 8), boss);
                    Main.HeThongCTG("Thủy Tinh đã xuất hiện vách ichigo ,các nhẫn giả mau tìm kiếm và tiêu diệt để nhận những phần quà hấp dẫn", 2);
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
