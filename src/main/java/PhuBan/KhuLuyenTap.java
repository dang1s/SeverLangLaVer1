//package PhuBan;
//
//import Manager.PhuBanManager;
//import MapService.*;
//import com.sg188.lib.Log;
//import com.sg188.real.Char;
//import com.sg188.real.Mob;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ExecutorService;
//
//public class KhuLuyenTap {
//    private ExecutorService executorService;
//    public volatile boolean isClosed = false;
//    public int id;
//    public Map map;
//    public Zone zones;
//    public boolean isRunning;
//    private int levelPhuBan = 0;
//    public long timeStart;
//    public int mapStart;
//    public int zoneIDStart;
//    public byte typeKLT;
//    public List<String> namePl = new ArrayList<>();
//    private long lastUpdateEveryHalfSecond;
//
//    public void create(int id,InfoToDoi todoi, int mapStart, int zoneId) {
//        this.id=id;
//        this.mapStart = mapStart;
//        this.zoneIDStart = zoneId;
//        this.timeStart = System.currentTimeMillis() + 60 * 1000 * 60 * 5;
//        this.toDoi = todoi;
//        this.typeKLT = (todoi.Chars.size() < 4) ? (byte) 1 : (byte) 2;
//
//        int levelSum = todoi.Chars.stream()
//                .filter(c -> c.Info._mapID == mapStart && c.zone.zoneID == zoneId)
//                .mapToInt(Char::level)
//                .sum();
//        this.levelPhuBan = levelSum / todoi.Chars.size();
//        for (Char pl : todoi.Chars) {
//            namePl.add(pl.Info.name);
//        }
//        init();
//        isRunning = true;
//        run();
//    }
//
//    public void AddChar(Char pl) {
//        map.addChar(pl);
//    }
//
//    public void close() {
//        isRunning = false;
//        cleanUp();
//        executorService.shutdownNow();
//    }
//
//    // BIG BOSS 238
//    public void init() {
//        map = new Map(84);
//        map.createWayPoint();
//        int l = 40;
//        Zone zone = new Zone(map, id);
//        zones = zone;
//        for (int i = 0; i < 29; i++) {
//            Mob mob = new Mob();
//            mob.id = 121;
//            mob.exp = levelPhuBan * 300;
//            mob.level = levelPhuBan;
//            switch (i) {
//                case 0:
//                    mob.cx = 219;
//                    mob.cy = 212;
//                    break;
//                case 1:
//                    mob.cx = 273;
//                    mob.cy = 212;
//                    break;
//                case 2:
//                    mob.cx = 303;
//                    mob.cy = 212;
//                    break;
//                case 3:
//                    mob.cx = 357;
//                    mob.cy = 212;
//                    break;
//                case 4:
//                    mob.cx = 429;
//                    mob.cy = 212;
//                    break;
//                case 5:
//                    mob.cx = 482;
//                    mob.cy = 212;
//                    break;
//                case 6:
//                    mob.cx = 587;
//                    mob.cy = 276;
//                    break;
//                case 7:
//                    mob.cx = 626;
//                    mob.cy = 276;
//                    break;
//                case 8:
//                    mob.cx = 706;
//                    mob.cy = 276;
//                    break;
//                case 9:
//                    mob.cx = 587;
//                    mob.cy = 276;
//                    break;
//                case 10:
//                    mob.cx = 134;
//                    mob.cy = 430;
//                    break;
//                case 11:
//                    mob.cx = 231;
//                    mob.cy = 430;
//                    break;
//                case 12:
//                    mob.cx = 332;
//                    mob.cy = 430;
//                    break;
//                case 13:
//                    mob.cx = 905;
//                    mob.cy = 380;
//                    break;
//                case 14:
//                    mob.cx = 1010;
//                    mob.cy = 380;
//                    break;
//                case 15:
//                    mob.cx = 1111;
//                    mob.cy = 380;
//                    break;
//                case 16:
//                    mob.cx = 967;
//                    mob.cy = 559;
//                    break;
//                case 17:
//                    mob.cx = 1084;
//                    mob.cy = 559;
//                    break;
//                case 18:
//                    mob.cx = 1204;
//                    mob.cy = 559;
//                    break;
//                case 19:
//                    mob.cx = 778;
//                    mob.cy = 695;
//                    break;
//                case 20:
//                    mob.cx = 876;
//                    mob.cy = 695;
//                    break;
//                case 21:
//                    mob.cx = 975;
//                    mob.cy = 695;
//                    break;
//                case 22:
//                    mob.cx = 104;
//                    mob.cy = 553;
//                    break;
//                case 23:
//                    mob.cx = 189;
//                    mob.cy = 553;
//                    break;
//                case 24:
//                    mob.cx = 294;
//                    mob.cy = 553;
//                    break;
//                case 25:
//                    mob.cx = 405;
//                    mob.cy = 596;
//                    break;
//                case 26:
//                    mob.cx = 1270;
//                    mob.cy = 187;
//                    break;
//                case 27:
//                    mob.cx = 1192;
//                    mob.cy = 188;
//                    break;
//                case 28:
//                    mob.cx = 1035;
//                    mob.cy = 151;
//                    break;
//            }
//
//            mob.status = 2;
//            mob.hpGoc = mob.hp = mob.hpFull = levelPhuBan * 3500;
//            if (mob.hpGoc <= 0) {
//                mob.hpGoc = 1;
//            }
//            mob.expGoc = levelPhuBan * 203;
//            if (mob.expGoc <= 0) {
//                mob.expGoc = 1;
//            }
//            mob.paintMiniMap = false;
//            mob.idEntity = i;
//
//            mob.reSpawn(zone);
//            zone.monsters.add(mob);
//        }
//        map.getZones().add(zone);
//        if (toDoi != null) {
//            for (Char c : toDoi.Chars) {
//                if (c.Info._mapID == mapStart && c.zone.zoneID == zoneIDStart) {
//                    c.Info.TimeStartHD = timeStart;
//                    c.Info._mapID = 84;
//                    c.Info.cx = 98;
//                    c.Info.cy = 763;
//                    map.addChar(c);
//                    c.InfoGame.KhuLuyenTap = this;
//                }
//            }
//        }
//    }
//
//    private void run() {
//        executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(() -> {
//            while (isRunning) {
//                update();
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    Log.error("Error in KhuLuyenTap execution: " + e.getMessage());
//                }
//                if (!isRunning || timeStart < System.currentTimeMillis()) {
//                    close();
//                }
//            }
//        });
//    }
//
//    public void update() {
//        long l = System.currentTimeMillis();
//        if (zones != null && zones.players != null && zones.monsters != null) {
//            boolean isUpdateEveryHalfSecond = ((l - this.lastUpdateEveryHalfSecond) >= 500);
//            if (isUpdateEveryHalfSecond) {
//                this.lastUpdateEveryHalfSecond = l;
//            }
//            for (Char pl : zones.getChars()) {
//                if (pl != null && pl.user != null) {
//                    if (isUpdateEveryHalfSecond) {
//                        pl.updateEveryHalfSecond();
//                    }
//                    pl.update();
//                }
//            }
//            for (int i = 0; i < zones.monsters.size(); i++) {
//                Mob mob = zones.monsters.get(i);
//                if (mob != null) {
//                    if (mob.isReSpawn && System.currentTimeMillis() - mob.timeDie >= 5500L) {
//                        mob.reSpawn(zones);
//                        zones.reSpawnMobToAllChar(mob);
//                    }
//                    if (!mob.isDie) {
//                        for (int j = 0; j < zones.players.size(); j++) {
//                            Char mChar = zones.players.get(j);
//                            if (mChar != null && mChar.user != null) {
//                                if (mChar.Point.hp > 0 && mob.getRe(mChar.Info) < 50 + mob.getMobTemplate().speedMove) {
//                                    if (System.currentTimeMillis() - mob.delayAttack >= 2500) {
//                                        zones.mobAttackChar(mob, mChar);
//                                        mob.delayAttack = System.currentTimeMillis();
//                                    }
//                                }
//                            }
//                        }
//                        mob.UpdateEff(zones);
//                    }
//                }
//            }
//        }
//    }
//
//    // 375 : 514
//    private void cleanUp() {
//        if (!isClosed) {
//            // Giải phóng tài nguyên tại đây
//            isClosed = true;
//            PhuBanManager.removePhoBan(id);
//            if (zones != null && zones.players != null) {
//                for (int i = 0; i < zones.players.size(); i++) {
//                    Char pl = zones.players.get(i);
//                    if (pl != null && pl.user != null) {
//                        pl.Info.cx = 375;
//                        pl.Info.cy = 515;
//                        pl.Info._mapID = pl.Info.mapReSpawm;
//                        pl.zone = null;
//                        pl.Info.TimeStartHD = 0;
//                        pl.InfoGame.KhuLuyenTap = null;
//                        Map.maps[pl.Info.mapReSpawm].addChar(pl);
//                    }
//                }
//            }
//
//            // Clear và set null các collection để giúp GC
//            if (zones != null) {
//                zones.players.clear();
//                zones.monsters.clear();
//            }
//            zones = null;
//            map = null;
//            namePl = null;
//        }
//    }
//}
