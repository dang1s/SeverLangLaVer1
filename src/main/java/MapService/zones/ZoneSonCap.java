package MapService.zones;

import MapService.Map;
import MapService.Zone;
import MapService.world.SonCapMyo;
import MapService.world.Territory;
import Service.HanderMessage;
import com.sg188.lib.Utlis;
import com.sg188.real.*;
import com.sg188.server.lib.Writer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ZoneSonCap extends ZWorld {
    public int level;
    private int[] listitem = {354, // đá myo, ngọc myo
            174, 175, 179, 216, 217, 218, 248, 278, 302, 315, //lệnh bài Hokage
            310, 312, 313, 599};  //mảnh bk, mảnh huyết kế, mảnh tns, mảnh kns};
    private boolean createBoss;
    private int mobid;

    public ZoneSonCap(Map map, int id) {
        super(map, id);
    }

    @Override
    public void createMob() {
        monsters.clear();
        List<XYEntity> entityList = new ArrayList<>();
        int x = 60;
        if (map.mapID == 94) {
            mobid = 263;
            for (int i = 0; i < 8; i++) {
                entityList.add(new XYEntity((short) (140 + x), (short) 518));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 12; i++) {
                entityList.add(new XYEntity((short) (220 + x), (short) 262));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 9; i++) {
                entityList.add(new XYEntity((short) (1000 + x), (short) 238));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 20; i++) {
                entityList.add(new XYEntity((short) (700 + x), (short) 452));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 6; i++) {
                entityList.add(new XYEntity((short) (780 + x), (short) 360));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 5; i++) {
                entityList.add(new XYEntity((short) (1900 + x), (short) 518));
                x += 60;
            }
        } else if (map.mapID == 95) {
            mobid = 262;
            for (int i = 0; i < 6; i++) {
                entityList.add(new XYEntity((short) (128 + x), (short) 250));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 11; i++) {
                entityList.add(new XYEntity((short) (0 + x), (short) 468));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 3; i++) {
                entityList.add(new XYEntity((short) (800 + x), (short) 442));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 8; i++) {
                entityList.add(new XYEntity((short) (1000 + x), (short) 506));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 15; i++) {
                entityList.add(new XYEntity((short) (1587 + x), (short) 440));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 9; i++) {
                entityList.add(new XYEntity((short) (1000 + x), (short) 312));
                x += 60;
            }
        } else if (map.mapID == 97) {
            mobid = 266;
            for (int i = 0; i < 7; i++) {
                entityList.add(new XYEntity((short) (120 + x), (short) 220));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 6; i++) {
                entityList.add(new XYEntity((short) (0 + x), (short) 435));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 5; i++) {
                entityList.add(new XYEntity((short) (450 + x), (short) 419));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 6; i++) {
                entityList.add(new XYEntity((short) (1000 + x), (short) 475));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 12; i++) {
                entityList.add(new XYEntity((short) (1400 + x), (short) 436));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 6; i++) {
                entityList.add(new XYEntity((short) (2100 + x), (short) 420));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 12; i++) {
                entityList.add(new XYEntity((short) (600 + x), (short) 280));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 9; i++) {
                entityList.add(new XYEntity((short) (1460 + x), (short) 215));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 12; i++) {
                entityList.add(new XYEntity((short) (2300 + x), (short) 279));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 8; i++) {
                entityList.add(new XYEntity((short) (2600 + x), (short) 465));
                x += 60;
            }
        } else if (map.mapID == 93) {
            mobid = 265;
            for (int i = 0; i < 6; i++) {
                entityList.add(new XYEntity((short) (100 + x), (short) 779));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 8; i++) {
                entityList.add(new XYEntity((short) (240 + x), (short) 691));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 5; i++) {
                entityList.add(new XYEntity((short) (60 + x), (short) 551));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 10; i++) {
                entityList.add(new XYEntity((short) (240 + x), (short) 302));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 7; i++) {
                entityList.add(new XYEntity((short) (808 + x), (short) 398));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 6; i++) {
                entityList.add(new XYEntity((short) (700 + x), (short) 601));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 6; i++) {
                entityList.add(new XYEntity((short) (1000 + x), (short) 842));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 7; i++) {
                entityList.add(new XYEntity((short) (1000 + x), (short) 1035));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 7; i++) {
                entityList.add(new XYEntity((short) (0 + x), (short) 996));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 5; i++) {
                entityList.add(new XYEntity((short) (220 + x), (short) 890));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 8; i++) {
                entityList.add(new XYEntity((short) (500 + x), (short) 982));
                x += 60;
            }
        } else {
            mobid = 264;
            for (int i = 0; i < 7; i++) {
                entityList.add(new XYEntity((short) (0 + x), (short) 447));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 13; i++) {
                entityList.add(new XYEntity((short) (200 + x), (short) 186));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 22; i++) {
                entityList.add(new XYEntity((short) (960 + x), (short) 171));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 6; i++) {
                entityList.add(new XYEntity((short) (1800 + x), (short) 313));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 7; i++) {
                entityList.add(new XYEntity((short) (1700 + x), (short) 496));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 7; i++) {
                entityList.add(new XYEntity((short) (720 + x), (short) 313));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 7; i++) {
                entityList.add(new XYEntity((short) (700 + x), (short) 496));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 35; i++) {
                entityList.add(new XYEntity((short) (0 + x), (short) 590));
                x += 60;
            }
        }
        for (int i = 0; i < entityList.size(); i++) {
            Mob mob = new Mob();
            mob.id = mobid;
            mob.exp = level * mobid * 10;
            mob.level = level;
            mob.cx = entityList.get(i).cx;
            mob.cy = entityList.get(i).cy;
            mob.status = 4;
            mob.hpGoc = mob.hp = mob.hpFull = level * mobid * 1000;
            mob.expGoc = mob.hpGoc / 8;

            mob.levelBoss = 0;
            mob.paintMiniMap = false;
            mob.idEntity = i;
            monsters.add(mob);
            mob.reSpawn(this);
        }
    }

    @Override
    public void nextMap(Char player) {
        boolean b = false;
        XYEntity xy = player.Info;
        WayPoint waypoint_next = map.getWayPoint(xy);
        if (waypoint_next != null) {
            int nextID = waypoint_next.mapNext;
            Zone z = world.find(nextID);
            if (z != null) {
                if (z.isOpened) {
                    SonCapMyo sonCapMyo = (SonCapMyo) world;
                    sonCapMyo.joinZone(player, nextID);
                    return;
                }
            }
        }
        if (!b) {
            player.service.serverMessage("Cửa này chưa mở bạn ơi");
            player.Info.backXY();
            player.service.setXYChar();
            return;
        }
    }

    @Override
    protected boolean canRespawn(Mob mob) {
        // Điều kiện mới cho việc hồi sinh, ví dụ:
        return false; // Luôn chặn việc hồi sinh
    }

    @Override
    public void setMobDie(Char player, Mob mob) {
        super.setMobDie(player, mob);
        if (getLivingMonstersClan().size() == 0) {
            if (!createBoss)
                createBoss();
        }
        if (mob.levelBoss < 3)
            player.pointDungeon += 5;
        else {
            player.pointDungeon += 10;
        }
        player.service.updatepointDungeon();
        List<Char> member = getChars();
        for (Char pl : member) {
            if (pl != null && pl.user != null) {
                pl.pointDungeon += 5;
                pl.service.updatepointDungeon();
//                Calendar calendar = Calendar.getInstance();
//                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
//                if (dayOfWeek == Calendar.SUNDAY) {
//                    if (pl.clan != null) {
//                        pl.addClanPoint(100);
//                    }
//                    pl.Info.chuyenCan += 100;
//                    pl.Info.chuyenCanTuan += 100;
//                } else {
//                    if (pl.clan != null) {
//                        pl.addClanPoint(50);
//                    }
//                    pl.Info.chuyenCan += 50;
//                    pl.Info.chuyenCanTuan += 50;
//                }
//                pl.user.session.sendMessage(HanderMessage.SendThongBao("Bạn nhận được 50 điểm chuyên cần, 50 điểm cống hiến gia tộc", HanderMessage.YELLOW_MID));
            }
        }
        SonCapMyo sonCapMyo = (SonCapMyo) world;
        switch (mob.id) {
            case 267:

                for (int i = 0; i < listitem.length; i++) {
                    Item it = new Item(listitem[i], false);
                    it.amount = 1;
                    player.addItem(it);
                    player.msgAddItemBag(it);
                }

                sonCapMyo.zones.get(1).isOpened = true;
                sonCapMyo.getService().serverMessage("Cửa tiếp theo đã mở");
                break;
            case 268:
                for (int i = 0; i < listitem.length; i++) {
                    Item it = new Item(listitem[i], false);
                    it.amount = 1;
                    player.addItem(it);
                    player.msgAddItemBag(it);
                }
                sonCapMyo.zones.get(2).isOpened = true;
                sonCapMyo.getService().serverMessage("Cửa tiếp theo đã mở");
                break;
            case 269:
                for (int i = 0; i < listitem.length; i++) {
                    Item it = new Item(listitem[i], false);
                    it.amount = 1;
                    player.addItem(it);
                    player.msgAddItemBag(it);
                }
                sonCapMyo.zones.get(3).isOpened = true;
                sonCapMyo.getService().serverMessage("Cửa tiếp theo đã mở");
                break;
            case 270:
                for (int i = 0; i < listitem.length; i++) {
                    Item it = new Item(listitem[i], false);
                    it.amount = 1;
                    player.addItem(it);
                    player.msgAddItemBag(it);
                }
                sonCapMyo.zones.get(4).isOpened = true;
                sonCapMyo.getService().serverMessage("Cửa tiếp theo đã mở");
                break;
            case 271:
                for (int i = 0; i < listitem.length; i++) {
                    Item it = new Item(listitem[i], false);
                    it.amount = 1;
                    player.addItem(it);
                    player.msgAddItemBag(it);
                }
                sonCapMyo.finish();
                break;
        }
    }

    private void createBoss() {
        if (!createBoss) {
            createBoss = true;
            Mob boss = new Mob();
            switch (map.mapID) {
                case 94:
                    boss.id = 267;
                    boss.cx = 2012;
                    boss.cy = 264;
                    break;
                case 95:
                    boss.id = 268;
                    boss.cx = 2072;
                    boss.cy = 228;
                    break;
                case 97:
                    boss.id = 269;
                    boss.cx = 2933;
                    boss.cy = 475;
                    break;
                case 93:
                    boss.id = 270;
                    boss.cx = 1163;
                    boss.cy = 277;
                    break;
                case 96:
                    boss.id = 271;
                    boss.cx = 251;
                    boss.cy = 447;
                    break;
            }
            boss.level = level;
            boss.levelBoss = 3;
            boss.status = 2;
            boss.hpGoc = boss.hp = boss.hpFull = 1000000000;
            boss.exp = 200000000;
            boss.expGoc = boss.hpGoc / 8;
            boss.paintMiniMap = false;
            boss.he = Utlis.nextInt(1, 5);
            boss.idEntity = monsters.size();
            monsters.add(boss);
            List<Char> member = getChars();
            for (Char pl : member) {
                pl.Info.cx = boss.cx;
                pl.Info.cy = boss.cy;
                pl.service.setXYChar();
                pl.getService().sendMessage(HanderMessage.AddMob(boss));
            }
        }
    }
}
